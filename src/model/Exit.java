package model;

import utils.SoundService;

import static org.lwjgl.opengl.GL11.*;

public class Exit extends WorldObject {

    public Exit(String texture, int listNumber, double width, double height) {
        setTexture(texture);

        glNewList(listNumber,GL_COMPILE);
        glEnable(GL_TEXTURE_2D);
        getTexture2D().bind();
        glBegin(GL_TRIANGLE_STRIP);

        glColor3f(1,1,1);
        glNormal3d(-1,0,0);
        glTexCoord2f(0,1);
        glVertex3d(width,0,0);
        glTexCoord2f(0,0);
        glVertex3d(width,height,0);
        glTexCoord2f(1,1);
        glVertex3d(0,0,0);
        glTexCoord2f(1,0);
        glVertex3d(0,height,0);

        glEnd();
        glDisable(GL_TEXTURE_2D);
        glEndList();
    }

    public static void makeSound(){
        SoundService.play("res/sounds/door.wav");
    }
}
