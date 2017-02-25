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
public interface FlagCapturedListener extends EventListener {

    @EventMethod
    public void onFlagCapturedEvent(FlagCapturedEvent event);
}
