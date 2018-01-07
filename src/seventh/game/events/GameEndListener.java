/*
 * see license.txt 
 */
package seventh.game.events;

import seventh.shared.EventListener;
import seventh.shared.EventMethod;

/**
 * @author Tony
 *
 */
public interface GameEndListener extends EventListener {

    /**
     * A game has ended
     * @param event
     */
    @EventMethod
    public void onGameEnd(GameEndEvent event);
}
