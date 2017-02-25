/**
 * 
 */
package seventh.game.weapons;

import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.game.entities.PlayerEntity;
import seventh.math.Line;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.SoundType;
import seventh.shared.TimeStep;

/**
 * Attacking by swinging a gun
 * 
 * @author Tony
 *
 */
public class GunSwing {

    private long swingTime;
    private long currentSwingTime;
    private long waitTime;
    
    private Vector2f swing;
    private int swingAngle;
    private float swingLength;
    
    private boolean hitSomeone, endSwing;
    
    private Game game;
    private Entity owner;
    
    private int damage;
    
    private Rectangle hitBox;
    
    /**
     * @param game
     * @param owner
     */
    public GunSwing(Game game, Entity owner) {
        this.game = game;
        this.owner = owner;
        
        this.swingTime = 500;
        this.currentSwingTime = 0;
        this.swingLength = 40.0f;
        
        this.swing = new Vector2f();
        this.hitSomeone = false;
        this.endSwing = true;
        
        this.damage = 100;
        
        this.hitBox = new Rectangle();
        this.hitBox.setSize(64, 64);
    }
    
    public void setOwner(Entity owner) {
        this.owner = owner;
    }
    
    /**
     * Determines if the swing is hitting anyone
     */
    private void doesSwingTouchPlayers() {
        
        swing.set(1,0);
        Vector2f.Vector2fRotate(swing, Math.toRadians(swingAngle), swing);

        Vector2f origin = owner.getCenterPos();
        Vector2f.Vector2fMA(origin, swing, swingLength, swing);
                
        PlayerEntity[] playerEntities = game.getPlayerEntities();
        for(int i = 0; i < playerEntities.length; i++) {
            Entity other = playerEntities[i];
            if(other == null) {
                continue;
            }
            
            if(other == owner) {
                continue;
            }
            
            if(!other.canTakeDamage()) {
                continue;
            }
            
            /* first do an inexpensive check */
            if(!hitBox.intersects(other.getBounds())) {
                continue;
            }
            
            
            if(Line.lineIntersectsRectangle(origin, swing, other.getBounds())) {
                other.damage(owner, damage);        
                game.emitSound(owner.getId(), SoundType.MELEE_HIT, origin);
                hitSomeone = true;
            }
        }
                        
    }

    /**
     * Updates the state
     * 
     * @param timeStep
     */
    public void update(TimeStep timeStep) {
        if(isSwinging()) {
            this.waitTime = 300;
            this.currentSwingTime -= timeStep.getDeltaTime();
            
            if(!hitSomeone) {
                doesSwingTouchPlayers();
            }
            
            int numberOfTicks = (int)(swingTime/timeStep.getDeltaTime());
            if(numberOfTicks == 0) {
                numberOfTicks = 1;
            }
            
            swingAngle = (swingAngle + ( 45 / numberOfTicks )) % 360;            
        }
        else {
            /* decay the wait time until we can swing again */            
            this.waitTime -= timeStep.getDeltaTime();
        }
    }
    
    /**
     * @return if we can swing
     */
    public boolean canSwing() {
        return currentSwingTime <= 0 && this.waitTime <= 0 && endSwing;
    }
    
    /**
     * @return true if we are currently swinging
     */
    public boolean isSwinging() {
        return this.currentSwingTime > 0;
    }
    
    /**
     * Starts a swing
     * @return true if started, false otherwise
     */
    public boolean beginSwing() {
        if(canSwing()) {
            float orientation = owner.getOrientation();
            
            swingAngle = (int)Math.toDegrees(orientation);// % 360;
            
            Vector2f origin = owner.getCenterPos();
            hitBox.centerAround(origin);
            
            currentSwingTime = swingTime;
            
            game.emitSound(owner.getId(), SoundType.MELEE_SWING, owner.getCenterPos());
            hitSomeone = false;
            endSwing = false;
            
            return true;
        }
        
        return false;
    }        
    
    /**
     * Done swinging
     */
    public void endSwing() {
        endSwing = true;
    }
}
