/*
 * see license.txt 
 */
package seventh.client.entities.vehicles;

import seventh.client.ClientGame;
import seventh.client.gfx.PanzerTankSprite;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class ClientPanzerTank extends ClientTank {

	/**
	 * @param game
	 * @param pos
	 */
	public ClientPanzerTank(ClientGame game, Vector2f pos) {
		super(game, pos);
		
		setTankSprite(new PanzerTankSprite(this));
	}

}
