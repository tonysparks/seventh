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
public interface RoundStartedListener extends EventListener {

    /**
     * A round has started
     * @param event
     */
    @EventMethod
    public void onRoundStarted(RoundStartedEvent event);
}
