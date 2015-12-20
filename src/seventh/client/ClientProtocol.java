/*
 * see license.txt 
 */
package seventh.client;

import harenet.api.Connection;
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
import seventh.network.messages.PlayerSpeechMessage;
import seventh.network.messages.PlayerSwitchTeamMessage;
import seventh.network.messages.RconMessage;
import seventh.network.messages.RconTokenMessage;
import seventh.network.messages.RoundEndedMessage;
import seventh.network.messages.RoundStartedMessage;
import seventh.network.messages.TeamTextMessage;
import seventh.network.messages.TextMessage;
import seventh.network.messages.TileRemovedMessage;
import seventh.network.messages.TilesRemovedMessage;



/**
 * Handles Seventh game network messages
 * 
 * @author Tony
 *
 */
public interface ClientProtocol {

    /**
     * The client has been disconnected from the server
     * 
     * @param conn
     */
    public void onDisconnect(Connection conn);
    
	/**
	 * The server has accepted our connection request.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void connectAccepted(Connection conn, ConnectAcceptedMessage msg);

	/**
	 * A game update (the current state of the world as the server sees it) send from the server.
	 * @param conn
	 * @param msg
	 */
	public void gameUpdate(Connection conn, GameUpdateMessage msg);
	
	/**
	 * A player and game statistics update.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void gameStats(Connection conn, GameStatsMessage msg);
	
	/**
	 * A player and game partial statistics update.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void gamePartialStats(Connection conn, GamePartialStatsMessage msg);
	
	/**
	 * The game has ended message
	 * @param conn
	 * @param msg
	 */
	public void gameEnded(Connection conn, GameEndedMessage msg);
	
	/**
	 * The game is ready, starting the match.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void gameReady(Connection conn, GameReadyMessage msg);
	
	/**
	 * A new player has connected.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void playerConnectedMessage(Connection conn, PlayerConnectedMessage msg);
	
	/**
	 * A player has disconnected.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void playerDisconnectedMessage(Connection conn, PlayerDisconnectedMessage msg);
	
	/**
	 * A Player has switched teams.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void playerSwitchedTeamMessage(Connection conn, PlayerSwitchTeamMessage msg);

	/**
	 * A global text message.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void textMessage(Connection conn, TextMessage msg);

	/**
	 * A text message for a particular team.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void teamTextMessage(Connection conn, TeamTextMessage msg);

	/**
	 * A Player has just spawned.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void playerSpawned(Connection conn, PlayerSpawnedMessage msg);
	
	/**
	 * A player has been killed.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void playerKilled(Connection conn, PlayerKilledMessage msg);
	
	/**
	 * A player has spoken
	 * 
	 * @param conn
	 * @param msg
	 */
	public void playerSpeech(Connection conn, PlayerSpeechMessage msg);
	
	/**
	 * An objective based round has just started.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void roundStarted(Connection conn, RoundStartedMessage msg);
	
	/**
	 * An objective based round has just ended.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void roundEnded(Connection conn, RoundEndedMessage msg);
	
	/**
	 * A bomb has been planted.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void bombPlanted(Connection conn, BombPlantedMessage msg);
	
	/**
	 * A bomb has been disarmed.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void bombDisarmed(Connection conn, BombDisarmedMessage msg);
	
	/**
	 * A bomb target has just exploded.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void bombExploded(Connection conn, BombExplodedMessage msg);
	
	/**
	 * A tile has been removed from the world
	 * 
	 * @param conn
	 * @param msg
	 */
	public void tileRemoved(Connection conn, TileRemovedMessage msg);
	
	/**
	 * A set of tiles have been removed from the world
	 * 
	 * @param conn
	 * @param msg
	 */
	public void tilesRemoved(Connection conn, TilesRemovedMessage msg);
	
	/**
	 * A remote control message (response) from the server
	 * 
	 * @param conn
	 * @param msg
	 */
	public void rconMessage(Connection conn, RconMessage msg);
	
	/**
	 * A remote control token response from the server.
	 * 
	 * @param conn
	 * @param msg
	 */
	public void rconTokenMessage(Connection conn, RconTokenMessage msg);
}