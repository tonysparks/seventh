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
public class ClientM1Garand extends ClientWeapon {

    /**
     * @param owner
     */
    public ClientM1Garand(ClientPlayerEntity owner) {
        super(owner);
        
        this.weaponIcon = Art.m1GarandIcon;
        this.weaponImage = Art.m1GarandImage;
        this.muzzleFlash = Art.newM1GarandMuzzleFlash();        
        this.weaponWeight = WeaponConstants.M1GARAND_WEIGHT;
        
        this.weaponKickTime = 50; 
        this.endFireKick = 0f; 
        this.beginFireKick = 2.5f; 
    }

}
