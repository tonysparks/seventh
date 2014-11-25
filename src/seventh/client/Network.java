/*
 * see license.txt 
 */
package seventh.client;

import harenet.api.Client;
import harenet.api.Endpoint;
import harenet.api.impl.HareNetClient;
import harenet.messages.NetMessage;

import java.io.IOException;
import java.net.InetSocketAddress;

import seventh.shared.Command;
import seventh.shared.Cons;
import seventh.shared.Console;
import seventh.shared.TimeStep;

/**
 * Simple network facade -- handles all network communications.
 * 
 * @author Tony
 *
 */
public class Network {

	private ClientProtocolListener listener;
	private Client client;
	
	
	/**
	 * @param config
	 * @param console
	 */
	public Network(ClientSeventhConfig config, Console console) {
		this.client = new HareNetClient(config.getNetConfig());
		console.addCommand(new Command("netstat") {
			/* (non-Javadoc)
			 * @see seventh.shared.Command#execute(seventh.shared.Console, java.lang.String[])
			 */
			@Override
			public void execute(Console console, String... args) {
				if(client.isConnected()) {
					console.println("");
					console.println("Bandwidth Usage:");
					console.println("\tTotal Incoming Bytes: " + (client.getNumberOfBytesReceived() / 1024) + "KiB");
					console.println("\tTotal Outgoing Bytes: " + (client.getNumberOfBytesSent() / 1024) + "KiB");
					console.println("\tIncoming bit/s: " + client.getAvgBitsPerSecRecv());
					console.println("\tOutgoing bit/s: " + client.getAvgBitsPerSecSent());
					console.println("");
				}
				else {
					console.println("Not connected.");
				}
				
			}
		});
	}
	
	/**
	 * Pump network updates
	 * 
	 * @param timeStep
	 */
	public void updateNetwork(TimeStep timeStep) {
		if(this.listener != null) {
			this.listener.updateNetwork(timeStep);
			this.listener.postQueuedMessages();
		}
	}

	/**
	 * Disconnect from the server
	 */
	public void disconnect() {
		if(this.listener != null) {
			this.listener.close();
		}
	}
	
	/**
	 * @return true if connected to a server
	 */
	public boolean isConnected() {
		return client.isConnected();
	}
	
	/**
	 * Connects to the server
	 * 
	 * @param host
	 * @param port
	 * @throws Exception
	 */
	public void connect(String host, int port) throws Exception {
		
		client.stop();
		if(listener!=null) {
			client.removeConnectionListener(listener);
		}

		this.listener = new ClientProtocolListener(client);
		client.addConnectionListener(listener);
//		client.addConnectionListener(new LagConnectionListener(50, 100, listener));
				
		InetSocketAddress address = new InetSocketAddress(host, port);
		if(client.connect(12000, address.getAddress(), address.getPort())) {
			client.start();
		}
		else {
			throw new IOException("Unable to connect to: " + host + ":" + port);
		}
	}
	
	/**
	 * Sets the protocol listener
	 * 
	 * @param protocol
	 */
	public void setProtocolHandler(ClientProtocol protocol) {
		if(this.listener == null) {
			throw new IllegalArgumentException("Not connected!");
		}
		
		this.listener.setHandler(protocol);
	}
	
	
	/**
	 * Queues a reliable {@link NetMessage} for the next available network update.
	 * @param msg
	 */
	public void queueSendReliableMessage(NetMessage msg) {
		this.listener.queueSendMessage(Endpoint.FLAG_RELIABLE, msg);
	}
	
	/**
	 * Queues an unreliable {@link NetMessage} for the next available network update.
	 * @param msg
	 */
	public void queueUnreliableMessage(NetMessage msg) {
		this.listener.queueSendMessage(Endpoint.FLAG_UNRELIABLE, msg);
	}
	
	/**
	 * Sends the reliable {@link NetMessage} immediately
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendReliableMessage(NetMessage message) {
		
		try {
			this.listener.sendMessage(Endpoint.FLAG_RELIABLE, message);
		} 
		catch (IOException e) {
			Cons.println("*** Error sending reliable packet - " + e);
		}
	}
	
	/**
	 * Sends the unreliable {@link NetMessage} immediately.
	 * 
	 * @param message
	 * @throws IOException
	 */
	public void sendUnReliableMessage(NetMessage message) {
		try {
			this.listener.sendMessage(Endpoint.FLAG_UNRELIABLE, message);
		}
		catch(IOException e) {
			Cons.println("*** Error sending unreliable packet - " + e);
		}
	}
}
