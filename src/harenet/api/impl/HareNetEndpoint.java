/*
 * (c)2006 Expeditors International of Washington, Inc.
 * Business confidential and proprietary.  This information may not be reproduced
 * in any form without advance written consent of an authorized officer of the
 * copyright holder.
 *
 */
package harenet.api.impl;

import harenet.ByteCounterIOBuffer;
import harenet.NetConfig;
import harenet.api.Connection;
import harenet.api.ConnectionListener;
import harenet.api.Endpoint;
import harenet.messages.Message;
import harenet.messages.NetMessage;
import harenet.messages.ReliableNetMessage;
import harenet.messages.UnReliableNetMessage;

import java.util.Vector;
import java.util.concurrent.atomic.AtomicBoolean;

import seventh.shared.Cons;

/**
 * JeNet Endpoint implementation.
 *
 */
public abstract class HareNetEndpoint implements Endpoint {

	private AtomicBoolean shutdown;
	private Thread thread;
	
	private Vector<ConnectionListener> listeners;
	
//	private Output output;	
		
	private ByteCounterIOBuffer byteCounter;
	private NetConfig netConfig;
	
	private int pollRate;
	
	/**
	 * @param netConfig
	 */
	public HareNetEndpoint(NetConfig netConfig) {	
		this.netConfig = netConfig;
		this.shutdown = new AtomicBoolean(true);
		this.listeners = new Vector<ConnectionListener>();
		
	//	this.output = new Output(1500, 4098);
		this.byteCounter = new ByteCounterIOBuffer();
		
		this.pollRate = netConfig.getPollRate();
	}
	
	/**
	 * @return the netConfig
	 */
	public NetConfig getNetConfig() {
		return netConfig;
	}
	
	
	/**
	 * Writes out a {@link NetMessage} 
	 * 
	 * @param protocolFlags
	 * @param message
	 * @return the {@link Message} containing the {@link NetMessage} pay load
	 */
	protected Message writeMessage(int protocolFlags, NetMessage message) {    	
		byteCounter.clear();
		message.write(byteCounter);
		
    	Message msg = ((protocolFlags&Endpoint.FLAG_RELIABLE)!=0) ? 
    						new ReliableNetMessage(message, (short)byteCounter.capacity()) :
    						new UnReliableNetMessage(message, (short)byteCounter.capacity()) ;
        return msg;
	}
	
	
	/**
	 * Reads in the {@link Message} extracting out the {@link NetMessage} pay load
	 * @param message
	 * @return the {@link NetMessage}
	 */
	protected NetMessage readMessage(Message message) {    	    	    	
		NetMessage obj = message.getMessage();
    	return obj;
	}
	
	/**
	 * @return the listeners
	 */
	public Vector<ConnectionListener> getListeners() {
		return listeners;
	}
	
	protected boolean isShutdown() {
		return this.shutdown.get();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Runnable#run()
	 */
	@Override
	public void run() {
		while(!this.shutdown.get()) {
			try {							
				update(this.pollRate);							
			}
			catch(Exception e) {
				Cons.println("*** Error updating network: " + e);
			}
		}
		
	}

	private void killNetworkThread() {
		if(thread != null) {
			this.shutdown.set(true);
			try {
				thread.join(5000);
			} catch(InterruptedException ignore) {}
		}
	}

	/* (non-Javadoc)
	 * @see net.jenet.api.Endpoint#start()
	 */
	@Override
	public void start() {
		killNetworkThread();
		
		thread = new Thread(this, "Network Thread");
		thread.setDaemon(true);
		thread.start();
		this.shutdown.set(false);
	}

	/* (non-Javadoc)
	 * @see net.jenet.api.Endpoint#stop()
	 */
	@Override
	public void stop() {
		if(this.shutdown.get()) {
			return;
		}				
		
		killNetworkThread();
				
		close();
		
		
	}

	/**
	 * @param conn
	 * @param event
	 */
	protected void fireOnConnectionEvent(Connection conn) {
		int size = this.listeners.size();
		for(int i = 0; i < size; i++) {
			this.listeners.get(i).onConnected(conn);
		}
	}
	
	/**
	 * @param conn
	 * @param event
	 */
	protected void fireOnDisconnectionEvent(Connection conn) {
		int size = this.listeners.size();
		for(int i = 0; i < size; i++) {
			this.listeners.get(i).onDisconnected(conn);
		}
	}
	
	/**
	 * @param conn
	 */
	protected void fireOnReceivedEvent(Connection conn, Message msg) {
		Object message = readMessage(msg);
		
		int size = this.listeners.size();
		for(int i = 0; i < size; i++) {
			this.listeners.get(i).onReceived(conn, message);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.jenet.api.Endpoint#addConnectionListener(net.jenet.api.ConnectionListener)
	 */
	@Override
	public void addConnectionListener(ConnectionListener listener) {
		this.listeners.add(listener);
	}

	/* (non-Javadoc)
	 * @see net.jenet.api.Endpoint#removeConnectionListener(net.jenet.api.ConnectionListener)
	 */
	@Override
	public void removeConnectionListener(ConnectionListener listener) {
		this.listeners.remove(listener);
	}

}
