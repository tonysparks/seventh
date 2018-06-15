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
public class ClientRisker extends ClientWeapon {

    /**
     * @param ownerId
     */
    public ClientRisker(ClientPlayerEntity owner) {
        super(owner);

        this.weaponIcon = Art.riskerIcon;
//        this.weaponImage = Art.sniperRifleImage;
        this.weaponImage = Art.riskerImage;
        this.muzzleFlash = Art.newRiskerMuzzleFlash();
        this.weaponWeight = WeaponConstants.RISKER_WEIGHT;
        
        this.weaponKickTime = 280; 
        this.endFireKick = 10.7f; 
        this.beginFireKick = 0f; 
    }
    
    /* (non-Javadoc)
     * @see seventh.client.weapon.ClientWeapon#isBurstFire()
     */
    @Override
    public boolean isBurstFire() {    
        return true;
    }
    
    /* (non-Javadoc)
     * @see palisma.client.weapon.ClientWeapon#onFire()
     */
    @Override
    protected boolean onFire() {        
        return true;
    }
}
