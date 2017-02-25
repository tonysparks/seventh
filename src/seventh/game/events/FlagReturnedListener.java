/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

/**
 * A flag has been captured
 * 
 * @author Tony
 *
 */
public interface FlagReturnedListener extends EventListener {

    @EventMethod
    public void onFlagReturnedEvent(FlagReturnedEvent event);
}
