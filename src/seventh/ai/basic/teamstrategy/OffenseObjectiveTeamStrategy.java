/*
 * see license.txt 
 */
package seventh.ai.basic.teamstrategy;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import seventh.ai.basic.Brain;
import seventh.ai.basic.DefaultAISystem;
import seventh.ai.basic.Stats;
import seventh.ai.basic.Zone;
import seventh.ai.basic.Zones;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Goals;
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
	
	private Stats stats;
	private OffensiveState currentState;
		
	private Goals goals;
	private long timeUntilOrganizedAttack;
	
	enum OffensiveState {
		INFILTRATE,
		PLANT_BOMB,
		DEFEND,
		RANDOM,
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
		
		this.goals = aiSystem.getGoals(); 
				//new Goals(aiSystem.getRuntime());
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.teamstrategy.TeamStrategy#getGoal(seventh.ai.basic.Brain)
	 */
	@Override
	public Action getGoal(Brain brain) {	
		return getCurrentAction(brain);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.teamstrategy.TeamStrategy#getTeam()
	 */
	@Override
	public Team getTeam() {
		return this.team;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.AIGameTypeStrategy#startOfRound(seventh.game.Game)
	 */
	@Override
	public void startOfRound(GameInfo game) {		
		this.zoneToAttack = calculateZoneToAttack();	
		this.currentState = OffensiveState.RANDOM;		
		this.timeUntilOrganizedAttack = 0; //30_000 + random.nextInt(30_000);				
	}
	
	
	/**
	 * @param brain
	 * @return the current marching orders
	 */
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
		case RANDOM:
			action = goals.goToRandomSpot(brain);
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
	
	
	/**
	 * @return determine which {@link Zone} to attack
	 */
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
		else {
			zoneToAttack = stats.getDeadliesZone();
		}
		
		return zoneToAttack;
	}

	/**
	 * @param zone
	 * @return the {@link BombTarget} that has a bomb planted on it in the supplied {@link Zone}
	 */
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
		this.currentState = OffensiveState.RANDOM;
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
	
	
	/**
	 * @param zone
	 * @return true if there is a {@link BombTarget} that is being planted or
	 * is planted in the supplied {@link Zone}
	 */
	private boolean isBombPlantedInZone(Zone zone) {
		boolean bombPlanted = false;
		
		List<BombTarget> targets = zone.getTargets();
		for(int i = 0; i < targets.size(); i++) {
			BombTarget target = targets.get(i);
			if(/*target.bombPlanting() ||*/ target.bombActive()) {				
				bombPlanted = true;
				break;
			}
		}
		
		return bombPlanted;
	}
	
	/**
	 * Gives all the available Agents orders
	 */
	private void giveOrders(OffensiveState state) {
		if(currentState != state) {
			currentState = state;
			
			List<Player> players = team.getPlayers();
			for(int i = 0; i < players.size(); i++) {
				Player player = players.get(i);
				if(player.isBot() && player.isAlive()) {
					Brain brain = aiSystem.getBrain(player);
					System.out.println("Orders posted: " + currentState);
					brain.getCommunicator().post(getCurrentAction(brain));		
				}
			}
		}
	}
		
	/* (non-Javadoc)
	 * @see seventh.ai.AIGameTypeStrategy#update(seventh.shared.TimeStep, seventh.game.Game)
	 */
	@Override
	public void update(TimeStep timeStep, GameInfo game) {
				
		
		/* lets do some random stuff for a while, this
		 * helps keep things dynamic
		 */
		if(this.timeUntilOrganizedAttack > 0) {
			this.timeUntilOrganizedAttack -= timeStep.getDeltaTime();
			
			giveOrders(OffensiveState.RANDOM);
			return;
		}
		
		
		/* if no zone to attack, exit out */
		if(zoneToAttack == null) {
			return;
		}
		
			
		/* Determine if the zone to attack still has
		 * Bomb Targets on it
		 */
		if(zoneToAttack.isTargetsStillActive()) {
			
			/* check to see if the bomb has been planted, if so set our state
			 * to defend the area
			 */						
			if(isBombPlantedInZone(zoneToAttack) ) {				
				giveOrders(OffensiveState.DEFEND);
			}
			else {
				
				/* Send a message to the Agents we need to plant the bomb 
				 */				
				giveOrders(OffensiveState.PLANT_BOMB);							
			}			
	
		}
		else {
			
			/* no more bomb targets, calculate a new 
			 * zone to attack...
			 */
			zoneToAttack = calculateZoneToAttack();			
			if(zoneToAttack != null) {				
				giveOrders(OffensiveState.INFILTRATE);
			}
		}
		
		
		List<Player> players = team.getPlayers();
		for(int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			if(player.isBot() && player.isAlive()) {
				Brain brain = aiSystem.getBrain(player);
				
				if(!brain.getCommunicator().hasPendingCommands()) {
					System.out.println("Orders posted: " + currentState);
					brain.getCommunicator().post(getCurrentAction(brain));
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation me = new DebugInformation();
		me.add("zone_to_attack", this.zoneToAttack)
		  .add("time_to_attack", this.timeUntilOrganizedAttack)
		  .add("state", this.currentState.name())
		  ;
		return me;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDebugInformation().toString();
	}
}
