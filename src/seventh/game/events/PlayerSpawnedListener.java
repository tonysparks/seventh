/*
 * see license.txt 
 */
package seventh.game.events;

import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

/**
 * @author Tony
 *
 */
public interface PlayerSpawnedListener extends EventListener {

    @EventMethod
    public void onPlayerSpawned(PlayerSpawnedEvent event);
}
