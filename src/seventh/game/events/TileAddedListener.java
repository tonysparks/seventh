/*
 * see license.txt
 */
package seventh.game.events;

import seventh.shared.EventListener;
import seventh.shared.EventMethod;

/**
 * 
 * Listens for {@link TileAddedEvent}'s
 * 
 * @author Tony
 *
 */
public interface TileAddedListener extends EventListener {

    @EventMethod
    public void onTileAdded(TileAddedEvent event);
}
