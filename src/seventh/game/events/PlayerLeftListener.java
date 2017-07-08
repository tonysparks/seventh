/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

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
