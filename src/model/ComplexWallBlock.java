package model;

import lwjglutils.OGLTexture2D;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;

public class ComplexWallBlock extends WorldObject {
    private final double width, height;

    public ComplexWallBlock(String textureSouth,String textureEast, String textureNorth
            ,String textureWest,int listNumber,double width,double height){

        this.width = width;
        this.height = height;

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

        OGLTexture2D textureS,textureE,textureN,textureW;
        try {
            textureS = new OGLTexture2D(textureSouth);
            textureE = new OGLTexture2D(textureEast);
            textureN = new OGLTexture2D(textureNorth);
            textureW = new OGLTexture2D(textureWest);
        } catch (IOException e) {
            textureS = null;
            textureE = null;
            textureN = null;
            textureW = null;
            e.printStackTrace();
        }

        glNewList(listNumber,GL_COMPILE);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);

        glBegin(GL_TRIANGLE_STRIP);
        glColor3f(1,1,1);

        assert textureS != null;
        textureS.bind();
        wallSouthStructure();

        textureE.bind();
        wallEastStructure();

        textureN.bind();
        wallNorthStructure();

        textureW.bind();
        wallWestStructure();

        glEnd();

        glDisable(GL_CULL_FACE);
        glDisable(GL_TEXTURE_2D);
        glEndList();
    }

    public ComplexWallBlock(String texture,int listNumber,double width,double height){
        this.width = width;
        this.height = height;

        setTexture(texture);

        glNewList(listNumber,GL_COMPILE);
        glEnable(GL_TEXTURE_2D);
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);

        glBegin(GL_TRIANGLE_STRIP);
        glColor3f(1,1,1);

        getTexture2D().bind();
        wallSouthStructure();
        wallEastStructure();
        wallNorthStructure();
        wallWestStructure();
        glEnd();

        glDisable(GL_CULL_FACE);
        glDisable(GL_TEXTURE_2D);
        glEndList();
    }

    private void wallSouthStructure(){
        glTexCoord2f(0,1);
        glNormal3d(0,0,-1);
        glVertex3d(width,0,0);
        glTexCoord2f(0,0);
        glVertex3d(width,height,0);
        glTexCoord2f(1,1);
        glVertex3d(0,0,0);
        glTexCoord2f(1,0);
        glVertex3d(0,height,0);
    }

    private void wallEastStructure(){
        glTexCoord2f(1,1);
        glNormal3d(-1,0,0);
        glVertex3d(0,0,0);
        glTexCoord2f(1,0);
        glVertex3d(0,height,0);
        glTexCoord2f(0,1);
        glVertex3d(0,0,width);
        glTexCoord2f(0,0);
        glVertex3d(0,height,width);
    }

    private void wallNorthStructure(){
        glTexCoord2f(0,1);
        glNormal3d(0,0,1);
        glVertex3d(0,0,width);
        glTexCoord2f(0,0);
        glVertex3d(0,height,width);
        glTexCoord2f(1,1);
        glVertex3d(width,0,width);
        glTexCoord2f(1,0);
        glVertex3d(width,height,width);
    }

    private void wallWestStructure(){
        glTexCoord2f(1,1);
        glNormal3d(1,0,0);
        glVertex3d(width,0,width);
        glTexCoord2f(1,0);
        glVertex3d(width,height,width);
        glTexCoord2f(0,1);
        glVertex3d(width,0,0);
        glTexCoord2f(0,0);
        glVertex3d(width,height,0);
    }
}