/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.shared.EventListener;
import seventh.shared.EventMethod;

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
