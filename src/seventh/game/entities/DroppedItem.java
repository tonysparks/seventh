/*
 * see license.txt 
 */
package seventh.game.entities;

import seventh.game.Game;
import seventh.game.net.NetDroppedItem;
import seventh.game.net.NetEntity;
import seventh.game.weapons.Weapon;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Represents a dropped item that can be picked up by a player.
 * 
 * @author Tony
 *
 */
public class DroppedItem extends Entity {

    private NetDroppedItem netEntity;
    private Weapon droppedItem;
    private long timeToLive;
    
    /**
     * @param position
     * @param game
     */
    public DroppedItem(Vector2f position, Game game, Weapon droppedItem) {
        super(position, 0, game, Type.DROPPED_ITEM);
        
        this.droppedItem = droppedItem;
        
        this.bounds.width = 24;
        this.bounds.height = 24;
        
        this.orientation = (float)Math.toRadians(game.getRandom().nextInt(360));
        
        this.netEntity = new NetDroppedItem();
        this.setNetEntity(netEntity);
        this.netEntity.type = Type.DROPPED_ITEM;
        this.netEntity.droppedItem = droppedItem.getType();
        
        this.timeToLive = 1 * 60 * 1000; // stay on the ground for 1 minute
    }
    
    /* (non-Javadoc)
     * @see seventh.game.Entity#update(seventh.shared.TimeStep)
     */
    @Override
    public boolean update(TimeStep timeStep) {    
        super.update(timeStep);
        
        if(timeToLive > 0) {
            timeToLive -= timeStep.getDeltaTime();            
        }
        else {
            kill(null);
        }
        
        
        /* if we haven't been picked up yet,
         * lets check and see if any entities are trying
         * to pick us up
         */
        if(isAlive()) {
        
            PlayerEntity[] players = game.getPlayerEntities();
            int size = players.length;
            for(int i = 0; i < size; i++) {
                PlayerEntity ent = players[i];
                if(canBePickedUpBy(ent)) {
                    if(pickup(ent)) {                                                        
                        break;
                    }
                }                
            }
        }
        
        return true;
    }
    
    /**
     * @return the droppedItem
     */
    public Weapon getDroppedItem() {
        return droppedItem;
    }


    /* (non-Javadoc)
     * @see seventh.game.Entity#getNetEntity()
     */
    @Override
    public NetEntity getNetEntity() {
        return this.netEntity;
    }

    /**
     * If this {@link DroppedItem} can be picked up by the supplied {@link PlayerEntity}
     * 
     * @param ent
     * @return true if it can be picked up; false otherwise
     */
    public boolean canBePickedUpBy(PlayerEntity ent) {
        if(isAlive()) {
            if(ent != null && ent.isAlive() && !ent.getType().isVehicle()) {
                return (ent.bounds.intersects(bounds));
            }
        }
        
        return false;
    }
    
    /**
     * Have the supplied {@link PlayerEntity} pick up the {@link DroppedItem}
     * 
     * @param ent
     * @return true if the player picked up the dropped item
     */
    public boolean pickup(PlayerEntity ent) {
        if(ent.pickupItem(droppedItem)) {
            kill(ent);
            return true;
        }
        
        return false;
    }
}
