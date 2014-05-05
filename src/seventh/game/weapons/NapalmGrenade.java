/*
 * see license.txt 
 */
package seventh.game.weapons;

import seventh.game.Entity;
import seventh.game.Game;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class NapalmGrenade extends Grenade {

	/**
	 * @param position
	 * @param speed
	 * @param game
	 * @param owner
	 * @param targetVel
	 * @param damage
	 */
	public NapalmGrenade(Vector2f position, int speed, final Game game, final Entity owner, Vector2f targetVel, final int damage) {
		super(position, speed, game, owner, targetVel, damage);
		
		setType(Type.NAPALM_GRENADE); 
		
		this.onKill = new KilledListener() {
			
			@Override
			public void onKill(Entity entity, Entity killer) {								
				game.newBigFire(getCenterPos(), owner, damage);
			}
		};
	}

}
