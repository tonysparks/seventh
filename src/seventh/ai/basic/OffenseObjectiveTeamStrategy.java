/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import seventh.ai.basic.actions.Action;
import seventh.game.BombTarget;
import seventh.game.GameInfo;
import seventh.game.Player;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.shared.TimeStep;

/**
 * Handles the objective based game type.
 * 
 * @author Tony
 *
 */
public class OffenseObjectiveTeamStrategy implements TeamStrategy {

	private Team team;
	private DefaultAISystem aiSystem;	
	
	private Zones zones;
	private Random random;
	
	private Zone zoneToAttack;
	
	private List<Player> bombPlanters;
	
	private Stats stats;
	private OffensiveState currentState;
	private boolean roundStarted = false;
	
	private Goals goals;
	
	enum OffensiveState {
		INFILTRATE,
		PLANT_BOMB,
		DEFEND,
		DONE,
	}
	
	/**
	 * 
	 */
	public OffenseObjectiveTeamStrategy(DefaultAISystem aiSystem, Team team) {
		this.aiSystem = aiSystem;
		this.team = team;
		
		this.stats = aiSystem.getStats();
		this.zones = aiSystem.getZones();
		this.random = aiSystem.getRandom();
		
		this.goals = new Goals(aiSystem.getRuntime());
		
		this.bombPlanters = new ArrayList<>();
	}

	/* (non-Javadoc)
	 * @see seventh.ai.AIGameTypeStrategy#startOfRound(seventh.game.Game)
	 */
	@Override
	public void startOfRound(GameInfo game) {		
		this.zoneToAttack = calculateZoneToAttack();	
		this.bombPlanters.clear();
		
		this.currentState = OffensiveState.INFILTRATE;
		this.roundStarted = true;
	}
	
	private Action getCurrentAction(Brain brain) {
		Action action = null;
		
		if(this.zoneToAttack==null) {
			this.zoneToAttack = calculateZoneToAttack();
		}
		
		switch(this.currentState) {
		case DEFEND:
			if(zoneToAttack != null) {
				BombTarget target = getPlantedBombTarget(zoneToAttack);
				if(target!=null) {
					action = goals.defendPlantedBomb(target);
				}
				else {
					action = goals.defend(zoneToAttack);
				}
			}
			break;		
		case INFILTRATE:
			if(zoneToAttack != null) {
				action = goals.infiltrate(zoneToAttack);
			}
			break;
		case PLANT_BOMB:			
			action = goals.plantBomb();			
			break;
		case DONE:			
		default:
			if(zoneToAttack != null) {
				action = goals.infiltrate(zoneToAttack);
			}
			break;		
		}
		
		return action;
	}
	
	private Zone calculateZoneToAttack() {
		Zone zoneToAttack = null;
		List<Zone> zonesWithBombs = this.zones.getBombTargetZones();
		if(!zonesWithBombs.isEmpty()) {
			List<Zone> validZones = new ArrayList<>();
			for(Zone zone : zonesWithBombs) {
				if(zone.isTargetsStillActive()) {
					validZones.add(zone);
				}
			}
			
			if(!validZones.isEmpty()) {
				zoneToAttack = validZones.get(random.nextInt(validZones.size()));
			}
		}
		
		return zoneToAttack;
	}

	private BombTarget getPlantedBombTarget(Zone zone) {
		List<BombTarget> targets = zone.getTargets();
		for(int i = 0; i < targets.size(); i++) {
			BombTarget target = targets.get(i);
			if(target.bombPlanting() || target.bombActive()) {
				return target;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.AIGameTypeStrategy#playerKilled(seventh.game.PlayerInfo)
	 */
	@Override
	public void playerKilled(PlayerInfo player) {
		this.bombPlanters.remove(player);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.AIGameTypeStrategy#playerSpawned(seventh.game.PlayerInfo)
	 */
	@Override
	public void playerSpawned(PlayerInfo player) {
		if(player.isBot()) {
			Brain brain = aiSystem.getBrain(player);
			
			Action action = getCurrentAction(brain);
			if(action != null) {
				brain.getCommunicator().post(action);
			}
		}	
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.AIGameTypeStrategy#endOfRound(seventh.game.Game)
	 */
	@Override
	public void endOfRound(GameInfo game) {
		this.roundStarted = false;
		this.bombPlanters.clear();
		this.currentState = OffensiveState.INFILTRATE;
		this.zoneToAttack = null;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.AIGameTypeStrategy#onGoaless(seventh.ai.basic.Brain)
	 */
	@Override
	public void onGoaless(Brain brain) {
		Action action = getCurrentAction(brain);
		if(action != null) {
			brain.getCommunicator().post(action);
		}
	}
	
	private void makeDecision(TimeStep timeStep, GameInfo game) {
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.AIGameTypeStrategy#update(seventh.shared.TimeStep, seventh.game.Game)
	 */
	@Override
	public void update(TimeStep timeStep, GameInfo game) {
	
		if(zoneToAttack != null) {
			if(!zoneToAttack.isTargetsStillActive()) {
				zoneToAttack = calculateZoneToAttack();
				if(zoneToAttack != null) {
					currentState = OffensiveState.INFILTRATE;
					
					
					/* give out the new orders */
					List<Player> players = team.getPlayers();
					for(int i = 0; i < players.size(); i++) {
						Player player = players.get(i);
						if(player.isBot() && player.isAlive()) {
							Brain brain = aiSystem.getBrain(player);
							brain.getCommunicator().post(getCurrentAction(brain));		
						}
					}
				}
			}
			else {
				
				boolean bombPlanted = false;
				/* check to see if the bomb has been planted, if so set our state
				 * to defend the area
				 */
				List<BombTarget> targets = zoneToAttack.getTargets();
				for(int i = 0; i < targets.size(); i++) {
					BombTarget target = targets.get(i);
					if(target.bombPlanting() || target.bombActive()) {
						currentState = OffensiveState.DEFEND;
						bombPlanted = true;
					}
				}
				
				if(!bombPlanted) {				
					List<Player> players = team.getPlayers();
					for(int i = 0; i < players.size(); i++) {
						Player player = players.get(i);
						if(player.isBot() && player.isAlive()) {
							//if( zoneToAttack.contains(player.getEntity())) 
							{
								currentState = OffensiveState.PLANT_BOMB;
								
								if(!bombPlanters.contains(player)) {
									
									bombPlanters.add(player);
									Brain brain = aiSystem.getBrain(player);
									brain.getCommunicator().post(goals.plantBomb());
								}
							}
						}
					}				
				}
			}
		}
	}

}
