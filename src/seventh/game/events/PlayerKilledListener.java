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
public interface PlayerKilledListener extends EventListener {

    @EventMethod
    public void onPlayerKilled(PlayerKilledEvent event);
}
