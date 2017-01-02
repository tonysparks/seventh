/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.type;

import java.util.ArrayList;
import java.util.List;

import leola.frontend.listener.EventDispatcher;
import leola.vm.Leola;
import seventh.game.Game;
import seventh.game.Player;
import seventh.game.Team;
import seventh.game.events.RoundEndedEvent;
import seventh.game.events.RoundStartedEvent;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Accomplish an objective to win
 * 
 * @author Tony
 *
 */
public class ObjectiveGameType extends AbstractTeamGameType {
	
	private int currentRound;
	private int minimumObjectivesToComplete;
	private List<Objective> outstandingObjectives;
	private List<Objective> completedObjectives;
	private long roundDelayTime;
	private long currentDelayTime;
	private boolean inIntermission;
	
	private Team attacker, defender;
	
	/**
	 * @param maxScore
	 * @param roundTime
	 */
	public ObjectiveGameType(Leola runtime,
			                 List<Objective> objectives, 	
							 List<Vector2f> alliedSpawnPoints,
							 List<Vector2f> axisSpawnPoints,
							 int minimumObjectivesToComplete,
							 int maxScore, 
							 long roundTime, 
							 long roundDelayTime,
							 byte defenderTeamId) {
		
		super(GameType.Type.OBJ, runtime, alliedSpawnPoints, axisSpawnPoints, maxScore, roundTime);
		
		this.outstandingObjectives = objectives;		
		this.minimumObjectivesToComplete = minimumObjectivesToComplete;		
		this.currentRound = 0;
		this.roundDelayTime = roundDelayTime;
		
		this.currentDelayTime = roundDelayTime;		
		this.inIntermission = true;	
		
		this.completedObjectives = new ArrayList<Objective>(this.outstandingObjectives.size());
		
		if(defenderTeamId == Team.ALLIED_TEAM_ID) {
			this.attacker = getAxisTeam();
			this.defender = getAlliedTeam();			
		}
		else {
			this.defender = getAxisTeam();
			this.attacker = getAlliedTeam();						
		}
		
		this.attacker.setAttacker(true);
		this.defender.setDefender(true);
	}
	
	/**
	 * @return the attacker
	 */
	public Team getAttacker() {
		return attacker;
	}
	
	/**
	 * @return the defender
	 */
	public Team getDefender() {
		return defender;
	}
	
	
	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#start(seventh.game.Game)
	 */
	@Override
	public void start(Game game) {
		setGameState(GameState.IN_PROGRESS);
		int size = this.outstandingObjectives.size();
		for(int i = 0; i < size; i++) {
			this.outstandingObjectives.get(i).init(game);
		}
	}

	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#update(leola.live.TimeStep)
	 */
	@Override
	protected GameState doUpdate(Game game, TimeStep timeStep) {
		
		if(this.inIntermission) {
			this.currentDelayTime -= timeStep.getDeltaTime();
			
			if(this.currentDelayTime <= 0) {
				startRound(game);
			}
		}
		else {

			// If there are objectives that are in progress
			// we must force the attackers to disarm them,
			// even if all the attackers are dead
			int numberOfObjectivesInProgress = 0;
			
			// if we are currently playing, check
			// and see if the objectives have been completed		
			int size = this.outstandingObjectives.size();
			for(int i = 0; i < size; i++) {
				Objective obj = this.outstandingObjectives.get(i); 
				if (obj.isCompleted(game)) {
					this.completedObjectives.add(obj);
				}
				else if(obj.isInProgress(game)) {
					numberOfObjectivesInProgress++;
				}
			}
			
			this.outstandingObjectives.removeAll(this.completedObjectives);
			
			
			if(this.completedObjectives.size() >= this.minimumObjectivesToComplete) {
				endRound(attacker, game);
			}
			else if(this.outstandingObjectives.isEmpty() && this.completedObjectives.size() > 0) {
				endRound(attacker, game);
			}
			else if( defender.isTeamDead() && defender.teamSize() > 0) {
				endRound(attacker, game);
			}	
			else if(getRemainingTime() <= 0 ) {
				endRound(defender, game);
			}
			else if( (attacker.isTeamDead() && attacker.teamSize() > 0)
				&& (numberOfObjectivesInProgress+this.completedObjectives.size() < this.minimumObjectivesToComplete) ) {
				//&& (!this.outstandingObjectives.isEmpty() ? numberOfObjectivesInProgress < this.outstandingObjectives.size() : true) ) {
				endRound(defender, game);
			}			
			else {
				checkSpectating(timeStep, game);
			}				
		}
		
		return this.currentRound >= this.getMaxScore() ? GameState.WINNER : GameState.IN_PROGRESS;
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.type.AbstractTeamGameType#isInProgress()
	 */
	@Override
	public boolean isInProgress() {	
		return super.isInProgress() && (!this.inIntermission || this.currentDelayTime == this.roundDelayTime);
	}
		
	/**
	 * End the round
	 * 
	 * @param winner
	 * @param game
	 */
	private void endRound(Team winner, Game game) {
		this.currentRound++;
		this.currentDelayTime = this.roundDelayTime;
		this.inIntermission = true;		
		
		// give a point to the winner
		if(winner!=null) {
			winner.score(1);
		}
		
		getDispatcher().queueEvent(new RoundEndedEvent(this, winner, game.getNetGameStats()));			
		
	}
	
	private void startRound(Game game) {		
		this.inIntermission = false;

		game.killAll();
		
		int size = this.completedObjectives.size();
		for(int i = 0; i < size; i++) {
			this.completedObjectives.get(i).reset(game);
		}	
		
		size = this.outstandingObjectives.size();
		for(int i = 0; i < size; i++) {
			outstandingObjectives.get(i).reset(game);
		}
				
		this.outstandingObjectives.addAll(completedObjectives);
		this.completedObjectives.clear();
		
		
		// if we are done with intermission, lets go 
		// ahead and spawn the players
		Player[] players = game.getPlayers().getPlayers();
		for (int i = 0; i < players.length; i++) {
			Player player = players[i];
			if (player != null) {
				if(!player.isPureSpectator() && !player.isCommander()) {
					spawnPlayer(player, game);
				}
			}
		}
		
		resetRemainingTime();
									
		getDispatcher().queueEvent(new RoundStartedEvent(this));
	}


	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#registerListeners(seventh.game.Game, leola.frontend.listener.EventDispatcher)
	 */
	@Override
	protected void doRegisterListeners(Game game, EventDispatcher dispatcher) {		
	}

	
}
