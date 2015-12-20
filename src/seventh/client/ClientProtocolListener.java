/*
 * see license.txt 
 */
package seventh.client;

import harenet.api.Client;
import harenet.api.Connection;
import harenet.messages.NetMessage;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import seventh.network.messages.BombDisarmedMessage;
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
import seventh.shared.Cons;
import seventh.shared.NetworkProtocol;


/**
 * @author Tony
 *
 */
public class ClientProtocolListener extends NetworkProtocol {
	static class QueuedMessage {
		int flags;
		NetMessage msg;
		
		/**
		 * 
		 */
		public QueuedMessage(int flags, NetMessage msg) {
			this.flags = flags;
			this.msg = msg;
		}
	}
	
	private Queue<QueuedMessage> outboundQ;
	private Client client;
	private ClientProtocol handler;
	
	/**
	 * @param client
	 * @param handler
	 */
	public ClientProtocolListener(Client client) {
		super(client);
		this.client = client;				
		this.outboundQ = new ConcurrentLinkedQueue<QueuedMessage>();
	}
	
	/**
	 * send all queued up messages
	 */
	public void postQueuedMessages() {
		while(!this.outboundQ.isEmpty()) {
//			this.client.sendTCP(this.outboundQ.poll());
			QueuedMessage msg = this.outboundQ.poll();
			try {
				this.client.send(msg.flags, msg.msg);
			} catch (IOException e) {
				Cons.println("*** Failed to send msg - " + e);
			}
		}
	}
	
	/**
	 * @param msg
	 */
	public void queueSendMessage(int flags, NetMessage msg) {
		this.outboundQ.add(new QueuedMessage(flags, msg));
	}
	
	public void sendMessage(int flags, NetMessage msg) throws IOException {
//		this.client.sendTCP(msg);
		this.client.send(flags, msg);
	}
	
	/**
	 * @param handler the handler to set
	 */
	public void setHandler(ClientProtocol handler) {
		this.handler = handler;
	}
	
	/**
	 * @return the handler
	 */
	public ClientProtocol getHandler() {
		return handler;
	}
	
	/* (non-Javadoc)
	 * @see net.ConnectionListener#onConnected(net.Connection)
	 */
	@Override
	public void onConnected(Connection conn) {
		Cons.println("Client connected");
	}
	
	/*
	 * (non-Javadoc)
	 * @see net.ConnectionListener#onDisconnected(net.Connection)
	 */
	@Override
	public void onDisconnected(Connection conn) {			
		Cons.println("Client disconnected");
		if(this.handler != null) {
		    this.handler.onDisconnect(conn);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see palisma.shared.NetworkProtocol#processMessage(com.esotericsoftware.kryonet.Connection, palisma.network.messages.Message)
	 */
	@Override
	protected void processMessage(Connection conn, NetMessage message) throws IOException {
		if(this.handler == null) {
			return;
		}
		
		/* game messages first, from most frequent to least */
		
		if(message instanceof GameUpdateMessage) {
			this.handler.gameUpdate(conn, (GameUpdateMessage)message);
		}
		else if(message instanceof GamePartialStatsMessage) {
			this.handler.gamePartialStats(conn, (GamePartialStatsMessage)message);
		}
		else if(message instanceof GameStatsMessage) {
			this.handler.gameStats(conn, (GameStatsMessage)message);
		}		
		else if(message instanceof PlayerKilledMessage) {
			this.handler.playerKilled(conn, (PlayerKilledMessage)message);
		}
		else if(message instanceof PlayerSpawnedMessage) {
			this.handler.playerSpawned(conn, (PlayerSpawnedMessage)message);
		}
		else if(message instanceof GameEndedMessage) {
			this.handler.gameEnded(conn, (GameEndedMessage)message);
		}
		else if(message instanceof GameReadyMessage) {
			this.handler.gameReady(conn, (GameReadyMessage)message);
		}
		else if(message instanceof PlayerSwitchTeamMessage) {
			this.handler.playerSwitchedTeamMessage(conn, (PlayerSwitchTeamMessage)message);
		}
		else if(message instanceof RoundStartedMessage) {
			this.handler.roundStarted(conn, (RoundStartedMessage)message);
		}
		else if(message instanceof RoundEndedMessage) {
			this.handler.roundEnded(conn, (RoundEndedMessage)message);
		}
		else if(message instanceof BombPlantedMessage) {
			this.handler.bombPlanted(conn, (BombPlantedMessage)message);
		}
		else if(message instanceof BombDisarmedMessage) {
			this.handler.bombDisarmed(conn, (BombDisarmedMessage)message);
		}
		else if(message instanceof TileRemovedMessage) {
		    this.handler.tileRemoved(conn, (TileRemovedMessage)message);
		}
		else if(message instanceof TilesRemovedMessage) {
		    this.handler.tilesRemoved(conn, (TilesRemovedMessage)message);
		}
		/* None game messages */
		
		else if(message instanceof ConnectAcceptedMessage) {
			this.handler.connectAccepted(conn, (ConnectAcceptedMessage)message);
		}
		else if(message instanceof PlayerConnectedMessage) {
			this.handler.playerConnectedMessage(conn, (PlayerConnectedMessage)message);
		}
		else if(message instanceof PlayerDisconnectedMessage) {
			this.handler.playerDisconnectedMessage(conn, (PlayerDisconnectedMessage)message);
		}
		else if(message instanceof TextMessage) {
			this.handler.textMessage(conn, (TextMessage)message);
		}		
		else if(message instanceof TeamTextMessage) {
			this.handler.teamTextMessage(conn, (TeamTextMessage)message);
		}	
		else if(message instanceof PlayerSpeechMessage) {
			this.handler.playerSpeech(conn, (PlayerSpeechMessage)message);
		}
		else if(message instanceof RconMessage) {
			this.handler.rconMessage(conn, (RconMessage)message);
		}
		else if(message instanceof RconTokenMessage) {
			this.handler.rconTokenMessage(conn, (RconTokenMessage)message);
		}
		else {
			Cons.println("Unknown message: " + message);
		}
	}
			
}
