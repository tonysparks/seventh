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
public class ClientShotgun extends ClientWeapon {

    private final long weaponTime = 1300;
    private long timer;
    
    /**
     * @param ownerId
     */
    public ClientShotgun(ClientPlayerEntity owner) {
        super(owner);
        this.weaponIcon = Art.shotgunIcon;
        this.weaponImage = Art.shotgunImage;
        this.muzzleFlash = Art.newShotgunMuzzleFlash();        
        this.weaponWeight = WeaponConstants.SHOTGUN_WEIGHT;
        
        this.weaponKickTime = 200; 
        this.endFireKick = 18.7f; 
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
//                Sounds.startPlaySound(fireSound, channelId, this.);
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
     * @see seventh.client.weapon.ClientWeapon#isPumpAction()
     */
    @Override
    public boolean isPumpAction() {
        return true;
    }
}
