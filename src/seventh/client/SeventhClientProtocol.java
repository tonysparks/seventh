/*
 * see license.txt 
 */
package seventh.client;

import harenet.api.Connection;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;

import leola.vm.Args;
import leola.vm.Leola;
import leola.vm.types.LeoMap;
import seventh.client.screens.InGameScreen;
import seventh.client.screens.MenuScreen;
import seventh.game.net.NetGameState;
import seventh.game.net.NetMap;
import seventh.map.Map;
import seventh.map.MapLoader;
import seventh.map.TiledMapLoader;
import seventh.network.messages.BombDisarmedMessage;
import seventh.network.messages.BombExplodedMessage;
import seventh.network.messages.BombPlantedMessage;
import seventh.network.messages.ConnectAcceptedMessage;
import seventh.network.messages.GameEndedMessage;
import seventh.network.messages.GamePartialStatsMessage;
import seventh.network.messages.GameReadyMessage;
import seventh.network.messages.GameStatsMessage;
import seventh.network.messages.GameUpdateMessage;
import seventh.network.messages.PlayerConnectedMessage;
import seventh.network.messages.PlayerDisconnectedMessage;
import seventh.network.messages.PlayerKilledMessage;
import seventh.network.messages.PlayerSpawnedMessage;
import seventh.network.messages.PlayerSwitchTeamMessage;
import seventh.network.messages.RconMessage;
import seventh.network.messages.RconTokenMessage;
import seventh.network.messages.RoundEndedMessage;
import seventh.network.messages.RoundStartedMessage;
import seventh.network.messages.TeamTextMessage;
import seventh.network.messages.TextMessage;
import seventh.shared.Cons;

/**
 * The Seventh implementation of the {@link ClientProtocol}
 * 
 * @author Tony
 *
 */
public class SeventhClientProtocol implements ClientProtocol {

	public static interface GameCreationListener {
		public void onGameCreated(ClientGame game);
	}
	
	private GameCreationListener listener;
	private SeventhGame app;
	private Network network;
	
	private java.util.Map<Integer, ClientPlayer> players;
	private int localPlayerId;
	
	private ClientGame game;
		
	/**
	 * 
	 */
	public SeventhClientProtocol(SeventhGame app, Network network, GameCreationListener listener) {
		this.app = app;
		this.network = network;
		this.listener = listener;
		
		this.localPlayerId = -1;
		
		this.players = new HashMap<Integer, ClientPlayer>();
	}
	
	
	public Map loadMap(String mapFile) throws Exception {
		File file = new File(mapFile);
		String contents = loadFileContents(file);
		contents = "return " + contents.replace(":", "->"); /* converts to leola map format */

		Args args = new Args();
		args.setStackSize(1024 * 1024 * 2);
		Leola runtime = new Leola(args);
		
		LeoMap mapData = runtime.eval(contents).as();
		MapLoader mapLoader = new TiledMapLoader();
		Map map = mapLoader.loadMap(mapData, true);
		
		return map;
	}
	
	private String loadFileContents(File file) throws IOException {
		RandomAccessFile raf = new RandomAccessFile(file, "r");
		try {
			StringBuilder sb = new StringBuilder((int)raf.length());
			String line = null;
			do {
				line = raf.readLine();
				if ( line != null) {
					sb.append(line).append("\n");
				}
				
			} while(line != null);
			
			return sb.toString();
		}
		finally {
			raf.close();
		}
		
	}

	
	

	private void applyGameState(NetGameState gameState, int playerId, boolean sendNotification) {
		if(this.localPlayerId < 0) {
			Cons.println("*** Received gameReady message before connectionAccepted message, skipping!");
		}
		else {
		
			NetMap serverMap = gameState.map;
			try {
				Map map = loadMap(serverMap.path);
				
				if(game != null) {
					game.destroy();
				}
				
				game = new ClientGame(app, players, map, playerId);
				game.applyFullGameState(gameState);
				
				loadProperties(serverMap.path, game);
				
				if(listener != null && sendNotification) {
					listener.onGameCreated(game);
				}
				
			} catch (Exception e) {
				Cons.println("*** Unable load the game state: " + e);
				app.setScreen(new MenuScreen(app));
			}
		}
	}
	
	/**
	 * Load the client maps properties file
	 * 
	 * @param mapFile
	 * @param game
	 */
	private void loadProperties(String mapFile, ClientGame game) {		
		File propertiesFile = new File(mapFile + ".client.props.leola");
		if(propertiesFile.exists()) {
			try {
				Args args = new Args();
				args.setBarebones(true);		
				Leola runtime = new Leola(args);
				
				runtime.putGlobal("game", game);
				runtime.eval(propertiesFile);
			}
			catch(Exception e) {
				Cons.println("*** ERROR -> Loading " + propertiesFile.getName() + ":" + e);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see palisma.client.ClientProtocol#connectAccepted(net.Connection, palisma.network.messages.ConnectAcceptedMessage)
	 */
	@Override
	public void connectAccepted(Connection conn, ConnectAcceptedMessage msg) {				
		this.localPlayerId = msg.playerId;
		this.players.put(localPlayerId, new ClientPlayer(app.getConfig().get("name").toString(), localPlayerId));
		
		applyGameState(msg.gameState, localPlayerId, true);
	}

	/* (non-Javadoc)
	 * @see palisma.client.ClientProtocol#gameUpdate(net.Connection, palisma.network.messages.GameUpdateMessage)
	 */
	@Override
	public void gameUpdate(Connection conn, GameUpdateMessage msg) {
		if(game != null) {
			game.applyGameUpdate(msg);
		}
	}

	/* (non-Javadoc)
	 * @see palisma.client.ClientProtocol#gameStats(net.Connection, palisma.network.messages.GameStatsMessage)
	 */
	@Override
	public void gameStats(Connection conn, GameStatsMessage msg) {
		if(game != null) {
			game.applyGameStats(msg.stats);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientProtocol#gamePartialStats(harenet.api.Connection, seventh.network.messages.GamePartialStatsMessage)
	 */
	@Override
	public void gamePartialStats(Connection conn, GamePartialStatsMessage msg) {
		if(game != null) {
			game.applyGamePartialStats(msg.stats);
		}
	}

	/* (non-Javadoc)
	 * @see palisma.client.ClientProtocol#gameEnded(net.Connection, palisma.network.messages.GameEndedMessage)
	 */
	@Override
	public void gameEnded(Connection conn, GameEndedMessage msg) {
		if(game != null) {
			game.gameEnded(msg);
		}
	}

	/* (non-Javadoc)
	 * @see palisma.client.ClientProtocol#gameReady(net.Connection, palisma.network.messages.GameReadyMessage)
	 */
	@Override
	public void gameReady(Connection conn, GameReadyMessage msg) {				
		NetGameState gs = msg.gameState;
		applyGameState(gs, localPlayerId, false);
		app.setScreen(new InGameScreen(app, network, game));
	}

	/* (non-Javadoc)
	 * @see palisma.client.ClientProtocol#playerConnectedMessage(net.Connection, palisma.network.messages.PlayerConnectedMessage)
	 */
	@Override
	public void playerConnectedMessage(Connection conn,
			PlayerConnectedMessage msg) {
		if(this.game != null) {
			this.game.playerConnected(msg);
		}
	}

	/* (non-Javadoc)
	 * @see palisma.client.ClientProtocol#playerDisconnectedMessage(net.Connection, palisma.network.messages.PlayerDisconnectedMessage)
	 */
	@Override
	public void playerDisconnectedMessage(Connection conn,
			PlayerDisconnectedMessage msg) {
		if(this.game != null) {
			this.game.playerDisconnected(msg);
		}
	}

	/* (non-Javadoc)
	 * @see palisma.client.ClientProtocol#textMessage(net.Connection, palisma.network.messages.TextMessage)
	 */
	@Override
	public void textMessage(Connection conn, TextMessage msg) {
		if(game != null) {
			game.textMessage(msg);
		}
	}

	/* (non-Javadoc)
	 * @see palisma.client.ClientProtocol#teamTextMessage(net.Connection, palisma.network.messages.TeamTextMessage)
	 */
	@Override
	public void teamTextMessage(Connection conn, TeamTextMessage msg) {
		if(game!=null) {
			game.teamTextMessage(msg);
		}
	}

	/* (non-Javadoc)
	 * @see palisma.client.ClientProtocol#playerSpawned(net.Connection, palisma.network.messages.PlayerSpawnedMessage)
	 */
	@Override
	public void playerSpawned(Connection conn, PlayerSpawnedMessage msg) {
		if(game != null) {
			game.playerSpawned(msg);
		}
	}

	/* (non-Javadoc)
	 * @see palisma.client.ClientProtocol#playerKilled(net.Connection, palisma.network.messages.PlayerKilledMessage)
	 */
	@Override
	public void playerKilled(Connection conn, PlayerKilledMessage msg) {
		if(game != null) {
			game.playerKilled(msg);
		}
	}

	/* (non-Javadoc)
	 * @see seventh.client.ClientProtocol#playerSwitchedTeamMessage(net.Connection, seventh.network.messages.PlayerSwitchTeamMessage)
	 */
	@Override
	public void playerSwitchedTeamMessage(Connection conn,
			PlayerSwitchTeamMessage msg) {
		if(game!=null) {
			game.playerSwitchedTeam(msg);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientProtocol#roundEnded(net.Connection, seventh.network.messages.RoundEndedMessage)
	 */
	@Override
	public void roundEnded(Connection conn, RoundEndedMessage msg) {
		if(game!=null) {
			game.roundEnded(msg);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientProtocol#roundStarted(net.Connection, seventh.network.messages.RoundStartedMessage)
	 */
	@Override
	public void roundStarted(Connection conn, RoundStartedMessage msg) {
		if(game!=null) {
			game.roundStarted(msg);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientProtocol#bombDefused(net.Connection, seventh.network.messages.BombDefusedMessage)
	 */
	@Override
	public void bombDisarmed(Connection conn, BombDisarmedMessage msg) {
		if(game!=null) {
			game.bombDisarmed(msg);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientProtocol#bombPlanted(net.Connection, seventh.network.messages.BombPlantedMessage)
	 */
	@Override
	public void bombPlanted(Connection conn, BombPlantedMessage msg) {
		if(game!=null) {
			game.bombPlanted(msg);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientProtocol#bombExploded(net.Connection, seventh.network.messages.BombExplodedMessage)
	 */
	@Override
	public void bombExploded(Connection conn, BombExplodedMessage msg) {
		if(game!=null) {
			game.bombExploded(msg);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientProtocol#rconMessage(net.Connection, seventh.network.messages.RconMessage)
	 */
	@Override
	public void rconMessage(Connection conn, RconMessage msg) {
		app.getConsole().println(msg.getCommand());
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.ClientProtocol#rconTokenMessage(net.Connection, seventh.network.messages.RconTokenMessage)
	 */
	@Override
	public void rconTokenMessage(Connection conn, RconTokenMessage msg) {
		if(game!=null) {
			game.setRconToken(msg.getToken());
		}
	}
}
