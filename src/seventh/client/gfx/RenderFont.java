/*
 * see license.txt 
 */
package seventh.client.gfx;


/**
 * @author Tony
 *
 */
public class RenderFont {

    /**
     * Draws a shaded string
     * @param canvas
     * @param str
     * @param x
     * @param y
     * @param color
     */
    public static void drawShadedString(Canvas canvas, String str, int x, int y, Integer color) {        
        canvas.drawString(str, x-2, y+1, 0xff000000);
        canvas.drawString(str, x, y, color);
    }
    
    public static void drawShadedString(Canvas canvas, String str, float x, float y, Integer color) {        
        canvas.drawString(str, x-2f, y+1f, 0xff000000);
        canvas.drawString(str, x, y, color);
    }

}
