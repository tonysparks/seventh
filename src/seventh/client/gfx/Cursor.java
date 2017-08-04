/*
 * see license.txt 
 */
package seventh.client.gfx;

import com.badlogic.gdx.Gdx;

import seventh.client.SeventhGame;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Updatable;

/**
 * Represents the mouse pointer during menu screens, but more importantly acts as the players cursor/reticle in game. 
 * 
 * @author Tony
 *
 */
public abstract class Cursor implements Updatable {

    private Vector2f cursorPos;
    private Rectangle bounds;
    private boolean isVisible;
    
    private float mouseSensitivity;
    private float accuracy;
    
    private int color;
    private int prevX, prevY;

    
    /**
     * @param bounds
     *             the cursor size
     */
    public Cursor(Rectangle bounds) {
        this.bounds = bounds;
        this.cursorPos = new Vector2f();
        this.isVisible = true;
        this.mouseSensitivity = 1.0f;
        this.accuracy = 1.0f;
    }

    /**
     * @param color the color to set
     */
    public void setColor(int color) {
        this.color = color;
    }
    
    /**
     * @param bounds the bounds to set
     */
    public void setBounds(Rectangle bounds) {
        this.bounds = bounds;
    }
    
    /**
     * Sets the dimensions of the cursor
     * 
     * @param width
     * @param height
     */
    public void setBounds(int width, int height) {
        this.bounds.setSize(width, height);
    }
    
    /**
     * @param mouseSensitivity the mouseSensitivity to set
     */
    public void setMouseSensitivity(float mouseSensitivity) {
        this.mouseSensitivity = mouseSensitivity;    
    }
    
    /**
     * optional method to flex the accuracy
     */
    public void touchAccuracy() {        
    }
    
    /**
     * @param accuracy the accuracy to set
     */
    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
        if(this.accuracy < 0) this.accuracy = 0f;
        if(this.accuracy > 1f) this.accuracy = 1f;
    }
    
    /**
     * Centers the mouse
     */
    public void centerMouse() {
        int width = SeventhGame.DEFAULT_MINIMIZED_SCREEN_WIDTH;
        int height = SeventhGame.DEFAULT_MINIMIZED_SCREEN_HEIGHT;
        
        
        int x = width/2;
        int y = height/2;
        
        moveNativeMouse(x, y);
        moveTo(x,y);
    }
    
    /**
     * Moves the native mouse
     * @param x
     * @param y
     */
    private void moveNativeMouse(int x, int y) {
        Gdx.input.setCursorPosition(x,y);
    }
    
    
    /**
     * Clamp the cursor position so that it doesn't
     * move outside of the screen
     */
    private void clamp() {
        if(this.cursorPos.x < 0)  {
            this.cursorPos.x = 0f;
        }
        
        if(this.cursorPos.y < 0)  {
            this.cursorPos.y = 0f;
        }
        
        int width = SeventhGame.DEFAULT_MINIMIZED_SCREEN_WIDTH;
        int height = SeventhGame.DEFAULT_MINIMIZED_SCREEN_HEIGHT;
        
        if(this.cursorPos.x > width) {
            this.cursorPos.x = width;
        }
        
        if(this.cursorPos.y > height) {
            this.cursorPos.y = height;
        }
    }
    
    /**
     * @return the mouseSensitivity
     */
    public float getMouseSensitivity() {
        return mouseSensitivity;
    }
    
    /**
     * @return the accuracy
     */
    public float getAccuracy() {
        return accuracy;
    }
    
    /**
     * @return the color
     */
    public int getColor() {
        return color;
    }
    
    /**
     * @return the bounds
     */
    public Rectangle getBounds() {
        bounds.centerAround(getCursorPos());
        return bounds;
    }
    
    /**
     * @return the isVisible
     */
    public boolean isVisible() {
        return isVisible;
    }
    
    /**
     * @param isVisible the isVisible to set
     */
    public void setVisible(boolean isVisible) {
        this.isVisible = isVisible;
    }

    /**
     * Moves the cursor to the specified location
     * 
     * @param x
     * @param y
     */
    public void moveTo(int x, int y) {
        if(isVisible()) {            
            float deltaX = this.mouseSensitivity * (this.prevX - x);
            float deltaY = this.mouseSensitivity * (this.prevY - y);
                        
            this.cursorPos.x -= deltaX;            
            this.cursorPos.y -= deltaY;
                
            this.prevX = x;
            this.prevY = y;            
            
            clamp();
        }
    }
    
    /**
     * Moves the cursor based on the delta movement
     * @param dx either 1, -1 or 0
     * @param dy either 1, -1 or 0
     */
    public void moveByDelta(float dx, float dy) {
        float deltaX = this.mouseSensitivity * (dx*20);
        float deltaY = this.mouseSensitivity * (dy*20);
        
        this.prevX = (int)cursorPos.x;
        this.prevY = (int)cursorPos.y;
        
                
        this.cursorPos.x += deltaX;
        this.cursorPos.y += deltaY;        
        
        clamp();
    }
    
    /**
     * @return the x position
     */
    public int getX() {
        return (int)this.cursorPos.x;
    }
    
    /**
     * @return the y position
     */
    public int getY() {
        return (int)this.cursorPos.y;
    }
    
    /**
     * @return the cursorPos
     */
    public Vector2f getCursorPos() {
        return cursorPos;
    }
    
    /**
     * Draws the cursor on the screen
     * 
     * @param canvas
     */
    public void render(Canvas canvas) {
        if(isVisible()) {
            doRender(canvas);
        }
    }
    
    protected abstract void doRender(Canvas canvas);
}
