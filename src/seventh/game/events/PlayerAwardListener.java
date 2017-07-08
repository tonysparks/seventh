/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

/**
 * Listens for {@link PlayerAwardEvent}s
 * 
 * @author Tony
 *
 */
public interface PlayerAwardListener extends EventListener {

    @EventMethod
    public void onPlayerAward(PlayerAwardEvent event);
}
