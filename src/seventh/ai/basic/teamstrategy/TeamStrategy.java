/*
 * see license.txt 
 */
package seventh.ai.basic.teamstrategy;

import seventh.ai.basic.Brain;
import seventh.game.GameInfo;
import seventh.game.PlayerInfo;
import seventh.game.type.GameType;
import seventh.shared.TimeStep;

/**
 * Provides a high level game type for a {@link GameType}
 * 
 * @author Tony
 *
 */
public interface TeamStrategy {

	/**
	 * The agent has no immeidate goals, free to take
	 * orders
	 * 
	 * @param brain
	 */
	public void onGoaless(Brain brain);
	
	/**
	 * Start of a round
	 * 
	 * @param game
	 */
	public void startOfRound(GameInfo game);
	
	/**
	 * An end of a round
	 * 
	 * @param game
	 */
	public void endOfRound(GameInfo game);

	/**
	 * Player spawned
	 * 
	 * @param player
	 */
	public void playerSpawned(PlayerInfo player);
	
	/**
	 * Player was killed
	 * 
	 * @param player
	 */
	public void playerKilled(PlayerInfo player);
	
	/**
	 * Updates the logic
	 * 
	 * @param timeStep
	 * @param game
	 */
	public void update(TimeStep timeStep, GameInfo game);
}
