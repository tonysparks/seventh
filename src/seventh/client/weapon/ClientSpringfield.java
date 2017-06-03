/*
 * see license.txt 
 */
package seventh.client.weapon;

import seventh.client.entities.ClientPlayerEntity;
import seventh.client.gfx.Art;
import seventh.game.weapons.Weapon.WeaponState;
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * @author Tony
 *
 */
public class ClientSpringfield extends ClientWeapon {

    private final long weaponTime = 1300;
    private long timer;
    
    /**
     * @param ownerId
     */
    public ClientSpringfield(ClientPlayerEntity owner) {
        super(owner);

        this.weaponIcon = Art.springfieldIcon;
        this.weaponImage = Art.springfieldImage;
        this.muzzleFlash = Art.newSpringfieldMuzzleFlash();        
        this.weaponWeight = WeaponConstants.SPRINGFIELD_WEIGHT;
        
        this.weaponKickTime = 150; 
        this.endFireKick = 28.7f; 
        this.beginFireKick = 0f; 
    }

    /* (non-Javadoc)
     * @see palisma.client.weapon.ClientWeapon#update(leola.live.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        super.update(timeStep);
        
        if(getState() == WeaponState.READY) {            
            timer = -1;            
        }
                
        timer -= timeStep.getDeltaTime();
        
        if(getState() == WeaponState.FIRING) {
            if(timer<=0) {
                timer = weaponTime;
            }
        }
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
