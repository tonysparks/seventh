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
public class ClientFlameThrower extends ClientWeapon {
    
    /**
     * @param ownerId
     */
    public ClientFlameThrower(ClientPlayerEntity owner) {
        super(owner);

        this.weaponIcon = Art.flameThrowerIcon;
//        this.weaponImage = Art.sniperRifleImage;
        this.weaponImage = Art.flameThrowerImage;
        this.muzzleFlash = null;//Art.newRiskerMuzzleFlash(); // TODO
        this.weaponWeight = WeaponConstants.FLAME_THROWER_WEIGHT;
        
        this.weaponKickTime = 0; 
        this.endFireKick = 0.0f; 
        this.beginFireKick = 0f; 
    }
    
    @Override
    public boolean emitBulletCasing() {
        return false;
    }
    
    @Override
    public boolean isBurstFire() {    
        return false;
    }
    
    
    @Override
    protected boolean onFire() {        
        return true;
    }
}
