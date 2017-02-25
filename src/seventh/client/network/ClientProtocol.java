/*
 * see license.txt 
 */
package seventh.client.network;

import harenet.api.Connection;
import seventh.client.ClientGame;
import seventh.network.messages.AICommandMessage;
import seventh.network.messages.BombDisarmedMessage;
import seventh.network.messages.BombExplodedMessage;
import seventh.network.messages.BombPlantedMessage;
import seventh.network.messages.ClientReadyMessage;
import seventh.network.messages.ConnectAcceptedMessage;
import seventh.network.messages.ConnectRequestMessage;
import seventh.network.messages.FlagCapturedMessage;
import seventh.network.messages.FlagReturnedMessage;
import seventh.network.messages.FlagStolenMessage;
import seventh.network.messages.GameEndedMessage;
import seventh.network.messages.GamePartialStatsMessage;
import seventh.network.messages.GameReadyMessage;
import seventh.network.messages.GameStatsMessage;
import seventh.network.messages.GameUpdateMessage;
import seventh.network.messages.PlayerCommanderMessage;
import seventh.network.messages.PlayerConnectedMessage;
import seventh.network.messages.PlayerDisconnectedMessage;
import seventh.network.messages.PlayerInputMessage;
import seventh.network.messages.PlayerKilledMessage;
import seventh.network.messages.PlayerNameChangeMessage;
import seventh.network.messages.PlayerSpawnedMessage;
import seventh.network.messages.PlayerSpeechMessage;
import seventh.network.messages.PlayerSwitchTeamMessage;
import seventh.network.messages.PlayerSwitchWeaponClassMessage;
import seventh.network.messages.RconMessage;
import seventh.network.messages.RconTokenMessage;
import seventh.network.messages.RoundEndedMessage;
import seventh.network.messages.RoundStartedMessage;
import seventh.network.messages.TeamTextMessage;
import seventh.network.messages.TextMessage;
import seventh.network.messages.TileRemovedMessage;
import seventh.network.messages.TilesRemovedMessage;



/**
 * The client protocol of network messages that may be received or sent to a Seventh game server.
 * 
 * @author Tony
 *
 */
public interface ClientProtocol {

    /**
     * Listens for when a {@link ClientGame} is created.  This is required
     * because the game is created asynchronously.
     * 
     * @author Tony
     *
     */
    public static interface GameCreationListener {
        public void onGameCreated(ClientGame game);
    }
    
        
    /**
     * The server has accepted our connection request.
     * 
     * @param conn
     * @param msg
     */
    public void receiveConnectAcceptedMessage(Connection conn, ConnectAcceptedMessage msg);

    /**
     * A game update (the current state of the world as the server sees it) send from the server.
     * @param conn
     * @param msg
     */
    public void receiveGameUpdateMessage(Connection conn, GameUpdateMessage msg);
    
    /**
     * A player and game statistics update.
     * 
     * @param conn
     * @param msg
     */
    public void receiveGameStatsMessage(Connection conn, GameStatsMessage msg);
    
    /**
     * A player and game partial statistics update.
     * 
     * @param conn
     * @param msg
     */
    public void receiveGamePartialStatsMessage(Connection conn, GamePartialStatsMessage msg);
    
    /**
     * The game has ended message
     * @param conn
     * @param msg
     */
    public void receiveGameEndedMessage(Connection conn, GameEndedMessage msg);
    
    /**
     * The game is ready, starting the match.
     * 
     * @param conn
     * @param msg
     */
    public void receiveGameReadyMessage(Connection conn, GameReadyMessage msg);
    
    /**
     * A new player has connected.
     * 
     * @param conn
     * @param msg
     */
    public void receivePlayerConnectedMessage(Connection conn, PlayerConnectedMessage msg);
    
    /**
     * A player has disconnected.
     * 
     * @param conn
     * @param msg
     */
    public void receivePlayerDisconnectedMessage(Connection conn, PlayerDisconnectedMessage msg);
    
    /**
     * A Player has switched teams.
     * 
     * @param conn
     * @param msg
     */
    public void receivePlayerSwitchedTeamMessage(Connection conn, PlayerSwitchTeamMessage msg);

    /**
     * A global text message.
     * 
     * @param conn
     * @param msg
     */
    public void receiveTextMessage(Connection conn, TextMessage msg);

    /**
     * A text message for a particular team.
     * 
     * @param conn
     * @param msg
     */
    public void receiveTeamTextMessage(Connection conn, TeamTextMessage msg);

    /**
     * A Player has just spawned.
     * 
     * @param conn
     * @param msg
     */
    public void receivePlayerSpawnedMessage(Connection conn, PlayerSpawnedMessage msg);
    
    /**
     * A player has been killed.
     * 
     * @param conn
     * @param msg
     */
    public void receivePlayerKilledMessage(Connection conn, PlayerKilledMessage msg);
    
    /**
     * A player has spoken
     * 
     * @param conn
     * @param msg
     */
    public void receivePlayerSpeechMessage(Connection conn, PlayerSpeechMessage msg);
    
    
    /**
     * A player has gone to or from Commander
     * 
     * @param conn
     * @param msg
     */
    public void receivePlayerCommanderMessage(Connection conn, PlayerCommanderMessage msg);
    
    /**
     * An objective based round has just started.
     * 
     * @param conn
     * @param msg
     */
    public void receiveRoundStartedMessage(Connection conn, RoundStartedMessage msg);
    
    /**
     * An objective based round has just ended.
     * 
     * @param conn
     * @param msg
     */
    public void receiveRoundEndedMessage(Connection conn, RoundEndedMessage msg);
    
    /**
     * A bomb has been planted.
     * 
     * @param conn
     * @param msg
     */
    public void receiveBombPlantedMessage(Connection conn, BombPlantedMessage msg);
    
    /**
     * A bomb has been disarmed.
     * 
     * @param conn
     * @param msg
     */
    public void receiveBombDisarmedMessage(Connection conn, BombDisarmedMessage msg);
    
    /**
     * A bomb target has just exploded.
     * 
     * @param conn
     * @param msg
     */
    public void receiveBombExplodedMessage(Connection conn, BombExplodedMessage msg);
    
    /**
     * A tile has been removed from the world
     * 
     * @param conn
     * @param msg
     */
    public void receiveTileRemovedMessage(Connection conn, TileRemovedMessage msg);
    
    /**
     * A set of tiles have been removed from the world
     * 
     * @param conn
     * @param msg
     */
    public void receiveTilesRemovedMessage(Connection conn, TilesRemovedMessage msg);
    
    /**
     * A remote control message (response) from the server
     * 
     * @param conn
     * @param msg
     */
    public void receiveRconMessage(Connection conn, RconMessage msg);
    
    /**
     * A remote control token response from the server.
     * 
     * @param conn
     * @param msg
     */
    public void receiveRconTokenMessage(Connection conn, RconTokenMessage msg);
    
    /**
     * A Flag has been captured
     * 
     * @param conn
     * @param msg
     */
    public void receiveFlagCapturedMessage(Connection conn, FlagCapturedMessage msg);
    
    
    /**
     * A Flag has been returned
     * 
     * @param conn
     * @param msg
     */
    public void receiveFlagReturnedMessage(Connection conn, FlagReturnedMessage msg);
    
    
    /**
     * A Flag has been stolen
     * 
     * @param conn
     * @param msg
     */
    public void receiveFlagStolenMessage(Connection conn, FlagStolenMessage msg);
    
    /**
     * Sends a {@link ClientReadyMessage}
     * 
     * @param msg
     */
    public void sendClientReadyMessage(ClientReadyMessage msg);
    
    /**
     * Sends a {@link ConnectRequestMessage}
     * 
     * @param msg
     */
    public void sendConnectRequestMessage(ConnectRequestMessage msg);
    
    /**
     * Sends a {@link PlayerNameChangeMessage}
     * 
     * @param msg
     */
    public void sendPlayerNameChangedMessage(PlayerNameChangeMessage msg);
    
    /**
     * Sends a {@link PlayerSwitchWeaponClassMessage}
     * 
     * @param msg
     */
    public void sendPlayerSwitchWeaponClassMessage(PlayerSwitchWeaponClassMessage msg);
    
    /**
     * Sends a {@link PlayerSpeechMessage}
     * 
     * @param msg
     */
    public void sendPlayerSpeechMessage(PlayerSpeechMessage msg);
    
    /**
     * Sends a {@link PlayerSwitchTeamMessage}
     * 
     * @param msg
     */
    public void sendPlayerSwitchTeamMessage(PlayerSwitchTeamMessage msg);
    
    /**
     * Sends a {@link PlayerInputMessage}
     * 
     * @param msg
     */
    public void sendPlayerInputMessage(PlayerInputMessage msg);
    
    
    /**
     * Sends a {@link PlayerCommanderMessage}
     * 
     * @param msg
     */
    public void sendPlayerCommanderMessage(PlayerCommanderMessage msg);
    
    /**
     * Sends an {@link AICommandMessage}
     * 
     * @param msg
     */
    public void sendAICommandMessage(AICommandMessage msg);
    
    /**
     * Sends a {@link TextMessage}
     * 
     * @param msg
     */
    public void sendTextMessage(TextMessage msg);
    
    /**
     * Sends a {@link TeamTextMessage}
     * 
     * @param msg
     */
    public void sendTeamTextMessage(TeamTextMessage msg);
    
    /**
     * Sends an {@link RconMessage}
     * 
     * @param msg
     */
    public void sendRconMessage(RconMessage msg);
    
    
}