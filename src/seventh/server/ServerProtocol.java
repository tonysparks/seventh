/*
 * see license.txt
 */
package seventh.server;

import java.io.IOException;

import harenet.api.Connection;
import seventh.network.messages.AICommandMessage;
import seventh.network.messages.BombDisarmedMessage;
import seventh.network.messages.BombExplodedMessage;
import seventh.network.messages.BombPlantedMessage;
import seventh.network.messages.ClientDisconnectMessage;
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
import seventh.network.messages.PlayerAwardMessage;
import seventh.network.messages.PlayerCommanderMessage;
import seventh.network.messages.PlayerConnectedMessage;
import seventh.network.messages.PlayerDisconnectedMessage;
import seventh.network.messages.PlayerInputMessage;
import seventh.network.messages.PlayerKilledMessage;
import seventh.network.messages.PlayerNameChangeMessage;
import seventh.network.messages.PlayerSpawnedMessage;
import seventh.network.messages.PlayerSpeechMessage;
import seventh.network.messages.PlayerSwitchPlayerClassMessage;
import seventh.network.messages.PlayerSwitchTeamMessage;
import seventh.network.messages.PlayerSwitchWeaponClassMessage;
import seventh.network.messages.RconMessage;
import seventh.network.messages.RconTokenMessage;
import seventh.network.messages.RoundEndedMessage;
import seventh.network.messages.RoundStartedMessage;
import seventh.network.messages.SurvivorEventMessage;
import seventh.network.messages.TeamTextMessage;
import seventh.network.messages.TextMessage;
import seventh.network.messages.TileAddedMessage;
import seventh.network.messages.TileRemovedMessage;
import seventh.network.messages.TilesAddedMessage;
import seventh.network.messages.TilesRemovedMessage;

/**
 * The messages the server receives from the clients
 * 
 * @author Tony
 *
 */
public interface ServerProtocol {

    /**
     * Connection request from a client
     * 
     * @param conn
     * @param msg
     * @throws IOException
     */
    public void receiveConnectRequestMessage(Connection conn, ConnectRequestMessage msg) throws IOException;

    
    
    /**
     * The client is ready for game state messages, all network negotiations are completed.
     * 
     * @param conn
     * @param msg
     */
    public void receiveClientReadyMessage(Connection conn, ClientReadyMessage msg) throws IOException;

    
    /**
     * The client has disconnected from the server.
     * 
     * @param conn
     * @param msg
     * @throws IOException
     */
    public void receiveClientDisconnectMessage(Connection conn, ClientDisconnectMessage msg) throws IOException;
    
    
    /**
     * The Players input controls to their character.
     * 
     * @param conn
     * @param msg
     * @throws IOException
     */
    public void receivePlayerInputMessage(Connection conn, PlayerInputMessage msg) throws IOException;

    
    
    /**
     * The Player has requested to switch teams
     * 
     * @param conn
     * @param msg
     * @throws IOException
     */
    public void receivePlayerSwitchedTeamMessage(Connection conn, PlayerSwitchTeamMessage msg) throws IOException;

    
    /**
     * The Player has requested to switch weapon class
     * 
     * @param conn
     * @param message
     * @throws IOException
     */
    public void receivePlayerSwitchWeaponClassMessage(Connection conn, PlayerSwitchWeaponClassMessage message) throws IOException;
    
    /**
     * The Player has requested to switch player classes
     * 
     * @param conn
     * @param message
     * @throws IOException
     */
    public void receivePlayerSwitchClassMessage(Connection conn, PlayerSwitchPlayerClassMessage message) throws IOException;
    
    /**
     * The Player has issued a voice command
     * 
     * @param conn
     * @param msg
     * @throws IOException
     */
    public void receivePlayerSpeechMessage(Connection conn, PlayerSpeechMessage msg) throws IOException;

    /**
     * The Player has changed their name
     * 
     * @param conn
     * @param msg
     * @throws IOException
     */
    public void receivePlayerNameChangedMessage(Connection conn, PlayerNameChangeMessage msg) throws IOException;
    

    /**
     * A player has gone to or from Commander
     * 
     * @param conn
     * @param msg
     */
    public void receivePlayerCommanderMessage(Connection conn, PlayerCommanderMessage msg);
    
    /**
     * The client has issued a global text message
     * 
     * @param conn
     * @param msg
     * @throws IOException
     */
    public void receiveTextMessage(Connection conn, TextMessage msg) throws IOException;

    
    /**
     * The client has issued a team text message
     * 
     * @param conn
     * @param msg
     * @throws IOException
     */
    public void receiveTeamTextMessage(Connection conn, TeamTextMessage msg) throws IOException;

    
    /**
     * Issue an AI command for a Bot
     * 
     * @param conn
     * @param msg
     * @throws IOException
     */
    public void receiveAICommand(Connection conn, AICommandMessage msg) throws IOException;

    
    /**
     * A client has issued a remote command request
     *  
     * @param conn
     * @param msg
     * @throws IOException
     */
    public void receiveRconMessage(Connection conn, RconMessage msg) throws IOException;

    /**
     * Sends a {@link GamePartialStatsMessage} to all clients
     * 
     * @param msg
     * @throws IOException
     */
    public void sendGamePartialStatsMessage(GamePartialStatsMessage msg) throws IOException;
    
    /**
     * Sends a {@link GameStatsMessage} to all clients
     * 
     * @param msg
     * @throws IOException
     */
    public void sendGameStatsMessage(GameStatsMessage msg) throws IOException;
    
    /**
     * Sends a {@link GameUpdateMessage} to the supplied client id
     * 
     * @param msg
     * @param clientId
     * @throws IOException
     */
    public void sendGameUpdateMessage(GameUpdateMessage msg, int clientId) throws IOException;
    
    
    /**
     * Sends a {@link GameReadyMessage} to the supplied client id
     * 
     * @param msg
     * @param clientId
     * @throws IOException
     */
    public void sendGameReadyMessage(GameReadyMessage msg, int clientId) throws IOException;
    
    /**
     * Sends a {@link GameEndedMessage} to all clients
     * 
     * @param msg
     */
    public void sendGameEndedMessage(GameEndedMessage msg);
    
    /**
     * Sends a {@link RoundStartedMessage} to all clients
     * 
     * @param msg
     */
    public void sendRoundStartedMessage(RoundStartedMessage msg);
    
    /**
     * Sends a {@link RoundEndedMessage} to all clients
     * @param msg
     */
    public void sendRoundEndedMessage(RoundEndedMessage msg);
    
    /**
     * Sends a {@link PlayerKilledMessage} to all clients
     * 
     * @param msg
     */
    public void sendPlayerKilledMessage(PlayerKilledMessage msg);
    
    
    /**
     * Sends a {@link PlayerSpawnedMessage} to all clients
     * @param msg
     */
    public void sendPlayerSpawnedMessage(PlayerSpawnedMessage msg);
    
    /**
     * Sends a {@link BombPlantedMessage} to all clients
     * 
     * @param msg
     */
    public void sendBombPlantedMessage(BombPlantedMessage msg);
    
    /**
     * Sends a {@link BombDisarmedMessage} to all clients
     * 
     * @param msg
     */
    public void sendBombDisarmedMessage(BombDisarmedMessage msg);
    
    /**
     * Sends a {@link BombExplodedMessage} to all clients
     * 
     * @param msg
     */
    public void sendBombExplodedMessage(BombExplodedMessage msg);
    
    /**
     * Sends a {@link TileRemovedMessage} to all clients
     * 
     * @param msg
     */
    public void sendTileRemovedMessage(TileRemovedMessage msg);
    
    /**
     * Sends a {@link TilesRemovedMessage} to all clients
     * 
     * @param msg
     */
    public void sendTilesRemovedMessage(TilesRemovedMessage msg);
    
    /**
     * Sends a {@link TileAddedMessage} to all clients
     * 
     * @param msg
     */
    public void sendTileAddedMessage(TileAddedMessage msg);
    
    /**
     * Sends a {@link TilesAddedMessage} to all clients
     * 
     * @param msg
     */
    public void sendTilesAddedMessage(TilesAddedMessage msg);
    
    /**
     * Sends an {@link RconTokenMessage} to a particular client
     * 
     * @param clientId
     * @param msg
     */
    public void sendRconTokenMessage(int clientId, RconTokenMessage msg);
    
    /**
     * Sends an {@link RconMessage} to a particular client
     * 
     * @param clientId
     * @param msg
     */
    public void sendRconMessage(int clientId, RconMessage msg);
    
    
    /**
     * Sends a {@link TeamTextMessage} to a particular client
     * 
     * @param clientId
     * @param msg
     */
    public void sendTeamTextMessage(int clientId, TeamTextMessage msg);
    
    /**
     * Sends a {@link TextMessage} to all clients
     * @param msg
     */
    public void sendTextMessage(TextMessage msg);
    
    /**
     * Sends a {@link PlayerConnectedMessage} to all clients except the supplied client id.
     * 
     * @param msg
     * @param exceptClientId
     */
    public void sendPlayerConnectedMessage(PlayerConnectedMessage msg, int exceptClientId);
    
    /**
     * Sends a {@link PlayerDisconnectedMessage} to all clients except the supplied client id.
     * 
     * @param msg
     * @param exceptClientId
     */
    public void sendPlayerDisconnectedMessage(PlayerDisconnectedMessage msg, int exceptClientId);
    
    
    /**
     * Sends a {@link TeamTextMessage} to all clients
     * 
     * @param msg
     */
    public void sendPlayerSwitchTeamMessage(PlayerSwitchTeamMessage msg);
    
    /**
     * Sends a {@link PlayerSpeechMessage} to all clients except the supplied client id.
     * 
     * @param msg
     * @param exceptClientId
     */
    public void sendPlayerSpeechMessage(PlayerSpeechMessage msg, int exceptClientId);
    
    
    /**
     * Sends a {@link PlayerSwitchPlayerClassMessage} to all clients
     * 
     * @param conn
     * @param message
     * @throws IOException
     */
    public void sendPlayerSwitchClassMessage(PlayerSwitchPlayerClassMessage message) throws IOException;
    
    /**
     * Sends a {@link ConnectAcceptedMessage} to a particular client
     * 
     * @param clientId
     * @param msg
     */
    public void sendConnectAcceptedMessage(int clientId, ConnectAcceptedMessage msg);
    
    /**
     * Sends a {@link FlagCapturedMessage} message to all clients
     * 
     * @param msg
     */
    public void sendFlagCapturedMessage(FlagCapturedMessage msg);
    
    /**
     * Sends a {@link FlagReturnedMessage} message to all clients
     * @param msg
     */
    public void sendFlagReturnedMessage(FlagReturnedMessage msg);
    
    /**
     * Sends a {@link FlagStolenMessage} message to all clients
     * 
     * @param msg
     */
    public void sendFlagStolenMessage(FlagStolenMessage msg);
    
    /**
     * Sends a {@link PlayerCommanderMessage}
     * 
     * @param msg
     */
    public void sendPlayerCommanderMessage(PlayerCommanderMessage msg);
    
    /**
     * Sends a {@link PlayerAwardMessage}
     * 
     * @param msg
     */
    public void sendPlayerAwardMessage(PlayerAwardMessage msg);
    
    /**
     * Sends a {@link SurvivorEventMessage}
     * 
     * @param msg
     */
    public void sendSurvivoEventrMessage(SurvivorEventMessage msg);
}