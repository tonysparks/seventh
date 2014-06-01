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
import leola.vm.Args;
import leola.vm.Args.ArgsBuilder;
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
import seventh.game.type.GameType;
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
import seventh.shared.Command;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.State;
import seventh.shared.TimeStep;



/**
 * @author Tony
 *
 */
public class InGameState implements State {

	private Game game;
	private java.util.Map<Integer, RemoteClient> clients;
	
	private GameServer server;
	private ServerProtocolListener listener;
	
	private EventDispatcher dispatcher;
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
	private long nextGameStatUpdate, nextGamePartialStatUpdate, gameEndTime;
	private boolean gameEnded;
		
	private GameStatsMessage statsMessage;
	private GamePartialStatsMessage partialStatsMessage;
	
	private boolean calculatePing;
	private Players players;
	
	private Server network;
	
	/**
	 * 
	 */
	public InGameState(Players players, GameMap map, GameType gameType, GameServer server) {
		this.players = players;
		this.server = server;
		this.network = server.getServer();
		this.clients = server.getClients();
		
		this.dispatcher = new EventDispatcher();				
		this.game = new Game(server.getConfig(), players, gameType, map, dispatcher);
						
		this.listener = this.server.getProtocolListener();
		this.listener.setGame(this.game);
		
		loadProperties(map.getMapFileName(), game);
		
		this.nextGameStatUpdate = GAME_STAT_UPDATE;
		this.nextGamePartialStatUpdate = GAME_PARTIAL_STAT_UPDATE;
				
		this.statsMessage = new GameStatsMessage();
		this.partialStatsMessage = new GamePartialStatsMessage();
		
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
	private void loadProperties(String mapFile, Game game) {		
		File propertiesFile = new File(mapFile + ".props.leola");
		if(propertiesFile.exists()) {
			try {
				Args args = new ArgsBuilder().setAllowThreadLocals(false)
											 .setBarebones(true).build();		
				Leola runtime = new Leola(args);
				
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
	
	
	
	private void gameReady() {
		GameReadyMessage msg = new GameReadyMessage();
		msg.gameState = this.game.getNetGameState();
		sendReadyMessage(msg);	
	}
	
	private void sendReadyMessage(GameReadyMessage msg) {
		Server s = server.getServer();
		for(Connection conn : s.getConnections()) {
			try {
				if(conn != null) {
					RemoteClient client = server.getClients().get(conn.getId());
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

	/* (non-Javadoc)
	 * @see palisma.shared.State#enter()
	 */
	@Override
	public void enter() {			
		server.getConsole().addCommand(new Command("sv_fow") {
			
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
						
		players.forEachPlayer(new PlayerIterator() {
			
			@Override
			public void onPlayer(Player player) {
				game.playerJoined(player);
				
			}
		});
		
		this.game.startGame();
		
		gameReady();
	}

	/* (non-Javadoc)
	 * @see palisma.shared.State#exit()
	 */
	@Override
	public void exit() {
		this.game.destroy();
		this.dispatcher.removeAllEventListeners();
		this.dispatcher.clearQueue();
		
		server.getConsole().removeCommand("sv_fow");
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
		
		for(RemoteClient c : this.clients.values()) {
			if(c.isReady()) {			
				sendGameUpdateMessage(c.getId());
			}
			
			if(calculatePing) {
				int ping = c.getConnection().getReturnTripTime();
				c.getPlayer().setPing(ping);
			}
		}

		this.game.postUpdate();
		
		// check for game end
		if(gameEnded) {
			if(gameEndTime>GAME_END_DELAY) {				
				server.getSm().changeState(new LoadingState(server, server.getMapCycle().getNextMap()));
			}
			
			gameEndTime += timeStep.getDeltaTime();
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
