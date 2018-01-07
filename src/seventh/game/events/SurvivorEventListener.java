/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.shared.EventListener;
import seventh.shared.EventMethod;

/**
 * @author Tony
 *
 */
public interface SurvivorEventListener extends EventListener {

    @EventMethod
    public void onSurvivorEvent(SurvivorEvent event);
}
