/*
 * see license.txt 
 */
package seventh.client.gfx;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;
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
public class ReticleCursor extends Cursor {

    private float desiredAccuracy;
    //private boolean isTouched;
    private Sprite reticle;
    
    /**
     */
    public ReticleCursor() {
        this(96, 96);
    }
    
    /**
     * @param image 
     *             the cursor image to use
     */
    public ReticleCursor(int width, int height) {
        super(new Rectangle(width, height));    
        setColor(0xafffff00);
        this.setAccuracy(1);
        this.reticle = new Sprite(Art.reticleImg);
        this.reticle.rotate(90);
        this.reticle.setScale(0.8f);
    }
    
    @Override
    public void touchAccuracy() {    
        //this.isTouched = true;
    }
    
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Cursor#setAccuracy(float)
     */
    @Override
    public void setAccuracy(float accuracy) {
        this.desiredAccuracy = accuracy;
    }
    
    @Override
    public void update(TimeStep timeStep) {
        /*if(this.isTouched) {
            if(getAccuracy() < 0.501f) {
                this.isTouched = false;
                this.desiredAccuracy = 1f;
            }
            else {
                this.desiredAccuracy = 0.5f;
            }            
        }*/
        
        
        float delta = this.getAccuracy() - this.desiredAccuracy;

        if(Math.abs(delta) > 0.001f) {
            float speed = (delta < 0) ? 0.33f : 0.5f;             
            float newAccuracy = this.getAccuracy() - (delta * speed); 
            super.setAccuracy(newAccuracy);
        }
        else {
            super.setAccuracy(this.desiredAccuracy);
        }
    }
    
    /**
     * Draws the cursor on the screen
     * @param canvas
     */
    @Override 
    protected void doRender(Canvas canvas) {
        Vector2f cursorPos = getCursorPos();
        Rectangle bounds = getBounds();
        
        final float accuracy = getAccuracy();
        
        
        final float xRange = bounds.width / 2f;
        final float yRange = bounds.height / 2f;
        
        final float centerSize = 5;
        
        final float markHeight = 16;//15;
        final float markWidth = 32;//2;
        
        final int color = getColor();
        
        // center
        {
            //float x = cursorPos.x; 
            //float y = cursorPos.y;
            //canvas.fillRect(x-1, y, centerSize, centerSize, 0xff000000);
            //canvas.fillRect(x, y, centerSize, centerSize, color);
            //canvas.fillRect(x, y, centerSize, centerSize, color);
        }
        
        reticle.setRotation(90);
                
        Color col = reticle.getColor();
        Color.argb8888ToColor(col, color);
        reticle.setColor(col);
        
        // top
        {
            float x = cursorPos.x - markWidth/2f;
            float y = (cursorPos.y - markHeight - centerSize) - (yRange - (yRange * accuracy));                
            //canvas.fillRect(x, y, markWidth, markHeight, color);
            
            reticle.setPosition(x+2, y+2);            
            canvas.drawRawSprite(reticle);            
        }
        
        // bottom
        {
            float x = cursorPos.x - markWidth/2f;
            float y = (cursorPos.y + centerSize + centerSize) + (yRange - (yRange * accuracy));
            //canvas.fillRect(x, y, markWidth, markHeight, color);
            
            reticle.setPosition(x+2, y-4);            
            canvas.drawRawSprite(reticle);
        }
        
        reticle.setRotation(0);
        
        // left
        {
            float x = (cursorPos.x - markHeight - centerSize) - (xRange - (xRange * accuracy));
            float y = cursorPos.y - markWidth/2f;
            //canvas.fillRect(x, y, markHeight, markWidth, color);            
            
            reticle.setPosition(x-5, y+10);
            canvas.drawRawSprite(reticle);
        }
        
        // right
        {
            float x = (cursorPos.x + centerSize + centerSize) + (xRange - (xRange * accuracy));
            float y = cursorPos.y - markWidth/2f;
            //canvas.fillRect(x, y, markHeight, markWidth, color);
            
            reticle.setPosition(x-12, y+10);
            canvas.drawRawSprite(reticle);
        }
    }
}
