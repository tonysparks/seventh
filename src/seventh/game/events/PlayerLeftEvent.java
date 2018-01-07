/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.game.Player;
import seventh.shared.Event;

/**
 * A {@link Player} has left the game
 * 
 * @author Tony
 *
 */
public class PlayerLeftEvent extends Event {

    private Player player;
    
    /**
     * 
     */
    public PlayerLeftEvent(Object source, Player player) {
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
