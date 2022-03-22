package model;

import static org.lwjgl.opengl.GL11.*;

public class Floor extends WorldObject {

    public Floor(String texture, int listNumber,double width) {
        setTexture(texture);
        glNewList(listNumber,GL_COMPILE);
        glEnable(GL_TEXTURE_2D);
        getTexture2D().bind();
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);
        glBegin(GL_TRIANGLE_STRIP);
        glColor3f(1,1,1);
        glTexCoord2f(0,0);
        glNormal3d(0,1,0);
        glVertex3d(width,0,0);
        glTexCoord2f(1,0);
        glVertex3d(width,0,width);
        glTexCoord2f(0,1);
        glVertex3d(0,0,0);
        glTexCoord2f(1,1);
        glVertex3d(0,0,width);
        glEnd();

        glDisable(GL_CULL_FACE);
        glDisable(GL_TEXTURE_2D);
        glEndList();
    }
}
