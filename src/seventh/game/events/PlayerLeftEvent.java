/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.Event;
import seventh.game.Player;

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
