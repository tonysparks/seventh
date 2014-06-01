/*
 * see license.txt 
 */
package seventh.server;

import leola.vm.Leola;
import seventh.game.Game;
import seventh.game.GameMap;
import seventh.game.Players;
import seventh.game.type.GameType;
import seventh.game.type.ObjectiveScript;
import seventh.game.type.TeamDeathMatchScript;
import seventh.map.GameLeolaLibrary;
import seventh.map.Map;
import seventh.shared.Cons;
import seventh.shared.State;
import seventh.shared.StateMachine;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class LoadingState implements State {
	
	private GameMap map;
	private GameType gameType;
	
	private	Players players;
	
	private Leola runtime;
	private String mapFile;
	private StateMachine<State> sm;
		
	private GameServer server;
	
	private boolean successfullyLoaded;
	/**
	 * 
	 */
	public LoadingState(GameServer server, String mapFile) {
		this.server = server;
		
		this.sm = server.getSm();
		this.runtime = server.getRuntime();
				
		this.mapFile = mapFile;
		this.successfullyLoaded = false;				
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
		int maxKills = server.getMaxScore();
		long matchTime = server.getMatchTime();
		
//		Cons.println("Successfully loaded!");
		TeamDeathMatchScript script = new TeamDeathMatchScript(runtime);
		return script.loadGameType(mapFile, maxKills, matchTime);
	}
	
	private GameType loadObjGameType(String mapFile) throws Exception {
		int maxScore = server.getMaxScore();
		long matchTime = server.getMatchTime();
		
		ObjectiveScript script = new ObjectiveScript(runtime);
		return script.loadGameType(mapFile, maxScore, matchTime);
	}
	
	/**
	 * Ends the game
	 */
	private void endGame() {				
		Game game = this.server.getProtocolListener().getGame();
		if(game != null) {								
			/* remember which players are still playing */
			this.players = game.getPlayers();
			game.destroy();
			
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
			
			this.map = loadMap(mapFile);
			switch(server.getGameType()) {
				case OBJ: {
					this.gameType = loadObjGameType(mapFile);
					break;
				}
				case TDM: {
					this.gameType = loadTDMGameType(mapFile);
					break;
				}
			}			
								
			this.successfullyLoaded = true;
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
		if(successfullyLoaded) {
			Cons.println("Starting the game...");
			sm.changeState(new InGameState(players, map, gameType, server));
		}
		else {
			sm.changeState(new ServerStartState());
		}
	}

	/* (non-Javadoc)
	 * @see palisma.shared.State#exit()
	 */
	@Override
	public void exit() {
	}

}
