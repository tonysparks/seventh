/**
 * 
 */
package seventh.game.vehicles;

import seventh.game.Controllable;
import seventh.game.Entity;
import seventh.game.Game;
import seventh.game.PlayerEntity;
import seventh.math.OOB;
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
	protected final OOB vehicleBB;
	
	private final Vector2f center;
	protected int aabbWidth, aabbHeight;
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
		this.vehicleBB = new OOB();
		this.center = new Vector2f();
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
	 * @return the vehicleBB
	 */
	public OOB getOBB() {
		return vehicleBB;
	}
	
	/**
	 * Synchronize the {@link OOB} with the current orientation and position
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
	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#isTouching(seventh.game.Entity)
	 */
	@Override
	public boolean isTouching(Entity other) {
		
		// first check the cheap AABB
		if(bounds.intersects(other.getBounds())) {
		
			if(other instanceof Tank) {
				Tank otherTank = (Tank)other;
				return this.vehicleBB.intersects(otherTank.vehicleBB);
			}
			else {
				return this.vehicleBB.intersects(other.getBounds());
			}
		}
		
		return false; 
	}
}
