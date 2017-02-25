/*
 * see license.txt 
 */
package seventh.server;

import leola.frontend.listener.EventDispatcher;
import seventh.game.Game;
import seventh.game.GameMap;
import seventh.game.Players;
import seventh.game.type.GameType;
import seventh.shared.SeventhConfig;

/**
 * A game is in session, represents a structure holding session data.
 * 
 * @author Tony
 *
 */
public class GameSession {

    private Game game;
    private GameMap map;
    private GameType gameType;
    private Players players;
    
    private EventDispatcher eventDispatcher;

    /**
     * @param config
     * @param map
     * @param gameType
     * @param players
     */
    public GameSession(SeventhConfig config, GameMap map, GameType gameType, Players players) {                
        this.map = map;
        this.gameType = gameType;
        this.players = players;
        this.eventDispatcher = new EventDispatcher();
        this.game = new Game(config, players, gameType, map, this.eventDispatcher);
    }
    
    /**
     * @param game
     * @param map
     * @param gameType
     * @param players
     * @param eventDispatcher
     */
    public GameSession(Game game, GameMap map, GameType gameType, Players players, EventDispatcher eventDispatcher) {        
        this.game = game;
        this.map = map;
        this.gameType = gameType;
        this.players = players;
        this.eventDispatcher = eventDispatcher;
    }

    /**
     * Destroys the {@link GameSession}, clearing out all
     * state associated with the session.
     * 
     */
    public void destroy() {
        this.game.destroy();
        this.eventDispatcher.removeAllEventListeners();
        this.eventDispatcher.clearQueue();
    }
    
    /**
     * @return the game
     */
    public Game getGame() {
        return game;
    }

    /**
     * @return the map
     */
    public GameMap getMap() {
        return map;
    }

    /**
     * @return the gameType
     */
    public GameType getGameType() {
        return gameType;
    }

    /**
     * @return the players
     */
    public Players getPlayers() {
        return players;
    }

    /**
     * @return the eventDispatcher
     */
    public EventDispatcher getEventDispatcher() {
        return eventDispatcher;
    }

}
