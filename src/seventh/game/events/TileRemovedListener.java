/*
 * see license.txt
 */
package seventh.game.events;

import leola.frontend.listener.EventListener;
import leola.frontend.listener.EventMethod;

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
