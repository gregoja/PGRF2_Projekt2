package world;

import global.GLCamera;
import transforms.Vec2D;
import transforms.Vec3D;

public class Player {
    private final GLCamera camera;
    private boolean hasKey = false;

    public Player(Vec3D position) {
        camera = new GLCamera();
        camera.setZenith(0);
        camera.setAzimuth(Math.PI);
        camera.setPosition(position);
    }

    public GLCamera getCamera() {
        return camera;
    }

    public void addZenith(double zenith){
        camera.addZenith(zenith);
    }

    public void addAzimuth(double azimuth){
        camera.addAzimuth(azimuth);
    }

    public void addToPosition(double dx, double dz){
        camera.setPosition(camera.getPosition().add(new Vec3D(dx,0,dz)));
    }

    public void addToPosition(Vec2D dxdz){
        addToPosition(dxdz.getX(),dxdz.getY());
    }

    public Vec3D getEyeVector(){
        return camera.getEyeVector();
    }

    public double getX(){
        return camera.getPosition().getX();
    }

    public double getY(){
        return camera.getPosition().getY();
    }

    public double getZ(){
        return camera.getPosition().getZ();
    }

    public void setPosition(Vec3D position){
        camera.setPosition(position);
    }

    public Vec3D getPosition(){
        return camera.getPosition();
    }

    public double getAzimuth(){
        return camera.getAzimuth()%360;
    }

    public void setHasKey(boolean hasKey) {
        this.hasKey = hasKey;
    }

    public boolean hasKey() {
        return hasKey;
    }

    public Vec2D calculateForwardMove(double speed){
        double dx = Math.sin(getAzimuth())*speed;
        double dz = -Math.cos(getAzimuth())*speed;
        return new Vec2D(dx,dz);
    }

    public Vec2D calculateBackwardMove(double speed){
        return calculateForwardMove(-speed);
    }

    public Vec2D calculateRightMove(double speed){
        double dx = -Math.sin(getAzimuth() - Math.PI / 2) * speed;
        double dz = +Math.cos(getAzimuth() - Math.PI / 2) * speed;
        return new Vec2D(dx,dz);
    }

    public Vec2D calculateLeftMove(double speed){
        return calculateRightMove(-speed);
    }

}
