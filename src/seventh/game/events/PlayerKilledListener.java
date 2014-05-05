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
public interface PlayerKilledListener extends EventListener {

	@EventMethod
	public void onPlayerKilled(PlayerKilledEvent event);
}
