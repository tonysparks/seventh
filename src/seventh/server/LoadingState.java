/*
 * see license.txt 
 */
package seventh.server;

import java.io.File;

import leola.vm.Leola;
import seventh.game.Game;
import seventh.game.GameMap;
import seventh.game.LightBulb;
import seventh.game.Players;
import seventh.game.type.GameType;
import seventh.game.type.ObjectiveScript;
import seventh.game.type.TeamDeathMatchScript;
import seventh.map.GameLeolaLibrary;
import seventh.map.Layer;
import seventh.map.Map;
import seventh.map.Tile;
import seventh.shared.Cons;
import seventh.shared.Scripting;
import seventh.shared.State;
import seventh.shared.TimeStep;

/**
 * Responsible for loading maps
 * 
 * @author Tony
 *
 */
public class LoadingState implements State {
	
	private ServerContext serverContext;
	private Leola runtime;
	private GameSession gameSession;
	private	Players players;
	private String mapFile;
	
	private GameSessionListener gameSessionListener;
	
	/**
	 * @param serverContext
	 * @param gameSessionListener
	 * @param mapFile
	 */
	public LoadingState(ServerContext serverContext, GameSessionListener gameSessionListener, String mapFile) {
		this.serverContext = serverContext;
		this.gameSessionListener = gameSessionListener;
				
		this.mapFile = mapFile;
		
		this.runtime = serverContext.getRuntime();
		
		/* indicates we haven't successfully loaded the map */
		this.gameSession = null;
	}

	/**
	 * Loads a new {@link Map}
	 * 
	 * @param file
	 * @throws Exception
	 */
	private GameMap loadMap(String file) throws Exception {		
		Cons.println("Loading " + file + " map...");
		
		GameLeolaLibrary gameLib = new GameLeolaLibrary();
		gameLib.init(runtime, runtime.getGlobalNamespace());
		
		Map map = gameLib.loadMap(file, false);
		GameMap gameMap = new GameMap(file, "Unknown", map);
		Cons.println("Successfully loaded!");
		return gameMap;			
	}
	
	private GameType loadTDMGameType(String mapFile) throws Exception {		
		ServerSeventhConfig config = this.serverContext.getConfig();
		
		int maxKills = config.getMaxScore();
		long matchTime = config.getMatchTime();
		
//		Cons.println("Successfully loaded!");
		TeamDeathMatchScript script = new TeamDeathMatchScript(runtime);
		return script.loadGameType(mapFile, maxKills, matchTime);
	}
	
	private GameType loadObjGameType(String mapFile) throws Exception {
		ServerSeventhConfig config = this.serverContext.getConfig();
		
		int maxScore = config.getMaxScore();
		long matchTime = config.getMatchTime();
		
		ObjectiveScript script = new ObjectiveScript(runtime);
		return script.loadGameType(mapFile, maxScore, matchTime);
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
	
	/**
	 * Ends the game
	 */
	private void endGame() {				
		
		if(this.serverContext.hasGameSession()) {
			GameSession session = this.serverContext.getGameSession(); 
			this.gameSessionListener.onGameSessionDestroyed(session);
			
			/* remember which players are still playing */
			this.players = session.getPlayers();
			session.destroy();			
		}
		else {
			this.players = new Players();			
		}			
	}
	
	/* (non-Javadoc)
	 * @see palisma.shared.State#enter()
	 */
	@Override
	public void enter() {
		try {

			endGame();
			
			GameMap gameMap = loadMap(mapFile);
			GameType gameType = null;
			
			switch(this.serverContext.getConfig().getGameType()) {
				case OBJ: {
					gameType = loadObjGameType(mapFile);
					break;
				}
				case TDM: {
					gameType = loadTDMGameType(mapFile);
					break;
				}
			}			
			
			this.gameSession = new GameSession(serverContext.getConfig(), gameMap, gameType, players);		
			Game game = this.gameSession.getGame();
			
			
			loadProperties(gameMap, game);			
		}
		catch(Exception e) {
			Cons.println("*** Unable to load map: " + this.mapFile + " -> " + e);
		}
	}

	/* (non-Javadoc)
	 * @see palisma.shared.State#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		if(this.gameSession != null) {
			Cons.println("Starting the game...");								
			this.gameSessionListener.onGameSessionCreated(gameSession);			
			this.serverContext.getStateMachine().changeState(new InGameState(serverContext, gameSession));
		}
		else {
			this.serverContext.getStateMachine().changeState(new ServerStartState());
		}
	}

	/* (non-Javadoc)
	 * @see palisma.shared.State#exit()
	 */
	@Override
	public void exit() {
	}

}
