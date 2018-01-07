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
public interface BombPlantedListener extends EventListener {

    @EventMethod
    public void onBombPlanted(BombPlantedEvent event);
}
