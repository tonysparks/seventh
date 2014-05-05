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
public class ClientMP40 extends ClientWeapon {

	/**
	 * @param owner
	 */
	public ClientMP40(ClientPlayerEntity owner) {
		super(owner);
		
		this.weaponIcon = Art.mp40Icon;
		this.weaponImage = Art.mp40Image;
		this.muzzleFlash = Art.newMP40MuzzleFlash();
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
