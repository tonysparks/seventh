/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.game.Player;
import seventh.shared.Event;

/**
 * A {@link Player} has joined the game
 * 
 * @author Tony
 *
 */
public class PlayerJoinedEvent extends Event {

    private Player player;
    
    /**
     * 
     */
    public PlayerJoinedEvent(Object source, Player player) {
        super(source);
        this.player = player;
    }
    
    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

}
