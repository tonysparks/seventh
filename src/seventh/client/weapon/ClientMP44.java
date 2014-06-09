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
public class ClientMP44 extends ClientWeapon {

	/**
	 * @param owner
	 */
	public ClientMP44(ClientPlayerEntity owner) {
		super(owner);
		
		this.weaponIcon = Art.mp44Icon;
		this.weaponImage = Art.mp44Image;
		this.muzzleFlash = Art.newMP44MuzzleFlash();
	}

//	@Override
//	protected boolean onFire() {		
//		return false;
//	}
	/* (non-Javadoc)
	 * @see seventh.client.weapon.ClientWeapon#isAutomatic()
	 */
	@Override
	public boolean isAutomatic() {	
		return true;
	}
	
	public boolean isBurstFire() {
		return false;
	}
}
