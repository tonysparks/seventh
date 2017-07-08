/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

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
