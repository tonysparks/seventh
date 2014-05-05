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
public interface GameEndListener extends EventListener {

	/**
	 * A game has ended
	 * @param event
	 */
	@EventMethod
	public void onGameEnd(GameEndEvent event);
}
