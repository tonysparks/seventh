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
public interface BombExplodedListener extends EventListener {

    @EventMethod
    public void onBombExplodedEvent(BombExplodedEvent event);
}
