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
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * @author Tony
 *
 */
public class Kar98 extends Weapon {

    private boolean wasReloading;
    private boolean endFire;
    
    /**
     * @param game
     * @param owner
     */
    public Kar98(Game game, Entity owner) {
        super(game, owner, Type.KAR98);
    
        this.damage = 100;
        this.reloadTime = 2500;
        this.clipSize = 5;
        this.totalAmmo = 35;
        this.bulletsInClip = this.clipSize;
                
        this.spread = 5;
        
        this.weaponWeight = WeaponConstants.KAR98_WEIGHT;
        this.lineOfSight = WeaponConstants.KAR98_LINE_OF_SIGHT;
    
        this.endFire = true;
                
        applyScriptAttributes("kar98");
    }
    
    /* (non-Javadoc)
     * @see palisma.game.Weapon#update(leola.live.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {    
        super.update(timeStep);
                
        if(wasReloading) {
            //game.emitSound(getOwnerId(), SoundType.KAR98_RECHAMBER, getPos());
            if(weaponTime<=0) {
                wasReloading = false;
                game.emitSound(getOwnerId(), SoundType.KAR98_RECHAMBER, getPos());
            }
        }        
    }
    
    /* (non-Javadoc)
     * @see palisma.game.Weapon#reload()
     */
    @Override
    public boolean reload() {
        boolean reloaded = super.reload();
        if(reloaded) {
            wasReloading = true;
            game.emitSound(getOwnerId(), SoundType.KAR98_RELOAD, getPos());
        }
        
        return reloaded;
    }
    
    @Override
    public boolean beginFire() {
        if ( canFire() ) {
            game.emitSound(getOwnerId(), SoundType.KAR98_FIRE, getPos());
            game.emitSound(getOwnerId(), SoundType.KAR98_RECHAMBER, getPos());
            
            newBullet(true);
            bulletsInClip--;
            weaponTime = 900;
            
            setFireState(); 
            return true;
        }
        else if (bulletsInClip <= 0 ) {
            setFireEmptyState();            
        }
        
        this.endFire = false;
        
        return false;
    }
    
    /* (non-Javadoc)
     * @see palisma.game.Weapon#endFire()
     */
    @Override
    public boolean endFire() {
        this.endFire = true;
        
        if(isFiring()) {
            game.emitSound(getOwnerId(), SoundType.BULLET_SHELL, owner.getPos());
        }        
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
