/*
 * The Seventh
 * see license.txt 
 */
package seventh.game;

import seventh.game.net.NetBombTarget;
import seventh.game.net.NetEntity;
import seventh.math.Vector2f;

/**
 * A {@link BombTarget} is something that a {@link Bomb} can blow up.  This is
 * used for objective game types.  A {@link BombTarget} may be a Radio communication
 * or something like that.
 * 
 * @author Tony
 *
 */
public class BombTarget extends Entity {

	private NetBombTarget netBombTarget;
	private Bomb bomb;
	
	
	/**
	 * @param position
	 * @param game
	 */
	public BombTarget(Vector2f position, Game game) {
		super(game.getNextPersistantId(), position, 0, game, Type.BOMB_TARGET);
		
		this.bounds.width = 64;
		this.bounds.height = 32;
		
		this.bounds.setLocation(position);
		
		this.netBombTarget = new NetBombTarget();		
		setNetEntity(netBombTarget);		
	}

	/**
	 * Rotates the bomb target by 90 degrees, this will only
	 * do it once
	 */
	public BombTarget rotate90() {
		int width = this.bounds.width;
		this.bounds.width = this.bounds.height;
		this.bounds.height = width;
		this.netBombTarget.rotate90();
		return this;
	}
	
	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#canTakeDamage()
	 */
	@Override
	public boolean canTakeDamage() {
		/* don't allow bullets and such to 
		 * hurt this
		 */
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#getNetEntity()
	 */
	@Override
	public NetEntity getNetEntity() {
		return this.netBombTarget;
	}

	/**
	 * Attaches a bomb to this
	 * @param bomb
	 */
	public void attachBomb(Bomb bomb) {
		if(this.bomb==null) {
			this.bomb = bomb;
		}
	}
	
	/**
	 * Detaches the bomb
	 */
	public void reset() {
		this.bomb = null;			
	}
	
	/**
	 * Determines if the supplied {@link Entity} can plant/disarm
	 * this {@link BombTarget}
	 * @param handler
	 * @return true if the supplied {@link Entity} is able to plant/disarm
	 */
	public boolean canHandle(Entity handler) {
		return this.bounds.intersects(handler.getBounds());
	}
	
	/**
	 * @return if the bomb is planted
	 */
	public boolean bombActive() {
		return this.bomb != null && this.bomb.isPlanted();
	}
	
	/**
	 * @return if a player is planting a bomb
	 */
	public boolean bombPlanting() {
		return this.bomb != null && this.bomb.isPlanting();
	}
	
	/**
	 * @return if a player is disarming the bomb
	 */
	public boolean bombDisarming() {
		return this.bomb==null || this.bomb.isDisarming();
	}
	
	/**
	 * @return true if a bomb is already associated with this 
	 */
	public boolean isBombAttached() {
		return this.bomb != null;
	}
	
	/**
	 * @return true if the bomb is currently detonating and 
	 * destroying this target
	 */
	public boolean isBeingDestroyed() {
		return this.bomb != null && this.bomb.isBlowingUp();
	}
	
	/**
	 * @return the bomb
	 */
	public Bomb getBomb() {
		return bomb;
	}
}
