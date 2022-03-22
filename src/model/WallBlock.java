package model;

import static org.lwjgl.opengl.GL11.*;

public class WallBlock extends WorldObject {

    public WallBlock(String texture,int listNumber,double width,double height){
        setTexture(texture);

        glNewList(listNumber,GL_COMPILE);
        glEnable(GL_TEXTURE_2D);
        getTexture2D().bind();
        glEnable(GL_CULL_FACE);
        glCullFace(GL_FRONT);

        glBegin(GL_TRIANGLE_STRIP);
        glColor3f(1,1,1);
        glTexCoord2f(0,1);
        glVertex3d(width,0,0);
        glTexCoord2f(0,0);
        glVertex3d(width,height,0);
        glTexCoord2f(1,1);
        glVertex3d(0,0,0);
        glTexCoord2f(1,0);
        glVertex3d(0,height,0);

        glTexCoord2f(0,1);
        glVertex3d(0,0,width);
        glTexCoord2f(0,0);
        glVertex3d(0,height,width);

        glTexCoord2f(1,1);
        glVertex3d(width,0,width);
        glTexCoord2f(1,0);
        glVertex3d(width,height,width);

        glTexCoord2f(0,1);
        glVertex3d(width,0,0);
        glTexCoord2f(0,0);
        glVertex3d(width,height,0);

        glEnd();

        glDisable(GL_CULL_FACE);
        glDisable(GL_TEXTURE_2D);
        glEndList();
    }
}
