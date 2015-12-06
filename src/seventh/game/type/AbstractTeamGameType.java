/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.type;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import leola.frontend.listener.EventDispatcher;
import leola.vm.Leola;
import leola.vm.types.LeoObject;
import seventh.game.Game;
import seventh.game.Player;
import seventh.game.Players;
import seventh.game.Team;
import seventh.game.events.RoundEndedEvent;
import seventh.game.events.RoundEndedListener;
import seventh.game.events.RoundStartedEvent;
import seventh.game.events.RoundStartedListener;
import seventh.game.net.NetGameTypeInfo;
import seventh.game.net.NetTeam;
import seventh.game.net.NetTeamStat;
import seventh.shared.Cons;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public abstract class AbstractTeamGameType implements GameType {

	/**
	 * The team indexes
	 */
	protected static final int AXIS=0, 
							   ALLIED=1;
	
	private final int maxScore;
	private final long matchTime;
	private long timeRemaining;
	
	private Team[] teams;
	
	private NetGameTypeInfo gameTypeInfo;
	private GameState gameState;
	
	private List<Team> highScoreTeams;
	private Random random;
	
	/**
	 * A Huge hack to work around the frame
	 * delay for counting deaths for players (queue'd up eventdispatcher event)
	 * and the fact that the game has ended.  We want to
	 * delay the game ending by one frame.
	 */
	private int numberOfFramesForGameEnd;
	private Type type;
	
	private Leola runtime;
	
	/**
	 * @param type
	 * @param runtime
	 * @param maxScore
	 * @param matchTime
	 */
	public AbstractTeamGameType(GameType.Type type, Leola runtime, int maxScore, long matchTime) {
		this.type = type;
		this.runtime = runtime;
		this.matchTime = matchTime;
		this.maxScore = maxScore;
		this.timeRemaining = this.matchTime;
		
		this.teams = new Team[2];
		this.teams[AXIS] = Team.newAxisTeam();
		this.teams[ALLIED] = Team.newAlliedTeam();
		
		this.highScoreTeams = new ArrayList<Team>(2);
		
		this.gameTypeInfo = new NetGameTypeInfo();
		this.gameTypeInfo.type = type.netValue();
		this.gameTypeInfo.maxScore = maxScore;
		this.gameTypeInfo.maxTime = matchTime;		
		
		this.gameTypeInfo.teams = new NetTeam[2];
		
		this.gameState = GameState.IN_PROGRESS;
		this.random = new Random();
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#registerListeners(seventh.game.GameInfo, leola.frontend.listener.EventDispatcher)
	 */
	@Override
	public void registerListeners(final Game game, EventDispatcher dispatcher) {		
		dispatcher.addEventListener(RoundEndedEvent.class, new RoundEndedListener() {
			@Override
			public void onRoundEnded(RoundEndedEvent event) {
				executeCallbackScript("onRoundEnded", game);
			}
		});
		
		dispatcher.addEventListener(RoundStartedEvent.class, new RoundStartedListener() {			
			@Override
			public void onRoundStarted(RoundStartedEvent event) {
				executeCallbackScript("onRoundStarted", game);
			}
		});
		
		doRegisterListeners(game, dispatcher);
	}

	/**
	 * Used by the inherited game types to implement.
	 * 
	 * @param game
	 * @param dispatcher
	 */
	protected abstract void doRegisterListeners(final Game game, EventDispatcher dispatcher);
	
	
	/**
	 * Executes the callback function
	 * 
	 * @param functionName
	 * @param game
	 */
	private void executeCallbackScript(String functionName, Game game) {
		LeoObject function = runtime.get(functionName);
		if(LeoObject.isTrue(function)) {
			LeoObject result = function.call(LeoObject.valueOf(game));
			if(result.isError()) {
				Cons.println("*** ERROR: Calling '" + functionName + "' - " + result.toString());
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#getType()
	 */
	@Override
	public Type getType() {
		return type;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#isInProgress()
	 */
	@Override
	public boolean isInProgress() {
		return GameState.IN_PROGRESS == getGameState() || (this.numberOfFramesForGameEnd < 2);
	}
	
	public Team getAlliedTeam() {
		return this.teams[ALLIED];
	}
	
	public Team getAxisTeam() {
		return this.teams[AXIS];
	}
	
	protected Team getRandomTeam() {
		if(random.nextBoolean()) {
			return getAxisTeam();
		}
		return getAlliedTeam();
	}
	
	/**
	 * @return the random
	 */
	protected Random getRandom() {
		return random;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#getNextPlayerToSpectate(seventh.game.Player)
	 */
	@Override
	public Player getNextPlayerToSpectate(Players players, Player spectator) {
		if(spectator.isPureSpectator()) {
			return players.getNextAlivePlayerFrom(spectator.getSpectating());
		}
		Player player = spectator.getSpectating();
		return player!=null ? spectator.getTeam().getNextAlivePlayerFrom(player) : spectator.getTeam().getAlivePlayer();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#getPrevPlayerToSpectate(seventh.game.Players, seventh.game.Player)
	 */
	@Override
	public Player getPrevPlayerToSpectate(Players players, Player spectator) {
		if(spectator.isPureSpectator()) {
			return players.getPrevAlivePlayerFrom(spectator.getSpectating());
		}
		Player player = spectator.getSpectating();
		return player!=null ? spectator.getTeam().getPrevAlivePlayerFrom(player) : spectator.getTeam().getAlivePlayer();
	}
	
	/**
	 * @param gameState the gameState to set
	 */
	protected void setGameState(GameState gameState) {
		this.gameState = gameState;
	}
	
	/**
	 * @return the gameState
	 */
	protected GameState getGameState() {
		return gameState;
	}
	
	
	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#getMatchTime()
	 */
	@Override
	public long getMatchTime() {
		return this.matchTime;
	}

	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#getMaxScore()
	 */
	@Override
	public int getMaxScore() {	
		return this.maxScore;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.type.GameType#getRemainingTime()
	 */
	@Override
	public long getRemainingTime() {	
		return this.timeRemaining;
	}
	
	/**
	 * Resets the remaining time to the original match time
	 */
	protected void resetRemainingTime() {
		this.timeRemaining = this.matchTime;
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.type.GameType#playerJoin(palisma.game.Player)
	 */
	@Override
	public void playerJoin(Player player) {
		
		// check and see if they are already on a team
		byte teamId = player.getTeamId();
		if(teamId != Team.SPECTATOR_TEAM_ID) {
			switch(teamId) {
				case Team.ALLIED_TEAM_ID: {
					teams[ALLIED].addPlayer(player);
					break;
				}
				case Team.AXIS_TEAM_ID: {
					teams[AXIS].addPlayer(player);
					break;
				}
				default: {
					throw new IllegalArgumentException("Illegal Team value: " + teamId);
				}
			}
		}
		
		// if not, then try to balance out the teams
		else {
			Team teamWithLeast = null;
			int minSize = Integer.MAX_VALUE;
			for(int i = 0; i < teams.length; i++) {
				Team team = teams[i];
				if(team.teamSize()<=minSize) {
					teamWithLeast = team;
					minSize = teamWithLeast.teamSize();
				}
			}
			if(teamWithLeast==null) {
				teamWithLeast = teams[this.random.nextInt(2)];
			}
			teamWithLeast.addPlayer(player);
		}
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.type.GameType#playerLeft(palisma.game.Player)
	 */
	@Override
	public void playerLeft(Player player) {
		for(int i = 0; i < teams.length; i++) {
			Team team = teams[i];
			team.removePlayer(player);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see palisma.game.type.GameType#update(leola.live.TimeStep)
	 */
	@Override
	public GameState update(Game game, TimeStep timeStep) {
		this.timeRemaining -= timeStep.getDeltaTime();
				
		/**
		 * A Huge hack to work around the frame
		 * delay for counting deaths for players (queue'd up eventdispatcher event)
		 * and the fact that the game has ended.  We want to
		 * delay the game ending by one frame.
		 */
		if(getGameState() != GameState.IN_PROGRESS) {
			this.numberOfFramesForGameEnd++;
		}
		else {
			this.numberOfFramesForGameEnd = 0;
		}
		
		return doUpdate(game, timeStep);
	}
	
	protected abstract GameState doUpdate(Game game, TimeStep timeStep);
		
	/**
	 * @return the winning teams (if multiple, it means a tie)
	 */
	public List<Team> getWinners() {
		return new ArrayList<Team>(getTeamsWithHighScore());
	}
	
	/**
	 * @return the team(s) with the highest score
	 */
	public List<Team> getTeamsWithHighScore() {
		this.highScoreTeams.clear();
		int axisScore = this.teams[AXIS].getScore();
		int alliedScore = this.teams[ALLIED].getScore();
		
		if(axisScore > alliedScore) {
			this.highScoreTeams.add(teams[AXIS]);
		}
		else if (alliedScore > axisScore) {
			this.highScoreTeams.add(teams[ALLIED]);
		}
		else {
			this.highScoreTeams.add(this.teams[AXIS]);
			this.highScoreTeams.add(this.teams[ALLIED]);
		}
		
		return this.highScoreTeams;
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.type.GameType#getTeam(palisma.game.Player)
	 */
	@Override
	public Team getTeam(Player player) {
		for(int i = 0; i < this.teams.length; i++) {
			Team team = this.teams[i];
			if(team.onTeam(player)) {
				return team;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.type.GameType#switchTeam(palisma.game.Player, byte)
	 */
	@Override
	public boolean switchTeam(Player player, byte teamId) {
		boolean assigned = false;
		
		Team currentTeam = getTeam(player);
		if(currentTeam != null && currentTeam.getId() != teamId) {
			currentTeam.removePlayer(player);
			assigned = true;
		}
		
		for(int i = 0; i < this.teams.length; i++) {
			Team team = this.teams[i];
			if(team.getId() == teamId) {
				team.addPlayer(player);
				assigned = true;
			}
		}
		
		return assigned;
	}
	
	
	/* (non-Javadoc)
	 * @see palisma.game.type.GameType#getNetGameTypeInfo()
	 */
	@Override
	public NetGameTypeInfo getNetGameTypeInfo() {
		this.gameTypeInfo.teams[AXIS] = this.teams[AXIS].getNetTeam();
		this.gameTypeInfo.teams[ALLIED] = this.teams[ALLIED].getNetTeam();
		return this.gameTypeInfo;
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.type.GameType#getNetTeamStats()
	 */
	@Override
	public NetTeamStat[] getNetTeamStats() {
		NetTeamStat[] stats = new NetTeamStat[this.teams.length];
		for(int i = 0; i < this.teams.length; i++) {
			Team team = this.teams[i];
			NetTeamStat s = new NetTeamStat();
			
			s.id = team.getId();
			s.score = (short)team.getScore();			
			stats[i] = s;
			
		}
		return stats;
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation me = new DebugInformation();
		  
	    me.add("game_type", getType().name())
		  .add("match_time", new Date(getMatchTime()).toString())
		  .add("max_score", getMaxScore())
		  .add("remaining_time", getRemainingTime() / 1000)
		  .add("allied_spawn_points", getAlliedSpawnPoints())
		  .add("axis_spawn_points", getAxisSpawnPoints())
		  .add("teams", new Team[] { 
				  getAlliedTeam(),
				  getAxisTeam()
		  });
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

