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
     * Text color directory
     */
    public static final Integer[] COLORS = {
            0xffff0000,  // RED    
            0xff00ff00,  // GREEN
            0xffffff00,  // YELLOW
            0xff0000ff,  // BLUE
            0xff00ffff,  // LIGHT BLUE
            0xffff00ff,  // PINK
            0xffffffff,  // WHITE
            0xff006600,  // DARK_GREEN
            0xffd3d3d3,  // GREY
            0xff000000,  // BLCK
    };
    

    /**
     * Get the text width (which will exclude any color encoding characters)
     * 
     * @param canvas
     * @param str
     * @return the width of the text
     */
    public static float getTextWidth(Canvas canvas, String str) {
        return getTextWidth(canvas, str, true, false);
    }
    
    /**
     * Get the text width (which will exclude any color encoding characters)
     * 
     * @param canvas
     * @param str
     * @param colorEncoding true if color encoding should be accounted for
     * @param monospaced if the font is monospaced
     * @return the width of the text
     */
    public static float getTextWidth(Canvas canvas, String str, boolean colorEncoding, boolean monospaced) {
        int len = str.length();
        
        float width = 0f;
        final float CharWidth = canvas.getWidth("W");
        
        for(int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if(c == '^' && colorEncoding) {
                if(i+1 < len) {
                    char c2 = str.charAt(i+1);
                    if(Character.isDigit(c2)) {        
                        i++;
                        continue;
                    }
                }
            }
                    
            String text = Character.toString(c);
            width += monospaced ? CharWidth : (canvas.getWidth(text) - 0.5f);
        }
        
        return width;
    }
    
    /**
     * Get the text without any color encodings
     * 
     * @param str
     * @return the text without any color encodings
     */
    public static String getDecodedText(String str) {
        StringBuilder sb = new StringBuilder(str.length());
        int len = str.length();
        
        for(int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if(c == '^') {
                if(i+1 < len) {
                    char c2 = str.charAt(i+1);
                    if(Character.isDigit(c2)) {                            
                        i++;
                        continue;
                    }
                }
            }
                        
            sb.append(c);
        }
        
        return sb.toString();
    }
    
    /**
     * Allow for embedding color encoding into strings just like Call Of Duty
     * 
     * @param canvas
     * @param str
     * @param x
     * @param y
     * @param color
     * @param drawShadow
     */
    public static void drawShadedString(Canvas canvas, String str, float x, float y, Integer color, boolean drawShadow, boolean drawColors, boolean monospaced) {
        int len = str.length();
        float xPos = x;
        float yPos = y;
        
        Integer currentColor = color;
        final float CharWidth = canvas.getWidth("W");
        
        if(drawShadow) {
            for(int i = 0; i < len; i++) {
                char c = str.charAt(i);
                if(c == '^' && drawColors) {
                    if(i+1 < len) {
                        char c2 = str.charAt(i+1);
                        if(Character.isDigit(c2)) {                            
                            i++;
                            continue;
                        }
                    }
                }
                else if(c == '\n') {
                    xPos = x;
                    yPos += canvas.getHeight("n") - 0.5f;
                }
                
                
                String text = Character.toString(c);                
                canvas.drawString(text, xPos-1.5f, yPos+1, 0xff000000);
                                
                xPos += monospaced ? CharWidth : (canvas.getWidth(text) - 0.5f);
            }   
        }
        
        xPos = x;
        yPos = y;
        
        for(int i = 0; i < len; i++) {
            char c = str.charAt(i);
            if(c == '^' && drawColors) {
                if(i+1 < len) {
                    char c2 = str.charAt(i+1);
                    if(Character.isDigit(c2)) {
                        currentColor = COLORS[Character.getNumericValue(c2)];
                        i++;
                        continue;
                    }
                }
            }
            else if(c == '\n') {
                xPos = x;
                yPos += canvas.getHeight("n") - 0.5f;
            }
            
            String text = Character.toString(c);
            canvas.drawString(text, xPos, yPos, currentColor);
            
            xPos += monospaced ? CharWidth : (canvas.getWidth(text) - 0.5f);
        }
    }
    
    public static void drawShadedString(Canvas canvas, String str, int x, int y, Integer color, boolean drawShadow) {
        drawShadedString(canvas, str, x, y, color, drawShadow, true, false);
    }
    
    public static void drawShadedString(Canvas canvas, String str, float x, float y, Integer color, boolean drawShadow) {
        drawShadedString(canvas, str, x, y, color, drawShadow, true, false);
    }
    
    /**
     * Draws a shaded string
     * @param canvas
     * @param str
     * @param x
     * @param y
     * @param color
     */
    public static void drawShadedString(Canvas canvas, String str, int x, int y, Integer color) {        
        drawShadedString(canvas, str, x, y, color, true, true, false);
    }
    
    public static void drawShadedString(Canvas canvas, String str, float x, float y, Integer color) {        
        drawShadedString(canvas, str, x, y, color, true, true, false);
    }

}
