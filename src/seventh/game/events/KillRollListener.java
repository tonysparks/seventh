/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.shared.EventListener;
import seventh.shared.EventMethod;

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
