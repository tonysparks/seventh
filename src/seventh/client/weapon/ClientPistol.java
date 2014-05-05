/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.weapon;

import seventh.client.ClientPlayerEntity;
import seventh.client.gfx.Art;

/**
 * @author Tony
 *
 */
public class ClientPistol extends ClientWeapon {

	/**
	 * @param owner
	 */
	public ClientPistol(ClientPlayerEntity owner) {
		super(owner);
		
		this.weaponIcon = Art.pistolIcon;
		this.weaponImage = Art.pistolImage;
		this.muzzleFlash = Art.newThompsonMuzzleFlash();
	}

}
