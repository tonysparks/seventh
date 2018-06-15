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
public class ClientShotgun extends ClientWeapon {
    
    /**
     * @param ownerId
     */
    public ClientShotgun(ClientPlayerEntity owner) {
        super(owner);
        this.weaponIcon = Art.shotgunIcon;
        this.weaponImage = Art.shotgunImage;
        this.muzzleFlash = Art.newShotgunMuzzleFlash();        
        this.weaponWeight = WeaponConstants.SHOTGUN_WEIGHT;
        
        this.weaponKickTime = 66; 
        this.endFireKick = 18.7f; 
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
     * @see seventh.client.weapon.ClientWeapon#isPumpAction()
     */
    @Override
    public boolean isPumpAction() {
        return true;
    }
}
