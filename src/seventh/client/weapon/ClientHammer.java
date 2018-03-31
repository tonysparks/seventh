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
public class ClientHammer extends ClientWeapon {

    /**
     * @param owner
     */
    public ClientHammer(ClientPlayerEntity owner) {
        super(owner);
        
        this.weaponIcon = Art.hammerIcon;
        this.weaponImage = Art.hammerImage;
        // this.muzzleFlash = Art.newThompsonMuzzleFlash();
        this.weaponWeight = WeaponConstants.HAMMER_WEIGHT;
        
        this.weaponKickTime = 0; 
        this.endFireKick = 0f; 
        this.beginFireKick = 0f; 
    }
    
    @Override
    public boolean emitBulletCasing() {
        return false;
    }
    
    @Override
    public boolean emitBarrelSmoke() {
        return false;
    }
}
