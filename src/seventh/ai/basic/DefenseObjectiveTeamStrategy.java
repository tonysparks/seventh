/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.List;

import seventh.game.Bomb;
import seventh.game.BombTarget;
import seventh.game.GameInfo;
import seventh.game.Player;
import seventh.game.PlayerEntity;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.shared.TimeStep;

/**
 * Handles the objective based game type.
 * 
 * @author Tony
 *
 */
public class DefenseObjectiveTeamStrategy implements TeamStrategy {

	private Team team;
	
	private Player[] defenders;
	private DefaultAISystem aiSystem;
	/**
	 * 
	 */
	public DefenseObjectiveTeamStrategy(DefaultAISystem aiSystem, Team team) {
		this.aiSystem = aiSystem;		
		this.team = team;
		 
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.AIGameTypeStrategy#onGoaless(seventh.ai.basic.Brain)
	 */
	@Override
	public void onGoaless(Brain brain) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.AIGameTypeStrategy#playerKilled(seventh.game.PlayerInfo)
	 */
	@Override
	public void playerKilled(PlayerInfo player) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.AIGameTypeStrategy#playerSpawned(seventh.game.PlayerInfo)
	 */
	@Override
	public void playerSpawned(PlayerInfo player) {
		// TODO Auto-generated method stub
		
	}

	/* (non-Javadoc)
	 * @see seventh.ai.AIGameTypeStrategy#startOfRound(seventh.game.Game)
	 */
	@Override
	public void startOfRound(GameInfo game) {				
		List<BombTarget> targets = game.getBombTargets();
		
		this.defenders = new Player[targets.size()];
		
		Player nextPlayer = team.getAliveBot();
		for(int i = 0; i < targets.size(); i++) {
			BombTarget target = targets.get(i);
			if(nextPlayer!=null) {
				
				Brain brain = this.aiSystem.getBrain(nextPlayer.getId());
				if(brain != null) {
//					brain.getThoughtProcess().setStrategy(new DefendAreaStrategy(target.getCenterPos()));
				}
			}
			
			nextPlayer = team.getNextAliveBotFrom(nextPlayer);
		}
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
		List<BombTarget> targets = game.getBombTargets();
		
		for(int i = 0; i < targets.size(); i++) {
			BombTarget target = targets.get(i);
			if(this.defenders[i] == null || !this.defenders[i].isAlive()) {
			
				if(target.bombPlanting()) {
					Bomb bomb = target.getBomb();
					if(bomb != null) {
						PlayerEntity planter = bomb.getPlanter();
						Player defender = team.getClosestBotTo(planter);
						if(defender != null) {
							this.defenders[i] = defender;
							
							Brain brain = aiSystem.getBrain(defender);
							if(brain != null) {
//								brain.getThoughtProcess().setStrategy(new AttackStrategy(planter));
							}
						}
					}
				}
				else if (target.bombActive()) {
					Player defender = team.getClosestBotTo(target);
					if(defender != null) {
						this.defenders[i] = defender;
						
						Brain brain = aiSystem.getBrain(defender);
						if(brain != null) {
//							brain.getThoughtProcess().setStrategy(new DefuseBombStrategy());
						}
					}
				}
			}
		}
	}

}
