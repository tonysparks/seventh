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
public class ClientSpringfield extends ClientWeapon {
    
    /**
     * @param ownerId
     */
    public ClientSpringfield(ClientPlayerEntity owner) {
        super(owner);

        this.weaponIcon = Art.springfieldIcon;
        this.weaponImage = Art.springfieldImage;
        this.muzzleFlash = Art.newSpringfieldMuzzleFlash();        
        this.weaponWeight = WeaponConstants.SPRINGFIELD_WEIGHT;
        
        this.weaponKickTime = 100; 
        this.endFireKick = 20.7f; 
        this.beginFireKick = 0f; 
    }

    
    /* (non-Javadoc)
     * @see palisma.client.weapon.ClientWeapon#onFire()
     */
    @Override
    protected boolean onFire() {        
        return true;
    }
    
    /* (non-Javadoc)
     * @see seventh.client.weapon.ClientWeapon#isBoltAction()
     */
    @Override
    public boolean isBoltAction() {
        return true;
    }
}
