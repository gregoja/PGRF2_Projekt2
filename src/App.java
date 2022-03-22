import model.Exit;
import utils.Constants;
import global.GLCamera;
import lwjglutils.OGLTextRenderer;
import lwjglutils.OGLTexture2D;
import lwjglutils.OGLUtils;
import model.Skybox;
import org.lwjgl.BufferUtils;
import org.lwjgl.glfw.*;
import org.lwjgl.opengl.GL;
import org.lwjgl.system.MemoryStack;
import transforms.Vec3D;
import utils.Utils;
import world.*;

import java.awt.*;
import java.io.IOException;
import java.nio.DoubleBuffer;
import java.nio.IntBuffer;
import java.util.Locale;

import static global.GluUtils.gluPerspective;
import static org.lwjgl.glfw.Callbacks.glfwFreeCallbacks;
import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.system.MemoryStack.stackPush;
import static org.lwjgl.system.MemoryUtil.NULL;

public class App {

    private int width = Constants.WIDTH, height = Constants.HEIGHT;
    private OGLTextRenderer textRenderer;

    private long window;
    private double dx, dy, ox, oy;
    private double oldTime;
    private boolean paused;
    // used for marking whether is this the first frame of paused screen
    // and whether the buffers should swap
    private boolean swap = true;

    private OGLTexture2D.Viewer textureViewer;

    private Maze maze;
    private Player player;
    private World world;
    private OGLTexture2D pauseScreen;

    private boolean moveForward, moveBackward, moveLeft, moveRight, actionKey;
    private double speed;

    private String textFeedback = "";

    private boolean fog = true;
    private boolean nightMode = false;

    public static void main(String[] args) {
        new App().run();
    }

    private void init() {
        // Setup an error callback. The default implementation
        // will print the error message in System.err.
        GLFWErrorCallback.createPrint(System.err).set();

        // Initialize GLFW. Most GLFW functions will not work before doing this.
        if (!glfwInit())
            throw new IllegalStateException("Unable to initialize GLFW");

        // Configure GLFW
        glfwDefaultWindowHints(); // optional, the current window hints are already the default
        glfwWindowHint(GLFW_VISIBLE, GLFW_FALSE); // the window will stay hidden after creation
        glfwWindowHint(GLFW_RESIZABLE, GLFW_TRUE); // the window will be resizable

        // Create the window
        window = glfwCreateWindow(Constants.WIDTH, Constants.HEIGHT, "Slightly scary maze v1.0", NULL, NULL);
        if (window == NULL) throw new RuntimeException("Failed to create the GLFW window");

        glfwSetKeyCallback(window, (window, key, scancode, action, mods) -> {
            if (!paused) {
                if (key == GLFW_KEY_ESCAPE) pauseGame(window);
                speed = glfwGetKey(window, Constants.SPRINT_KEY) != GLFW_RELEASE ? Constants.RUNNING_SPEED : Constants.WALKING_SPEED;
                moveForward = glfwGetKey(window, Constants.FORWARD_KEY) != GLFW_RELEASE;
                moveBackward = glfwGetKey(window, Constants.BACKWARD_KEY) != GLFW_RELEASE;
                moveLeft = glfwGetKey(window, Constants.LEFT_KEY) != GLFW_RELEASE;
                moveRight = glfwGetKey(window, Constants.RIGHT_KEY) != GLFW_RELEASE;
                actionKey = glfwGetKey(window, Constants.ACTION_KEY) != GLFW_RELEASE;
                if (glfwGetKey(window, Constants.FOG_KEY) != GLFW_RELEASE) fog = !fog;
                if (glfwGetKey(window, Constants.NIGHT_MODE_KEY) != GLFW_RELEASE) nightMode = !nightMode;
            }
        });

        glfwSetCursorPosCallback(window, new GLFWCursorPosCallback() {
            @Override
            public void invoke(long window, double x, double y) {
            if (glfwGetInputMode(window, GLFW_CURSOR) == GLFW_CURSOR_DISABLED) {
                dx = x - ox;
                dy = y - oy;
                ox = x;
                oy = y;

                player.addAzimuth((Math.PI * (dx)) / width);
                player.addZenith(-(Math.PI * (dy)) / height);

                dx = 0;
                dy = 0;
            }
            }
        });

        glfwSetMouseButtonCallback(window, new GLFWMouseButtonCallback() {

            @Override
            public void invoke(long window, int button, int action, int mods) {
            if (paused) {
                unPauseGame(window);
                DoubleBuffer xBuffer = BufferUtils.createDoubleBuffer(1);
                DoubleBuffer yBuffer = BufferUtils.createDoubleBuffer(1);
                glfwGetCursorPos(window, xBuffer, yBuffer);
                double x = xBuffer.get(0);
                double y = yBuffer.get(0);
                if (button == GLFW_MOUSE_BUTTON_1 && action == GLFW_PRESS) {
                    ox = (float) x;
                    oy = (float) y;
                }
            }
            }

        });

        glfwSetFramebufferSizeCallback(window, new GLFWFramebufferSizeCallback() {
            @Override
            public void invoke(long window, int w, int h) {
            if (w > 0 && h > 0 &&
                    (w != width || h != height)) {
                width = w;
                height = h;

                if (textRenderer != null)
                    textRenderer.resize(width, height);
            }
            }
        });

        // Get the thread stack and push a new frame
        try (MemoryStack stack = stackPush()) {
            IntBuffer pWidth = stack.mallocInt(1); // int*
            IntBuffer pHeight = stack.mallocInt(1); // int*

            // Get the window size passed to glfwCreateWindow
            glfwGetWindowSize(window, pWidth, pHeight);

            // Get the resolution of the primary monitor
            GLFWVidMode vidmode = glfwGetVideoMode(glfwGetPrimaryMonitor());

            // Center the window
            glfwSetWindowPos(
                    window,
                    (vidmode.width() - pWidth.get(0)) / 2,
                    (vidmode.height() - pHeight.get(0)) / 2
            );
        } // the stack frame is popped automatically

        // Make the OpenGL context current
        glfwMakeContextCurrent(window);

        glfwSwapInterval(Constants.SWAP_INTERVAL);

        // Make the window visible
        glfwShowWindow(window);

        // This line is critical for LWJGL's interoperation with GLFW's
        // OpenGL context, or any context that is managed externally.
        // LWJGL detects the context that is current in the current thread,
        // creates the GLCapabilities instance and makes the OpenGL
        // bindings available for use.
        GL.createCapabilities();
        OGLUtils.printOGLparameters();

        textRenderer = new OGLTextRenderer(width, height);

        glClearColor(0.1f, 0.1f, 0.1f, 1.0f);
        glEnable(GL_DEPTH_TEST);
        glViewport(0, 0, width, height);

        maze = new Maze();
        new Skybox(Constants.SKYBOX, 250, Constants.SKYBOX_ID);

        player = new Player(new Vec3D(Constants.WALL_WIDTH * 1.5, Constants.PLAYER_HEIGHT, Constants.WALL_WIDTH * 1.5));
        world = new World(maze, player);

        textureViewer = new OGLTexture2D.Viewer();

        try {
            pauseScreen = new OGLTexture2D("textures/paused.png");
        } catch (IOException e) {
            e.printStackTrace();
        }

        float[] light_amb = new float[]{0.1f, 0.1f, 0.1f, 1.0f};
        float[] light_spec = new float[]{1f, 1f, 1f, 1.0f};
        float[] light_dif = new float[]{0.5f, 0.5f, 0.5f, 1.0f};

        glEnable(GL_LIGHT0);
        glLightfv(GL_LIGHT0, GL_AMBIENT, light_amb);
        glLightfv(GL_LIGHT0, GL_DIFFUSE, light_dif);
        glLightfv(GL_LIGHT0, GL_SPECULAR, light_spec);
        //glLightf(GL_LIGHT0, GL_SPOT_EXPONENT, 0.1f);
        glLightf(GL_LIGHT0, GL_SPOT_CUTOFF, 10);
        //glLightf(GL_LIGHT0, GL_CONSTANT_ATTENUATION,);
        glLightf(GL_LIGHT0, GL_LINEAR_ATTENUATION, 0.35f);
        //glLightf(GL_LIGHT0, GL_QUADRATIC_ATTENUATION, );


        float[] amb = {0.3f, 0.3f, 0.3f, 1.0f};
        float[] dif = {1.0f, 1.0f, 1f, 1.0f};
        float[] spec = {0.6f, 0.6f, 0.6f, 1.0f};
        float[] emis = new float[]{0.1f, 0.1f, 0.1f, 1};

        // global material for everything
        glMaterialfv(GL_FRONT_AND_BACK, GL_AMBIENT, amb);
        glMaterialfv(GL_FRONT_AND_BACK, GL_DIFFUSE, dif);
        glMaterialfv(GL_FRONT_AND_BACK, GL_SPECULAR, spec);
        glMaterialfv(GL_FRONT, GL_EMISSION, emis);
        glMaterialf(GL_FRONT_AND_BACK, GL_SHININESS, 3000);
    }

    private void unPauseGame(long window) {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_DISABLED);
        paused = false;
        swap = true;
    }

    private void pauseGame(long window) {
        glfwSetInputMode(window, GLFW_CURSOR, GLFW_CURSOR_NORMAL);
        paused = true;
        moveLeft = false;
        moveRight = false;
        moveForward = false;
        moveBackward = false;
    }

    private void loop() {
        glfwPollEvents();
        double time = glfwGetTime();

        double frameTime = time - oldTime;
        double frameTimeMils = frameTime * 1000;
        double fps = 1 / frameTime;
        oldTime = time;

        if(paused){
            glEnable(GL_BLEND);
            textureViewer.view(pauseScreen, -1, -1, 2);
            glDisable(GL_BLEND);
            if(swap){
                glfwSwapBuffers(window);
                swap = false;
            }
            return;
        }

        glClear(GL_COLOR_BUFFER_BIT | GL_DEPTH_BUFFER_BIT);

        if (moveForward) world.playerMoveForward(speed * frameTime);
        if (moveBackward) world.playerMoveBackward(speed * frameTime);
        if (moveLeft) world.playerMoveLeft(speed * frameTime);
        if (moveRight) world.playerMoveRight(speed * frameTime);

        int objInFront = world.getObjectInFrontOf();
        int objAt = world.getObjectAt();

        if (objInFront == Constants.EXIT_ID) {
            if (player.hasKey()) {
                if (actionKey && !maze.isExitOpened()){
                    maze.setExitOpened(true);
                    Exit.makeSound();
                }
                else textFeedback = "Press E to open the door";
            } else {
                textFeedback = "You must find the key";
            }
        }
        if (objInFront == Constants.KEY_ID) textFeedback = "The key to unlock exit door";
        if(maze.isVictory()) textFeedback = "Victory! Congratulations!";
        if (objAt == Constants.KEY_ID) {
            world.setObjectAt(Constants.FLOOR_ID);
            player.setHasKey(true);
        }

        if (fog) {
            glEnable(GL_FOG);
            glFogi(GL_FOG_START, Constants.FOG_START);
            glFogi(GL_FOG_END, Constants.FOG_END);
            glFogf(GL_FOG_DENSITY, Constants.FOG_DENSITY);
            glFogfv(GL_FOG_COLOR, new float[]{0.1f, 0.1f, 0.1f, 1f});
            glFogi(GL_FOG_MODE, GL_LINEAR);
        }

        GLCamera cameraSky = new GLCamera(player.getCamera());
        cameraSky.setPosition(new Vec3D());

        glMatrixMode(GL_PROJECTION);
        glLoadIdentity();
        gluPerspective(45, width / (float) height, 0.01, 500.0f);

        glMatrixMode(GL_MODELVIEW);
        glLoadIdentity();
        glPushMatrix();
        player.getCamera().setMatrix();
        if (nightMode) {
            glEnable(GL_LIGHTING);
            Vec3D lookVector = player.getEyeVector();
            Vec3D position = player.getPosition();
            float[] light_position = new float[]{(float) position.getX(), (float) position.getY(), (float) position.getZ(), 1};
            float[] light_direction = new float[]{(float) lookVector.getX(), (float) lookVector.getY(), (float) lookVector.getZ(), 0};
            glLightfv(GL_LIGHT0, GL_POSITION, light_position);
            glLightfv(GL_LIGHT0, GL_SPOT_DIRECTION, light_direction);
        }
        world.render(frameTime);
        glPopMatrix();

        glDisable(GL_FOG);
        glDisable(GL_LIGHTING);
        glPushMatrix();
        cameraSky.setMatrix();
        glCallList(Constants.SKYBOX_ID);
        glPopMatrix();

        textRenderer.clear();
        textRenderer.addStr2D(3, 20, String.format(Locale.US, "Frametime %3.2f ms", frameTimeMils));
        textRenderer.addStr2D(3, 40, String.format(Locale.US, "FPS %3.2f", fps));
        //textRenderer.addStr2D(3, 60, "[x][z] = ["+x+"]["+z+"]");
        if (textFeedback != null) {
            Font font = Constants.TEXT_FEEDBACK_FONT;
            textRenderer.setFont(font);
            int textWidth =  Utils.getStringWidth(font, textFeedback);
            int textHeight = Utils.getStringHeight(font, textFeedback);
            textRenderer.addStr2D((int) (width / 2. - (textWidth / 2.)), (int) (height / 2. + (textHeight / 2.)), textFeedback);
            textFeedback = null;
            textRenderer.setFont(Constants.NORMAL_FONT);
        }
        textRenderer.draw();

        glfwSwapBuffers(window);
    }

    public void run() {
        try {
            init();
            // two loops have to happen to fill both buffers
            loop();
            loop();
            paused = true;
            while (!glfwWindowShouldClose(window)) {
                loop();
            }

            glfwFreeCallbacks(window);
            glfwDestroyWindow(window);

        } catch (Throwable t) {
            t.printStackTrace();
        } finally {
            glfwTerminate();
            glfwSetErrorCallback(null).free();
        }
    }

}