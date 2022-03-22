package model;

import static global.GlutUtils.glutSolidSphere;
import static org.lwjgl.opengl.GL11.*;

public class Key extends WorldObject {

    public Key(String texture, int listNumber,double r, int longs, int lats) {
        setTexture(texture);
        glNewList(listNumber,GL_COMPILE);
        getTexture2D().bind();
        glEnable(GL_TEXTURE_2D);
        glMatrixMode(GL_MODELVIEW);
        glPushMatrix();
        glColor3f(1f, 1f, 1f);
        glRotatef(-90,1,0,0);
        glutSolidSphere(r, lats,longs);
        glPopMatrix();
        glDisable(GL_TEXTURE_2D);
        glEndList();
    }
}