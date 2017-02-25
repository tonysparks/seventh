/*
 * see license.txt 
 */
package seventh.client.gfx;

import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

/**
 * @author Tony
 *
 */
public class GlythData {

    public static int getWidth(BitmapFont font, GlyphLayout bounds, String str) {
        bounds.setText(font, str);        
        int textWidth = (int)bounds.width+1;
        
        // bug in libgdx, doesn't like strings ending with a space,
        // it ignores it
        if(str.endsWith(" ")) {                        
            textWidth += font.getSpaceWidth();
        }
        
        return textWidth;
    }
    

    
    public static int getHeight(BitmapFont font, GlyphLayout bounds, String str) {
        bounds.setText(font, str);
        return (int)bounds.height + 8;
    }
    
    
    private BitmapFont font;
    private GlyphLayout bounds;
    
    
    
    /**
     * @param font
     * @param bounds
     */
    public GlythData(BitmapFont font, GlyphLayout bounds) {
        super();
        this.font = font;
        this.bounds = bounds;
    }
    
    /**
     * Get the text width of the string
     * 
     * @param str
     * @return the width in pixels
     */
    public int getWidth(String str) {
        return getWidth(font, bounds, str);
    }

    
    /**
     * The text height of the string
     * 
     * @param str
     * @return the height in pixels
     */
    public int getHeight(String str) {
        return getHeight(font, bounds, str);
    }
}
