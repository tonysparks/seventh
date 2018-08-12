/*
 * see license.txt 
 */
package seventh.server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import harenet.api.Connection;
import harenet.api.Endpoint;
import harenet.api.Server;
import harenet.messages.NetMessage;
import seventh.game.Game;
import seventh.game.Player;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.game.entities.PlayerEntity;
import seventh.game.net.NetTeam;
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
import seventh.network.messages.PlayerSwitchTileMessage;
import seventh.network.messages.PlayerSwitchWeaponClassMessage;
import seventh.network.messages.RconMessage;
import seventh.network.messages.RconTokenMessage;
import seventh.network.messages.RoundEndedMessage;
import seventh.network.messages.RoundStartedMessage;
import seventh.network.messages.GameEventMessage;
import seventh.network.messages.TeamTextMessage;
import seventh.network.messages.TextMessage;
import seventh.network.messages.TileAddedMessage;
import seventh.network.messages.TileRemovedMessage;
import seventh.network.messages.TilesAddedMessage;
import seventh.network.messages.TilesRemovedMessage;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.Logger;
import seventh.shared.NetworkProtocol;



/**
 * Handles the server side of the {@link NetworkProtocol} of The Seventh.
 * 
 * @author Tony
 *
 */
public class ServerNetworkProtocol extends NetworkProtocol implements GameSessionListener, ServerProtocol {
    
    /**
     * Queued up message for clients
     * 
     * @author Tony
     *
     */
    private static class OutboundMessage {
        
        NetMessage msg;
        MessageType type;
        int flags;
        int id;
        
        public OutboundMessage(NetMessage msg, MessageType type, int id, int flags) {        
            this.msg = msg;
            this.type = type;
            this.id = id;
            this.flags = flags;
        }                
    }
    
    private Game game;
    private Console console;
    
    private Server server;
    private RemoteClients clients;    
    private ServerContext serverContext;

    private Queue<OutboundMessage> outboundQ;
    
    private Map<Integer, Logger> rconLoggers;
    
    /**
     * @param serverContext
     */
    public ServerNetworkProtocol(ServerContext serverContext) {
        super(serverContext.getServer());
        
        this.serverContext = serverContext;
        this.server = serverContext.getServer();    
        this.console = serverContext.getConsole();
        
        this.clients = serverContext.getClients();
        
        this.outboundQ = new ConcurrentLinkedQueue<ServerNetworkProtocol.OutboundMessage>();
        this.rconLoggers = new HashMap<>();
        
    }
    
    /* (non-Javadoc)
     * @see seventh.server.GameSessionListener#onGameSessionCreated(seventh.server.GameSession)
     */
    @Override
    public void onGameSessionCreated(GameSession session) {        
        this.game = session.getGame();
    }
    
    /* (non-Javadoc)
     * @see seventh.server.GameSessionListener#onGameSessionDestroyed(seventh.server.GameSession)
     */
    @Override
    public void onGameSessionDestroyed(GameSession session) {        
    }
    
    /*
     * (non-Javadoc)
     * @see seventh.shared.NetworkProtocol#postQueuedMessages()
     */
    @Override
    public void postQueuedMessages() {
        while(!this.outboundQ.isEmpty()) {
            try {
                OutboundMessage oMsg = this.outboundQ.poll();
                switch(oMsg.type) {
                    case ALL_CLIENTS:
                        this.server.sendToAll(oMsg.flags, oMsg.msg);
                        break;
                    case ALL_EXCEPT:
                        this.server.sendToAllExcept(oMsg.flags, oMsg.msg, oMsg.id);
                        break;
                    case ONE:
                        this.server.sendTo(oMsg.flags, oMsg.msg, oMsg.id);
                        break;
                    default:
                        break;            
                }
            }
            catch(Exception e) {
                Cons.println("Error sending packet: " + e);
            }
        }
    }
    
    private void queueSendToAll(int protocolFlags, NetMessage message) {
        this.outboundQ.add(new OutboundMessage(message, MessageType.ALL_CLIENTS, 0, protocolFlags));
    }
    
    private void sendTo(int protocolFlags, int clientId, NetMessage msg) {
        try {
            this.server.sendTo(protocolFlags, msg, clientId);
        }
        catch(IOException e) {
            Cons.println("*** ERROR: Failed to send message to: " + clientId + " - " + e);
        }
    }
    
    private void queueSendToAllExcept(int protocolFlags, NetMessage message, int id) {
        this.outboundQ.add(new OutboundMessage(message, MessageType.ALL_EXCEPT, id, protocolFlags));
    }
//    private void queueSendToClient(int protocolFlags, NetMessage message, int id) {
//        this.outboundQ.add(new OutboundMessage(message, MessageType.ONE, id, protocolFlags));
//    }
       
    @Override
    public void onConnected(Connection conn) {                
        this.clients.addRemoteClient(conn.getId(), new RemoteClient(conn));
        this.console.println("Negotiating connection with remote client: " + conn.getRemoteAddress());
    }
    
    @Override
    public void onDisconnected(Connection conn) {
        this.queueInboundMessage(conn, new ClientDisconnectMessage());                
    }
    
    @Override
    public void onServerFull(Connection conn) {
        Cons.println("Connected attempted but the server is full.");        
    }
    
    /*
     * (non-Javadoc)
     * @see palisma.shared.NetworkProtocol#processMessage(com.esotericsoftware.kryonet.Connection, palisma.network.messages.Message)
     */
    @Override
    protected void processMessage(Connection conn, NetMessage message) throws IOException {        
        if(message instanceof PlayerInputMessage) {
            receivePlayerInputMessage(conn, (PlayerInputMessage)message);
        }
        else if(message instanceof ConnectRequestMessage) {
            receiveConnectRequestMessage(conn, (ConnectRequestMessage)message);
        }
        else if(message instanceof ClientReadyMessage) {
            receiveClientReadyMessage(conn, (ClientReadyMessage)message);
        }
        else if(message instanceof PlayerSwitchWeaponClassMessage) {
            receivePlayerSwitchWeaponClassMessage(conn, (PlayerSwitchWeaponClassMessage)message );
        }
        else if(message instanceof AICommandMessage) {
            receiveAICommand(conn, (AICommandMessage)message);
        }
        else if(message instanceof PlayerSwitchTeamMessage) {
            receivePlayerSwitchedTeamMessage(conn, (PlayerSwitchTeamMessage)message);
        }
        else if(message instanceof PlayerSwitchTileMessage) {
            receivePlayerSwitchTileMessage(conn, (PlayerSwitchTileMessage)message);
        }
        else if(message instanceof PlayerSpeechMessage) {
            receivePlayerSpeechMessage(conn, (PlayerSpeechMessage)message);
        }
        else if(message instanceof PlayerCommanderMessage) {
            receivePlayerCommanderMessage(conn, (PlayerCommanderMessage)message);
        }
        else if(message instanceof ClientDisconnectMessage) {
            receiveClientDisconnectMessage(conn, (ClientDisconnectMessage)message);
        }
        else if(message instanceof TextMessage) {
            receiveTextMessage(conn, (TextMessage)message);
        }        
        else if(message instanceof TeamTextMessage) {
            receiveTeamTextMessage(conn, (TeamTextMessage)message);
        }
        else if(message instanceof PlayerNameChangeMessage) {
            receivePlayerNameChangedMessage(conn, (PlayerNameChangeMessage)message);
        }
        else if(message instanceof RconMessage) {
            receiveRconMessage(conn, (RconMessage)message);
        }
        else {
            Cons.println("Unknown message: " + message);
        }
    }
    
    /*
     * (non-Javadoc)
     * @see seventh.server.ServerProtocol#receivePlayerSwitchWeaponClassMessage(harenet.api.Connection, seventh.network.messages.PlayerSwitchWeaponClassMessage)
     */
    @Override
    public void receivePlayerSwitchWeaponClassMessage(Connection conn, PlayerSwitchWeaponClassMessage message) throws IOException {        
        game.playerSwitchWeaponClass(conn.getId(), message.weaponType);
    }

    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#receivePlayerSwitchClassMessage(harenet.api.Connection, seventh.network.messages.PlayerSwitchPlayerClassMessage)
     */
    @Override
    public void receivePlayerSwitchClassMessage(Connection conn, PlayerSwitchPlayerClassMessage message) throws IOException {
        game.playerSwitchPlayerClass(conn.getId(), message.playerClass);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#receivePlayerSwitchTileMessage(harenet.api.Connection, seventh.network.messages.PlayerSwitchTileMessage)
     */
    @Override
    public void receivePlayerSwitchTileMessage(Connection conn, PlayerSwitchTileMessage message) throws IOException {
        game.playerSwitchTile(conn.getId(), message.newTileId);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#connectRequest(harenet.api.Connection, seventh.network.messages.ConnectRequestMessage)
     */
    @Override
    public void receiveConnectRequestMessage(Connection conn, ConnectRequestMessage msg) throws IOException {
        this.console.println(msg.name + " is attempting to connect");
        RemoteClient client = this.clients.getClient(conn.getId());
        client.setName(msg.name);
        
        this.game.playerJoined(client.getPlayer());
        
        /* notify all players (accept the one connecting) that a new player
           is coming in
         */
        PlayerConnectedMessage connectBroadcast = new PlayerConnectedMessage();
        connectBroadcast.name = msg.name;
        connectBroadcast.playerId = client.getId();
        sendPlayerConnectedMessage(connectBroadcast, conn.getId());
        
        // Give the new player the full game state
        ConnectAcceptedMessage acceptedMessage = new ConnectAcceptedMessage();
        acceptedMessage.playerId = conn.getId();
        acceptedMessage.gameState = this.game.getNetGameState();    
        sendConnectAcceptedMessage(conn.getId(), acceptedMessage);                            
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#clientReady(harenet.api.Connection, seventh.network.messages.ClientReadyMessage)
     */
    @Override
    public void receiveClientReadyMessage(Connection conn, ClientReadyMessage msg) {
        RemoteClient client = this.clients.getClient(conn.getId());    
        if(client != null) {
            if(!client.isReady()) {
                client.setReady(true);
                client.getPlayer().setPing(conn.getReturnTripTime());
                this.console.println(client.getName() + " has connected.");
            }
        }
        
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#userInput(harenet.api.Connection, seventh.network.messages.UserInputMessage)
     */
    @Override
    public void receivePlayerInputMessage(Connection conn, PlayerInputMessage msg) {        
        this.game.applyPlayerInput(conn.getId(), msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#clientDisconnect(harenet.api.Connection, seventh.network.messages.ClientDisconnectMessage)
     */
    @Override
    public void receiveClientDisconnectMessage(Connection conn, ClientDisconnectMessage msg) throws IOException {        
        this.game.playerLeft(conn.getId());
        PlayerDisconnectedMessage disconnectBroadcast = new PlayerDisconnectedMessage();
        disconnectBroadcast.playerId = conn.getId();
        sendPlayerDisconnectedMessage(disconnectBroadcast, conn.getId());
        
        this.clients.removeClient(conn.getId());   
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#playerSwitchedTeam(harenet.api.Connection, seventh.network.messages.PlayerSwitchTeamMessage)
     */
    @Override
    public void receivePlayerSwitchedTeamMessage(Connection conn, PlayerSwitchTeamMessage msg) throws IOException {        
        if ( game.playerSwitchedTeam( (int)msg.playerId, msg.teamId) ) {
            sendPlayerSwitchTeamMessage(msg);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#playerSpeech(harenet.api.Connection, seventh.network.messages.PlayerSpeechMessage)
     */
    @Override
    public void receivePlayerSpeechMessage(Connection conn, PlayerSpeechMessage msg) throws IOException {
        if(conn.getId() == msg.playerId) {
            PlayerInfo player = game.getPlayerById(msg.playerId);
            if(player != null) {
                if(player.isAlive()) {
                    PlayerEntity entity = player.getEntity();
                    // server is authorative of the actual player position
                    msg.posX = (short)entity.getCenterPos().x;
                    msg.posY = (short)entity.getCenterPos().y;
                    
                    sendPlayerSpeechMessage(msg, conn.getId());
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#textMessage(harenet.api.Connection, seventh.network.messages.TextMessage)
     */
    @Override
    public void receiveTextMessage(Connection conn, TextMessage msg) throws IOException  {
        msg.playerId = conn.getId();        
        sendTextMessage(msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#teamTextMessage(harenet.api.Connection, seventh.network.messages.TeamTextMessage)
     */
    @Override
    public void receiveTeamTextMessage(Connection conn, TeamTextMessage msg) throws IOException  {
        RemoteClient client = this.clients.getClient(conn.getId());
        msg.playerId = conn.getId();
        
        Player player = client.getPlayer();        
        Team team = this.game.getGameType().getTeam(player);
        NetTeam netTeam = team.getNetTeam();
        for(int i = 0; i < netTeam.playerIds.length; i++) {
            sendTeamTextMessage(netTeam.playerIds[i], msg);
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#playerNameChangedMessage(harenet.api.Connection, seventh.network.messages.PlayerNameChangeMessage)
     */
    @Override
    public void receivePlayerNameChangedMessage(Connection conn, PlayerNameChangeMessage msg ) throws IOException {
        RemoteClient client = this.clients.getClient(conn.getId());    
        Player player = client.getPlayer();
        player.setName(msg.name);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#receivePlayerCommanderMessage(harenet.api.Connection, seventh.network.messages.PlayerCommanderMessage)
     */
    @Override
    public void receivePlayerCommanderMessage(Connection conn, PlayerCommanderMessage msg) {
        RemoteClient client = this.clients.getClient(conn.getId());    
        Player player = client.getPlayer();        
        if(game.playerCommander(player, msg.isCommander)) {
            sendPlayerCommanderMessage(msg);        
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#aiCommand(harenet.api.Connection, seventh.network.messages.AICommandMessage)
     */
    @Override
    public void receiveAICommand(Connection conn, AICommandMessage msg) throws IOException {
        game.receiveAICommand(conn.getId(), msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#rconMessage(harenet.api.Connection, seventh.network.messages.RconMessage)
     */
    @Override
    public void receiveRconMessage(Connection conn, RconMessage msg) throws IOException {
        RemoteClient client = this.clients.getClient(conn.getId());
        if(client != null) {
            String cmd = msg.getCommand();
            if(cmd != null) {
                
                /*
                 * Authentication process:
                 * 1) Client requests to login
                 * 2) Server sends session token
                 * 3) Client sends password (hashed with token)
                 * 4) Server sends pass/fail
                 * 5) If pass, client can send remote console commands
                 */
                
                if(cmd.startsWith("login")) {
                    long token = this.serverContext.createToken();
                    RconTokenMessage tokenMessage = new RconTokenMessage(token);
                    client.setRconToken(token);
                    
                    sendRconTokenMessage(client.getId(), tokenMessage);
                }
                else if (cmd.startsWith("logoff")) {
                    client.setRconAuthenticated(false);
                    client.setRconToken(ServerContext.INVALID_RCON_TOKEN);
                    console.removeLogger(this.rconLoggers.get(client.getId()));
                }
                else if(cmd.startsWith("password")) {
                    if(!client.hasRconToken()) {
                        RconMessage rconMsg = new RconMessage("You must first initiate a login: rcon login");
                        sendRconMessage(client.getId(), rconMsg);
                    }
                    else {
                        String password = cmd.replace("password ", "");
                        
                        // if the password hashes match, they are authenticated
                        String serverPassword = this.serverContext.getRconPassword(client.getRconToken()); 
                        
                        if( serverPassword.equals(password) ) {
                            client.setRconAuthenticated(true);
                            
                            Logger logger = this.rconLoggers.put(client.getId(), new RconLogger(client.getId(), this));
                            console.removeLogger(logger);
                            if(!serverContext.getGameServer().isLocal()) {
                                console.addLogger(this.rconLoggers.get(client.getId()));
                            }
                        }
                        else {
                            client.setRconAuthenticated(false);
                            RconMessage rconMsg = new RconMessage("Invalid password.");
                            sendRconMessage(client.getId(), rconMsg);
                            
                        }
                    }
                }
                else if(client.isRconAuthenticated()) {                    
                    console.execute(msg.getCommand());                                       
                }
                else {
                    RconMessage rconMsg = new RconMessage("You must first authenticate by executing these commands:\n rcon login\n rcon password [password]");
                    sendRconMessage(client.getId(), rconMsg);
                }
            }
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendGamePartialStatsMessage(seventh.network.messages.GamePartialStatsMessage)
     */
    @Override
    public void sendGamePartialStatsMessage(GamePartialStatsMessage msg) throws IOException {
        this.server.sendToAll(Endpoint.FLAG_UNRELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendGameStatsMessage(seventh.network.messages.GameStatsMessage)
     */
    @Override
    public void sendGameStatsMessage(GameStatsMessage msg) throws IOException {
        this.server.sendToAll(Endpoint.FLAG_UNRELIABLE, msg);   
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendGameUpdateMessage(seventh.network.messages.GameUpdateMessage, int)
     */
    @Override
    public void sendGameUpdateMessage(GameUpdateMessage msg, int clientId) throws IOException {
        this.server.sendTo(Endpoint.FLAG_UNRELIABLE, msg, clientId);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendGameReadyMessage(seventh.network.messages.GameReadyMessage)
     */
    @Override
    public void sendGameReadyMessage(GameReadyMessage msg, int clientId) throws IOException {
        this.server.sendTo(Endpoint.FLAG_RELIABLE, msg, clientId);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendGameEndedMessage(seventh.network.messages.GameEndedMessage)
     */
    @Override
    public void sendGameEndedMessage(GameEndedMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);        
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendRoundStartedMessage(seventh.network.messages.RoundStartedMessage)
     */
    @Override
    public void sendRoundStartedMessage(RoundStartedMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendRoundEndedMessage(seventh.network.messages.RoundEndedMessage)
     */
    @Override
    public void sendRoundEndedMessage(RoundEndedMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendPlayerKilledMessage(seventh.network.messages.PlayerKilledMessage)
     */
    @Override
    public void sendPlayerKilledMessage(PlayerKilledMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);   
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendPlayerSpawnedMessage(seventh.network.messages.PlayerSpawnedMessage)
     */
    @Override
    public void sendPlayerSpawnedMessage(PlayerSpawnedMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);   
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendBombPlantedMessage(seventh.network.messages.BombPlantedMessage)
     */
    @Override
    public void sendBombPlantedMessage(BombPlantedMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendBombDisarmedMessage(seventh.network.messages.BombDisarmedMessage)
     */
    @Override
    public void sendBombDisarmedMessage(BombDisarmedMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendBombExplodedMessage(seventh.network.messages.BombExplodedMessage)
     */
    @Override
    public void sendBombExplodedMessage(BombExplodedMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendTileRemovedMessage(seventh.network.messages.TileRemovedMessage)
     */
    @Override
    public void sendTileRemovedMessage(TileRemovedMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendTilesRemovedMessage(seventh.network.messages.TilesRemovedMessage)
     */
    @Override
    public void sendTilesRemovedMessage(TilesRemovedMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    @Override
    public void sendTileAddedMessage(TileAddedMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendTilesRemovedMessage(seventh.network.messages.TilesRemovedMessage)
     */
    @Override
    public void sendTilesAddedMessage(TilesAddedMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendRconTokenMessage(int, seventh.network.messages.RconTokenMessage)
     */
    @Override
    public void sendRconTokenMessage(int clientId, RconTokenMessage msg) {
        sendTo(Endpoint.FLAG_RELIABLE, clientId, msg);   
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendRconMessage(int, seventh.network.messages.RconMessage)
     */
    @Override
    public void sendRconMessage(int clientId, RconMessage msg) {
        sendTo(Endpoint.FLAG_RELIABLE, clientId, msg);        
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendTextMessage(int, seventh.network.messages.TextMessage)
     */
    @Override
    public void sendTextMessage(TextMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendTeamTextMessage(int, seventh.network.messages.TeamTextMessage)
     */
    @Override
    public void sendTeamTextMessage(int clientId, TeamTextMessage msg) {
        sendTo(Endpoint.FLAG_RELIABLE, clientId, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendConnectAcceptedMessage(int, seventh.network.messages.ConnectAcceptedMessage)
     */
    @Override
    public void sendConnectAcceptedMessage(int clientId, ConnectAcceptedMessage msg) {
        sendTo(Endpoint.FLAG_RELIABLE, clientId, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendPlayerConnectedMessage(seventh.network.messages.PlayerConnectedMessage, int)
     */
    @Override
    public void sendPlayerConnectedMessage(PlayerConnectedMessage msg, int exceptClientId) {
        queueSendToAllExcept(Endpoint.FLAG_RELIABLE, msg, exceptClientId);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendPlayerDisconnectedMessage(seventh.network.messages.PlayerDisconnectedMessage, int)
     */
    @Override
    public void sendPlayerDisconnectedMessage(PlayerDisconnectedMessage msg, int exceptClientId) {
        queueSendToAllExcept(Endpoint.FLAG_RELIABLE, msg, exceptClientId);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendPlayerSwitchTeamMessage(seventh.network.messages.PlayerSwitchTeamMessage)
     */
    @Override
    public void sendPlayerSwitchTeamMessage(PlayerSwitchTeamMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendPlayerSpeechMessage(seventh.network.messages.PlayerSpeechMessage, int)
     */
    @Override
    public void sendPlayerSpeechMessage(PlayerSpeechMessage msg, int exceptClientId) {
        queueSendToAllExcept(Endpoint.FLAG_RELIABLE, msg, exceptClientId);   
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendPlayerSwitchClassMessage(seventh.network.messages.PlayerSwitchPlayerClassMessage)
     */
    @Override
    public void sendPlayerSwitchClassMessage(PlayerSwitchPlayerClassMessage message) throws IOException {
        queueSendToAll(Endpoint.FLAG_RELIABLE, message);        
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendFlagCapturedMessage(seventh.network.messages.FlagCapturedMessage)
     */
    @Override
    public void sendFlagCapturedMessage(FlagCapturedMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendFlagReturnedMessage(seventh.network.messages.FlagReturnedMessage)
     */
    @Override
    public void sendFlagReturnedMessage(FlagReturnedMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendFlagStolenMessage(seventh.network.messages.FlagStolenMessage)
     */
    @Override
    public void sendFlagStolenMessage(FlagStolenMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendPlayerCommanderMessage(seventh.network.messages.PlayerCommanderMessage)
     */
    @Override
    public void sendPlayerCommanderMessage(PlayerCommanderMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendPlayerAwardMessage(seventh.network.messages.PlayerAwardMessage)
     */
    @Override
    public void sendPlayerAwardMessage(PlayerAwardMessage msg) {        
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);        
    }
    
    /* (non-Javadoc)
     * @see seventh.server.ServerProtocol#sendSurvivoEventrMessage(seventh.network.messages.GameEventMessage)
     */
    @Override
    public void sendGameEventMessage(GameEventMessage msg) {
        queueSendToAll(Endpoint.FLAG_RELIABLE, msg);
    }
}
