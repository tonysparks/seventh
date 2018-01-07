/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.events;

import seventh.game.Team;
import seventh.game.net.NetGameStats;
import seventh.shared.Event;

/**
 * @author Tony
 *
 */
public class RoundEndedEvent extends Event {

    private Team winner;
    private NetGameStats stats;
    
    /**
     * @param source
     */
    public RoundEndedEvent(Object source, Team winner, NetGameStats stats) {
        super(source);    
        this.winner = winner;
        this.stats = stats;
    }

    /**
     * @return the stats
     */
    public NetGameStats getStats() {
        return stats;
    }
    
    /**
     * @return the winner
     */
    public Team getWinner() {
        return winner;
    }
}
