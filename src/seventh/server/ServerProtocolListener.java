/*
 * see license.txt 
 */
package seventh.server;

import harenet.api.Connection;
import harenet.api.Endpoint;
import harenet.api.Server;
import harenet.messages.NetMessage;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import seventh.game.Entity.Type;
import seventh.game.Game;
import seventh.game.Player;
import seventh.game.PlayerInfo;
import seventh.game.Team;
import seventh.game.net.NetTeam;
import seventh.network.messages.AICommandMessage;
import seventh.network.messages.ClientDisconnectMessage;
import seventh.network.messages.ClientReadyMessage;
import seventh.network.messages.ConnectAcceptedMessage;
import seventh.network.messages.ConnectRequestMessage;
import seventh.network.messages.PlayerConnectedMessage;
import seventh.network.messages.PlayerDisconnectedMessage;
import seventh.network.messages.PlayerNameChangeMessage;
import seventh.network.messages.PlayerSwitchTeamMessage;
import seventh.network.messages.PlayerSwitchWeaponClassMessage;
import seventh.network.messages.RconMessage;
import seventh.network.messages.RconTokenMessage;
import seventh.network.messages.TeamTextMessage;
import seventh.network.messages.TextMessage;
import seventh.network.messages.UserInputMessage;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.NetworkProtocol;



/**
 * Handles the server side of the {@link NetworkProtocol} of The Seventh.
 * 
 * @author Tony
 *
 */
public class ServerProtocolListener extends NetworkProtocol implements GameSessionListener {
	
	private class OutboundMessage {
		
		NetMessage msg;
		MessageType type;
		int flags;
		int id;
		
		/**
		 * @param msg
		 * @param type
		 * @param id
		 */
		public OutboundMessage(NetMessage msg, MessageType type, int id, int flags) {		
			this.msg = msg;
			this.type = type;
			this.id = id;
			this.flags = flags;
		}				
	}
	
	private RemoteClients clients;	
	private Game game;
	
	private ServerContext serverContext;
	private Server server;
	private Console console;
	private Queue<OutboundMessage> outboundQ;
	
	
	/**
	 * @param serverContext
	 */
	public ServerProtocolListener(ServerContext serverContext) {
		super(serverContext.getServer());
		
		this.serverContext = serverContext;
		this.server = serverContext.getServer();	
		this.console = serverContext.getConsole();
		
		this.clients = serverContext.getClients();
		
		this.outboundQ = new ConcurrentLinkedQueue<ServerProtocolListener.OutboundMessage>();
		
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
	
	/**
	 * send all queued up messages
	 */
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
	
	public void queueSendToAll(int protocolFlags, NetMessage message) {
		this.outboundQ.add(new OutboundMessage(message, MessageType.ALL_CLIENTS, 0, protocolFlags));
	}
	public void queueSendToAllExcept(int protocolFlags, NetMessage message, int id) {
		this.outboundQ.add(new OutboundMessage(message, MessageType.ALL_EXCEPT, id, protocolFlags));
	}
	public void queueSendToClient(int protocolFlags, NetMessage message, int id) {
		this.outboundQ.add(new OutboundMessage(message, MessageType.ONE, id, protocolFlags));
	}
		
	/* (non-Javadoc)
	 * @see com.esotericsoftware.kryonet.Listener#connected(com.esotericsoftware.kryonet.Connection)
	 */
	@Override
	public void onConnected(Connection conn) {				
		this.clients.addRemoteClient(conn.getId(), new RemoteClient(conn));
	}
	
	/* (non-Javadoc)
	 * @see com.esotericsoftware.kryonet.Listener#disconnected(com.esotericsoftware.kryonet.Connection)
	 */
	@Override
	public void onDisconnected(Connection conn) {							
		try {
			this.clientDisconnect(conn, new ClientDisconnectMessage());
		} 
		catch (IOException e) {
			Cons.println("*** Error disconnecting client: " + e);
		}		
		
		this.clients.removeClient(conn.getId());		
	}
	
	/*
	 * (non-Javadoc)
	 * @see palisma.shared.NetworkProtocol#processMessage(com.esotericsoftware.kryonet.Connection, palisma.network.messages.Message)
	 */
	@Override
	protected void processMessage(Connection conn, NetMessage message) throws IOException {		
		if(message instanceof UserInputMessage) {
			userInput(conn, (UserInputMessage)message);
		}
		else if(message instanceof ConnectRequestMessage) {
			connectRequest(conn, (ConnectRequestMessage)message);
		}
		else if(message instanceof ClientReadyMessage) {
			clientReady(conn, (ClientReadyMessage)message);
		}
		else if(message instanceof PlayerSwitchWeaponClassMessage) {
			playerSwitchWeaponClass(conn, (PlayerSwitchWeaponClassMessage)message );
		}
		else if(message instanceof AICommandMessage) {
			aiCommand(conn, (AICommandMessage)message);
		}
		else if(message instanceof PlayerSwitchTeamMessage) {
			playerSwitchedTeam(conn, (PlayerSwitchTeamMessage)message);
		}
		else if(message instanceof ClientDisconnectMessage) {
			clientDisconnect(conn, (ClientDisconnectMessage)message);
		}
		else if(message instanceof TextMessage) {
			textMessage(conn, (TextMessage)message);
		}		
		else if(message instanceof TeamTextMessage) {
			teamTextMessage(conn, (TeamTextMessage)message);
		}
		else if(message instanceof PlayerNameChangeMessage) {
			playerNameChangedMessage(conn, (PlayerNameChangeMessage)message);
		}
		else if(message instanceof RconMessage) {
			rconMessage(conn, (RconMessage)message);
		}
		else {
			Cons.println("Unknown message: " + message);
		}
	}
	
	/**
	 * @param message
	 */
	private void playerSwitchWeaponClass(Connection conn, PlayerSwitchWeaponClassMessage message) {
		RemoteClient client = this.clients.getClient(conn.getId());
		if(client!=null) {
			Player player = client.getPlayer();
			
			Type weaponType = Type.fromNet(message.weaponType);
			Team team = player.getTeam();
			if(team!=null) {
				boolean allowed = false;
				
				switch(team.getId()) {
					case Team.ALLIED_TEAM_ID:
						switch(weaponType) {
							case THOMPSON:
							case M1_GARAND:
							case SPRINGFIELD:
							case RISKER:
							case SHOTGUN:
							case ROCKET_LAUNCHER:
								allowed = true;
								break;
							default: allowed = false;
						}
						break;
					case Team.AXIS_TEAM_ID:
						switch(weaponType) {
							case MP40:
							case MP44:
							case KAR98:
							case RISKER:
							case SHOTGUN:
							case ROCKET_LAUNCHER:
								allowed = true;
								break;
							default: allowed = false;
						}
						break;
					default:
							break;
				}
				
				if(allowed) {
					player.setWeaponClass(weaponType);
				}
			}
		}
	}

	public void connectRequest(Connection conn, ConnectRequestMessage msg) throws IOException {
		RemoteClient client = this.clients.getClient(conn.getId());
		client.setName(msg.name);
		
		this.game.playerJoined(client.getPlayer());
		
		/* notify all players (accept the one connecting) that a new player
		   is coming in
		 */
		PlayerConnectedMessage connectBroadcast = new PlayerConnectedMessage();
		connectBroadcast.name = msg.name;
		connectBroadcast.playerId = client.getId();
		this.server.sendToAllExcept(Endpoint.FLAG_RELIABLE, connectBroadcast, conn.getId());
		
		// Give the new player the full game state
		ConnectAcceptedMessage acceptedMessage = new ConnectAcceptedMessage();
		acceptedMessage.playerId = conn.getId();
		acceptedMessage.gameState = this.game.getNetGameState();	
		this.server.sendTo(Endpoint.FLAG_RELIABLE, acceptedMessage, conn.getId());
				
	}
	
	public void clientReady(Connection conn, ClientReadyMessage msg) {
		RemoteClient client = this.clients.getClient(conn.getId());	
		if(client != null) {
			if(!client.isReady()) {
				client.setReady(true);
					
//				this.game.spawnPlayerEntity(client.getId());
				client.getPlayer().setPing(conn.getReturnTripTime());
			}
		}
		
	}
	
	public void userInput(Connection conn, UserInputMessage msg) {		
		this.game.applyPlayerInput(conn.getId(), msg);
	}
	
	public void clientDisconnect(Connection conn, ClientDisconnectMessage msg) throws IOException {		
		this.game.playerLeft(conn.getId());
		PlayerDisconnectedMessage disconnectBroadcast = new PlayerDisconnectedMessage();
		disconnectBroadcast.playerId = conn.getId();
		this.server.sendToAllExcept(Endpoint.FLAG_RELIABLE, disconnectBroadcast, conn.getId());
	}
	
	public void playerSwitchedTeam(Connection conn, PlayerSwitchTeamMessage msg) throws IOException {		
		if ( game.playerSwitchedTeam( (int)msg.playerId, msg.teamId) ) {
			this.server.sendToAll(Endpoint.FLAG_RELIABLE, msg);
		}
	}
	
	public void textMessage(Connection conn, TextMessage msg) throws IOException  {
		msg.playerId = conn.getId();		
		this.server.sendToAll(Endpoint.FLAG_RELIABLE, msg);
	}
	
	public void teamTextMessage(Connection conn, TeamTextMessage msg) throws IOException  {
		RemoteClient client = this.clients.getClient(conn.getId());
		msg.playerId = conn.getId();
		
		Player player = client.getPlayer();		
		Team team = this.game.getGameType().getTeam(player);
		NetTeam netTeam = team.getNetTeam();
		for(int i = 0; i < netTeam.playerIds.length; i++) {
			this.server.sendTo( Endpoint.FLAG_RELIABLE, msg, netTeam.playerIds[i]);
		}
	}
	
	public void playerNameChangedMessage(Connection conn, PlayerNameChangeMessage msg ) throws IOException {
		RemoteClient client = this.clients.getClient(conn.getId());	
		Player player = client.getPlayer();
		player.setName(msg.name);
	}
	
	public void aiCommand(Connection conn, AICommandMessage msg) throws IOException {
		RemoteClient client = this.clients.getClient(conn.getId());	
		Player player = client.getPlayer();
		
		PlayerInfo botPlayer = game.getPlayerById(msg.botId);
		if(botPlayer.isBot()) {
			if(player.getTeamId() == botPlayer.getTeamId()) {				
				game.getAISystem().receiveAICommand(botPlayer, msg.command);
			}
		}
	}
	
	public void rconMessage(Connection conn, RconMessage msg) throws IOException {
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
					
					this.server.sendTo(Endpoint.FLAG_RELIABLE, tokenMessage, client.getId());
				}
				else if (cmd.startsWith("logoff")) {
					client.setRconAuthenticated(false);
					client.setRconToken(ServerContext.INVALID_RCON_TOKEN);
				}
				else if(cmd.startsWith("password")) {
					if(!client.hasRconToken()) {
						RconMessage rconMsg = new RconMessage("You must first initiate a login: rcon login");
						this.server.sendTo(Endpoint.FLAG_RELIABLE, rconMsg, client.getId());
					}
					else {
						String password = cmd.replace("password ", "");
						
						// if the password hashes match, they are authenticated
						String serverPassword = this.serverContext.getRconPassword(client.getRconToken()); 
						
						if( serverPassword.equals(password) ) {
							client.setRconAuthenticated(true);
						}
						else {
							RconMessage rconMsg = new RconMessage("Invalid password.");
							this.server.sendTo(Endpoint.FLAG_RELIABLE, rconMsg, client.getId());
							client.setRconAuthenticated(false);
						}
					}
				}
				else if(client.isRconAuthenticated()) {
					console.execute(msg.getCommand());
				}
				else {
					RconMessage rconMsg = new RconMessage("You must first authenticate by executing these commands:\n rcon login\n rcon password [password]");
					this.server.sendTo(Endpoint.FLAG_RELIABLE, rconMsg, client.getId());
				}
			}
		}
	}
}
