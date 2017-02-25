/**
 * 
 */
package seventh.game.entities.vehicles;

import seventh.game.Controllable;
import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.game.entities.PlayerEntity;
import seventh.math.OBB;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;
import seventh.shared.WeaponConstants;

/**
 * Something a Player can ride
 * 
 * @author Tony
 *
 */
public abstract class Vehicle extends Entity implements Controllable {
    
    protected final Rectangle operateHitBox;
    protected final OBB vehicleBB;
    
    private final Vector2f center;
    protected int aabbWidth, aabbHeight;
    private PlayerEntity operator;
    
    private Timer killTimer;
    private Entity killer;
    
    /**
     * @param position
     * @param speed
     * @param game
     * @param type
     */
    public Vehicle(Vector2f position, int speed, Game game, Type type, long timeToKill) {
        super(game.getNextPersistantId(), position, speed, game, type);                
        
        this.operateHitBox = new Rectangle();
        this.vehicleBB = new OBB();
        this.center = new Vector2f();
        
        this.killTimer = new Timer(false, timeToKill < 0 ? Long.MAX_VALUE : timeToKill);
    }

    /* (non-Javadoc)
     * @see seventh.game.Entity#kill(seventh.game.Entity)
     */
    @Override
    public void kill(Entity killer) {
        if(this.currentState!=State.DESTROYED &&
           this.currentState!=State.DEAD) { 
            this.killTimer.start();
            
            this.currentState = State.DESTROYED;
            this.killer = killer;
            
            if(hasOperator()) {
                getOperator().kill(killer);
            }
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.game.Entity#update(seventh.shared.TimeStep)
     */
    @Override
    public boolean update(TimeStep timeStep) {    
        if(isDestroyed()) {
            return false;
        }
        
        boolean blocked = super.update(timeStep);
        updateOperateHitBox();
        
        killTimer.update(timeStep);
        if(killTimer.isOnFirstTime()) {
            super.kill(killer);
        }
        
        return blocked;
    }

    /**
     * @return the vehicleBB
     */
    public OBB getOBB() {
        return vehicleBB;
    }
    
    /**
     * Synchronize the {@link OBB} with the current orientation and position
     * of the vehicle
     * 
     * @param orientation
     * @param pos
     */
    protected void syncOOB(float orientation, Vector2f pos) {
        center.set(pos);
        center.x += aabbWidth/2f;
        center.y += aabbHeight/2f;

        vehicleBB.update(orientation, center);
    }
    
    /**
     * Updates the operation hit box to keep up
     * with any type of movement
     */
    protected void updateOperateHitBox() {
        operateHitBox.x = bounds.x - WeaponConstants.VEHICLE_HITBOX_THRESHOLD/2;
        operateHitBox.y = bounds.y - WeaponConstants.VEHICLE_HITBOX_THRESHOLD/2;
    }
    
    
    /**
     * @return true if this vehicle is moving
     */
    public boolean isMoving() {
        return currentState == State.RUNNING;
    }
    
    /**
     * Determines if the supplied {@link Entity} can operate this {@link Vehicle}
     * @param operator
     * @return true if the {@link Entity} is close enough to operate this {@link Vehicle}
     */
    public boolean canOperate(Entity operator) {
        if(!isAlive() || isDestroyed()) {
            return false;
        }
        
        return !hasOperator() && this.operateHitBox.intersects(operator.getBounds());
    }
    
    
    /**
     * @return the operator
     */
    public PlayerEntity getOperator() {
        return operator;
    }
    
    /**
     * @return true if there is an operator
     */
    public boolean hasOperator() {
        return this.operator != null && this.operator.isAlive();
    }
    
    /**
     * @param operator the operator to set
     */
    public void operate(PlayerEntity operator) {
        if(!hasOperator()) {
            beginOperating();
        }
        
        this.operator = operator;
    }

    /**
     * The {@link PlayerEntity} has stopped operating this {@link Vehicle}
     * @param operator
     */
    public void stopOperating(PlayerEntity operator) {
        this.operator = null;
        endOperating();
    }    
    
    protected void beginOperating() {        
    }
    
    protected void endOperating() {        
    }
    
    /* (non-Javadoc)
     * @see seventh.game.Entity#isTouching(seventh.game.Entity)
     */
    @Override
    public boolean isTouching(Entity other) {
        
        // first check the cheap AABB
        if(bounds.intersects(other.getBounds())) {
        
            if(other instanceof Vehicle) {
                Vehicle otherVehicle = (Vehicle)other;
                return this.vehicleBB.intersects(otherVehicle.vehicleBB);
            }
            else {
                return this.vehicleBB.intersects(other.getBounds());
            }
        }
        
        return false; 
    }
    
    /**
     * @return true if the current state is destroyed
     */
    public boolean isDestroyed() {
        return getCurrentState()==State.DESTROYED;
    }
    
}
