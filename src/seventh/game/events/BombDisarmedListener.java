/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

/**
 * @author Tony
 *
 */
public interface BombDisarmedListener extends EventListener {

    @EventMethod
    public void onBombDisarmedEvent(BombDisarmedEvent event);
}
