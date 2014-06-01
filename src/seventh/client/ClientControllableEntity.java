/*
 * see license.txt 
 */
package seventh.client;

import seventh.game.Entity;
import seventh.game.Entity.State;
import seventh.map.Map;
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
	
	
	/**
	 * @param game
	 * @param pos
	 */
	public ClientControllableEntity(ClientGame game, Vector2f pos) {
		super(game, pos);	
		
		this.predictedPos = new Vector2f();
		this.renderPos = new Vector2f();
		this.isControlledByLocalPlayer = false;
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
	public Vector2f getRenderPos() {
		if(isControlledByLocalPlayer()) {
			renderPos.x = predictedPos.x * 0.6f + pos.x * 0.4f;
			renderPos.y = predictedPos.y * 0.6f + pos.y * 0.4f;
		}
		else {
			Vector2f.Vector2fCopy(pos, renderPos);
		}
		
		//return predictedPos;
		return renderPos;
	}
	
	
	/**
	 * @return the center render position
	 */
	public Vector2f getRenderCenterPos() {
		if(isControlledByLocalPlayer()) {
			renderPos.x = (predictedPos.x+(bounds.width/2)) * 0.6f + (pos.x+(bounds.width/2) * 0.4f);
			renderPos.y = (predictedPos.y+(bounds.height/2)) * 0.6f + (pos.y+(bounds.height/2) * 0.4f);
		}
		else {
			Vector2f.Vector2fCopy(pos, renderPos);
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
	 * Does client side movement prediction
	 * 
	 */
	public void movementPrediction(Map map, TimeStep timeStep, Vector2f vel) {				
		if(isAlive() && !vel.isZero()) {			
			int movementSpeed = calculateMovementSpeed();
									
			double dt = timeStep.asFraction();
			int newX = (int)Math.round(predictedPos.x + vel.x * movementSpeed * dt);
			int newY = (int)Math.round(predictedPos.y + vel.y * movementSpeed * dt);
						
			bounds.x = newX;
			if( map.rectCollides(bounds) ) {
				bounds.x = (int)predictedPos.x;
						
			}
			
			bounds.y = newY;
			if( map.rectCollides(bounds)) {
				bounds.y = (int)predictedPos.y;								
			}
			
			predictedPos.x = bounds.x;
			predictedPos.y = bounds.y;
			
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
}
