/*
 * see license.txt
 */
package seventh.game.events;

import seventh.shared.EventListener;
import seventh.shared.EventMethod;

/**
 * 
 * Listens for {@link TileRemovedEvent}'s
 * 
 * @author Tony
 *
 */
public interface TileRemovedListener extends EventListener {

    @EventMethod
    public void onTileRemoved(TileRemovedEvent event);
}
