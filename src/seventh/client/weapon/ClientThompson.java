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
public class ClientThompson extends ClientWeapon {

    /**
     * @param ownerId
     */
    public ClientThompson(ClientPlayerEntity owner) {
        super(owner);
        
        this.weaponIcon = Art.thompsonIcon;
        this.weaponImage = Art.thompsonImage;
        this.muzzleFlash = Art.newThompsonMuzzleFlash();        
        this.weaponKickTime = 1;
        this.endFireKick = 0f; 
        this.beginFireKick = 6f; // 85.7 
        this.weaponWeight = WeaponConstants.THOMPSON_WEIGHT;
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
