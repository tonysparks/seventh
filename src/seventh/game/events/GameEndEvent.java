/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.Event;
import seventh.game.net.NetGameStats;

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
