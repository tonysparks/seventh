/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.game.net.NetGameStats;
import seventh.shared.Event;

/**
 * @author Tony
 *
 */
public class GameEndEvent extends Event {

    private NetGameStats stats;
    /**
     * @param source
     */
    public GameEndEvent(Object source, NetGameStats stats) {
        super(source);
        this.stats = stats;
    }
    
    /**
     * @return the stats
     */
    public NetGameStats getStats() {
        return stats;
    }
}
