/*
 * see license.txt 
 */
package seventh.ai.basic.teamstrategy;

import seventh.ai.basic.Brain;
import seventh.game.GameInfo;
import seventh.game.PlayerInfo;
import seventh.shared.TimeStep;

/**
 * The Team Death Match {@link TeamStrategy}.  
 * 
 * @author Tony
 *
 */
public class TDMTeamStrategy implements TeamStrategy {

	/**
	 * 
	 */
	public TDMTeamStrategy() {	
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.AIGameTypeStrategy#onGoaless(seventh.ai.basic.Brain)
	 */
	@Override
	public void onGoaless(Brain brain) {		
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.AIGameTypeStrategy#playerKilled(seventh.game.PlayerInfo)
	 */
	@Override
	public void playerKilled(PlayerInfo player) {		
	}
	
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.AIGameTypeStrategy#playerSpawned(seventh.game.PlayerInfo)
	 */
	@Override
	public void playerSpawned(PlayerInfo player) {		
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.AIGameTypeStrategy#startOfRound(seventh.game.Game)
	 */
	@Override
	public void startOfRound(GameInfo game) {
	}

	/* (non-Javadoc)
	 * @see seventh.ai.AIGameTypeStrategy#endOfRound(seventh.game.Game)
	 */
	@Override
	public void endOfRound(GameInfo game) {
	}

	/* (non-Javadoc)
	 * @see seventh.ai.AIGameTypeStrategy#update(seventh.shared.TimeStep, seventh.game.Game)
	 */
	@Override
	public void update(TimeStep timeStep, GameInfo game) {
	}

}
