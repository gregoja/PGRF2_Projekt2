package utils;

import java.awt.*;
import java.awt.font.FontRenderContext;
import java.awt.geom.AffineTransform;

public class Utils {
    private static final AffineTransform affinetransform = new AffineTransform();
    private static final FontRenderContext frc = new FontRenderContext(affinetransform,true,true);

    public static int getStringWidth(Font font,String string){
        return (int)(font.getStringBounds(string, frc).getWidth());
    }

    public static int getStringHeight(Font font,String string){
        return (int)(font.getStringBounds(string, frc).getHeight());
    }
}
