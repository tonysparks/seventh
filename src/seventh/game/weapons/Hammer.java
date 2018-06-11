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
 * Allows a player to construct things
 * 
 * @author Tony
 *
 */
public class Hammer extends Weapon {

    private Vector2f tilePos;
    private boolean endFire;
    
    /**
     * @param game
     * @param owner
     * @param type
     */
    public Hammer(Game game, Entity owner) {
        super(game, owner, Type.HAMMER);
        
        this.tilePos = new Vector2f();
        
        this.endFire = true;
        this.bulletsInClip = 1;
        
        this.weaponWeight = WeaponConstants.HAMMER_WEIGHT;        
        this.lineOfSight = WeaponConstants.HAMMER_LINE_OF_SIGHT;
    }
    
    /**
     * Attempts to build a tile
     */
    private boolean placeTile() {        
        if(owner != null) {           
            Vector2f.Vector2fMA(owner.getCenterPos(), owner.getFacing(), 38f, tilePos);
            
            // TODO: Pick the current tile the user has
            // TODO: Only allow if current weapon is Hammer
            return game.addTile( (byte)1, tilePos);
        }
        
        return false;
        
    }

    @Override
    public boolean beginFire() {
        if(canFire() && placeTile()) {
            game.emitSound(getOwnerId(), SoundType.HAMMER_SWING, getPos());
            weaponTime = 300;
            
            setFireState();            
            this.endFire = false;            
            return true;
        }
        
        return false;
    }
    
    @Override
    public boolean endFire() {
        this.endFire = true;
        return super.endFire();
    }
    
    @Override
    public boolean canFire() {    
        return super.canFire() && this.endFire;
    }
    
    @Override
    protected Vector2f calculateVelocity(Vector2f facing) {
        return spread(facing, 0);
    }

}
