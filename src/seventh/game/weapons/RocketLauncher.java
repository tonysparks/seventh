/*
 * see license.txt 
 */
package seventh.game.weapons;

import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.game.entities.Entity.Type;
import seventh.math.Vector2f;
import seventh.shared.SoundType;
import seventh.shared.WeaponConstants;

/**
 * @author Tony
 *
 */
public class RocketLauncher extends Weapon {
    
    private boolean endFire;
    
    /**
     * @param game
     * @param owner
     */
    public RocketLauncher(Game game, Entity owner) {
        super(game, owner, Type.ROCKET_LAUNCHER);
        
        this.damage = 120;
        this.reloadTime = 0;
        this.clipSize = 5;
        this.totalAmmo = 0;
        this.bulletsInClip = this.clipSize;
        this.lineOfSight = WeaponConstants.RPG_LINE_OF_SIGHT;
        this.weaponWeight = WeaponConstants.RPG_WEIGHT;
        this.endFire = true;
        
        applyScriptAttributes("rocket_launcher");
        
        this.bulletsInClip = 500;
    }
    
    @Override
    public boolean isHeavyWeapon() {
        return true;
    }

    /**
     * Emits the fire rocket sound
     */
    protected void emitFireSound() {
        game.emitSound(getOwnerId(), SoundType.RPG_FIRE, getPos());
    }
    
    /*
     * (non-Javadoc)
     * @see seventh.game.weapons.Weapon#beginFire()
     */
    @Override
    public boolean beginFire() {
        if(canFire()) {
            newRocket();
            emitFireSound();
            
            bulletsInClip--;
            weaponTime = 1500;
            
            setFireState();
            return true;
        }
        else if (bulletsInClip <= 0 && !isFiring()) {
            setFireEmptyState();
        }
//        else {
//            setWaitingState();
//        }
        return false;
    }
    
    /* (non-Javadoc)
     * @see palisma.game.Weapon#endFire()
     */
    @Override
    public boolean endFire() {    
        this.endFire = true;
        return false;
    }
    
    /*
     * (non-Javadoc)
     * @see seventh.game.weapons.Weapon#canFire()
     */
    @Override
    public boolean canFire() {    
        return super.canFire() && this.endFire;
    }
    
    /* (non-Javadoc)
     * @see palisma.game.Weapon#calculateVelocity(palisma.game.Direction)
     */
    @Override
    protected Vector2f calculateVelocity(Vector2f facing) {    
        return facing.createClone();
    }

}
