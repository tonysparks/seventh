/*
 * see license.txt 
 */
package seventh.server;

import java.io.IOException;

import harenet.api.Connection;
import seventh.game.Game;
import seventh.game.Player;
import seventh.game.PlayerAwardSystem.Award;
import seventh.game.Players;
import seventh.game.Players.PlayerIterator;
import seventh.game.Team;
import seventh.game.entities.Entity;
import seventh.game.events.BombDisarmedEvent;
import seventh.game.events.BombDisarmedListener;
import seventh.game.events.BombExplodedEvent;
import seventh.game.events.BombExplodedListener;
import seventh.game.events.BombPlantedEvent;
import seventh.game.events.BombPlantedListener;
import seventh.game.events.FlagCapturedEvent;
import seventh.game.events.FlagCapturedListener;
import seventh.game.events.FlagReturnedEvent;
import seventh.game.events.FlagReturnedListener;
import seventh.game.events.FlagStolenEvent;
import seventh.game.events.FlagStolenListener;
import seventh.game.events.GameEndEvent;
import seventh.game.events.GameEndListener;
import seventh.game.events.KillRollEvent;
import seventh.game.events.KillRollListener;
import seventh.game.events.KillStreakEvent;
import seventh.game.events.KillStreakListener;
import seventh.game.events.PlayerAwardEvent;
import seventh.game.events.PlayerAwardListener;
import seventh.game.events.PlayerKilledEvent;
import seventh.game.events.PlayerKilledListener;
import seventh.game.events.PlayerSpawnedEvent;
import seventh.game.events.PlayerSpawnedListener;
import seventh.game.events.RoundEndedEvent;
import seventh.game.events.RoundEndedListener;
import seventh.game.events.RoundStartedEvent;
import seventh.game.events.RoundStartedListener;
import seventh.game.events.SurvivorEvent;
import seventh.game.events.SurvivorEventListener;
import seventh.game.events.TileAddedEvent;
import seventh.game.events.TileAddedListener;
import seventh.game.events.TileRemovedEvent;
import seventh.game.events.TileRemovedListener;
import seventh.game.net.NetGameUpdate;
import seventh.game.net.NetMapAddition;
import seventh.network.messages.BombDisarmedMessage;
import seventh.network.messages.BombExplodedMessage;
import seventh.network.messages.BombPlantedMessage;
import seventh.network.messages.FlagCapturedMessage;
import seventh.network.messages.FlagReturnedMessage;
import seventh.network.messages.FlagStolenMessage;
import seventh.network.messages.GameEndedMessage;
import seventh.network.messages.GamePartialStatsMessage;
import seventh.network.messages.GameReadyMessage;
import seventh.network.messages.GameStatsMessage;
import seventh.network.messages.GameUpdateMessage;
import seventh.network.messages.PlayerAwardMessage;
import seventh.network.messages.PlayerKilledMessage;
import seventh.network.messages.PlayerSpawnedMessage;
import seventh.network.messages.RoundEndedMessage;
import seventh.network.messages.RoundStartedMessage;
import seventh.network.messages.SurvivorEventMessage;
import seventh.network.messages.TileAddedMessage;
import seventh.network.messages.TileRemovedMessage;
import seventh.server.RemoteClients.RemoteClientIterator;
import seventh.shared.Command;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.EventDispatcher;
import seventh.shared.EventMethod;
import seventh.shared.State;
import seventh.shared.TimeStep;



/**
 * Represents the state in which the game is active.
 * 
 * @author Tony
 *
 */
public class InGameState implements State {
    
    /**
     * When a game ends, lets have a time delay to let the
     * score display and players to calm down from the carnage
     */
    private static final long GAME_END_DELAY = 20_000;
    
    private Game game;
    private GameSession gameSession;
    private ServerContext serverContext;
    
    private EventDispatcher dispatcher;
    
    private final long netFullStatDelay;
    private final long netPartialStatDelay;
    private final long netUpdateRate;
    
    private long nextGameStatUpdate;
    private long nextGamePartialStatUpdate;
    private long nextGameUpdate;
    private long gameEndTime;

    private boolean gameEnded;
    private boolean calculatePing;
    
    private GameStatsMessage statsMessage;
    private GamePartialStatsMessage partialStatsMessage;
    
    
    private Players players;
    private RemoteClients clients;
    private RemoteClientIterator clientIterator;
    
    private ServerNetworkProtocol protocol;

    /**
     * @param serverContext
     * @param gameSession
     */
    public InGameState(final ServerContext serverContext, 
                       final GameSession gameSession) {
        this.serverContext = serverContext;
        this.gameSession = gameSession;
        
        this.players = gameSession.getPlayers();        
        this.clients = serverContext.getClients();        
        this.protocol = serverContext.getServerProtocol();
        
        this.dispatcher = gameSession.getEventDispatcher();                
        this.game = gameSession.getGame();
                                
        ServerSeventhConfig config = serverContext.getConfig();
        this.netFullStatDelay = config.getServerNetFullStatDelay();
        this.netPartialStatDelay = config.getServerNetPartialStatDelay();
        
        final long netRate = Math.abs(config.getServerNetUpdateRate());        
        this.netUpdateRate = 1000 / netRate == 0 ? 20 : netRate;
        
        this.nextGameStatUpdate = 2_000; // first big update, wait only 2 seconds
        this.nextGamePartialStatUpdate = this.netPartialStatDelay;
        this.nextGameUpdate = this.netUpdateRate;
                
        this.statsMessage = new GameStatsMessage();
        this.partialStatsMessage = new GamePartialStatsMessage();
        
        this.clientIterator = new RemoteClientIterator() {
            
            @Override
            public void onRemoteClient(RemoteClient client) {
                if(client.isReady()) {            
                    sendGameUpdateMessage(client.getId());
                }
                
                if(calculatePing) {
                    int ping = client.getConnection().getReturnTripTime();
                    client.getPlayer().setPing(ping);
                }                
            }
        };
        
        this.dispatcher.addEventListener(PlayerKilledEvent.class, new PlayerKilledListener() {
            
            @Override
            @EventMethod
            public void onPlayerKilled(PlayerKilledEvent event) {
                PlayerKilledMessage msg = new PlayerKilledMessage();
                                
                msg.deathType = event.getMeansOfDeath();
                msg.killedById = event.getKillerId();
                msg.playerId = event.getPlayer().getId();
                msg.posX = (short)event.getPos().x;
                msg.posY = (short)event.getPos().y;
                                                
                protocol.sendPlayerKilledMessage(msg);
            }
        });
            
        this.dispatcher.addEventListener(PlayerSpawnedEvent.class, new PlayerSpawnedListener() {
             
            @Override
            @EventMethod
            public void onPlayerSpawned(PlayerSpawnedEvent event) {
                PlayerSpawnedMessage msg = new PlayerSpawnedMessage();
                
                Player player = event.getPlayer();
                msg.playerId = player.getId();
                msg.posX = (short)event.getSpawnLocation().x;
                msg.posY = (short)event.getSpawnLocation().y;                                                
                
                protocol.sendPlayerSpawnedMessage(msg);
            }
        });
        
        this.dispatcher.addEventListener(GameEndEvent.class, new GameEndListener() {
            
            @Override
            @EventMethod
            public void onGameEnd(GameEndEvent event) {            
                if(!gameEnded) {
                    GameEndedMessage msg = new GameEndedMessage();
                    msg.stats = game.getNetGameStats();
                    protocol.sendGameEndedMessage(msg);            
                    gameEnded = true;                    
                }
            }
        });
        
        this.dispatcher.addEventListener(RoundEndedEvent.class, new RoundEndedListener() {
            
            @Override
            @EventMethod
            public void onRoundEnded(RoundEndedEvent event) {
                RoundEndedMessage msg = new RoundEndedMessage();
                msg.stats = game.getNetGameStats();//event.getStats();
                
                Team winner = event.getWinner();
                if(winner != null) {
                    msg.winnerTeamId = winner.getId();
                }
                
                protocol.sendRoundEndedMessage(msg);
            }
        });
        
        this.dispatcher.addEventListener(RoundStartedEvent.class, new RoundStartedListener() {
            
            @Override
            @EventMethod
            public void onRoundStarted(RoundStartedEvent event) {                                                
                RoundStartedMessage msg = new RoundStartedMessage();
                msg.gameState = game.getNetGameState();
                protocol.sendRoundStartedMessage(msg);    
            }
        });
        
        
        this.dispatcher.addEventListener(BombPlantedEvent.class, new BombPlantedListener() {
            
            @Override
            @EventMethod
            public void onBombPlanted(BombPlantedEvent event) {
                int bombTargetId = event.getBombTarget() != null ? event.getBombTarget().getId() : Entity.INVALID_ENTITY_ID;
                protocol.sendBombPlantedMessage(new BombPlantedMessage(bombTargetId));
            }
        });
        
        this.dispatcher.addEventListener(BombDisarmedEvent.class, new BombDisarmedListener() {
            @EventMethod
            @Override
            public void onBombDisarmedEvent(BombDisarmedEvent event) {
                int bombTargetId = event.getBombTarget() != null ? event.getBombTarget().getId() : Entity.INVALID_ENTITY_ID;
                protocol.sendBombDisarmedMessage(new BombDisarmedMessage(bombTargetId));                
            }
        });
        
        this.dispatcher.addEventListener(BombExplodedMessage.class, new BombExplodedListener() {
            @EventMethod
            @Override
            public void onBombExplodedEvent(BombExplodedEvent event) {
                protocol.sendBombExplodedMessage(new BombExplodedMessage());
            }
        });
        
        this.dispatcher.addEventListener(TileRemovedEvent.class, new TileRemovedListener() {
            
            @Override
            public void onTileRemoved(TileRemovedEvent event) {
                TileRemovedMessage msg = new TileRemovedMessage();
                msg.x = event.getTileX();
                msg.y = event.getTileY();
                protocol.sendTileRemovedMessage(msg);
            }
        });
        
        this.dispatcher.addEventListener(TileAddedEvent.class, new TileAddedListener() {
            
            @Override
            public void onTileAdded(TileAddedEvent event) {
                TileAddedMessage msg = new TileAddedMessage();
                msg.tile = new NetMapAddition(event.getTileX(), event.getTileY(), event.getType());
                
                protocol.sendTileAddedMessage(msg);
            }
        });
        
        
        
        this.dispatcher.addEventListener(FlagCapturedEvent.class, new FlagCapturedListener() {
            
            @Override
            public void onFlagCapturedEvent(FlagCapturedEvent event) {
                FlagCapturedMessage msg = new FlagCapturedMessage();
                msg.flagId = event.getFlag().getId();
                msg.capturedBy = event.getPlayerId();
                protocol.sendFlagCapturedMessage(msg);
            }
        });
        
        this.dispatcher.addEventListener(FlagReturnedEvent.class, new FlagReturnedListener() {
            
            @Override
            public void onFlagReturnedEvent(FlagReturnedEvent event) {
                FlagReturnedMessage msg = new FlagReturnedMessage();
                msg.flagId = event.getFlag().getId();
                msg.returnedBy = event.getPlayerId();
                protocol.sendFlagReturnedMessage(msg);
            }
        });
        
        this.dispatcher.addEventListener(FlagStolenEvent.class, new FlagStolenListener() {
            
            @Override
            public void onFlagStolenEvent(FlagStolenEvent event) {
                FlagStolenMessage msg = new FlagStolenMessage();
                msg.flagId = event.getFlag().getId();
                msg.stolenBy = event.getPlayerId();
                protocol.sendFlagStolenMessage(msg);
            }
        });
        
        this.dispatcher.addEventListener(KillStreakEvent.class, new KillStreakListener() {
            
            @Override
            public void onKillStreak(KillStreakEvent event) {
                PlayerAwardMessage msg = new PlayerAwardMessage();
                msg.playerId = event.getPlayer().getId();
                msg.award = Award.KillStreak;
                msg.killStreak = (byte) event.getStreak();
                protocol.sendPlayerAwardMessage(msg);
            }
        });
        
        this.dispatcher.addEventListener(KillRollEvent.class, new KillRollListener() {
            
            @Override
            public void onKillRoll(KillRollEvent event) {
                PlayerAwardMessage msg = new PlayerAwardMessage();
                msg.playerId = event.getPlayer().getId();
                msg.award = Award.KillRoll;
                msg.killStreak = (byte) event.getStreak();
                protocol.sendPlayerAwardMessage(msg);
            }
        });
        
        this.dispatcher.addEventListener(PlayerAwardEvent.class, new PlayerAwardListener() {
            
            @Override
            public void onPlayerAward(PlayerAwardEvent event) {
                PlayerAwardMessage msg = new PlayerAwardMessage();
                msg.playerId = event.getPlayer().getId();
                msg.award = event.getAward();                
                protocol.sendPlayerAwardMessage(msg);
            }
        });
        
        this.dispatcher.addEventListener(SurvivorEvent.class, new SurvivorEventListener() {
            
            @Override
            public void onSurvivorEvent(SurvivorEvent event) {
                SurvivorEventMessage msg = new SurvivorEventMessage();
                msg.eventType = event.getEventType();
                msg.path = event.getPath();
                msg.pos = event.getPos();
                msg.playerId1 = event.getPlayerId1();
                msg.playerId2 = event.getPlayerId2();
                msg.light = event.getLight();
                
                protocol.sendSurvivoEventrMessage(msg);                
            }
        });
    }
        
    
    /* (non-Javadoc)
     * @see palisma.shared.State#enter()
     */
    @Override
    public void enter() {            
        this.serverContext.getConsole().addCommand(new Command("sv_fow") {
            
            @Override
            public void execute(Console console, String... args) {
                if(args.length > 0) {
                    int enabled = Integer.parseInt(args[0]);
                    game.enableFOW(enabled != 0);
                }
                
                console.println("sv_fow: " + (game.isEnableFOW() ? 1 : 0));                
            }
        });
        
        this.gameEnded = false;
        this.gameEndTime = 0;
                        
        this.players.forEachPlayer(new PlayerIterator() {
            
            @Override
            public void onPlayer(Player player) {
                game.playerJoined(player);
                
            }
        });
        
        this.game.startGame();
        
        sendReadyMessage();
        
        Cons.println("Server InGameState initialiazed and ready for players");
    }

    /* (non-Javadoc)
     * @see palisma.shared.State#exit()
     */
    @Override
    public void exit() {        
        this.gameSession.destroy();        
        this.serverContext.getConsole().removeCommand("sv_fow");
    }
    
    /* (non-Javadoc)
     * @see palisma.shared.State#update(leola.live.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {                
        this.protocol.updateNetwork(timeStep);
        this.game.update(timeStep);
        
        this.dispatcher.processQueue();
        this.protocol.postQueuedMessages();
        
        /* only send a partial update if we did NOT send
         * a full update
         */
        if ( ! sendGameStatMessage(timeStep) ) {
            sendGamePartialStatMessage(timeStep);
        }
        
        sendClientGameUpdates(timeStep);
        
        this.game.postUpdate();
        
        // check for game end
        if(gameEnded) {
            if(gameEndTime > GAME_END_DELAY) {    
                /* load up the next level */
                serverContext.spawnGameSession();                
            }
            
            gameEndTime += timeStep.getDeltaTime();
        }
        
        if(this.serverContext.hasDebugListener()) {
            this.serverContext.getDebugableListener().onDebugable(this.game);
        }
    }
    
    private void sendClientGameUpdates(TimeStep timeStep) {
        this.nextGameUpdate -= timeStep.getDeltaTime();        
        if(this.nextGameUpdate <= 0) {
            this.clients.foreach(this.clientIterator);
            
            this.nextGameUpdate = this.netUpdateRate;
        }
    }
    
    /**
     * Send out a message to all clients indicating that
     * the game is now ready for play
     * 
     */
    private void sendReadyMessage() {
        GameReadyMessage msg = new GameReadyMessage();
        msg.gameState = this.game.getNetGameState();
        
        for(Connection conn : serverContext.getServer().getConnections()) {
            try {
                if(conn != null) {
                    RemoteClient client = clients.getClient(conn.getId());
                    if(client != null && client.isReady()) {
                        protocol.sendGameReadyMessage(msg, client.getId());
                    }
                }
            } 
            catch (IOException e) {
                Cons.println("Failed to send game ready state message - " + e );    
            }
        }
    }
    
    /**
     * Sends a game update to the client
     * 
     * @param clientId
     */
    private void sendGameUpdateMessage(int clientId) {
        NetGameUpdate netUpdate = this.game.getNetGameUpdateFor(clientId);
        if(netUpdate != null) {
            
            GameUpdateMessage updateMessage = new GameUpdateMessage();
            updateMessage.netUpdate = netUpdate;
                        
            try {
                protocol.sendGameUpdateMessage(updateMessage, clientId);
            }
            catch(Exception e) {
                Cons.println("*** Error sending game update to client: " + e);
            }
        }
    }
    
    /**
     * Sends a partial stat update
     * @param timeStep
     */
    private void sendGamePartialStatMessage(TimeStep timeStep) {
        nextGamePartialStatUpdate -= timeStep.getDeltaTime();        
        if(nextGamePartialStatUpdate <= 0) {
            
            partialStatsMessage.stats = this.game.getNetGamePartialStats();            
            this.nextGamePartialStatUpdate = this.netPartialStatDelay;
                                
            try {
                protocol.sendGamePartialStatsMessage(partialStatsMessage);
            }
            catch(Exception e) {
                Cons.println("*** Error sending game stats to client: " + e);
            }
        
        }        
    }
    
    /**
     * Sends a full game stat update.
     * 
     * @param timeStep
     * @return true if an update was sent
     */
    private boolean sendGameStatMessage(TimeStep timeStep) {
        this.nextGameStatUpdate -= timeStep.getDeltaTime();        
        if(this.nextGameStatUpdate <= 0) {
                        
            this.statsMessage.stats = this.game.getNetGameStats();
        
            this.nextGameStatUpdate = this.netFullStatDelay;
            this.nextGamePartialStatUpdate = this.netPartialStatDelay;
                        
            try {
                this.protocol.sendGameStatsMessage(this.statsMessage);
                this.calculatePing = true;
            }
            catch(Exception e) {
                Cons.println("*** Error sending game stats to client: " + e);
            }
        
        }
        else {
            this.calculatePing = false;
        }

        return this.calculatePing;
    }

}
