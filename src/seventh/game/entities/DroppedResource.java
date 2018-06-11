/*
 * see license.txt 
 */
package seventh.game.entities;

import seventh.game.Game;
import seventh.game.net.NetEntity;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class DroppedResource extends Entity {

    
    private PlayerEntity carriedBy;
    private int amount;
    
    /**
     * @param position
     * @param game
     * @param type
     */
    public DroppedResource(Vector2f position, Game game, Type type) {
        super(game.getNextEntityId(), position, 0, game, type);
        
        this.carriedBy = null;
    }
    
    /**
     * @return the carriedBy
     */
    public PlayerEntity getCarriedBy() {
        return carriedBy;
    }
    
    
    /**
     * @return if this is being carried by a player
     */
    public boolean isBeingCarried() {
        return this.carriedBy != null;
    }
    
    /**
     * Set who is carrying this flag
     * @param player
     */
    public void carriedBy(PlayerEntity player) {
        if(!isBeingCarried() && player.isAlive()) {            
            this.carriedBy = player;
           // this.carriedBy.pickupFlag(this);
            // TODO: Pick up the resource
        }
    }

    /* (non-Javadoc)
     * @see seventh.game.entities.Entity#getNetEntity()
     */
    @Override
    public NetEntity getNetEntity() {
        // TODO Auto-generated method stub
        return null;
    }

}
