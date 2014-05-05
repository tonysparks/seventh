/*
 * see license.txt
 */
package harenet.api;

import java.io.IOException;

/**
 * Connection endpoint
 * 
 * @author Tony
 *
 */
public interface Endpoint extends Runnable {

	/**
	 * A flag for a reliable message, by default messages are
	 * unreliable
	 */
	public static final int FLAG_RELIABLE = (1<<0);
	
	/**
	 * A flag for unsequenced, by default messages are
	 * sequenced
	 */
	public static final int FLAG_UNSEQUENCED = (1<<1);
	
	/**
	 * A flag for sequenced but unreliable
	 */
	public static final int FLAG_UNRELIABLE = 0;
		
	/**
	 * Close out the connection
	 */
	public void close();
	
	/**
	 * Reads/writes any pending data
	 * 
	 * @param timeout
	 */
	public void update(int timeout) throws IOException;
	
	/**
	 * Starts a new thread, continually invoking {@link Client#run()}
	 */
	public void start();
	
	/**
	 * closes the connection, and terminates the thread
	 */
	public void stop();
	
	
	/**
	 * Adds a {@link ConnectionListener}
	 * @param listener
	 */
	public void addConnectionListener(ConnectionListener listener);
	
	/**
	 * Removes a {@link ConnectionListener}
	 * @param listener
	 */
	public void removeConnectionListener(ConnectionListener listener);
	
	/**
	 * Removes all {@link ConnectionListener}s
	 */
//	public void removeAllConnectionListeners();
}
