/*
 * see license.txt 
 */
package seventh.client.entities;

import java.util.Random;

import seventh.client.ClientGame;
import seventh.client.gfx.Renderable;
import seventh.client.sfx.Sound;
import seventh.game.entities.Entity.Type;
import seventh.game.net.NetEntity;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Base entity class for the client representation.  This will receive updates from the
 * server.  There are various variables that do the book keeping of the state of the entity
 * and when it was last updated from the server.
 * 
 * @author Tony
 *
 */
public abstract class ClientEntity implements Renderable {
    protected int id;
    
    protected ClientGame game;
    protected Vector2f pos, facing, centerPos, movementDir;
    protected Vector2f renderPos, previousPos;
    protected Rectangle bounds;    
    protected float orientation;
        
    protected long lastUpdate;
    protected long previousNetUpdate;
    protected long gameClock;
    protected int  zOrder;
    protected Type type;
    
    private boolean updateReceived;
    
    protected NetEntity prevState, nextState;
    protected long prevTime, nextTime;
    
    private boolean isAlive;
    
    private boolean isDestroyed;
    
    private Sound[] attachedSounds;
    
    /**
     * Invoked on each update
     * @author Tony
     *
     */
    public static interface OnUpdate {
        void onUpdate(TimeStep timeStep, ClientEntity me);
    }
    
    private OnUpdate onUpdate;
    
    /**
     * Invoked when removed from the world.
     * @author Tony
     *
     */
    public static interface OnRemove {
        void onRemove(ClientEntity me, ClientGame game);
    }
    
    private OnRemove onRemove;
    
    
    /**
     * 
     */
    public ClientEntity(ClientGame game, Vector2f pos) {
        this.game = game;
        this.pos = pos;

        this.previousPos = new Vector2f(pos);
        this.renderPos = new Vector2f();
        
        this.facing = new Vector2f();
        this.centerPos = new Vector2f();
        this.movementDir = new Vector2f();
        
        this.bounds = new Rectangle();        
        this.isAlive = true;
        
        this.zOrder = 100;
    }
    
    /**
     * A destroyed object mean it can be reused.  This is purely
     * for performance reasons.
     * 
     * @return the isDestroyed
     */
    public boolean isDestroyed() {
        return isDestroyed;
    }
    
    /**
     * Destroys this entity
     */
    public void destroy() {
        this.isDestroyed = true;
    }
    
    /**
     * Reset's this object so that it can be reused 
     * again by the engine.
     */
    public void reset() {
        this.isDestroyed = false;
        this.isAlive = true;
        
        this.previousPos.zeroOut();
        this.renderPos.zeroOut();
        
        this.pos.zeroOut();
        this.facing.zeroOut();
        this.centerPos.zeroOut();
        this.movementDir.zeroOut();
        this.bounds.setLocation(this.pos);
        
        if(this.attachedSounds != null) {
            for(int i = 0; i < this.attachedSounds.length; i++) {
                this.attachedSounds[i] = null;
            }
        }
    }
    
    /**
     * Updates the state of this entity
     * 
     * @param state
     */
    public void updateState(NetEntity state, long time) {
        this.prevState = nextState;
        this.nextState = state;
        
        this.prevTime = this.nextTime;
        this.nextTime = time;
        
        this.id = state.id;
        this.type = state.type;
                
        this.bounds.setLocation(pos);
        this.orientation = (float)Math.toRadians(state.orientation);
        this.facing.set(1,0);
        Vector2f.Vector2fRotate(facing, orientation, facing);
        
        this.updateReceived = true;
    }
    
    /* (non-Javadoc)
     * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        Vector2f.Vector2fCopy(this.pos, previousPos);
        this.gameClock = timeStep.getGameClock();
        
        if(attachedSounds != null) {
            updateSounds(attachedSounds);
        }
        
        if(this.updateReceived) {
            previousNetUpdate = lastUpdate;
            lastUpdate = timeStep.getGameClock();

            updateReceived = false;
        }        
        
        interpolate(timeStep);
        
        if(onUpdate != null) {
            onUpdate.onUpdate(timeStep, this);
        }
    }

    /**
     * Update the attached sounds position
     * 
     * @param sounds
     */
    protected void updateSounds(Sound[] sounds) {
        Vector2f pos = getCenterPos();        
        for(int i = 0; i < sounds.length; i++) {
            Sound snd = sounds[i];
            if(snd != null) {
                if(!snd.isPlaying()) {
                    sounds[i] = null;
                }
                else {
                    snd.setPosition(pos.x, pos.y);                    
                }
            }
        }
    }
    
    /**
     * Attach a sound to this entity
     * 
     * @param sound
     */
    public void attachSound(Sound sound) {
        if(this.attachedSounds==null) {
            this.attachedSounds = new Sound[8];
        }
        
        for(int i = 0; i < this.attachedSounds.length; i++) {
            Sound snd = this.attachedSounds[i];
            if(snd == null) {
                this.attachedSounds[i] = sound;
            }
        }
    }
    
    /**
     * @return the onRemove
     */
    public OnRemove getOnRemove() {
        return onRemove;
    }
    
    /**
     * @param onRemove the onRemove to set
     */
    public void setOnRemove(OnRemove onRemove) {
        this.onRemove = onRemove;
    }
    
    /**
     * @return the onUpdate
     */
    public OnUpdate getOnUpdate() {
        return onUpdate;
    }
    
    /**
     * @param onUpdate the onUpdate to set
     */
    public void setOnUpdate(OnUpdate onUpdate) {
        this.onUpdate = onUpdate;
    }
    
    /**
     * @return true if the object should be rendered first (this
     * is applicable to bombs, dropped items, etc. stuff that entities
     * will render over
     */
    public boolean isBackgroundObject() {
        return false;
    }
    
    /**
     * @return the zOrder
     */
    public int getZOrder() {
        return zOrder;
    }
    
    /**
     * @return true if this entity is a player
     */
    public boolean isPlayer() {
        return this.type.isPlayer();
    }
    
    /**
     * @return the isAlive
     */
    public boolean isAlive() {
        return isAlive;
    }
    
    /**
     * @param isAlive the isAlive to set
     */
    public void setAlive(boolean isAlive) {
        this.isAlive = isAlive;
    }
    
    /**
     * Kill this {@link ClientEntity}
     * 
     * @param meansOfDeath
     * @param locationOfDeath
     */
    public void kill(Type meansOfDeath, Vector2f locationOfDeath) {
        setAlive(false);
    }
    
    
    /**
     * Interpolates between the previous and current state sent
     * from the server.
     * 
     * @param timeStep
     */
    protected void interpolate(TimeStep timeStep) {
        if(this.prevState != null && this.nextState != null) {
            // TODO :: figure out ping time
            float alpha = 0.75f;             
            float dist = (pos.x - nextState.posX) * (pos.x - nextState.posX) + 
                         (pos.y - nextState.posY) * (pos.y - nextState.posY);
            
            /* if the entity is more than two tile off, snap
             * into position
             */
            if(dist > 64 * 64) {
                this.pos.x = nextState.posX;
                this.pos.y = nextState.posY;            
                this.previousPos.set(this.pos);
            }
            else {
            
                this.pos.x = pos.x + (alpha * (nextState.posX - pos.x));
                this.pos.y = pos.y + (alpha * (nextState.posY - pos.y));
            }
            
            this.bounds.setLocation(pos);
            
            /* calculate movement direction */
            this.movementDir.x = nextState.posX - prevState.posX;
            this.movementDir.y = nextState.posY - prevState.posY;
            
            
            //if( Math.abs(prevState.orientation - nextState.orientation) > (Math.PI/2))
            if( Math.abs(prevState.orientation - nextState.orientation) > (30))
            {
                this.orientation = nextState.orientation;
            }
            else {
                this.orientation = prevState.orientation + (alpha * (nextState.orientation - prevState.orientation));
            }
            
            this.orientation = (float) Math.toRadians(this.orientation);
        }
    }
    
    /**
     * @return the movementDir
     */
    public Vector2f getMovementDir() {
        return movementDir;
    }
    
    /**
     * @return the previous position
     */
    public Vector2f getPrevPos() {
        return this.pos;
    }
    
    /**
     * @param alpha
     * @return the position (accounted for time frame alpha) in which this entity should
     * be drawn
     */
    public Vector2f getRenderPos(float alpha) {
        Vector2f.Vector2fLerp(previousPos, pos, alpha, renderPos);
        return renderPos;
    }

    /**
     * @return the random number generator
     */
    public Random getRandom() {
        return game.getRandom();
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @return the pos
     */
    public Vector2f getPos() {
        return pos;
    }
    
    /**
     * @return the center position of this entity
     */
    public Vector2f getCenterPos() {
        centerPos.set(pos.x + bounds.width/2, pos.y + bounds.height/2);
        return centerPos;
    }

    /**
     * @return the facing
     */
    public Vector2f getFacing() {        
        return facing;
    }

    /**
     * @return the bounds
     */
    public Rectangle getBounds() {
        return bounds;
    }
    
    /**
     * @return the orientation
     */
    public float getOrientation() {
        return orientation;
    }

    /**
     * @return the lastUpdate
     */
    public long getLastUpdate() {
        return lastUpdate;
    }
    
    /**
     * @return the type
     */
    public Type getType() {
        return type;
    }
    
    /**
     * Determines if this entity has been updated by the server relatively
     * recently
     * 
     * @return true if it's been updated recently
     */
    public boolean isRelativelyUpdated() {
        return isRelativelyUpdated(800);
    }
    
    
    /**
     * Determines if this entity has been updated by the server relatively
     * recently
     * 
     * @param time the amount of time that is allowed to pass before we consider this
     * too old
     * @return true if it's been updated recently
     */
    public boolean isRelativelyUpdated(int timeMSec) {
        return (gameClock - this.lastUpdate) < timeMSec;
    }
    
    /**
     * @return true if this entity should be killed
     * off because an update has not been sent out for it
     * in a bit of time
     */
    public boolean killIfOutdated(long gameClock) {
        return (gameClock - lastUpdate) > 2000;
    }
    
    /**
     * Determines if this entity touches another entity
     * @param other
     * @return true if both entities touch
     */
    public boolean touches(ClientEntity other) {
        return this.bounds.intersects(other.getBounds());
    }
    
    public boolean isTouching(Rectangle bounds) {
        return this.bounds.intersects(bounds);
    }
}
