/*
 * see license.txt
 */
package harenet.api.impl;

import harenet.Host;
import harenet.Host.MessageListener;
import harenet.NetConfig;
import harenet.Peer;
import harenet.api.Client;
import harenet.messages.Message;
import harenet.messages.NetMessage;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Harenet Client implementation
 * 
 * @author Tony
 */
public class HareNetClient extends HareNetEndpoint implements Client {

	private int connectionId;
	
	private Host host;
	private Peer peer;
		
	
	private class EndpointMessageListener implements MessageListener {
				
		/* (non-Javadoc)
		 * @see netspark.Host.MessageListener#onConnected(netspark.Peer)
		 */
		@Override
		public void onConnected(Peer peer) {
			fireOnConnectionEvent(HareNetClient.this);
		}
		
		/* (non-Javadoc)
		 * @see netspark.Host.MessageListener#onDisconnected(netspark.Peer)
		 */
		@Override
		public void onDisconnected(Peer peer) {
			fireOnDisconnectionEvent(HareNetClient.this);			
		}
		
		/* (non-Javadoc)
		 * @see netspark.Host.MessageListener#onMessage(netspark.Peer, netspark.messages.Message)
		 */
		@Override
		public void onMessage(Peer peer, Message message) {
			fireOnReceivedEvent(HareNetClient.this, message);
		}
	}
	
	private MessageListener listener;
	
	/**
	 */
	public HareNetClient(NetConfig netConfig) {
		super(netConfig);
		this.connectionId = -1;
		
		this.listener = new EndpointMessageListener();
	}
			
	/**
	 * @param kryo
	 * @param connectionId
	 * @param host
	 * @param peer
	 */
	public HareNetClient(NetConfig netConfig, int connectionId, Host host, Peer peer) {
		super(netConfig);
		this.connectionId = connectionId;
		this.host = host;
		this.peer = peer;
	}



	/* (non-Javadoc)
	 * @see net.jenet.api.Connection#getId()
	 */
	@Override
	public int getId() {
		return this.connectionId;
	}

	/* (non-Javadoc)
	 * @see net.Connection#getReturnTripTime()
	 */
	@Override
	public int getReturnTripTime() {	
		return (int)this.peer.getRoundTripTime();
	}
	
	/* (non-Javadoc)
	 * @see net.Connection#getNumberOfBytesReceived()
	 */
	@Override
	public long getNumberOfBytesReceived() {	
		return this.peer.getNumberOfBytesRecv();
	}
	
	/* (non-Javadoc)
	 * @see net.Connection#getNumberOfBytesSent()
	 */
	@Override
	public long getNumberOfBytesSent() {	
		return this.peer.getNumberOfBytesSent();
	}
	
	/* (non-Javadoc)
	 * @see net.Connection#getAvgBitsPerSecRecv()
	 */
	@Override
	public long getAvgBitsPerSecRecv() {
		return peer.getAvgBitsPerSecRecv();
	}
	
	/* (non-Javadoc)
	 * @see net.Connection#getAvgBitsPerSecSent()
	 */
	@Override
	public long getAvgBitsPerSecSent() {
		return peer.getAvgBitsPerSecSent();
	}
	
	
	/* (non-Javadoc)
	 * @see harenet.api.Connection#getNumberOfDroppedPackets()
	 */
	@Override
	public long getNumberOfDroppedPackets() {	
		return peer.getNumberOfDroppedPackets();
	}
	
	/* (non-Javadoc)
	 * @see harenet.api.Connection#getNumberOfBytesCompressed()
	 */
	@Override
	public long getNumberOfBytesCompressed() {
	    return peer.getNumberOfBytesCompressed();
	}
	
	/* (non-Javadoc)
	 * @see net.jenet.api.Connection#isConnected()
	 */
	@Override
	public boolean isConnected() {
		return (this.peer != null) && (this.peer.isConnected());
	}

	/* (non-Javadoc)
	 * @see net.jenet.api.Connection#send(net.jenet.api.Endpoint.ProtocolType, java.lang.Object)
	 */
	@Override
	public void send(int protocolFlags, NetMessage msg) throws IOException {
		if(isConnected()) {
			Message packet = writeMessage(protocolFlags, msg);
			peer.send(packet);
		}
	}

	/* (non-Javadoc)
	 * @see net.jenet.api.Connection#getRemoteAddress()
	 */
	@Override
	public InetSocketAddress getRemoteAddress() {
		return (this.peer !=null) ? this.peer.getAddress() : null;
	}

	/*
	 * (non-Javadoc)
	 * @see harenet.api.Client#connect(int, java.net.InetSocketAddress)
	 */
	@Override
	public boolean connect(int timeout, InetSocketAddress address)
			throws IOException {
			
		host = new Host(getNetConfig(), null);			
		peer=host.connect(address);
		this.connectionId = peer.getId();
		
		update(timeout);
		                  			
		return peer.isConnected();
	}

	/* (non-Javadoc)
	 * @see net.jenet.api.Client#setKeepAlive(int)
	 */
	@Override
	public void setKeepAlive(int keepAliveMillis) {
	}


	
	/* (non-Javadoc)
	 * @see net.jenet.api.Endpoint#close()
	 */
	@Override
	public void close() {
		
		try {
			if(this.peer != null && this.peer.isConnected()) {
				this.peer.disconnect();
				update(100);
			}
		}
		catch(Exception ignore) {					
		}
		finally {
			try {
				if(host!=null) {
					host.destroy();
				}
			}
			catch(Exception ignore) {					
			}
		}
	}

	/* (non-Javadoc)
	 * @see net.jenet.api.Endpoint#update(int)
	 */
	@Override
	public void update(int timeout) {
		if (host != null) {
			synchronized(host) {
				try {
					host.update(this.listener, timeout);
				} 
				catch (IOException e) {
					host.getLogger().error(e.toString());
				}
			}
		}
	}
	
}
