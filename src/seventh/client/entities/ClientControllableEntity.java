/*
 * see license.txt 
 */
package seventh.client.entities;

import java.util.List;

import seventh.client.ClientGame;
import seventh.client.entities.vehicles.ClientVehicle;
import seventh.game.entities.Entity;
import seventh.game.entities.Entity.State;
import seventh.map.Map;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public abstract class ClientControllableEntity extends ClientEntity {
    
    protected State currentState;
    protected int lineOfSight;
    
    protected Vector2f predictedPos;
    protected Vector2f renderPos;
    
    protected ClientVehicle vehicle;
    
    protected long lastMoveTime;    
    private boolean isControlledByLocalPlayer;
    
    protected Rectangle hearingBounds;
    
    /**
     * @param game
     * @param pos
     */
    public ClientControllableEntity(ClientGame game, Vector2f pos) {
        super(game, pos);    
        
        this.predictedPos = new Vector2f();                
        this.renderPos = new Vector2f(pos);
        
        this.isControlledByLocalPlayer = false;
        

        this.hearingBounds = new Rectangle(200, 200);
        this.hearingBounds.centerAround(pos);
    }

    /**
     * @return true if we are operating a vehicle
     */
    public boolean isOperatingVehicle() {
        return this.vehicle != null;
    }
    
    
    /**
     * @return the vehicle
     */
    public ClientVehicle getVehicle() {
        return vehicle;
    }
    
    /**
     * @param isControlledByLocalPlayer the isControlledByLocalPlayer to set
     */
    public void setControlledByLocalPlayer(boolean isControlledByLocalPlayer) {
        this.isControlledByLocalPlayer = isControlledByLocalPlayer;
    }
    
    /**
     * @return the isControlledByLocalPlayer
     */
    public boolean isControlledByLocalPlayer() {
        return isControlledByLocalPlayer;
    }
    
    /**
     * @return the lineOfSight
     */
    public int getLineOfSight() {
        return lineOfSight;
    }
        
    
    /**
     * @return the currentState
     */
    public State getCurrentState() {
        return currentState;
    }
    
    /**
     * @return the client side predicted position
     */
    public Vector2f getPredictedPos() {
        return predictedPos;
    }
        
    /**
     * @return the renderPos
     */
    public Vector2f getRenderPos(float alpha) {
        if(isControlledByLocalPlayer()) {
            //Vector2f.Vector2fLerp(predictedPos, pos, alpha, renderPos);
            
//            renderPos.x = predictedPos.x * 0.6f + pos.x * 0.4f;
//            renderPos.y = predictedPos.y * 0.6f + pos.y * 0.4f;
            renderPos.x = renderPos.x * 0.8f + predictedPos.x * 0.2f; // was pos
            renderPos.y = renderPos.y * 0.8f + predictedPos.y * 0.2f;

        }
        else {            
            Vector2f.Vector2fLerp(previousPos, pos, alpha, renderPos);
        }
        
        return renderPos;
    }
        
    /**
     * @return the height mask for if the entity is crouching or standing
     */
    public int getHeightMask() {
        if( currentState == State.CROUCHING ) {
            return Entity.CROUCHED_HEIGHT_MASK;
        }
        return Entity.STANDING_HEIGHT_MASK;
    }
    
    /**
     * Determines if this entity would be able to hear the other {@link ClientEntity}
     * 
     * @param ent
     * @return true if in ear shot distance of the other {@link ClientEntity}
     */
    public boolean inEarShot(ClientEntity ent) {
        return hearingBounds.intersects(ent.getBounds());
    }
    
    /**
     * Does client side movement prediction
     * 
     */
    public void movementPrediction(Map map, TimeStep timeStep, Vector2f vel) {    
        
        if(isAlive() && !vel.isZero()) {            
            int movementSpeed = calculateMovementSpeed();
                                    
            float dt = (float)timeStep.asFraction();            
            float deltaX = (vel.x * movementSpeed * dt);
            float deltaY = (vel.y * movementSpeed * dt);
            
            float newX = predictedPos.x + deltaX;
            float newY = predictedPos.y + deltaY;
            
            bounds.x = (int)newX;
            if( map.rectCollides(bounds) || collidesAgainstEntity(bounds)) {
                bounds.x = (int)predictedPos.x;
                newX = predictedPos.x;
            }
            
            bounds.y = (int)newY;
            if( map.rectCollides(bounds) || collidesAgainstEntity(bounds)) {
                bounds.y = (int)predictedPos.y;
                newY = predictedPos.y;
            }
            
            predictedPos.set(newX, newY);
            
            clientSideCorrection(pos, predictedPos, predictedPos, 0.15f);
            lastMoveTime = timeStep.getGameClock();
        }        
        else {
            float alpha = 0.15f + 0.108f * (float)((timeStep.getGameClock() - lastMoveTime) / timeStep.getDeltaTime());
            if(alpha > 0.75f) {
                alpha = 0.75f;
            }
            clientSideCorrection(pos, predictedPos, predictedPos, alpha);
        }
    }
    
    protected boolean collidesAgainstEntity(Rectangle bounds) {
    	List<ClientVehicle> vehicles = game.getVehicles();
    	for(int i = 0; i < vehicles.size(); i++) {
    		ClientVehicle v = vehicles.get(i);
    		if(v.isRelativelyUpdated()) {
    			//if(v.touches()
    		}
    	}
    	
    	// TODO: Doors, vehicles
    	List<ClientDoor> doors = game.getDoors();
    	for(int i = 0; i < doors.size(); i++) {
    		ClientDoor door = doors.get(i);
    		if(door.isTouching(bounds)) {
    			return true;
    		}
    	}
    	
    	return false;
    }
    
    /**
     * @return calculates the movement speed based on
     * state + current weapon + stamina
     */
    protected abstract int calculateMovementSpeed();
    
    /**
     * Correct the position
     * 
     * @param serverPos
     * @param predictedPos
     * @param out the output vector
     */
    public void clientSideCorrection(Vector2f serverPos, Vector2f predictedPos, Vector2f out, float alpha) {
        //float alpha = 0.15f;
        float dist = Vector2f.Vector2fDistanceSq(serverPos, predictedPos);
        
        /* if the entity is more than two tile off, snap
         * into position
         */
        if(dist > 62 * 62) {            
            Vector2f.Vector2fCopy(serverPos, out);
        }        
        else //if (dist > 22 * 22) 
        {                    
            out.x = predictedPos.x + (alpha * (serverPos.x - predictedPos.x));
            out.y = predictedPos.y + (alpha * (serverPos.y - predictedPos.y));            
        }            
            
    }        
    
    /**
     * Calculates the aiming accuracy of this entity.  The accuracy is impacted
     * by the entities current state.
     * 
     * @return a number ranging from [0,1] with 1 being the most accurate
     */
    public float getAimingAccuracy() {
        float accuracy = 0f;
        switch(getCurrentState()) {
            case CROUCHING:
                accuracy = 1f;
                break;                                        
            case IDLE:
                accuracy = .9f;
                break;
            case OPERATING_VEHICLE:
                accuracy = 1f;
                break;
            case RUNNING:
                accuracy = .5f;
                break;
            case SPRINTING:
                accuracy = 0f;
                break;
            case WALKING:
                accuracy = .9f;
                break;                    
            default: accuracy = 0f;
        }
        return accuracy;
    }
}
