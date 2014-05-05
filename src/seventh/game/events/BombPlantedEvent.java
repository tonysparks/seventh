/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.events;

import seventh.game.Bomb;
import leola.frontend.listener.Event;

/**
 * @author Tony
 *
 */
public class BombPlantedEvent extends Event {

	private Bomb bomb;
	
	/**
	 * @param source
	 */
	public BombPlantedEvent(Object source, Bomb bomb) {
		super(source);
		this.bomb = bomb;
	}

	/**
	 * @return the bomb
	 */
	public Bomb getBomb() {
		return bomb;
	}
}
