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
public class FlameThrower extends Weapon {

    private int emitRatePerSec;
    private boolean isDoneFiring;
    
    /**
     * @param game
     * @param owner
     * @param type
     */
    public FlameThrower(Game game, Entity owner) {
        super(game, owner, Type.FLAME_THROWER);
        
        this.bulletsInClip = 140;
        this.spread = 1;
        this.emitRatePerSec = 8;
        this.isDoneFiring = true;
        this.lineOfSight = WeaponConstants.FLAME_THROWER_LINE_OF_SIGHT;
        this.weaponWeight = WeaponConstants.FLAME_THROWER_WEIGHT;
        
        setBulletSpawnDistance(55);
    }
    
    @Override
    public boolean isHeavyWeapon() {
        return true;
    }
    
    protected Vector2f newBulletPosition() {
        Vector2f ownerDir = owner.getFacing();
        Vector2f ownerPos = owner.getPos();
        
        Vector2f pos = new Vector2f(ownerPos);     
        Vector2f.Vector2fMA(pos, ownerDir, getBulletSpawnDistance(), pos);
        
        return pos;
    }

    @Override
    public boolean beginFire() {
        if(canFire()) {
            newFire();
            
            if(isDoneFiring) {
                game.emitSound(getOwnerId(), SoundType.FLAMETHROWER_SHOOT, getPos());
            }
            
            isDoneFiring = false;
            
            weaponTime = 1000/this.emitRatePerSec;
            bulletsInClip--;
            
            setFireState(); 
            return true;
        }
        else if (bulletsInClip <= 0 ) {                
            setFireEmptyState();            
        }

        return false;
    }
    
    @Override
    public boolean endFire() {
        this.isDoneFiring = true;
        return super.endFire();
    }
    
    @Override
    protected Vector2f calculateVelocity(Vector2f facing) {    
        return facing.createClone();
    }

}
