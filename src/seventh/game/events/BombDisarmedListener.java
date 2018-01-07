/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.events;

import seventh.shared.EventListener;
import seventh.shared.EventMethod;

/**
 * @author Tony
 *
 */
public interface BombDisarmedListener extends EventListener {

    @EventMethod
    public void onBombDisarmedEvent(BombDisarmedEvent event);
}
