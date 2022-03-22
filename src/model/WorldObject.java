package model;

import lwjglutils.OGLTexture2D;

import java.io.IOException;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_REPLACE;

public class WorldObject {
    private OGLTexture2D texture2D;

    public WorldObject() {
    }

    public void setTexture(String texture) {
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL_REPEAT);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL_REPEAT);
        glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_MODULATE);

        try {
            texture2D = new OGLTexture2D(texture);
        } catch (IOException e) {
            try {
                texture2D = new OGLTexture2D("textures/testTexture.jpg");
                System.err.println("Texture problem, replacing with default texture");
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }
    }

    public OGLTexture2D getTexture2D() {
        return texture2D;
    }
}
