package model;

import lwjglutils.OGLTexture2D;
import utils.Constants;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.glEndList;
import static org.lwjgl.opengl.GL30.glGenVertexArrays;

public class Skybox {
    public Skybox(String fileName,int size, int listName) {

        OGLTexture2D skybox = null;
        try {
            skybox = new OGLTexture2D(fileName);
        } catch (IOException e) {
            try {
                skybox = new OGLTexture2D(Constants.DEFAULT_SKYBOX);
                System.err.println("Texture problem, replacing with default skybox");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        glNewList(listName, GL_COMPILE);
        glPushMatrix();
        glColor3d(1, 1, 1);

        glEnable(GL_TEXTURE_2D);

        assert skybox != null;
        skybox.bind();

        glBegin(GL_QUADS);
        glTexCoord2d(0, 2./3);
        glVertex3d(-size, -size, -size);
        glTexCoord2d(0, 1./3);
        glVertex3d(-size, size, -size);
        glTexCoord2d(1./4, 1./3);
        glVertex3d(-size, size, size);
        glTexCoord2d(1./4, 2./3);
        glVertex3d(-size, -size, size);
        glEnd();

        glBegin(GL_QUADS);
        glTexCoord2d(3./4, 2./3);
        glVertex3d(size, -size, -size);
        glTexCoord2d(2./4, 2./3);
        glVertex3d(size, -size, size);
        glTexCoord2d(2./4, 1./3);
        glVertex3d(size, size, size);
        glTexCoord2d(3./4, 1./3);
        glVertex3d(size, size, -size);
        glEnd();

        glBegin(GL_QUADS);
        glTexCoord2d(1./4, 0);
        glVertex3d(-size, size, -size);
        glTexCoord2d(2./4, 0);
        glVertex3d(size, size, -size);
        glTexCoord2d(2./4, 1./3);
        glVertex3d(size, size, size);
        glTexCoord2d(1./4, 1./3);
        glVertex3d(-size, size, size);
        glEnd();

        glBegin(GL_QUADS);
        glTexCoord2d(3./4, 2./3);
        glVertex3d(size, -size, -size);
        glTexCoord2d(1., 2./3);
        glVertex3d(-size, -size, -size);
        glTexCoord2d(1., 1./3);
        glVertex3d(-size, size, -size);
        glTexCoord2d(3./4, 1./3);
        glVertex3d(size, size, -size);
        glEnd();

        glBegin(GL_QUADS);
        glTexCoord2d(1./4, 1./3);
        glVertex3d(-size, size, size);
        glTexCoord2d(1./4, 2./3);
        glVertex3d(-size, -size, size);
        glTexCoord2d(2./4, 2./3);
        glVertex3d(size, -size, size);
        glTexCoord2d(2./4, 1./3);
        glVertex3d(size, size, size);
        glEnd();


        glDisable(GL_TEXTURE_2D);
        glPopMatrix();

        glEndList();
    }
}
