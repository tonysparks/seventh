/*
 * see license.txt 
 */
package seventh.server;

import harenet.api.Connection;

import java.io.IOException;

import leola.frontend.listener.EventDispatcher;
import leola.frontend.listener.EventMethod;
import seventh.game.Entity;
import seventh.game.Game;
import seventh.game.Player;
import seventh.game.Players;
import seventh.game.Players.PlayerIterator;
import seventh.game.Team;
import seventh.game.events.BombDisarmedEvent;
import seventh.game.events.BombDisarmedListener;
import seventh.game.events.BombExplodedEvent;
import seventh.game.events.BombExplodedListener;
import seventh.game.events.BombPlantedEvent;
import seventh.game.events.BombPlantedListener;
import seventh.game.events.GameEndEvent;
import seventh.game.events.GameEndListener;
import seventh.game.events.PlayerKilledEvent;
import seventh.game.events.PlayerKilledListener;
import seventh.game.events.PlayerSpawnedEvent;
import seventh.game.events.PlayerSpawnedListener;
import seventh.game.events.RoundEndedEvent;
import seventh.game.events.RoundEndedListener;
import seventh.game.events.RoundStartedEvent;
import seventh.game.events.RoundStartedListener;
import seventh.game.events.TileRemovedEvent;
import seventh.game.events.TileRemovedListener;
import seventh.game.net.NetGameUpdate;
import seventh.network.messages.BombDisarmedMessage;
import seventh.network.messages.BombExplodedMessage;
import seventh.network.messages.BombPlantedMessage;
import seventh.network.messages.GameEndedMessage;
import seventh.network.messages.GamePartialStatsMessage;
import seventh.network.messages.GameReadyMessage;
import seventh.network.messages.GameStatsMessage;
import seventh.network.messages.GameUpdateMessage;
import seventh.network.messages.PlayerKilledMessage;
import seventh.network.messages.PlayerSpawnedMessage;
import seventh.network.messages.RoundEndedMessage;
import seventh.network.messages.RoundStartedMessage;
import seventh.network.messages.TileRemovedMessage;
import seventh.server.RemoteClients.RemoteClientIterator;
import seventh.shared.Command;
import seventh.shared.Cons;
import seventh.shared.Console;
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
	 * Send out game stat updates every 20 seconds
	 */
	private static final long GAME_STAT_UPDATE = 20000;
	private static final long GAME_PARTIAL_STAT_UPDATE = 5000;
	
	/**
	 * When a game ends, lets have a time delay to let the
	 * score display and players to calm down from the carnage
	 */
	private static final long GAME_END_DELAY = 20000;
	
	private Game game;
	private GameSession gameSession;
	private ServerContext serverContext;
	
	private EventDispatcher dispatcher;
	
	private long nextGameStatUpdate;
	private long nextGamePartialStatUpdate;
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
								
		//loadProperties(gameSession.getMap(), game);
		
		this.nextGameStatUpdate = GAME_STAT_UPDATE;
		this.nextGamePartialStatUpdate = GAME_PARTIAL_STAT_UPDATE;
				
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
								
				msg.deathType = event.getMeansOfDeath().netValue();
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
				if(winner!=null) {
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
	}
		
	
	/* (non-Javadoc)
	 * @see palisma.shared.State#enter()
	 */
	@Override
	public void enter() {			
		this.serverContext.getConsole().addCommand(new Command("sv_fow") {
			
			@Override
			public void execute(Console console, String... args) {
				if(args.length>0) {
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
		
		this.clients.foreach(this.clientIterator);
		
		this.game.postUpdate();
		
		// check for game end
		if(gameEnded) {
			if(gameEndTime>GAME_END_DELAY) {	
				/* load up the next level */
				serverContext.spawnGameSession();				
			}
			
			gameEndTime += timeStep.getDeltaTime();
		}
		
		if(this.serverContext.hasDebugListener()) {
			this.serverContext.getDebugableListener().onDebugable(this.game);
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
						// TODO is this a correct replacement? 
					    // conn.send(Endpoint.FLAG_RELIABLE, msg);
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
			nextGamePartialStatUpdate = GAME_PARTIAL_STAT_UPDATE;
								
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
			
			this.nextGameStatUpdate = GAME_STAT_UPDATE;
			this.nextGamePartialStatUpdate = GAME_PARTIAL_STAT_UPDATE;
								
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
