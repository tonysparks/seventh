/*
 * see license.txt 
 */
package seventh.client.entities.vehicles;

import seventh.client.ClientGame;
import seventh.client.entities.ClientControllableEntity;
import seventh.client.entities.ClientEntity;
import seventh.client.entities.ClientPlayerEntity;
import seventh.game.entities.Entity.State;
import seventh.math.Rectangle;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public abstract class ClientVehicle extends ClientControllableEntity {

    
    private ClientPlayerEntity operator;
    protected Rectangle operateHitBox;
    
    /**
     * @param game
     * @param pos
     */
    public ClientVehicle(ClientGame game, Vector2f pos) {
        super(game, pos);           
        this.operateHitBox = new Rectangle();
    }
    
    public boolean hasOperator() {
        return this.operator != null && this.operator.isAlive();
    }
    
    /**
     * @return the operator
     */
    public ClientPlayerEntity getOperator() {
        return operator;
    }
    
    /**
     * @param operator the operator to set
     */
    public void setOperator(ClientPlayerEntity operator) {
        this.operator = operator;
        if(this.operator != null) {
            setControlledByLocalPlayer(this.operator.isControlledByLocalPlayer());
        }
    }
    
    
    /* (non-Javadoc)
     * @see seventh.client.ClientEntity#killIfOutdated(long)
     */
    @Override
    public boolean killIfOutdated(long gameClock) {
        return false;
    }
    
    /**
     * @return the lineOfSight
     */
    public int getLineOfSight() {
        return lineOfSight;
    }
        
    
//    /**
//     * @return the currentState
//     */
//    public State getCurrentState() {
//        return currentState;
//    }
    
    /**
     * @return the client side predicted position
     */
    public Vector2f getPredictedPos() {
        return predictedPos;
    }
    
    /**
     * @return the operateHitBox
     */
    public Rectangle getOperateHitBox() {
        operateHitBox.centerAround(getCenterPos());
        return operateHitBox;
    }
    
    /**
     * @return true if this vehicle has been blowup/disabled/inoperable.
     */
    public boolean isDisabled() {
        return this.currentState==State.DESTROYED;
    }
    
    /**
     * Determines if the supplied {@link ClientEntity} is within
     * the vicinity to operate this vehicle.
     * 
     * @param entity
     * @return true if the supplied entity could operate this vehicle
     */
    public boolean canOperate(ClientEntity entity) {
        if(!this.hasOperator() && isAlive() && !isDisabled()) {
            return getOperateHitBox().intersects(entity.getBounds());
        }
        return false;
    }
    
}
