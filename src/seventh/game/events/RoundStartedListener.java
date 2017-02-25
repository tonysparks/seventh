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
public interface RoundStartedListener extends EventListener {

    /**
     * A round has started
     * @param event
     */
    @EventMethod
    public void onRoundStarted(RoundStartedEvent event);
}
