/*
 *    leola-live 
 *  see license.txt
 */
package seventh.client.gfx;

import static seventh.math.Vector2f.Vector2fAdd;
import static seventh.math.Vector2f.Vector2fCopy;
import static seventh.math.Vector2f.Vector2fGreaterOrEq;
import static seventh.math.Vector2f.Vector2fLerp;
import static seventh.math.Vector2f.Vector2fMult;
import static seventh.math.Vector2f.Vector2fNormalize;
import static seventh.math.Vector2f.Vector2fSubtract;

import java.util.Random;
import java.util.Stack;

import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class Camera2d implements Camera {
    
    private static final Vector2f SHAKE_SPEED = new Vector2f(4000, 4000);
    
    private Vector2f screenCoord;
    private Vector2f destination;
    
    private Vector2f position,prevPosition;
        
    private Rectangle viewport;
    private Rectangle originalViewport;
    private Rectangle worldViewport;
        
    private long timeToShake;
            
    private float shakeIntensity;
    private float zoom;
    
    private Vector2f vShakeVelocity;
    private Vector2f renderPosition;
    
    private Random rand;
    private Stack<Vector2f> pathToFollow;
    
    /*
     * Cache vectors to remove new overhead
     */
    
    private Vector2f vSpeed;        
    private Vector2f vVelocity;
    private Vector2f vDelta;
    private Vector2f vMovementSpeed;
    
    private Vector2f worldBounds;    
        
    /**
     * Constructs a new {@link Camera}
     * 
     * @param data
     * @param physObj
     */
    public Camera2d() {        
        
        this.timeToShake     = 0L;
        this.shakeIntensity = 1.0f;        
        this.zoom             = 1.0f;
                
        this.pathToFollow   = new Stack<Vector2f>();
        this.screenCoord      = new Vector2f(0,0);
        this.destination    = new Vector2f(0,0);
                
        this.vSpeed         = new Vector2f();
        this.vDelta            = new Vector2f();
        this.vVelocity        = new Vector2f();
        this.vMovementSpeed    = new Vector2f();
        
        this.position = new Vector2f();
        this.prevPosition = new Vector2f();
                
        this.worldViewport  = new Rectangle();
        this.worldBounds    = new Vector2f();
        
        this.originalViewport = new Rectangle();
        this.viewport           = new Rectangle();        
                
        this.vShakeVelocity = new Vector2f();
        this.renderPosition = new Vector2f();
        
        this.rand = new Random();        
        
        load();
    }    

    /**
     * Load the EntDict data.
     * 
     * @param data
     */
    protected void load() {        
        this.setViewPort(
                new Rectangle(0,0,DEFAULT_VIEWPORT_WIDTH, DEFAULT_VIEWPORT_HEIGHT));
            
        this.worldBounds.set(this.viewport.width, this.viewport.height);
    }
    
    public Vector2f getMovementSpeed() {
        return this.vMovementSpeed;
    }
    
    public void setMovementSpeed(Vector2f speed) {
        this.vMovementSpeed.set(speed);
    }
    
    /* (non-Javadoc)
     * @see org.myriad.render.Camera#abortPath()
     */
    @Override
    public void abortPath() {
        this.pathToFollow.clear();
    }


    /* (non-Javadoc)
     * @see org.myriad.render.Camera#addToPath(org.lwjgl.util.vector.Vector2f)
     */
    @Override
    public void addToPath(Vector2f v) {
        this.pathToFollow.add(v);
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#contains(org.lwjgl.util.Rectangle)
     */
    @Override
    public boolean contains(Rectangle rect) {        
        return getWorldViewPort().contains(rect);
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#contains(org.lwjgl.util.vector.Vector2f)
     */
    @Override
    public boolean contains(Vector2f p) {        
        return getWorldViewPort().contains(p);
    }
    
    /* (non-Javadoc)
     * @see org.myriad.render.Camera#intersects(org.lwjgl.util.Rectangle)
     */
    @Override
    public boolean intersects(Rectangle rect) {
        return getWorldViewPort().intersects(rect);
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#getWorldViewPort()
     */
    @Override
    public Rectangle getWorldViewPort() {
        this.worldViewport.setBounds(this.viewport);
        
        Vector2f position = this.getPosition();
        this.worldViewport.setX((int)position.x);
        this.worldViewport.setY((int)position.y);    
        
        return this.worldViewport;
    }
    
    /* (non-Javadoc)
     * @see org.myriad.render.Camera#getPosition()
     */
    @Override
    public Vector2f getPosition() {
        return this.position;
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Camera#getRenderPosition(float)
     */
    @Override
    public Vector2f getRenderPosition(float alpha) {
        Vector2fLerp(prevPosition, position, alpha, renderPosition);
        // Fixes the pixel jitters of stationary objects
        Vector2f.Vector2fRound(renderPosition, renderPosition);
        return renderPosition;
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#getScreenCoord()
     */
    @Override
    public Vector2f getScreenCoord() {
        return this.screenCoord;
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#getViewPort()
     */
    @Override
    public Rectangle getViewPort() {        
        return this.viewport;
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#getZoom()
     */
    @Override
    public float getZoom() {
        return this.zoom;
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#isShaking()
     */
    @Override
    public boolean isShaking() {
        return this.timeToShake > 0;
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#moveTo(org.lwjgl.util.vector.Vector2f)
     */
    @Override
    public void moveTo(Vector2f dest) {
        this.destination.x = Math.abs( dest.x - this.screenCoord.x );
        this.destination.y = Math.abs( dest.y - this.screenCoord.y );        
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#setPath(java.util.List)
     */
    @Override
    public void setPath(Stack<Vector2f> pathToFollow) {
        this.pathToFollow = pathToFollow;
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#setPosition(org.lwjgl.util.vector.Vector2f)
     */
    @Override
    public void setPosition(Vector2f pos) {        
        this.position.set(pos);
        Vector2f.Vector2fSnap(position, position);
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#setScreenCoord(org.lwjgl.util.vector.Vector2f)
     */
    @Override
    public void setScreenCoord(Vector2f pos) {
        this.screenCoord = pos;
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#setViewPort(org.lwjgl.util.Rectangle)
     */
    @Override
    public void setViewPort(Rectangle rect) {
        this.viewport.set(rect);
        this.originalViewport.set(this.viewport);
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#shake(org.myriad.core.TimeUnit, float)
     */
    @Override
    public void shake(long time, float magnitude) {
        this.randomAngle = rand.nextInt(360);
        this.shakeIntensity = magnitude;
        this.timeToShake = time; 
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Camera#addShake(long, float)
     */
    @Override
    public void addShake(long time, float magnitude) {
        this.shakeIntensity = (this.shakeIntensity + magnitude) / 2.0f;
        this.timeToShake = (this.timeToShake + time) / 2;
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#zoom(float)
     */
    @Override
    public void zoom(float zoom) {
        // do not allow a negative zoom
        if ( zoom <= 0.0f ) {
            zoom = 1.0f;
        }
        this.zoom = zoom;

        Rectangle view = this.originalViewport;
        float width = view.getWidth();
        float height = view.getHeight();

        if ( zoom < 1 ) {
            width = width / zoom;
            height = height / zoom;
        } else {
            width = width * zoom;
            height = height * zoom;
        }
        
//        this.worldProjection.x = this.worldBounds.x * ;
//        this.worldProjection.y = height;

        this.viewport.set(view.getX(), view.getY(), (int)width, (int)height);        
    }

    /* (non-Javadoc)
     * @see org.myriad.game.Entity#update(org.myriad.core.TimeStep)
     */    
    public void update(TimeStep timeStep) {        
                
        Vector2f vPosition = this.position;        
        Vector2fCopy(vPosition, prevPosition);
        Vector2f vNextPosition=null;

        float dt = (float)timeStep.asFraction();
                
        /*
         * Determine if the camera is to follow
         * a path
         */
        if ( ! this.pathToFollow.isEmpty() ) {
            vNextPosition = this.pathToFollow.peek();
            
            /* If we reached our destination remove it from the path */
            if ( Vector2fGreaterOrEq(vNextPosition, vPosition) ) {
                this.pathToFollow.pop();
            }
        } 
        else {
            
            /* Else move to our destination (if there is one) */
            vNextPosition = this.destination;
        }
        
        boolean useEase = true;
        
        if(useEase) {            
            this.vDelta.zeroOut();
            Vector2fSubtract(vNextPosition, vPosition, this.vDelta);                                    
            
            if(Vector2f.Vector2fLengthSq(vDelta) > 0.99f) 
            { 
                //Vector2fLerp(vPosition, vNextPosition, 0.242f, vPosition);
                //Vector2f.Vector2fInterpolate(vPosition, vNextPosition, 0.242f, vPosition);
                final float speed = 0.242f;
                final float ispeed = 1.0f - speed;
                Vector2fMult(vPosition, ispeed, vPosition);
                Vector2fMult(vNextPosition, speed, vVelocity);
                Vector2fAdd(vPosition, vVelocity, vPosition);
                
                Vector2f.Vector2fRound(vPosition, vPosition);                
            }            
        }
        else {                            
            this.vDelta.zeroOut();
            Vector2fSubtract(vNextPosition, vPosition, this.vDelta);
            if(Vector2f.Vector2fLengthSq(vDelta) > 0.5f) {    
                this.vVelocity.zeroOut();
                Vector2fCopy(this.vDelta, this.vVelocity);
                Vector2fNormalize(this.vVelocity, this.vVelocity);
                                        
                float friction = 1.0f;
                if(isShaking()) {
                    Vector2fCopy(SHAKE_SPEED, this.vSpeed );
                }
                else {
                    Vector2fCopy(this.vMovementSpeed, this.vSpeed );    
                }
                Vector2fMult(this.vSpeed, friction * dt, this.vSpeed);
                Vector2fMult(this.vVelocity, this.vSpeed, this.vVelocity);
                
                /* If the velocity for this frame is greater than our destination, 
                 * clamp it.
                 */
                if ( Vector2fGreaterOrEq(this.vVelocity, this.vDelta) ) {
                    Vector2fCopy(this.vDelta, this.vVelocity);
                }
                
                // Store the updated position 
                Vector2fAdd(vPosition, this.vVelocity, this.vVelocity);
                            
                setPosition(this.vVelocity);
            }
        }
        // shake camera if needed
        checkShake(timeStep);
    }

    /**
     * Check to see if the Camera needs to be shaking.
     * 
     * @param timeStep
     */
    float randomAngle;
    private void checkShake(TimeStep timeStep) {
                
        /*
         * If we are to shake do so
         */
        if ( this.isShaking() ) {

            /* Decrement the amount of time to shake */
            this.timeToShake -= timeStep.getDeltaTime();    
            
//            Vector2fSet(this.vShakeVelocity, rand.nextInt(3), rand.nextInt(3) );
//            if ( this.vShakeVelocity.x >= 2 ) {
//                this.vShakeVelocity.x = -1;
//            }
//            
//            if ( this.vShakeVelocity.y >= 2 ) {
//                this.vShakeVelocity.y = -1;
//            }
            
//            this.vShakeVelocity.set(0,1);
//            Vector2f.Vector2fRotate(vShakeVelocity, Math.toRadians(rand.nextInt(360)), vShakeVelocity);
//            float dt = (float)timeStep.asFraction();
//            Vector2fMult(this.vShakeVelocity, this.shakeIntensity * dt, this.vShakeVelocity);
            
            this.randomAngle += (150 + rand.nextInt(60));
            this.vShakeVelocity.set((float)Math.sin(randomAngle) * this.shakeIntensity, (float)Math.cos(randomAngle) * this.shakeIntensity);
            shakeIntensity *= 0.9f;
            
            
            
            Vector2f position = getPosition();            
            Vector2fAdd(position, this.vShakeVelocity, position);
            setPosition(position);            
        }
    }

    
    /* (non-Javadoc)
     * @see org.myriad.render.Camera#getRenderPosition()
     */
    @Override
    public Vector2f getCenterPosition() {
        Vector2f position = this.getPosition();
        float ratio = (float)this.viewport.getWidth() / (float)this.viewport.getHeight();
        this.renderPosition.x = position.x+(ratio*this.zoom*25.0f);
        this.renderPosition.y = position.y+(ratio*this.zoom*25.0f);
        // new_view_center = zoom_center + zoom_ratio * (old_view_center - zoom_center)
        return this.renderPosition;
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#checkSceneBounds(int, int)
     */
    @Override
    public void centerAround(Vector2f pos) {
        Rectangle viewport = getViewPort();
        
        // screen dimensions
        float halfHeight = viewport.getHeight() / 2.0f;
        float halfWidth = viewport.getWidth() / 2.0f;

        // handle being on top edge of map
        if ( pos.y + halfHeight > this.worldBounds.y )
            halfHeight = pos.y - this.worldBounds.y + halfHeight * 2.0f;

        // handle bottom edge
        if ( pos.y - halfHeight < 0 )
            halfHeight = pos.y;

        // handle left edge
        if ( pos.x - halfWidth < 0 )
            halfWidth = pos.x;

        // handle right edge
        if ( pos.x + halfWidth > this.worldBounds.x )
            halfWidth = pos.x + this.worldBounds.x - halfWidth * 2.0f;

        // adjust the camera
        this.screenCoord.set(halfWidth, halfHeight);
        
        // move the camera
        moveTo(pos);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.Camera#centerAroundNow(seventh.math.Vector2f)
     */
    @Override
    public void centerAroundNow(Vector2f pos) {
        Rectangle viewport = getViewPort();
        
        // screen dimensions
        float halfHeight = viewport.getHeight() / 2.0f;
        float halfWidth = viewport.getWidth() / 2.0f;

        // handle being on top edge of map
        if ( pos.y + halfHeight > this.worldBounds.y )
            halfHeight = pos.y - this.worldBounds.y + halfHeight * 2.0f;

        // handle bottom edge
        if ( pos.y - halfHeight < 0 )
            halfHeight = pos.y;

        // handle left edge
        if ( pos.x - halfWidth < 0 )
            halfWidth = pos.x;

        // handle right edge
        if ( pos.x + halfWidth > this.worldBounds.x )
            halfWidth += (pos.x + halfWidth) - this.worldBounds.x;

        this.position.x = pos.x - halfWidth;
        this.position.y = pos.y - halfHeight;
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#screenToWorld(org.myriad.shared.math.Vector2f, org.myriad.shared.math.Vector2f)
     */
    @Override
    public void screenToWorld(Vector2f screenPos, Vector2f worldPos) {
//        Vector2f pos = this.getPosition();
//        worldPos.x = (screenPos.x - this.viewport.width / 2.0f) * this.zoom + pos.x;
//        worldPos.y = (screenPos.y - this.viewport.height / 2.0f) * this.zoom + pos.y;
        
        Vector2f cameraPosition = this.getPosition();
        Vector2fAdd(screenPos, cameraPosition, worldPos);
                
        worldPos.x -= this.viewport.x;
        worldPos.y -= this.viewport.y;        
                        
        worldPos.x = (worldPos.x * this.worldBounds.x) / this.originalViewport.width;
        worldPos.y = (worldPos.y * this.worldBounds.y) / this.originalViewport.height;
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#worldToScreen(org.myriad.shared.math.Vector2f, org.myriad.shared.math.Vector2f)
     */
    @Override
    public void worldToScreen(Vector2f worldPos, Vector2f screenPos) {
//        Vector2f pos = this.getPosition();
//        screenPos.x = ((worldPos.x - pos.x) / this.zoom) + this.viewport.width / 2.0f;
//        screenPos.y = ((worldPos.y - pos.y) / this.zoom) + this.viewport.height / 2.0f;
        
        Vector2f cameraPosition = this.getPosition();
        Vector2fSubtract(worldPos, cameraPosition, screenPos);
                
        screenPos.x += this.viewport.x;
        screenPos.y += this.viewport.y;        

        //screenPos.x = screenPos.x - this.worldBounds.x/2;
        //screenPos.y = screenPos.y - this.worldBounds.y/2;
        
        screenPos.x = (screenPos.x * (float)this.originalViewport.width) / (float)this.worldBounds.x;
        screenPos.y = (screenPos.y * (float)this.originalViewport.height) / (float)this.worldBounds.y;
        
        
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#getWorldBounds()
     */
    @Override
    public Vector2f getWorldBounds() {
        return this.worldBounds;
    }

    /* (non-Javadoc)
     * @see org.myriad.render.Camera#setWorldBounds(org.myriad.shared.math.Vector2f)
     */
    @Override
    public void setWorldBounds(Vector2f bounds) {
        this.worldBounds.set(bounds);
    }

}
