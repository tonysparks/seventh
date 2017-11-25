/*
 * see license.txt 
 */
package seventh.game.weapons;

import leola.vm.types.LeoMap;
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
public class Thompson extends Weapon {

    private int roundsPerSecond;
        
    /**
     * @param game
     * @param owner
     */
    public Thompson(Game game, Entity owner) {
        super(game, owner, Type.THOMPSON);
        
        this.roundsPerSecond = 7;
        this.damage = 21;
        this.reloadTime = 1600;
        this.clipSize = 30; 
        this.totalAmmo = 180;
        this.spread = 8;
        this.bulletsInClip = this.clipSize;        
        this.lineOfSight = WeaponConstants.THOMPSON_LINE_OF_SIGHT;
        this.weaponWeight = WeaponConstants.THOMPSON_WEIGHT;
        
        applyScriptAttributes("thompson");        
        this.bulletsInClip = this.clipSize;
    }
    

    /* (non-Javadoc)
     * @see seventh.game.weapons.Weapon#applyScriptAttributes(java.lang.String)
     */
    @Override
    protected LeoMap applyScriptAttributes(String weaponName) {
        LeoMap attributes = super.applyScriptAttributes(weaponName);
        if(attributes!=null) {
            this.roundsPerSecond = attributes.getInt("rounds_per_second");
        }
        return attributes;
    }


    /* (non-Javadoc)
     * @see seventh.game.Weapon#reload()
     */
    @Override
    public boolean reload() {    
        boolean reloaded = super.reload();
        if(reloaded) {
            game.emitSound(getOwnerId(), SoundType.THOMPSON_RELOAD, getPos());
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
            game.emitSound(getOwnerId(), SoundType.THOMPSON_FIRE, getPos());
            
            weaponTime = 1000/roundsPerSecond;
            bulletsInClip--;
            
            setFireState(); 
            return true;
        }
        else if (bulletsInClip <= 0 ) {                
            setFireEmptyState();            
        }

        return false;
    }
    
    /* (non-Javadoc)
     * @see palisma.game.Weapon#calculateVelocity(palisma.game.Direction)
     */
    @Override
    protected Vector2f calculateVelocity(Vector2f facing) {    
        return spread(facing, spread);
    }
        
}
