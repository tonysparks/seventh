/*
 * see license.txt 
 */
package seventh.client.gfx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Represents the mouse pointer for the game.  This implementation relies on an {@link TextureRegion} for the cursor
 * image.
 * 
 * @author Tony
 *
 */
public class ImageCursor extends Cursor {

    private TextureRegion cursorImg;
    private Vector2f imageOffset;

    /**
     */
    public ImageCursor() {
        this(Art.cursorImg);        
    }
    
    /**
     * @param image 
     *             the cursor image to use
     */
    public ImageCursor(TextureRegion image) {
        super(new Rectangle(image.getRegionWidth(), image.getRegionHeight()));
        this.cursorImg = image;
        
        int imageWidth = cursorImg.getRegionWidth();
        int imageHeight = cursorImg.getRegionHeight();
        this.imageOffset = new Vector2f(imageWidth/2, imageHeight/2);
    }
    
    /**
     * Offset the image so that the image can correctly point
     * to the correct part of the image
     * 
     * @param x
     * @param y
     * @return this instance for method chaining
     */
    public ImageCursor setImageOffset(float x, float y) {
        this.imageOffset.set(x, y);
        return this;
    }
    
    @Override
    public void update(TimeStep timeStep) {            
    }
    
    /**
     * Draws the cursor on the screen
     * @param canvas
     */
    @Override 
    protected void doRender(Canvas canvas) {
        Vector2f cursorPos = getCursorPos();        
        canvas.drawImage(cursorImg, (int)cursorPos.x - imageOffset.x, (int)cursorPos.y - imageOffset.y, null);                
    }
}
