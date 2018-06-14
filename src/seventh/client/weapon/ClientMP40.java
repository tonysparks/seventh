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
public class ClientMP40 extends ClientWeapon {

    /**
     * @param owner
     */
    public ClientMP40(ClientPlayerEntity owner) {
        super(owner);
        
        this.weaponIcon = Art.mp40Icon;
        this.weaponImage = Art.mp40Image;
        this.muzzleFlash = Art.newMP40MuzzleFlash();
        this.weaponWeight = WeaponConstants.MP40_WEIGHT;
        
        this.weaponKickTime = 100; 
        this.endFireKick = 0f; 
        this.beginFireKick = 3.2f; 
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
