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
import seventh.ai.basic.World;
import seventh.ai.basic.Zone;
import seventh.ai.basic.Zones;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Goals;
import seventh.game.BombTarget;
import seventh.game.GameInfo;
import seventh.game.Player;
import seventh.game.PlayerEntity;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.shared.TimeStep;

/**
 * Handles the defensive strategy objective based game type.
 * 
 * @author Tony
 *
 */
public class DefenseObjectiveTeamStrategy implements TeamStrategy {

	private Team team;
	private DefaultAISystem aiSystem;	
	
	private Zones zones;
	private Random random;
	
	private Zone zoneToAttack;
	
	private Stats stats;
	private DefensiveState currentState;
		
	private World world;
	private Goals goals;
	
	private long timeUntilOrganizedAttack;
	private List<PlayerEntity> playersInZone;
	
	enum DefensiveState {		
		DEFUSE_BOMB,
		ATTACK_ZONE,
		DEFEND,
		RANDOM,
		DONE,
	}
	
	/**
	 * 
	 */
	public DefenseObjectiveTeamStrategy(DefaultAISystem aiSystem, Team team) {
		this.aiSystem = aiSystem;
		this.team = team;
		
		this.stats = aiSystem.getStats();
		this.zones = aiSystem.getZones();
		this.random = aiSystem.getRandom();
		
		this.goals = aiSystem.getGoals(); 
//				new Goals(aiSystem.getRuntime());
		
		this.playersInZone = new ArrayList<>();
		
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
		this.currentState = DefensiveState.RANDOM;		
		this.timeUntilOrganizedAttack = 30_000 + random.nextInt(60_000);		
		this.world = new World(aiSystem.getConfig(), game, zones, goals);
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
			case DEFUSE_BOMB:			
				action = goals.defuseBomb();			
				break;
			case RANDOM:
				action = goals.goToRandomSpot(brain);
				break;
			case ATTACK_ZONE:
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
			
			/* Look for targets that are being planted or have been
			 * planted so that we can give them highest priority
			 */
			List<Zone> zonesToAttack = new ArrayList<>();
			for(int i = 0; i < zonesWithBombs.size(); i++) {
				Zone zone = zonesWithBombs.get(i);
				
				if(zone.hasActiveBomb()) {
					zonesToAttack.add(zone);
				}
			}
			
			
			if(zonesToAttack.isEmpty()) {
				
				/* All targets are free of bombs, so lets pick a random one to 
				 * go to
				 */
				zoneToAttack = zonesWithBombs.get(random.nextInt(zonesWithBombs.size()));

				/* check to see if there are too many agents around this bomb */
				if(world != null && zonesToAttack != null) {
					world.playersIn(this.playersInZone, zoneToAttack.getBounds());
					
					int numberOfFriendliesInArea = team.getNumberOfPlayersOnTeam(playersInZone);
					if(numberOfFriendliesInArea > 0) {
						float percentageOfTeamInArea = team.getNumberOfAlivePlayers() / numberOfFriendliesInArea;
						if(percentageOfTeamInArea > 0.3f ) {
							zoneToAttack = world.findAdjacentZone(zoneToAttack, 30);
						}
					}
				}
			}
			else {
				/* someone has planted or is planting a bomb on a target, go rush to defend
				 * it
				 */
				zoneToAttack = zonesToAttack.get(random.nextInt(zonesToAttack.size()));
			}
			
		}
		
		/*
		 * If all else fails, just pick a statistically
		 * good Zone to go to
		 */
		if(zoneToAttack == null) {
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
		this.currentState = DefensiveState.DEFEND;
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
	 * @return true if a bomb has been planted
	 */
	private boolean isBombPlanted() {
		List<BombTarget> targets = world.getBombTargets();
		for(int i = 0; i < targets.size(); i++) {
			BombTarget target = targets.get(i);
			if(target.isAlive()) {
				if(target.bombActive()) {
					return true;
				}
			}
		}
		return false;
	}
	
	/**
	 * @param zone
	 * @return true if there is a {@link BombTarget} that has been planted
	 * and someone is actively disarming it
	 */
	private boolean isBombBeingDisarmed(Zone zone) {
		boolean bombDisarming = false;
		
		List<BombTarget> targets = zone.getTargets();
		for(int i = 0; i < targets.size(); i++) {
			BombTarget target = targets.get(i);
			if(target.isBombAttached() && target.bombDisarming()) {				
				bombDisarming = true;
				break;
			}
		}
		
		return bombDisarming;
	}
	
	/**
	 * Gives all the available Agents orders
	 */
	private void giveOrders(DefensiveState state) {
		if(currentState != state) {
			currentState = state;
			
			List<Player> players = team.getPlayers();
			for(int i = 0; i < players.size(); i++) {
				Player player = players.get(i);
				if(player.isBot() && player.isAlive()) {
					Brain brain = aiSystem.getBrain(player);
					if( !brain.getMotion().isDefusing() ) {					
						brain.getCommunicator().post(getCurrentAction(brain));
					}
				}
			}
		}
	}
		
	/* (non-Javadoc)
	 * @see seventh.ai.AIGameTypeStrategy#update(seventh.shared.TimeStep, seventh.game.Game)
	 */
	@Override
	public void update(TimeStep timeStep, GameInfo game) {
	
		/* drop everything and go disarm the bomb */
		if(isBombPlanted()) {
			zoneToAttack = calculateZoneToAttack();
			
			if(isBombBeingDisarmed(zoneToAttack)) {
				giveOrders(DefensiveState.ATTACK_ZONE);
			}
			else {
				giveOrders(DefensiveState.DEFUSE_BOMB);
			}
		}
		else {
		
			/* lets do some random stuff for a while, this
			 * helps keep things dynamic
			 */
			if(this.timeUntilOrganizedAttack > 0) {
				this.timeUntilOrganizedAttack -= timeStep.getDeltaTime();
				
				giveOrders(DefensiveState.RANDOM);
				return;
			}
			
			
			zoneToAttack = calculateZoneToAttack();			
			if(zoneToAttack != null) {				
				giveOrders(DefensiveState.ATTACK_ZONE);
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
