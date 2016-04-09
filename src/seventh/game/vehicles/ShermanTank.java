/*
 * see license.txt 
 */
package seventh.game.vehicles;

import seventh.game.Game;
import seventh.math.Vector2f;

/**
 * An allied Sherman tank
 * 
 * @author Tony
 *
 */
public class ShermanTank extends Tank {

	/**
	 * @param position
	 * @param game
	 */
	public ShermanTank(Vector2f position, Game game, long timeToKill) {
		super(Type.SHERMAN_TANK, position, game, timeToKill);
	}

}
