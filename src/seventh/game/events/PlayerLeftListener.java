/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.shared.EventListener;
import seventh.shared.EventMethod;

/**
 * Listens for {@link PlayerLeftEvent}s
 * 
 * @author Tony
 *
 */
public interface PlayerLeftListener extends EventListener {

    @EventMethod
    public void onPlayerLeft(PlayerLeftEvent event);
}
