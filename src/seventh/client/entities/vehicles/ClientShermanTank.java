/*
 * see license.txt 
 */
package seventh.client.entities.vehicles;

import seventh.client.ClientGame;
import seventh.client.gfx.ShermanTankSprite;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ClientShermanTank extends ClientTank {

	/**
	 * @param game
	 * @param pos
	 */
	public ClientShermanTank(ClientGame game, Vector2f pos) {
		super(game, pos);
		
		setTankSprite(new ShermanTankSprite(this));
	}

}
