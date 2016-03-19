/*
 * see license.txt 
 */
package seventh.ai.basic.teamstrategy;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;
import seventh.game.GameInfo;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.game.type.GameType;
import seventh.shared.Debugable;
import seventh.shared.TimeStep;

/**
 * Provides a high level game type for a {@link GameType}
 * 
 * @author Tony
 *
 */
public interface TeamStrategy extends Debugable {

	/**
	 * @return the Team this strategy is for
	 */
	public Team getTeam();
	
	/**
	 * Retrieves an {@link Action} for a bot to take
	 * @param brain
	 * @return the goal for the bot
	 */
	public Action getAction(Brain brain);
	
	/**
	 * @param brain
	 * @return the desirability of executing the team strategy
	 * for this bot
	 */
	public double getDesirability(Brain brain);
	
	
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
