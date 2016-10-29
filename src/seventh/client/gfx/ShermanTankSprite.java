/*
 * see license.txt 
 */
package seventh.client.gfx;

import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.client.entities.vehicles.ClientTank;

/**
 * Represents a Sherman tank
 * 
 * @author Tony
 *
 */
public class ShermanTankSprite extends TankSprite {

	/**
	 * @param tank
	 */
	public ShermanTankSprite(ClientTank tank) {
		super(tank, Art.newShermanTankTracks(), new Sprite(Art.shermanTankTurret),
				    Art.newShermanTankTracksDamaged(), new Sprite(Art.shermanTankTurretDamaged));
	}

}
