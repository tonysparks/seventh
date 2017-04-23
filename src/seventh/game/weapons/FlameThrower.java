/*
 * see license.txt 
 */
package seventh.game.weapons;

import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.game.entities.Entity.Type;
import seventh.math.Vector2f;
import seventh.shared.SoundType;

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
        
        this.bulletsInClip = 40;
        this.spread = 20;
        this.emitRatePerSec = 2;
        this.isDoneFiring = true;
        
        setBulletSpawnDistance(75);
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
        return spread(facing, spread);
    }

}
