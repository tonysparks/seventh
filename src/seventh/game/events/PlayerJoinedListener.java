/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.shared.EventListener;
import seventh.shared.EventMethod;

/**
 * Listens for {@link PlayerJoinedEvent}s
 * 
 * @author Tony
 *
 */
public interface PlayerJoinedListener extends EventListener {

    @EventMethod
    public void onPlayerJoined(PlayerJoinedEvent event);
}
