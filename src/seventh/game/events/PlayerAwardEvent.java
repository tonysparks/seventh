/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.Event;
import seventh.game.Player;
import seventh.game.PlayerAwardSystem.Award;

/**
 * A {@link Player} has received an award
 * 
 * @author Tony
 *
 */
public class PlayerAwardEvent extends Event {

    private Player player;
    private Award award;
    
    public PlayerAwardEvent(Object source, Player player, Award award) {
        super(source);
        this.player = player;
        this.award = award;
    }
    
    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the award
     */
    public Award getAward() {
        return award;
    }
}
