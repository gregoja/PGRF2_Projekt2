package utils;

import java.awt.*;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.glGenLists;

public class Constants {
    public static final int FORWARD_KEY = GLFW_KEY_W;
    public static final int BACKWARD_KEY = GLFW_KEY_S;
    public static final int LEFT_KEY = GLFW_KEY_A;
    public static final int RIGHT_KEY = GLFW_KEY_D;
    public static final int ACTION_KEY = GLFW_KEY_E;
    public static final int SPRINT_KEY = GLFW_KEY_LEFT_SHIFT;
    public static final int FOG_KEY = GLFW_KEY_F;
    public static final int NIGHT_MODE_KEY = GLFW_KEY_N;

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;
    public static final int SWAP_INTERVAL = 1;

    public static final Font NORMAL_FONT = new Font("SansSerif", Font.PLAIN, WIDTH/80);
    public static final Font TEXT_FEEDBACK_FONT = new Font("LucidaSans", Font.BOLD, WIDTH/32);

    public static final double WALL_WIDTH = 2;
    public static final double WALL_HEIGHT = 2;
    public static final double PLAYER_HEIGHT = (2/3.)*WALL_HEIGHT;

    public static final double WALKING_SPEED = 1.25 * WALL_WIDTH;
    public static final double RUNNING_SPEED = 2.5 * WALL_WIDTH;

    public static final String SKYBOX = "textures/Skybox4.jpg";
    public static final String DEFAULT_SKYBOX = "textures/Skybox3.jpg";


    public static final int FOG_START = (int)(WALL_WIDTH*4);
    public static final int FOG_END = (int)(WALL_WIDTH*9);
    public static final float FOG_DENSITY = 0.05f;

    public static final int NOTHING_ID = glGenLists(1);
    public static final int SKYBOX_ID= glGenLists(1);
    public static final int WALL_BLOCK_ID = glGenLists(1);
    public static final int SCARY_WALL_ID = glGenLists(1);
    public static final int FLOOR_ID = glGenLists(1);
    public static final int KEY_ID = glGenLists(1);
    public static final int EXIT_ID = glGenLists(1);
}