/*
 * see license.txt 
 */
package seventh.client.weapon;

import seventh.client.entities.ClientPlayerEntity;
import seventh.client.gfx.Art;
import seventh.shared.WeaponConstants;

/**
 * @author Tony
 *
 */
public class ClientRocketLauncher extends ClientWeapon {

	/**
	 * @param ownerId
	 */
	public ClientRocketLauncher(ClientPlayerEntity owner) {
		super(owner);
	
		this.weaponIcon = Art.rocketIcon;
		this.weaponImage = Art.rpgImage;
	//	this.muzzleFlash = Art.newRocketMuzzleFlash();
		
		this.endFireKick = 280;
		this.weaponWeight = WeaponConstants.RPG_WEIGHT;
	}

}
