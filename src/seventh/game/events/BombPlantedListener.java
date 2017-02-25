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
public interface BombPlantedListener extends EventListener {

    @EventMethod
    public void onBombPlanted(BombPlantedEvent event);
}
