/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.shared.EventListener;
import seventh.shared.EventMethod;

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
