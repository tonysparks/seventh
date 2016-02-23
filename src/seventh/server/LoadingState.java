/*
 * see license.txt 
 */
package seventh.server;

import leola.vm.Leola;
import seventh.game.GameMap;
import seventh.game.Players;
import seventh.game.type.AbstractGameTypeScript;
import seventh.game.type.GameType;
import seventh.game.type.ObjectiveScript;
import seventh.game.type.TeamDeathMatchScript;
import seventh.map.Map;
import seventh.map.MapLoaderUtil;
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
		
		this.runtime = serverContext.newRuntime();
		
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
		
		Map map = MapLoaderUtil.loadMap(this.runtime, file, false);
		GameMap gameMap = new GameMap(file, "Unknown", map);
		Cons.println("Successfully loaded!");
		return gameMap;			
	}

	/**
	 * Loads a game type
	 * 
	 * @param mapFile
	 * @param script
	 * @return the {@link GameType}
	 * @throws Exception
	 */
	private GameType loadGameType(String mapFile, AbstractGameTypeScript script) throws Exception {		
		ServerSeventhConfig config = this.serverContext.getConfig();
		
		int maxKills = config.getMaxScore();
		long matchTime = config.getMatchTime() * 60L * 1000L;
		
		return script.loadGameType(mapFile, maxKills, matchTime);
	}
	
	private GameType loadTDMGameType(String mapFile) throws Exception {
		return loadGameType(mapFile, new TeamDeathMatchScript(runtime));
	}
	
	private GameType loadObjGameType(String mapFile) throws Exception {
		return loadGameType(mapFile, new ObjectiveScript(runtime));
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
