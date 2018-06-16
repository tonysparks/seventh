/*
 * see license.txt 
 */
package seventh.game.type.svr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import leola.vm.Leola;
import leola.vm.types.LeoNativeFunction;
import leola.vm.types.LeoObject;
import leola.vm.util.ClassUtil;
import seventh.ai.basic.AILeolaLibrary;
import seventh.ai.basic.Brain;
import seventh.ai.basic.DefaultAISystem;
import seventh.ai.basic.actions.Action;
import seventh.game.Game;
import seventh.game.Player;
import seventh.game.Team;
import seventh.game.entities.PlayerEntity;
import seventh.game.events.PlayerKilledEvent;
import seventh.game.events.PlayerKilledListener;
import seventh.game.events.RoundEndedEvent;
import seventh.game.events.RoundStartedEvent;
import seventh.game.events.GameEvent;
import seventh.game.events.GameEvent.EventType;
import seventh.game.type.AbstractTeamGameType;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.EventDispatcher;
import seventh.shared.SeventhConstants;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public class SurvivalGameType extends AbstractTeamGameType {
    
    private GameState currentGameState;
    private List<Player> availablePlayers;
    private boolean joinAllies;
    private boolean gameTypeInitialized;
    
    private Game game;
    
    private Timer startTimer;
    
    /**
     * @param runtime
     * @param alliedSpawnPoints
     * @param axisSpawnPoints
     * @param maxScore
     * @param matchTime
     */
    public SurvivalGameType(Leola runtime, 
            List<Vector2f> alliedSpawnPoints, 
            List<Vector2f> axisSpawnPoints, 
            int maxScore,
            long matchTime) {
        
        super(Type.SVR, runtime, alliedSpawnPoints, axisSpawnPoints, maxScore, matchTime);
        
        this.availablePlayers = new ArrayList<>();
        this.startTimer = new Timer(false, 5_000).stop();
        
        this.currentGameState = GameState.IN_PROGRESS;
        this.joinAllies = true;
        this.gameTypeInitialized = false;
        
        addMethod("completeMission");
        addMethod("failedMission");
        addMethod("aiCommand", int.class, Action.class);
        addMethod("spawnEnemy", Rectangle.class);
        addMethod("playSound", String.class);
        addMethod("doIn", long.class, LeoObject.class);
    }
    
    private void addMethod(String methodName, Class<?> ... params) {
        LeoNativeFunction func = new LeoNativeFunction(ClassUtil.getMethodByName(SurvivalGameType.class, methodName, params), this);
        runtime.put(func.getMethodName().toString(), func);
    }
    
    public void doIn(long time, LeoObject action) {
        if(game!=null) {
            game.addGameTimer(false, time, action);
        }
    }
    
    public void completeMission() {
        this.currentGameState = GameState.WINNER;
        getAlliedTeam().score(1);
    }
    
    public void failedMission() {
        this.currentGameState = GameState.WINNER;
        getAxisTeam().score(1);
    }
    
    public void aiCommand(int playerId, Action action) {
        DefaultAISystem aiSystem = (DefaultAISystem) game.getAISystem();
        Brain brain = aiSystem.getBrain(playerId);
        if(brain!=null) {
            brain.doAction(action);
        }
    }
    
    public void playSound(String path) {
        if(game!=null) {            
            game.getDispatcher().queueEvent(new GameEvent(this, EventType.CustomSound, null, null, 0f, path, 0, 0, null));
        }
    }
    
    public int spawnEnemy(Rectangle bounds) {
        Vector2f spawn = game.findFreeRandomSpot(new Rectangle(SeventhConstants.PLAYER_WIDTH, SeventhConstants.PLAYER_HEIGHT), bounds);
        return spawnEnemy(spawn.x, spawn.y);
    }
    
    public int spawnEnemy(float x, float y) {
        Player player = null;
        Iterator<Player> it = this.availablePlayers.iterator();
        while(it.hasNext()) {
            player = it.next();
            it.remove();
            break;
        }
        
        if(player != null) {
            PlayerEntity entity = game.spawnPlayerEntity(player.getId(), new Vector2f(x, y));
            if(entity != null) {
                return player.getId();
            }
            
        }
        
        return -1;
    }
    
    
    
    @Override
    public void playerJoin(Player player) {
        player.commitSuicide();             
        player.resetStats();
        
        if(!gameTypeInitialized) {
            int teamId = player.getTeamId();
            if(teamId == Team.ALLIED_TEAM_ID) {
                joinTeam(getAlliedTeam(), player);
            }
            else if(teamId == Team.AXIS_TEAM_ID) {
                joinTeam(getAxisTeam(), player);
            }
            else {
                joinTeam(getAlliedTeam(), player);
            }
        }
        else {
            if(!joinAllies && player.isBot()) {
                joinTeam(getAxisTeam(), player);
            }
            else {
                joinTeam(getAlliedTeam(), player);
            }
        }
    }

    @Override
    public void start(final Game game) {
        this.game = game;
        
        
        AILeolaLibrary aiLib = new AILeolaLibrary(game.getAISystem());
        this.runtime.loadLibrary(aiLib, "ai");
        

        this.startTimer.start();
    }
    
    private void initializeGame(Game game) {
        this.gameTypeInitialized = true;
        
        game.killAll();
        
        final int maxAlliedTeamSize = 0;
        // spawn allied bots first
        for(int i = getAlliedTeam().getTeamSize(); i < maxAlliedTeamSize; i++) {
            int id = game.addBot("[b] Allied Soldier");
            if(id > -1) {
                game.playerSwitchedTeam(id, Team.ALLIED_TEAM_ID);
            }
        }
        
        for(Player player : getAlliedTeam().getPlayers()) {
            super.spawnPlayer(player, game);
        }
        
        this.joinAllies = false;
        
        for(int i = getAxisTeam().getTeamSize(); i < SeventhConstants.MAX_PLAYERS - maxAlliedTeamSize; i++) {
            int id = game.addBot("[b] Axis Soldier");
            if(id > -1) {
                game.playerSwitchedTeam(id, Team.AXIS_TEAM_ID);
            }
        }                
    }
    
    @Override
    protected void spawnPlayer(Player player, Game game) {
        // don't do anything, spawning of players is handled
        // by scripts
    }

    @Override
    protected void doRegisterListeners(final Game game, EventDispatcher dispatcher) {
        dispatcher.addEventListener(PlayerKilledEvent.class, new PlayerKilledListener() {
            
            @Override
            public void onPlayerKilled(PlayerKilledEvent event) {
                Player player = event.getPlayer();
                if(!availablePlayers.contains(player) && player.getTeamId() == Team.AXIS_TEAM_ID) {
                    availablePlayers.add(player);
                }
                
                if(getAlliedTeam().isTeamDead()) {
                    //failedMission();
                    getDispatcher().queueEvent(new RoundEndedEvent(this, getAxisTeam(), game.getNetGameStats()));
                    startTimer.reset().start();
                }
            }
        });
    }

    @Override
    protected GameState doUpdate(Game game, TimeStep timeStep) {
        this.startTimer.update(timeStep);
        if(this.startTimer.isOnFirstTime()) {
            this.availablePlayers.clear();
            
            initializeGame(game);
            
            for(Player player : getAxisTeam().getPlayers()) {                
                this.availablePlayers.add(player);
            }
            
            for(Player player : getAlliedTeam().getPlayers()) {
                super.spawnPlayer(player, game);
            }
            
            getDispatcher().queueEvent(new RoundStartedEvent(this));
        }
        
        return this.currentGameState;
    }

}
