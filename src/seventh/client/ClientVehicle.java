/*
 * see license.txt 
 */
package seventh.client;

import seventh.game.entities.Entity.State;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public abstract class ClientVehicle extends ClientControllableEntity {

	
	private ClientPlayerEntity operator;
	
	
	/**
	 * @param game
	 * @param pos
	 */
	public ClientVehicle(ClientGame game, Vector2f pos) {
		super(game, pos);			
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
	
}
