/*
 * The Seventh
 * see license.txt 
 */
package seventh.game.type;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import leola.vm.Leola;
import leola.vm.types.LeoObject;
import seventh.game.Game;
import seventh.game.Player;
import seventh.game.PlayerClass;
import seventh.game.Players;
import seventh.game.Team;
import seventh.game.entities.Entity;
import seventh.game.entities.PlayerEntity;
import seventh.game.events.GameEndEvent;
import seventh.game.events.PlayerKilledEvent;
import seventh.game.events.PlayerKilledListener;
import seventh.game.events.RoundEndedEvent;
import seventh.game.events.RoundEndedListener;
import seventh.game.events.RoundStartedEvent;
import seventh.game.events.RoundStartedListener;
import seventh.game.net.NetGamePartialStats;
import seventh.game.net.NetGameStats;
import seventh.game.net.NetGameTypeInfo;
import seventh.game.net.NetPlayerPartialStat;
import seventh.game.net.NetPlayerStat;
import seventh.game.net.NetTeam;
import seventh.game.net.NetTeamStat;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Cons;
import seventh.shared.EventDispatcher;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public abstract class AbstractTeamGameType implements GameType {

    /**
     * Time it take to warm up to when the game is ready to 
     * start being played
     */
    public static final int WARMUP_TIME = 5_000;
    
    /**
     * The team indexes
     */
    protected static final int AXIS=0, 
                               ALLIED=1;
    
    private final int maxScore;
    private final long matchTime;
    private long timeRemaining;
    
    private Team[] teams;
    
    private NetGameStats gameStats;
    private NetGamePartialStats gamePartialStats;
    
    private NetGameTypeInfo gameTypeInfo;
    private GameState gameState;
    private Type type;
    
    private List<Team> highScoreTeams;
    private Random random;
    
    /**
     * A Huge hack to work around the frame
     * delay for counting deaths for players (queue'd up eventdispatcher event)
     * and the fact that the game has ended.  We want to
     * delay the game ending by one frame.
     */
    private int numberOfFramesForGameEnd;
    private boolean gameEnded;
    
    private EventDispatcher dispatcher;
    protected Leola runtime;
    
    // avoid GC, so we create data members :\
    private List<Vector2f> alliedSpawnPoints, axisSpawnPoints;
    private Rectangle spawnBounds;
    
    private Timer warmupTimer;
    private Game game;
    
    /**
     * @param type
     * @param runtime
     * @param maxScore
     * @param matchTime
     */
    public AbstractTeamGameType(GameType.Type type, Leola runtime, List<Vector2f> alliedSpawnPoints,
             List<Vector2f> axisSpawnPoints, int maxScore, long matchTime) {
        this.type = type;
        this.runtime = runtime;
        
        this.alliedSpawnPoints = alliedSpawnPoints;
        this.axisSpawnPoints = axisSpawnPoints;
        
        this.matchTime = matchTime;
        this.maxScore = maxScore;
        this.timeRemaining = this.matchTime;
        
        this.teams = new Team[2];
        this.teams[AXIS] = Team.newAxisTeam();
        this.teams[ALLIED] = Team.newAlliedTeam();
        
        this.highScoreTeams = new ArrayList<Team>(2);
        
        this.gameTypeInfo = createNetGameTypeInfo();
        this.gameTypeInfo.type = type.netValue();
        this.gameTypeInfo.maxScore = maxScore;
        this.gameTypeInfo.maxTime = matchTime;        
        
        this.gameStats = createNetGameStats();
        this.gamePartialStats = createNetGamePartialStats();
        
        this.gameTypeInfo.alliedTeam = new NetTeam();
        this.gameTypeInfo.alliedTeam.id = Team.ALLIED_TEAM_ID;
        
        this.gameTypeInfo.axisTeam = new NetTeam();
        this.gameTypeInfo.axisTeam.id = Team.AXIS_TEAM_ID;
        
        this.gameState = GameState.INTERMISSION;
        this.random = new Random();
        this.spawnBounds = new Rectangle(300, 300);
        
        this.warmupTimer = new Timer(false, WARMUP_TIME);
        
    }
    
    /**
     * For sub classes to override with their own {@link NetGameTypeInfo}
     * if necessary
     * 
     * @return the {@link NetGameTypeInfo}
     */
    protected NetGameTypeInfo createNetGameTypeInfo() {
        return new NetGameTypeInfo();
    }
    
    
    protected NetGameStats createNetGameStats() {
        return new NetGameStats();
    }
    
    protected NetGamePartialStats createNetGamePartialStats() {
        return new NetGamePartialStats();
    }
    
    /* (non-Javadoc)
     * @see seventh.game.type.GameType#registerListeners(seventh.game.GameInfo, leola.frontend.listener.EventDispatcher)
     */
    @Override
    public void registerListeners(final Game game, EventDispatcher dispatcher) {
        this.game = game;
        this.dispatcher = dispatcher;
        
        dispatcher.addEventListener(RoundEndedEvent.class, new RoundEndedListener() {
            @Override
            public void onRoundEnded(RoundEndedEvent event) {
                executeCallbackScript("onRoundEnded", game);
                
                executeCallbackScript("coreDestroy", game);
            }
        });
        
        dispatcher.addEventListener(RoundStartedEvent.class, new RoundStartedListener() {            
            @Override
            public void onRoundStarted(RoundStartedEvent event) {
                executeCallbackScript("onRoundStarted", game);

                executeCallbackScript("coreInit", game);
            }
        });
        
        dispatcher.addEventListener(PlayerKilledEvent.class, new PlayerKilledListener() {
            
            @Override
            public void onPlayerKilled(PlayerKilledEvent event) {
                onPlayerDeath(event);
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
     * Defaults to dropping any held weapons/items
     * 
     * @param event
     */
    protected void onPlayerDeath(PlayerKilledEvent event) {
        final PlayerEntity ent = event.getPlayer().getEntity();
        final Entity killer = event.getKilledBy();
        
        ent.dropFlag();
        
        /* suicides don't leave weapons */
        if(killer != ent) {
            if(ent.isYielding(seventh.game.entities.Entity.Type.FLAME_THROWER)) {
                game.addGameTimer(new Timer(false, 20) {
                    public void onFinish(Timer timer) {
                        game.newBigFire(ent.getCenterPos(), ent, 1);
                        game.newBigExplosion(ent.getCenterPos(), ent, 30, 10, 1);
                    }
                });                
            }
            else {
                ent.dropItem(false);
            }
        } 
    }
    
    /**
     * Executes the callback function
     * 
     * @param functionName
     * @param game
     */
    private void executeCallbackScript(String functionName, Game game) {
        LeoObject function = runtime.get(functionName);
        if(LeoObject.isTrue(function)) {
            LeoObject result = function.call(game.asScriptObject());
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
    
    @Override
    public Leola getRuntime() {     
        return this.runtime;
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
    
    /* (non-Javadoc)
     * @see seventh.game.type.GameType#getAlliedSpawnPoints()
     */
    @Override
    public List<Vector2f> getAlliedSpawnPoints() {
        return this.alliedSpawnPoints;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.type.GameType#getAxisSpawnPoints()
     */
    @Override
    public List<Vector2f> getAxisSpawnPoints() {    
        return this.axisSpawnPoints;
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
        if(spectator.isPureSpectator()||spectator.isCommander()) {
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
        if(spectator.isPureSpectator()||spectator.isCommander()) {
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
    
    /**
     * @return the dispatcher
     */
    protected EventDispatcher getDispatcher() {
        return dispatcher;
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
                    if(!joinTeam(teams[ALLIED], player)) {
                        
                        if(!joinTeam(teams[AXIS], player)) {
                            // TODO Player couldn't join any team??
                        }
                    }
                    
                    break;
                }
                case Team.AXIS_TEAM_ID: {
                    if(!joinTeam(teams[AXIS], player)) {
                        
                        if(!joinTeam(teams[ALLIED], player)) {
                            // TODO Player couldn't join any team??
                        }
                    }
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
            if(teams[ALLIED].getTeamSize() > teams[AXIS].getTeamSize()) {
                teamWithLeast = teams[AXIS];
            }
            else if(teams[ALLIED].getTeamSize() < teams[AXIS].getTeamSize()) {
                teamWithLeast = teams[ALLIED];
            }
            
            if(teamWithLeast==null) {
                teamWithLeast = teams[this.random.nextInt(2)];
            }
            
            if(joinTeam(teamWithLeast, player)) {
                // TODO ??
            }            
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
        this.warmupTimer.update(timeStep);
    
        if(!this.warmupTimer.isExpired()) {
            return GameState.INTERMISSION;
        }
        
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
        
        GameState gameState = doUpdate(game, timeStep);
        checkEndGame(game, gameState);
        
        return gameState;
    }
    
    
    /**
     * Determine if the game has ended, and if it has, distribute a message
     * 
     * @param game
     * @param gameState
     */
    private void checkEndGame(Game game, GameState gameState) {
        if(gameState!=GameState.IN_PROGRESS) {
            if(!gameEnded) {
                this.dispatcher.queueEvent(new GameEndEvent(this, game.getNetGameStats()));            
                this.gameEnded = true;
            }
        }    
    }
    
    
    /**
     * Spawns the player at a random spawn point for their team.
     * 
     * @param player
     * @param game
     */
    protected void spawnPlayer(Player player, Game game) {
        
        List<Vector2f> spawnPoints = (player.getTeamId() == Team.ALLIED_TEAM_ID) ?
                                            getAlliedSpawnPoints() : getAxisSpawnPoints();
        
        Vector2f spawnPosition = findSpawnPosition(player, game, spawnPoints);                                    
        game.spawnPlayerEntity(player.getId(), spawnPosition);
    }
    
    private Vector2f findSpawnPosition(Player player, Game game, List<Vector2f> spawnPoints) {
        Vector2f spawnPosition = new Vector2f(-1,-1);
        int size = spawnPoints.size();
        if(size > 0) {
            int startingPosition = random.nextInt(spawnPoints.size());
            for(int i = 0; i < size; i++) {
                spawnPosition.set(spawnPoints.get(startingPosition));
                if(isSafeSpawnPosition(player, spawnPosition, game)) {
                    return spawnPosition;
                }
                
                startingPosition = (startingPosition + 1) % size;
            }
        }
        return spawnPosition;
    }
    
    /**
     * Determines if the supplied spawn point is safe enough to spawn to.
     * 
     * @param player
     * @param spawnPosition
     * @param game
     * @return true if it's safe
     */
    private boolean isSafeSpawnPosition(Player player, Vector2f spawnPosition, Game game) {
        Team enemy = getEnemyTeam(player);
                
        spawnBounds.centerAround(spawnPosition);
        
        if(enemy!=null) {
            List<Player> players = enemy.getPlayers();
            for(int i = 0; i < players.size(); i++) {
                Player p = players.get(i);
                if(p.isAlive()) {
                    if(spawnBounds.contains(p.getEntity().getBounds())) {
                        return false;
                    }
                }
            }
        }
        
        return true;
    }
    
    /**
     * Check and see if it's time to respawn players
     * 
     * @param timeStep
     * @param game
     */
    protected void checkRespawns(TimeStep timeStep, Game game) {
        if (GameState.IN_PROGRESS == getGameState()) {
            Player[] players = game.getPlayers().getPlayers();
            for (int i = 0; i < players.length; i++) {
                Player player = players[i];
                if (player != null) {
                    if (player.canSpawn()) {
                        player.updateSpawnTime(timeStep);
                        if (player.readyToSpawn()) {                            
                            spawnPlayer(player, game);
                        }
                    }
                }
            }
            
            
        }
    }
    
    /**
     * Checks to see if any players should be spectating, because they are dead
     * 
     * @param timeStep
     * @param game
     */
    protected void checkSpectating(TimeStep timeStep, Game game) {
        Player[] players = game.getPlayers().getPlayers();
        for (int i = 0; i < players.length; i++) {
            Player player = players[i];
            if (player != null) {
                if(player.isCommander()) {
                    continue;
                }
                
                if (!player.isPureSpectator() && !player.isSpectating() && player.isDead()) {
                    player.applyLookAtDeathDelay();
                }

                player.updateLookAtDeathTime(timeStep);

                if (!player.isPureSpectator() && !player.isSpectating() && player.readyToLookAwayFromDeath()) {
                    Player spectateMe = getNextPlayerToSpectate(game.getPlayers(), player);
                    player.setSpectating(spectateMe);
                }
            }

        }
    }
    
    /**
     * Do the actual logic for handling this game type
     * 
     * @param game
     * @param timeStep
     * @return the resulting {@link GameState}
     */
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
    
    
    @Override
    public Team getAttacker() {    
        return getAlliedTeam().isAttacker() ? getAlliedTeam() : getAxisTeam();
    }
    
    @Override
    public Team getDefender() {
        return getAlliedTeam().isDefender() ? getAlliedTeam() : getAxisTeam();
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
    
    @Override
    public Team getEnemyTeam(Player player) {
        Team a = player.getTeam();
        if(a==null) {
            return null;
        }
        
        if(a.getId()==this.teams[0].getId()) {
            return this.teams[1];
        }
        
        return this.teams[0];
    }
    
    protected boolean joinTeam(Team team, Player player) {
        Team otherTeam = team.getId() == Team.ALLIED_TEAM_ID ? getAxisTeam() : getAlliedTeam();
        leaveTeam(otherTeam, player);
        
        team.addPlayer(player);
        return true;
    }
    
    protected boolean leaveTeam(Team team, Player player) {
        team.removePlayer(player);
        return true;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.type.GameType#switchPlayerClass(seventh.game.Player, seventh.game.PlayerClass)
     */
    @Override
    public boolean switchPlayerClass(Player player, PlayerClass playerClass) {
        if(playerClass == PlayerClass.Default) {
            player.setPlayerClass(playerClass);
            return true;
        }
        
        return false;
    }
    
    /* (non-Javadoc)
     * @see palisma.game.type.GameType#switchTeam(palisma.game.Player, byte)
     */
    @Override
    public boolean switchTeam(Player player, byte teamId) {
        boolean assigned = false;
        
        Team currentTeam = getTeam(player);
        if(currentTeam != null && currentTeam.getId() != teamId) {            
            if(leaveTeam(currentTeam, player)) {
                assigned = true;
            }
        }
        
        for(int i = 0; i < this.teams.length; i++) {
            Team team = this.teams[i];
            if(team.getId() == teamId) {                
                if(joinTeam(team, player)) {
                    assigned = true;
                }
            }
        }
        
        return assigned;
    }
    
    
    /* (non-Javadoc)
     * @see palisma.game.type.GameType#getNetGameTypeInfo()
     */
    @Override
    public NetGameTypeInfo getNetGameTypeInfo() {
        this.gameTypeInfo.alliedTeam = this.teams[ALLIED].getNetTeam();
        this.gameTypeInfo.axisTeam = this.teams[AXIS].getNetTeam();
        return this.gameTypeInfo;
    }
    
    /**
     * @return just returns the networked game statistics
     */
    @Override
    public NetGameStats getNetGameStats() {        
        gameStats.playerStats = new NetPlayerStat[game.getPlayers().getNumberOfPlayers()];
        Player[] players = game.getPlayers().getPlayers();
        
        int j = 0;
        for(int i = 0; i < players.length; i++) {
            Player player = players[i];
            if(player != null) {
                gameStats.playerStats[j++] = player.getNetPlayerStat();
            }
        }
        
        gameStats.alliedTeamStats = getAlliedNetTeamStats();
        gameStats.axisTeamStats = getAxisNetTeamStats();
        return gameStats;
    }
    
    /**
     * @return returns the networked game (partial) statistics
     */
    @Override
    public NetGamePartialStats getNetGamePartialStats() {
        gamePartialStats.playerStats = new NetPlayerPartialStat[game.getPlayers().getNumberOfPlayers()];
        Player[] players = game.getPlayers().getPlayers();
        
        int j = 0;
        for(int i = 0; i < players.length; i++) {
            Player player = players[i];
            if(player != null) {
                gamePartialStats.playerStats[j++] = player.getNetPlayerPartialStat();
            }
        }
        
        gamePartialStats.alliedTeamStats = getAlliedNetTeamStats();
        gamePartialStats.axisTeamStats = getAxisNetTeamStats();     
        
        return gamePartialStats;
    }
    

    /* (non-Javadoc)
     * @see seventh.game.type.GameType#getAlliedNetTeamStats()
     */
    @Override
    public NetTeamStat getAlliedNetTeamStats() {
        return this.teams[ALLIED].getNetTeamStats();
    }
    
    /* (non-Javadoc)
     * @see seventh.game.type.GameType#getAxisNetTeamStats()
     */
    @Override
    public NetTeamStat getAxisNetTeamStats() {
        return this.teams[AXIS].getNetTeamStats();
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

