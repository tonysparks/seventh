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
public class ClientPistol extends ClientWeapon {

    /**
     * @param owner
     */
    public ClientPistol(ClientPlayerEntity owner) {
        super(owner);
        
        this.weaponIcon = Art.pistolIcon;
        this.weaponImage = Art.pistolImage;
        this.muzzleFlash = Art.newThompsonMuzzleFlash();
        this.weaponWeight = WeaponConstants.PISTOL_WEIGHT;
        
        this.weaponKickTime = 0; 
        this.endFireKick = 0f; 
        this.beginFireKick = 0f; 
    }

}
