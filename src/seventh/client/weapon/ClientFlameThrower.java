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
public class ClientFlameThrower extends ClientWeapon {

    private final long weaponTime = 1300;
    private long timer;
    
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
                //Sounds.startPlaySound(fireSound, channelId);
            }
        }
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
