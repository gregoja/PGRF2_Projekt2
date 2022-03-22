package world;

import utils.Constants;
import transforms.Vec2D;
import transforms.Vec3D;

import java.util.HashMap;
import java.util.Map;

public class World {
    private final Maze maze;
    private final Player player;
    private final Map<Integer,Boolean> colliders = new HashMap<>();

    private int xCurCoord;
    private int zCurCoord;

    public World(Maze maze, Player player) {
        this.maze = maze;
        this.player = player;
        colliders.put(Constants.WALL_BLOCK_ID,true);
        colliders.put(Constants.SCARY_WALL_ID,true);
        colliders.put(Constants.EXIT_ID,true);

        updateCoordinates();
    }

    /**
     *
     * @return display list number of object at current Position.
     */
    public int getObjectAt() {
        return maze.getWorldObject(zCurCoord, xCurCoord);
    }

    public int getObjectInFrontOf() {
        Vec3D lookVector = player.getEyeVector();
        int lookX = (int) Math.round(lookVector.getX());
        int lookZ = (int) Math.round(lookVector.getZ());

        return maze.getWorldObject(zCurCoord + lookZ, xCurCoord + lookX);
    }

    public void setObjectAt(int objectId){
        maze.setWorldObject(zCurCoord,xCurCoord,objectId);
    }

    public void playerMoveForward(double speed) {
        collisionMove(player.calculateForwardMove(speed));
    }

    public void playerMoveBackward(double speed) {
        playerMoveForward(-speed);
    }

    public void playerMoveLeft(double speed) {
        playerMoveRight(-speed);
    }

    /**
     *  Moves the player right
     * @param speed speed of the player
     */
    public void playerMoveRight(double speed) {
        collisionMove(player.calculateRightMove(speed));
    }

    /**
     * Updates coordinates to the maze array.
     * Coordinates are calculated as player position divided by wall width
     */
    private void updateCoordinates(){
        xCurCoord = (int) (player.getX()/ Constants.WALL_WIDTH);
        zCurCoord = (int) (player.getZ()/ Constants.WALL_WIDTH);
    }

    /**
     * Calculates new coordinates of the player, checks whether the move should be allowed
     * and updates player position accordingly
     * @param dxdz move vector
     */
    private void collisionMove(Vec2D dxdz){
        double xNew = player.getX() + dxdz.getX();
        double zNew = player.getZ() + dxdz.getY();
        Vec3D positionNew = player.getPosition();
        double wallWidth = Constants.WALL_WIDTH;
        double collisionSize = Constants.WALL_WIDTH / 10.;

        double zNewCoord = zNew / wallWidth;
        // do I move in positive or negative direction of x axis?
        double xNewCoord = (xNew - player.getX() > 0) ? (xNew + collisionSize) / wallWidth : (xNew - collisionSize) / wallWidth;
        if(colliders.get(maze.getWorldObject((int) zNewCoord, (int) xNewCoord)) == null){
            // if maze at calculated coordinates is something that is not specified in the hasmap
            // of clipped objects the movement in X axis is allowed.
            positionNew = positionNew.withX(xNew);
        }

        // xNewCoord have been tempered with in previous step. XnewCoord needs to be reset
        xNewCoord = xNew / wallWidth;
        zNewCoord =  (zNew - player.getZ() > 0) ? (zNew + collisionSize) / wallWidth : (zNew - collisionSize) / wallWidth;
        if (colliders.get(maze.getWorldObject((int) zNewCoord, (int) xNewCoord))==null) {
            positionNew = positionNew.withZ(zNew);
        }
        player.setPosition(positionNew);
        updateCoordinates();
    }

    public void render(double frameTime){
        maze.render(frameTime);
    }
}
