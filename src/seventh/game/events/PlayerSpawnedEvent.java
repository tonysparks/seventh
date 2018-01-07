/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.game.Player;
import seventh.math.Vector2f;
import seventh.shared.Event;

/**
 * @author Tony
 *
 */
public class PlayerSpawnedEvent extends Event {

    private Player player;
    private Vector2f spawnLocation;
    
    /**
     * @param source
     */
    public PlayerSpawnedEvent(Object source, Player player, Vector2f position) {
        super(source);
        
        this.player = player;
        this.spawnLocation = position;
    }
    
    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }
    
    /**
     * @return the spawnLocation
     */
    public Vector2f getSpawnLocation() {
        return spawnLocation;
    }

}
