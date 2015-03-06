/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.Event;
import seventh.game.Entity;
import seventh.game.Entity.Type;
import seventh.game.Player;
import seventh.game.PlayerEntity;
import seventh.game.weapons.Bullet;
import seventh.game.weapons.Explosion;
import seventh.game.weapons.Fire;
import seventh.game.weapons.Rocket;
import seventh.game.weapons.Weapon;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class PlayerKilledEvent extends Event {
	
	
	private Player player;
	private Entity killedBy;
	private Vector2f pos;
	
	private Type mod;
	
	/**
	 * @param source
	 * @param player
	 * @param killedBy
	 * @param pos 
	 */
	public PlayerKilledEvent(Object source, Player player, Entity killedBy, Vector2f pos) {
		super(source);
				
		this.player = player;
		this.killedBy = killedBy;
		this.pos = new Vector2f(pos);
		
		this.mod = calculateMeansOfDeath();
	}
	
	/**
	 * @return the pos
	 */
	public Vector2f getPos() {
		return pos;
	}
	
	/**
	 * @return the killedBy
	 */
	public Entity getKilledBy() {
		return killedBy;
	}
	
	/**
	 * @return the playerId
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * @return the means of death
	 */
	public Type getMeansOfDeath() {
		return this.mod;
	}
	
	private Type calculateMeansOfDeath() {
		Type mod = Type.UNKNOWN;
		Entity killer = getKilledBy();
		if(killer != null) {			
			switch(killer.getType()) {
				case NAPALM_GRENADE:
				case GRENADE: {
					mod = Type.GRENADE;
					break;
				}			
				case FIRE: {
					mod = Type.FIRE;
					break;
				}
				case EXPLOSION: {
					mod = Type.EXPLOSION;
					break;
				}						
				case ROCKET: {
					mod = Type.ROCKET;
					break;
				}
				case BULLET: {
					Bullet bullet = (Bullet)killer;
					killer = bullet.getOwner();
					// fall-through
				}
				default: {
					if(killer instanceof PlayerEntity) {
						PlayerEntity kp = (PlayerEntity)killer;
						Weapon weapon = kp.getInventory().currentItem();
						if(weapon!=null) {
							mod = weapon.getType();
						}
					}
				}
			}		
		}
		
		return mod;
	}
	
	/**
	 * @return attempts to find the players ID of the Killer.
	 */
	public int getKillerId() {
		Entity killer = getKilledBy();
		if(killer != null) {
			long killerId = killer.getId();
			switch(killer.getType()) {
				case NAPALM_GRENADE:
				case GRENADE:
				case BULLET: {
					Bullet bullet = (Bullet)killer;
					Entity owner = bullet.getOwner();
					if(owner!=null) {
						killerId = owner.getId();
					}
					break;
				}				
				case FIRE: {
					Fire fire = (Fire) killer;
					Entity owner = fire.getOwner();
					if(owner!=null) {
						killerId = owner.getId();
					}
					break;
				}
				case EXPLOSION: {
					Explosion explosion = (Explosion)killer;
					Entity owner = explosion.getOwner();
					if(owner!=null) {
						killerId = owner.getId();
					}
					break;
				}						
				case ROCKET: {
					Rocket rocket = (Rocket)killer;
					Entity owner = rocket.getOwner();
					if(owner!=null) {
						killerId = owner.getId();
					}
					break;
				}						
				default: {
					killerId = killer.getId();
				}
			}
		
			return (int)killerId;
		}
		return Integer.MIN_VALUE;
	}
}
