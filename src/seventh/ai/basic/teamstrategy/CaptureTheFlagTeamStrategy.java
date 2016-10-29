/*
 * see license.txt 
 */
package seventh.ai.basic.teamstrategy;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.AttackDirection;
import seventh.ai.basic.Brain;
import seventh.ai.basic.DefaultAISystem;
import seventh.ai.basic.World;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.WaitAction;
import seventh.ai.basic.squad.Squad;
import seventh.ai.basic.squad.SquadAction;
import seventh.ai.basic.squad.SquadDefendAction;
import seventh.ai.basic.teamstrategy.Roles.Role;
import seventh.game.GameInfo;
import seventh.game.Player;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.game.entities.Flag;
import seventh.game.entities.PlayerEntity;
import seventh.game.type.CaptureTheFlagGameType;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.SeventhConstants;
import seventh.shared.TimeStep;

/**
 * The overall team strategy for capture the flag bots
 * 
 * @author Tony
 *
 */
public class CaptureTheFlagTeamStrategy implements TeamStrategy {

	private Team team;
	private DefaultAISystem aiSystem;	
	
	private TeamState currentState;
		
	private Actions goals;
	
	
	enum TeamState {
		DefendFlag,
		StealFlag,
		ReturnFlag,		
		Random,
		None,
	}
	
	private Roles roles;
	
	private Flag alliedFlag;
	private Flag axisFlag;
	
	private Rectangle axisBase, alliedBase, captureArea, stealArea;
	
	private Flag teamsFlag, enemyFlag;
	
	private Vector2f captureDestination, stealDestination;
	
	private List<Vector2f> stealPositions, defendPositions;
	
	private PlayerInfo[] unassignedPlayers;
	
	private Squad defenseSquad;
	private int desiredDefenseSquadSize;
	
	private SquadAction currentSquadAction;	
	private boolean hasRoundStarted;
	
	/**
	 * The percentage point of being 
	 * aggressive
	 */
	private double aggressiveness;
	
	/**
	 * @param aiSystem
	 * @param team
	 */
	public CaptureTheFlagTeamStrategy(DefaultAISystem aiSystem, Team team) {
		this.aiSystem = aiSystem;
		this.team = team;
		
		this.goals = aiSystem.getGoals(); 
		
		this.roles = new Roles();
		
		this.stealPositions = new ArrayList<>();
		this.defendPositions = new ArrayList<>();
		
		this.unassignedPlayers = new PlayerInfo[SeventhConstants.MAX_PLAYERS];
		this.defenseSquad = new Squad(aiSystem);					
	}
	
	private void assignRoles() {
		
		/* Let's first assign roles to any idol bots
		 */
		for(int i = 0; i < unassignedPlayers.length; i++) {
			PlayerInfo player = this.unassignedPlayers[i];
			if(player != null && player.isAlive()) {
				Role role = roles.getAssignedRole(player);
				if(role==Role.None) {
					role = findRole(player);				
					assignRole(role, player);	
					dispatchRoleAction(role, player);
				}
			}
			
			this.unassignedPlayers[i] = null;
		}
		
		
		/* Now let's asses the game world and determine if we need
		 * to reassign any bots
		 */
		if(this.enemyFlag.isBeingCarried()||!this.enemyFlag.isAtHomeBase()) {
			
			/* Ensure we need to fulfill this role */
			if(!this.roles.hasRoleAssigned(Role.Retriever)) {
				
				/* Defenders automatically are Retrievers if the flag
				 * is gone, so if we don't have any defenders convert one
				 * of our offensive bots
				 */
				if(!this.roles.hasRoleAssigned(Role.Defender)) {				
					PlayerInfo retriever = getBestPlayerToRetrieveFlag();
					if(retriever!=null) {
						assignRole(Role.Retriever, retriever);
					}									
				}
				
			}		
		}
		else {
			
			// TODO: What else???
			
		}
	}
	
	
	/**
	 * @return the best possible player to go and retrieve the flag
	 */
	private PlayerInfo getBestPlayerToRetrieveFlag() {
		PlayerInfo bestPlayer = null;
		float bestDistance = -1;
		List<Player> players = this.team.getPlayers();
		for(int i = 0; i < players.size(); i++) {
			Player player = players.get(i);
			if(player.isAlive() && player.isBot()) {
				PlayerEntity ent = player.getEntity();
				
				// I know this is cheating like crazy, maybe
				// I can do something smarter...
				float distanceFromFlag = this.enemyFlag.distanceFromSq(ent);
				if(bestPlayer==null||distanceFromFlag<bestDistance) {
					
					/* check and see if we are about to score
					 * with this player, if so, don't use them
					 */
					if(this.teamsFlag.isBeingCarried()) {
						if(this.teamsFlag.getCarriedBy().getId() == player.getId()) {
							float distanceToScore = this.teamsFlag.distanceFromSq(captureDestination);
							if(distanceToScore < distanceFromFlag) {
								continue;
							}
						}
					}
					
					
					bestPlayer = player;
					bestDistance = distanceFromFlag;
				}
			}
		}
		
		return bestPlayer;
	}
	
	private Role findRole(PlayerInfo player) {
		
		/* if this player got side tracked and is currently carrying the flag, get their
		 * ass back to base to score
		 */
		if(this.teamsFlag.isBeingCarried() && this.teamsFlag.getCarriedBy().getId() == player.getId()) {
			return Role.Capturer;
		}
		
		/* if the enemy flag is out of our base, go send this bot hunting
		 * for it
		 */
		if(this.enemyFlag.isBeingCarried()||!this.enemyFlag.isAtHomeBase()) {
			if(!this.roles.hasRoleAssigned(Role.Retriever)) {
				return Role.Retriever;
			}
		}				
		
		/* if the enemy flag is safe at our base, let's be aggressive
		 * and go and try to capture our flag
		 */
		if(!this.teamsFlag.isBeingCarried()) {
			int numberAssigned = this.roles.getNumberAssignedToRole(Role.Capturer);
			
			int numberOfBots = this.team.getNumberOfBots();
			if(numberOfBots>0) {
				double amount = (double)numberAssigned / (double)numberOfBots;
				if(amount<this.aggressiveness) {
					return Role.Capturer;	
				}
			}						
		}
		
		return Role.Defender;		
	}
	
	private void assignRole(Role role, PlayerInfo player) {
		this.roles.assignRole(role, player);
		Brain brain = aiSystem.getBrain(player.getId());
		
		if(role==Role.Defender) {
			this.defenseSquad.addSquadMember(brain);
		}
		
		System.out.println("Assigning Role: " + role + " for " + player.getName());		
	}
	
	private void dispatchRoleAction(Role role, PlayerInfo player) {
		Brain brain = aiSystem.getBrain(player.getId());
		if(brain != null) {
			brain.doAction(getActionForRole(role));
		}
	}
	
	private Action getActionForRole(Role role) {
		switch(role) {
			case Defender: {
				if(this.enemyFlag.isAtHomeBase()) {
					if(this.defenseSquad.squadSize()>0) {
						return this.currentSquadAction.getAction(defenseSquad);
					}
					else {
						return goals.defendFlag(this.enemyFlag, this.captureArea);
					}
				}
				return goals.returnFlag(this.enemyFlag);
			}
			case Capturer: return goals.captureFlag(teamsFlag, captureDestination);
			case Retriever: return goals.returnFlag(this.enemyFlag);
			default: return goals.wander();
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.teamstrategy.TeamStrategy#getDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double getDesirability(Brain brain) {
		if(!this.enemyFlag.isAtHomeBase()) {
			return 1.0;
		}
		
		return 0.7;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.teamstrategy.TeamStrategy#getGoal(seventh.ai.basic.Brain)
	 */
	@Override
	public Action getAction(Brain brain) {
		if(this.hasRoundStarted) {
			PlayerInfo player = brain.getPlayer();
			Role role = this.roles.getAssignedRole(player);
			if(role==Role.None) {
				role = findRole(brain.getPlayer());
				assignRole(role, brain.getPlayer());
			}			
			
			return getActionForRole(role);					
		}
		
		// sometimes the brains get ahead
		// of the round starting
		return new WaitAction(1_000);
		
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
		CaptureTheFlagGameType gameType = (CaptureTheFlagGameType) game.getGameType();
		this.alliedFlag = gameType.getAlliedFlag();
		this.axisFlag = gameType.getAxisFlag();
		
		this.alliedBase = gameType.getAlliedHomeBase();
		this.axisBase = gameType.getAxisHomeBase();
				
		this.teamsFlag = this.team.getId() == Team.ALLIED_TEAM_ID 
							? this.alliedFlag : this.axisFlag;
		
		this.enemyFlag = this.team.getId() == Team.ALLIED_TEAM_ID 
							?  this.axisFlag : this.alliedFlag;
		
	
		// the location we need to take our flag in order to score
		this.captureDestination = (this.team.getId() == Team.ALLIED_TEAM_ID
									? this.axisFlag : this.alliedFlag).getSpawnLocation();
		
		this.captureArea = this.team.getId() == Team.ALLIED_TEAM_ID
								? this.axisBase : this.alliedBase;
		
		this.stealArea = this.team.getId() == Team.ALLIED_TEAM_ID
								? this.alliedBase: this.axisBase;
		
		// the location we need to go to in order to steal our flag
		this.stealDestination = this.teamsFlag.getSpawnLocation();
		
		World world = this.aiSystem.getWorld();
		List<AttackDirection> dirs = new ArrayList<>(world.getAttackDirections(this.captureDestination, (this.captureArea.width+this.captureArea.height) / 2f, 12));
		for(AttackDirection dir : dirs) {
			this.defendPositions.add(dir.getDirection());
		}
		
		dirs.clear();
		
		dirs.addAll(world.getAttackDirections(this.stealDestination, (this.stealArea.width+this.stealArea.height) / 2f, 12));
		for(AttackDirection dir : dirs) {
			this.stealPositions.add(dir.getDirection());
		}
		
		this.desiredDefenseSquadSize = this.team.getTeamSize() / 2;
		this.desiredDefenseSquadSize = Math.min(this.desiredDefenseSquadSize, this.team.getNumberOfBots());
		
		this.currentSquadAction = new SquadDefendAction(this.captureDestination);
		this.currentSquadAction.start(defenseSquad);
		
		this.hasRoundStarted = true;
		
		this.aggressiveness = world.getRandom().getRandomRangeMin(0.25f);
	}
	
		
	/* (non-Javadoc)
	 * @see seventh.ai.basic.AIGameTypeStrategy#playerKilled(seventh.game.PlayerInfo)
	 */
	@Override
	public void playerKilled(PlayerInfo player) {
		this.roles.removeDeadPlayer(player);
		this.defenseSquad.onPlayerKilled(player);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.AIGameTypeStrategy#playerSpawned(seventh.game.PlayerInfo)
	 */
	@Override
	public void playerSpawned(PlayerInfo player) {
		if(player.isBot()) {
			this.unassignedPlayers[player.getId()] = player;			
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
		if(this.hasRoundStarted) {
			assignRoles();		
		}
		
		//this.currentSquadAction.update(timeStep);
	}

	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation me = new DebugInformation();
		me
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
