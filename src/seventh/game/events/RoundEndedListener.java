/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

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
