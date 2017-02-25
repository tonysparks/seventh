/*
 * see license.txt 
 */
package seventh.game.entities;

import seventh.game.Game;
import seventh.game.net.NetEntity;
import seventh.game.net.NetFlag;
import seventh.math.Vector2f;
import seventh.shared.SeventhConstants;
import seventh.shared.SoundType;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public class Flag extends Entity {

    private PlayerEntity carriedBy;
    private NetFlag netFlag;
    
    private final Vector2f spawnLocation;
    
    private Timer resetTimer;
    
    /**
     * @param id
     * @param position
     * @param game
     * @param type
     */
    public Flag(final Game game, Vector2f position, Type type) {
        super(game.getNextPersistantId(), position, 0, game, type);
        
        this.spawnLocation = new Vector2f(position);
        
        this.bounds.width = SeventhConstants.FLAG_WIDTH;
        this.bounds.height = SeventhConstants.FLAG_HEIGHT;
        this.bounds.setLocation(position);
        
        this.carriedBy = null;
        
        this.netFlag = new NetFlag(type);
        
        this.resetTimer = new Timer(false, 20_000);
        
    }

    /* (non-Javadoc)
     * @see seventh.game.Entity#update(seventh.shared.TimeStep)
     */
    @Override
    public boolean update(TimeStep timeStep) {
        if(this.carriedBy != null && !this.carriedBy.isAlive()) {
            drop();                        
        }
        
        this.resetTimer.update(timeStep);
        
        if(isBeingCarried()) {
            this.resetTimer.stop();
            
            this.pos.set(this.carriedBy.getCenterPos());
            this.bounds.centerAround(this.pos);
        }
        else {
            if(this.resetTimer.isExpired()) {
                returnHome();
            }
            
            game.doesTouchPlayers(this);
        }
        return true;
    }
    
    public void drop() {
        if(isBeingCarried()) {
            
            Vector2f flagPos = new Vector2f(this.carriedBy.getFacing());
            Vector2f.Vector2fMA(this.carriedBy.getCenterPos(), flagPos, 40, flagPos);
            
            /* do not allow this to be dropped here */
            if(game.getMap().pointCollides((int)flagPos.x, (int)flagPos.y)) {
                flagPos.set(this.carriedBy.getCenterPos());
            }
            
            this.pos.set(flagPos);
            this.bounds.centerAround(this.pos);
            
            this.carriedBy.dropFlag();
            
            game.emitSound(getId(), SoundType.WEAPON_DROPPED, flagPos);

            this.resetTimer.reset();
            this.resetTimer.start();
        }
        
        this.carriedBy = null;
    }
    
    /**
     * @return true if the flag is at its home base
     */
    public boolean isAtHomeBase() {
        return Vector2f.Vector2fApproxEquals(this.pos, getSpawnLocation());
    }
    
    /**
     * Returns to the home base
     */
    public void returnHome() {
        drop();
        this.pos.set(getSpawnLocation());        
        this.bounds.centerAround(this.pos);
        
    }
    
    /**
     * @return the carriedBy
     */
    public PlayerEntity getCarriedBy() {
        return carriedBy;
    }
    
    /**
     * Set who is carrying this flag
     * @param player
     */
    public void carriedBy(PlayerEntity player) {
        if(!isBeingCarried() && player.isAlive()) {            
            this.carriedBy = player;
            this.carriedBy.pickupFlag(this);
        }
    }
    
    /**
     * @return true if this flag is currently being carried
     */
    public boolean isBeingCarried() {
        return this.carriedBy != null && this.carriedBy.isAlive();
    }
    
    /**
     * @return the spawnLocation
     */
    public Vector2f getSpawnLocation() {
        return spawnLocation;
    }
    
    public NetFlag getNetFlag() {
        this.setNetEntity(netFlag);
        this.netFlag.carriedBy = isBeingCarried() ? 
                carriedBy.getId() : SeventhConstants.INVALID_PLAYER_ID;
        return this.netFlag;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.Entity#getNetEntity()
     */
    @Override
    public NetEntity getNetEntity() {
        return getNetFlag();
    }

}
