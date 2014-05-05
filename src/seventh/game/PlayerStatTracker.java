/*
 * see license.txt 
 */
package seventh.game;

import seventh.game.Entity.Type;
import seventh.game.weapons.Bullet;
import seventh.game.weapons.Explosion;
import seventh.game.weapons.Rocket;

/**
 * @author Tony
 *
 */
public class PlayerStatTracker {

	private Players players;
	/**
	 * 
	 */
	public PlayerStatTracker(Game game) {
		this.players = game.getPlayers();
	}

	/**
	 * A player has died
	 * 
	 * @param killed
	 * @param killer
	 */
	public void onPlayerDeath(PlayerEntity killed, Entity killer) {
		Player killerPlayer = players.getPlayer(killer.id);
		Player killedPlayer = players.getPlayer(killed.id);
		
		if(killedPlayer!=null) {
			killedPlayer.incrementDeaths();
		}
		
		if(killerPlayer==null) {
			Entity owner = null;
			switch(killer.getType()) {
				case BULLET: {
					Bullet bullet = (Bullet)killer;
					owner = bullet.getOwner();					
					break;
				}
				case EXPLOSION: {
					Explosion explosion = (Explosion)killer;
					owner = explosion.getOwner();
					break;
				}
				case ROCKET: {
					Rocket rocket = (Rocket)killer;
					owner = rocket.getOwner();
					break;
				}
				case PLAYER: {
					owner = killer;
					break; 
				}
				case AMMO:					
				case THOMPSON:													
				case ROCKET_LAUNCHER:					
				case SHOTGUN:
				case SPRINGFIELD:
				case RISKER:
				default:
					break;
				
			}
						
			if(owner != null && owner.getType()==Type.PLAYER) {
				killerPlayer = players.getPlayer(owner.id);
			}
		}
		
		if(killerPlayer != null) {
			killerPlayer.incrementKills();
		}
	}

}
