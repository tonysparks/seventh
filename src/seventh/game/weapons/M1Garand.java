/*
 * The Seventh
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
public class M1Garand extends Weapon {

    private boolean endFire;
    
    /**
     * @param game
     * @param owner
     * @param type
     */
    public M1Garand(Game game, Entity owner) {
        super(game, owner, Type.M1_GARAND);
        
        this.damage = 50;
        this.reloadTime = 1500;
        this.clipSize = 8;
        this.totalAmmo = 40;
        this.bulletsInClip = this.clipSize;
        this.spread = 8;
        
        this.lineOfSight = WeaponConstants.M1GARAND_LINE_OF_SIGHT;
        this.weaponWeight = WeaponConstants.M1GARAND_WEIGHT;
        
        this.endFire = true;
                    
        this.netWeapon.type = Type.M1_GARAND.netValue();
        
        applyScriptAttributes("m1_garand");
    }
        
    /* (non-Javadoc)
     * @see seventh.game.Weapon#reload()
     */
    @Override
    public boolean reload() {
        boolean reloaded = false;
        if(bulletsInClip<=0) {
            reloaded = super.reload();
            if(reloaded) {
                game.emitSound(getOwnerId(), SoundType.M1_GARAND_RELOAD, getPos());
            }
        }
        
        return reloaded;
    }
    
    /* (non-Javadoc)
     * @see palisma.game.Weapon#beginFire()
     */
    @Override
    public boolean beginFire() {
        if(canFire()) {
            newBullet();
                        
            weaponTime = 200;
            bulletsInClip--;
            if(bulletsInClip==0) {
                game.emitSound(getOwnerId(), SoundType.M1_GARAND_LAST_FIRE, getPos());
            }
            else {
                game.emitSound(getOwnerId(), SoundType.M1_GARAND_FIRE, getPos());
            }
            
            setFireState(); 
            return true;
        }
        else if (bulletsInClip <= 0 ) {                            
            setFireEmptyState();            
        }
        
        this.endFire = false;

        return false;
    }
    
    @Override
    public boolean endFire() {
        this.endFire = true;
        return false;
    }
    
    @Override
    public boolean canFire() {    
        return super.canFire() && this.endFire;
    }
    
    /* (non-Javadoc)
     * @see palisma.game.Weapon#calculateVelocity(palisma.game.Direction)
     */
    @Override
    protected Vector2f calculateVelocity(Vector2f facing) {    
        return spread(facing, spread);
    }
}
