/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

/**
 * Listens for {@link KillRollEvent}s
 * 
 * @author Tony
 *
 */
public interface KillRollListener extends EventListener {

    @EventMethod
    public void onKillRoll(KillRollEvent event);
}
