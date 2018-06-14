/*
 *    leola-live 
 *  see license.txt
 */
package seventh.client.gfx;

import java.util.Stack;

import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Camera
 * 
 * @author Tony
 *
 */
public abstract interface Camera {

    /**
     * Default Viewport width
     */
    public static final int DEFAULT_VIEWPORT_WIDTH = 800;
    
    /**
     * Default Viewport height
     */
    public static final int DEFAULT_VIEWPORT_HEIGHT = 600;
    
    /**
     * Default movement speed
     */
    public static final Vector2f DEFAULT_MOVE_SPEED = new Vector2f(200, 200);

    
    /**
     * Updates the Camera
     * @param timeStep
     */
    public void update(TimeStep timeStep);
    

    /**
     * @return the position relative to the {@link Camera#getPosition()} and {@link Camera#getZoom()}
     */
    public abstract Vector2f getCenterPosition();
    
    /**
     * @return the movement speed
     */
    public Vector2f getMovementSpeed();
    
    /**
     * Sets the movement speed
     * @param speed
     */
    public void setMovementSpeed(Vector2f speed);
    
    /**
     * Centers the camera around the supplied position
     */
    public abstract void centerAround(Vector2f position);
    
    /**
     * Centers the camera around the supplied position, moving the
     * camera there immediately.
     * @param position
     */
    public abstract void centerAroundNow(Vector2f position);
    
    /**
     * Move the camera to a new location.
     * 
     * @param destination
     */
    public abstract void moveTo( Vector2f destination );
    
    /**
     * Shake the camera
     * 
     * @param time
     * @param magnitude
     */
    public abstract void shake(long time, float magnitude);
    public abstract void addShake(long time, float magnitude);
    public abstract void shakeFrom(long time, Vector2f direction, float magnitude);
    /**
     * Determine if the camera is shaking
     * 
     * @return
     */
    public abstract boolean isShaking();
    
    /**
     * Zoom in.
     * 
     * @param zoom
     */
    public abstract void zoom(float zoom);
    
    /**
     * Zoom factor.
     * 
     * @return
     */
    public abstract float getZoom();
    
    /**
     * Path to move the camera to.
     * 
     * @param pathToFollow
     */
    public abstract void setPath( Stack<Vector2f> pathToFollow );
    
    /**
     * Add to the path.
     * 
     * @param v
     */
    public abstract void addToPath(Vector2f v);
    
    /**
     * Abort the path.
     */
    public abstract void abortPath();

    
    /**
     * Determine if the {@link Rectangle} is in the viewport.
     * 
     * @param rect
     * @return
     */
    public abstract boolean contains(Rectangle rect);
    
    /**
     * Determine if the {@link Vector2f} is in the viewport.
     * 
     * @param p
     * @return
     */
    public abstract boolean contains(Vector2f p);
    
    /**
     * Test intersection.
     * 
     * @param rect
     * @return
     */
    public abstract boolean intersects(Rectangle rect);
    
    /**
     * Get the Screen coordinates of the camera.
     * 
     * @return
     */
    public abstract Vector2f getScreenCoord();
    
    /**
     * Set the Screen coordinates of the camera.
     *  
     * @param pos
     */
    public abstract void setScreenCoord(Vector2f pos);
    
    /**
     * Get the world coordinates of the camera.
     * 
     * @return
     */
    public abstract Vector2f getPosition();
    public abstract Vector2f getRenderPosition(float alpha);
    
    /**
     * Set the world coordinates of the camera.
     *  
     * @param pos
     */
    public abstract void setPosition(Vector2f pos);
            
    /**
     * Get the Viewport of this camera
     * @return
     */
    public abstract Rectangle getViewPort();
    
    /**
     * Set the Viewport of this camera
     * 
     * @return
     */
    public abstract void setViewPort(Rectangle rect);
    
    /**
     * Get the Viewport relative to the world.
     * 
     * @return
     */
    public abstract Rectangle getWorldViewPort();

    /**
     * @return the world bounds
     */
    public abstract Vector2f getWorldBounds();
    
    /**
     * The worlds bounds 
     * @param bounds
     */
    public abstract void setWorldBounds(Vector2f bounds);
    
    /**
     * Converts the supplied screen coordinates to world coordinates.
     * 
     * @param screenPos
     * @param worldPos
     */
    public abstract void screenToWorld(Vector2f screenPos, Vector2f worldPos);
    
    /**
     * Converts the supplied world coordinates to screen coordinates.
     * 
     * @param worldPos
     * @param screenPos
     */
    public abstract void worldToScreen(Vector2f worldPos, Vector2f screenPos);
}
