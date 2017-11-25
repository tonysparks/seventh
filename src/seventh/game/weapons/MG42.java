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
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * The mechs rail gun
 * 
 * @author Tony
 *
 */
public class MG42 extends Weapon {


    private int roundsPerSecond;
            
    private long heatTime;
    private long maxHeat;
    
    /**
     * @param game
     * @param owner
     * @param type
     */
    public MG42(Game game, Entity owner) {
        super(game, owner, Type.MG42);
        
        this.roundsPerSecond = 15;
        this.damage = 40;
        this.reloadTime = 1600;
        this.clipSize = 30;
        this.totalAmmo = 180;
        this.spread = 10;
        this.maxHeat = 3000;
        this.bulletsInClip = this.clipSize;        
        this.lineOfSight = WeaponConstants.THOMPSON_LINE_OF_SIGHT;
        this.weaponWeight = WeaponConstants.THOMPSON_WEIGHT;
        
        applyScriptAttributes("railgun");
    }
    
    /* (non-Javadoc)
     * @see seventh.game.weapons.Weapon#applyScriptAttributes(java.lang.String)
     */
    @Override
    protected LeoMap applyScriptAttributes(String weaponName) {
        LeoMap attributes = super.applyScriptAttributes(weaponName);
        if(attributes!=null) {
            this.roundsPerSecond = attributes.getInt("rounds_per_second");
            this.maxHeat = attributes.getInt("max_heat");
        }
        return attributes;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.weapons.Weapon#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {    
        super.update(timeStep);
        
        if(isFiring()) {
            this.heatTime += timeStep.getDeltaTime();
        }
        else if(this.heatTime > 0) {
            this.heatTime -= timeStep.getDeltaTime();
        }
    }
    
    /**
     * @return true if this gun is over heated
     */
    public boolean isOverheated() {
        return this.heatTime >= this.maxHeat;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.weapons.Weapon#reload()
     */
    @Override
    public boolean reload() {    
        return false;
    }

    /* (non-Javadoc)
     * @see palisma.game.Weapon#beginFire()
     */
    @Override
    public boolean beginFire() {
        if(canFire() && !isOverheated()) {
            newBullet();
            game.emitSound(getOwnerId(), SoundType.MG42_FIRE, getPos());
            
            weaponTime = 1000/roundsPerSecond;
            
            
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
