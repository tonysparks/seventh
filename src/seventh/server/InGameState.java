/*
 * see license.txt 
 */
package seventh.server;

import harenet.api.Connection;
import harenet.api.Endpoint;
import harenet.api.Server;

import java.io.File;
import java.io.IOException;

import leola.frontend.listener.EventDispatcher;
import leola.frontend.listener.EventMethod;
import leola.vm.Leola;
import seventh.game.Entity;
import seventh.game.Game;
import seventh.game.GameMap;
import seventh.game.LightBulb;
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
import seventh.game.net.NetGameUpdate;
import seventh.map.Layer;
import seventh.map.Map;
import seventh.map.Tile;
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
import seventh.server.RemoteClients.RemoteClientIterator;
import seventh.shared.Command;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.Scripting;
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
	
	private ServerContext serverContext;
	private GameSession gameSession;
	
	private ServerProtocolListener listener;
	
	private EventDispatcher dispatcher;
	
	private long nextGameStatUpdate, nextGamePartialStatUpdate, gameEndTime;
	private boolean gameEnded;
	
	private Game game;
	private RemoteClients clients;
	
	
	private GameStatsMessage statsMessage;
	private GamePartialStatsMessage partialStatsMessage;
	
	private boolean calculatePing;
	private Players players;
	
	private Server network;
	
	private RemoteClientIterator clientIterator;
	
	/**
	 * @param serverContext
	 * @param gameSession
	 */
	public InGameState(final ServerContext serverContext, 
					   final GameSession gameSession) {
		this.serverContext = serverContext;
		this.gameSession = gameSession;
		
		this.players = gameSession.getPlayers();		
		this.network = serverContext.getServer();
		this.clients = serverContext.getClients();		
		this.listener = serverContext.getServerProtocolListener();
		
		this.dispatcher = gameSession.getEventDispatcher();				
		this.game = gameSession.getGame();
								
		loadProperties(gameSession.getMap(), game);
		
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
								
				listener.queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
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
				
				listener.queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
			}
		});
		
		this.dispatcher.addEventListener(GameEndEvent.class, new GameEndListener() {
			
			@Override
			@EventMethod
			public void onGameEnd(GameEndEvent event) {			
				if(!gameEnded) {
					GameEndedMessage msg = new GameEndedMessage();
					msg.stats = game.getNetGameStats();//event.getStats();
					listener.queueSendToAll(Endpoint.FLAG_RELIABLE, msg);			
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
				
				listener.queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
			}
		});
		
		this.dispatcher.addEventListener(RoundStartedEvent.class, new RoundStartedListener() {
			
			@Override
			@EventMethod
			public void onRoundStarted(RoundStartedEvent event) {
				loadProperties(gameSession.getMap(), game);
				
				RoundStartedMessage msg = new RoundStartedMessage();
				msg.gameState = game.getNetGameState();
				listener.queueSendToAll(Endpoint.FLAG_RELIABLE, msg);	
			}
		});
		
		
		this.dispatcher.addEventListener(BombPlantedEvent.class, new BombPlantedListener() {
			
			@Override
			@EventMethod
			public void onBombPlanted(BombPlantedEvent event) {
				int bombTargetId = event.getBombTarget() != null ? event.getBombTarget().getId() : Entity.INVALID_ENTITY_ID;
				listener.queueSendToAll(Endpoint.FLAG_RELIABLE, new BombPlantedMessage(bombTargetId));
			}
		});
		
		this.dispatcher.addEventListener(BombDisarmedEvent.class, new BombDisarmedListener() {
			@EventMethod
			@Override
			public void onBombDisarmedEvent(BombDisarmedEvent event) {
				int bombTargetId = event.getBombTarget() != null ? event.getBombTarget().getId() : Entity.INVALID_ENTITY_ID;
				listener.queueSendToAll(Endpoint.FLAG_RELIABLE, new BombDisarmedMessage(bombTargetId));				
			}
		});
		
		this.dispatcher.addEventListener(BombExplodedMessage.class, new BombExplodedListener() {
			@EventMethod
			@Override
			public void onBombExplodedEvent(BombExplodedEvent event) {
				listener.queueSendToAll(Endpoint.FLAG_RELIABLE, new BombExplodedMessage());
			}
		});
	}
		
	/**
	 * Load the maps properties file
	 * 
	 * @param mapFile
	 * @param game
	 */
	private void loadProperties(GameMap gameMap, Game game) {		
		File propertiesFile = new File(gameMap.getMapFileName() + ".props.leola");
		if(propertiesFile.exists()) {
			try {	
				Leola runtime = Scripting.newSandboxedRuntime();
				
				runtime.putGlobal("game", game);
				runtime.eval(propertiesFile);
				
				Map map = game.getMap();
				Layer[] layers = map.getBackgroundLayers();
				for(int i = 0; i < layers.length; i++) {
					Layer layer = layers[i];
					if(layer != null) {
						if(layer.isLightLayer()) {
							for(int y = 0; y < map.getTileWorldHeight(); y++) {
								for(int x = 0; x < map.getTileWorldWidth(); x++) {
									Tile tile = layer.getRow(y).get(x);
									if(tile != null) {
										LightBulb light = game.newLight(map.tileToWorld(x, y));
										light.setColor(0.9f, 0.85f, 0.85f);
										light.setLuminacity(0.95f);
									}
								}
							}
						}
					}
				}
			}
			catch(Exception e) {
				Cons.println("*** ERROR -> Loading map properties file: " + propertiesFile.getName() + " -> ");
				Cons.println(e);
			}
		}
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
		this.listener.updateNetwork(timeStep);
		this.game.update(timeStep);
		
		this.dispatcher.processQueue();
		this.listener.postQueuedMessages();
		
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
						conn.send(Endpoint.FLAG_RELIABLE, msg);
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
				network.sendTo(Endpoint.FLAG_UNRELIABLE, updateMessage, clientId);
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
				network.sendToAll(Endpoint.FLAG_UNRELIABLE, partialStatsMessage);				
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
						
			statsMessage.stats = this.game.getNetGameStats();
			
			this.nextGameStatUpdate = GAME_STAT_UPDATE;
			this.nextGamePartialStatUpdate = GAME_PARTIAL_STAT_UPDATE;
								
			try {
				network.sendToAll(Endpoint.FLAG_UNRELIABLE, statsMessage);
				this.calculatePing = true;
			}
			catch(Exception e) {
				Cons.println("*** Error sending game stats to client: " + e);
			}
		
		}
		else {
			this.calculatePing = false;
		}

		return calculatePing;
	}

}
