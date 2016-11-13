/*
 * The Seventh
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
public class ClientKar98 extends ClientWeapon {

	/**
	 * @param owner
	 */
	public ClientKar98(ClientPlayerEntity owner) {
		super(owner);
		
		this.weaponIcon = Art.kar98Icon;
		this.weaponImage = Art.kar98Image;
		this.muzzleFlash = Art.newKar98MuzzleFlash();
		this.weaponWeight = WeaponConstants.KAR98_WEIGHT;
		
		this.weaponKickTime = 150; 
		this.endFireKick = 18.7f; 
		this.beginFireKick = 0f; 
	}

	/* (non-Javadoc)
	 * @see seventh.client.weapon.ClientWeapon#isBoltAction()
	 */
	@Override
	public boolean isBoltAction() {
		return true;
	}
}
