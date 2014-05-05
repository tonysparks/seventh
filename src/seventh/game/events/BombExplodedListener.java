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
public interface BombExplodedListener extends EventListener {

	@EventMethod
	public void onBombExplodedEvent(BombExplodedEvent event);
}
