/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.type;

import seventh.game.Game;
import seventh.game.GameInfo;

/**
 * @author Tony
 *
 */
public interface Objective {
	
	/**
	 * Reset the objective
	 * @param game
	 */
	public void reset(Game game);
	
	/**
	 * Initializes the objective
	 * @param game
	 */
	public void init(Game game);
	
	/**
	 * Determines if the objective is completed
	 * @param game
	 * @return true if completed, false otherwise
	 */
	public boolean isCompleted(GameInfo game);	
}
