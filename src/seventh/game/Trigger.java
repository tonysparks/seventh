/*
 * see license.txt 
 */
package seventh.game;

import seventh.game.entities.Entity;
import seventh.map.Tile;

/**
 * Bind to either a {@link Tile} or an {@link Entity} to cause something to happen when a condition is
 * met.
 * 
 * @author Tony
 *
 */
public interface Trigger {

	
	/**
	 * The condition in which a {@link Trigger} is triggered
	 * 
	 * @param game
	 * @return true if the condition is met
	 */
	public boolean checkCondition(Game game);

	/**
	 * Executes the {@link Trigger}
	 * 
	 * @param game
	 */
	public void execute(Game game);
}
