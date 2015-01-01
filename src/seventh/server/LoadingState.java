/*
 * see license.txt 
 */
package seventh.server;

import leola.vm.Leola;
import seventh.game.GameMap;
import seventh.game.Players;
import seventh.game.type.GameType;
import seventh.game.type.ObjectiveScript;
import seventh.game.type.TeamDeathMatchScript;
import seventh.map.GameLeolaLibrary;
import seventh.map.Map;
import seventh.shared.Cons;
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
