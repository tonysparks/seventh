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
public interface PlayerSpawnedListener extends EventListener {

    @EventMethod
    public void onPlayerSpawned(PlayerSpawnedEvent event);
}
