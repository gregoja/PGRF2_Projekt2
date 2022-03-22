package world;

import model.*;
import utils.Constants;

import static org.lwjgl.opengl.GL11.*;

public class Maze {
    private final int[][] structure;
    private double angle = 0;

    private final int floor = Constants.FLOOR_ID;
    private final int key = Constants.KEY_ID;
    private final int exit = Constants.EXIT_ID;

    private boolean exitOpened;
    private boolean victory = false;

    // used to measure the time passed from opening the door
    private double time = 0;

    public Maze(int[][] structure) {
        this.structure = structure;
        init();
    }

    public Maze() {
        int nothing = Constants.NOTHING_ID;
        int wb = Constants.WALL_BLOCK_ID;
        int sw = Constants.SCARY_WALL_ID;
        structure = new int[][]{
            {wb, wb, nothing, wb, wb, wb, wb, nothing, wb, wb, wb, nothing},
            {wb, floor, wb, floor, floor  , floor  , floor, wb, key  , floor, floor, wb},
            {wb, floor, wb, floor, wb, floor  , floor, wb, wb, floor, floor, wb},
            {wb, floor, floor  , floor, wb, floor  , floor, floor  , floor, floor, floor, wb},
            {nothing, wb, wb, wb, wb, floor  , sw, floor  , wb, wb, floor, wb},
            {wb, floor, floor  , floor, wb, floor  , floor, floor  , wb, floor, wb, nothing},
            {wb, floor, wb, floor, floor  , floor  , floor, wb, floor, wb, wb, nothing},
            {wb, floor, wb, wb, wb, wb, wb, nothing, wb, wb, floor, wb},
            {wb, floor, wb, floor, floor  , floor  , floor, wb, floor, floor, floor, wb},
            {wb, floor, floor  , floor, wb, wb, floor, floor  , floor, wb, exit , wb},
            {nothing, wb, wb, wb, nothing, nothing, wb, wb, wb, wb, floor, wb}};

        init();
    }

    private void init() {
        double wallWidth = Constants.WALL_WIDTH;
        double wallHeight = Constants.WALL_HEIGHT;
        new WallBlock("textures/bricks.jpg",Constants.WALL_BLOCK_ID, wallWidth, wallHeight);
        new ComplexWallBlock("textures/spider.png", "textures/bricks.jpg",
                "textures/catRedDwarf.png", "textures/bricks.jpg",
                Constants.SCARY_WALL_ID, wallWidth, wallHeight);
        new Floor("textures/Floor.jpg", floor, wallWidth);
        int longsLasts = (int)Math.floor(10*Constants.WALL_WIDTH);
        new Key("textures/venus_surface.jpg", key,0.1*Constants.WALL_WIDTH,longsLasts,longsLasts);
        new Exit("textures/old-door.jpg",exit, wallWidth, wallHeight);

    }

    int getWorldObject(int x, int z) {
        if(x>=0 && z>=0 && x < structure.length && z < structure[0].length) return structure[x][z];
        else return 0;
    }

    void setWorldObject(int x, int z, int listId){
        structure[x][z] = listId;
    }

    public void setExitOpened(boolean exitOpened) {
        this.exitOpened = exitOpened;
    }

    public boolean isExitOpened() {
        return exitOpened;
    }

    void render(double frameTime) {
        double step = 120 * frameTime;

        for (int i = 0; i < structure.length; i++) {
            for (int j = 0; j < structure[0].length; j++) {
                if(exitOpened && structure[i][j] == exit){
                    // exit opened
                    glPushMatrix();
                    time+=frameTime;
                    double doorAngle = -(time/2.)*90;
                    glRotated(doorAngle,0,1,0);
                    glCallList(exit);
                    glPopMatrix();
                    glCallList(floor);
                    if(doorAngle <= -90) {
                        structure[i][j] = floor;
                        time = 0;
                        victory = true;
                    }
                }
                else if (structure[i][j] == key) {
                    glPushMatrix();
                    glCallList(floor);
                    glTranslated(Constants.WALL_WIDTH/2., (float)Constants.PLAYER_HEIGHT/2.f, Constants.WALL_WIDTH/2.f);
                    angle = (angle + step) % 360;
                    glRotated(angle, 0, 1, 0);
                    glCallList(key);
                    glPopMatrix();
                } else {
                    glPushMatrix();
                    glCallList(structure[i][j]);
                    glPopMatrix();
                }
                glTranslated(Constants.WALL_WIDTH, 0f, 0);
            }
            glTranslated(-Constants.WALL_WIDTH * structure[0].length, 0, Constants.WALL_WIDTH);
        }
    }

    public boolean isVictory() {
        return victory;
    }
}