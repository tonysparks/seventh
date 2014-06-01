/**
 * 
 */
package seventh.game.vehicles;

import seventh.game.Controllable;
import seventh.game.Entity;
import seventh.game.Game;
import seventh.game.PlayerEntity;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * Something a Player can ride
 * 
 * @author Tony
 *
 */
public abstract class Vehicle extends Entity implements Controllable {
	
	protected final Rectangle operateHitBox;
	
	private PlayerEntity operator;
	
	/**
	 * @param position
	 * @param speed
	 * @param game
	 * @param type
	 */
	public Vehicle(Vector2f position, int speed, Game game, Type type) {
		super(game.getNextPersistantId(), position, speed, game, type);				
		this.operateHitBox = new Rectangle();
	}

	/* (non-Javadoc)
	 * @see seventh.game.Entity#update(seventh.shared.TimeStep)
	 */
	@Override
	public boolean update(TimeStep timeStep) {	
		boolean blocked = super.update(timeStep);
		updateOperateHitBox();
		
		return blocked;
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
		this.operator = operator;
	}

	/**
	 * The {@link PlayerEntity} has stopped operating this {@link Vehicle}
	 * @param operator
	 */
	public void stopOperating(PlayerEntity operator) {
		this.operator = null;
	}	
}
