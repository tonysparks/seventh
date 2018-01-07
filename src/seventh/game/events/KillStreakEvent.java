/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.game.Player;
import seventh.shared.Event;

/**
 * A {@link Player} has gone on a kill streak
 * 
 * @author Tony
 *
 */
public class KillStreakEvent extends Event {

    private Player player;
    private int streak;
    
    public KillStreakEvent(Object source, Player player, int streak) {
        super(source);
        this.player = player;
        this.streak = streak;
    }
    
    /**
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * @return the streak
     */
    public int getStreak() {
        return streak;
    }
}
