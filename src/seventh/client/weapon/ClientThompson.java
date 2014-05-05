/*
 * see license.txt 
 */
package seventh.client.weapon;

import seventh.client.ClientPlayerEntity;
import seventh.client.gfx.Art;


/**
 * @author Tony
 *
 */
public class ClientThompson extends ClientWeapon {

	/**
	 * @param ownerId
	 */
	public ClientThompson(ClientPlayerEntity owner) {
		super(owner);
		
		this.weaponIcon = Art.thompsonIcon;
		this.weaponImage = Art.thompsonImage;
		this.muzzleFlash = Art.newThompsonMuzzleFlash();
		this.beginFireKick = 180;	
	}

	/* (non-Javadoc)
	 * @see seventh.client.weapon.ClientWeapon#isAutomatic()
	 */
	@Override
	public boolean isAutomatic() {	
		return true;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.weapon.ClientWeapon#hasLongBarrel()
	 */
	@Override
	public boolean hasLongBarrel() {	
		return false;
	}
}
