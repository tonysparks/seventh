/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.events;

import seventh.shared.EventListener;
import seventh.shared.EventMethod;

/**
 * @author Tony
 *
 */
public interface RoundEndedListener extends EventListener {

    /**
     * A round has ended
     * @param event
     */
    @EventMethod
    public void onRoundEnded(RoundEndedEvent event);
}
