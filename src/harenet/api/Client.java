/*
 * see license.txt
 */
package harenet.api;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Represents a network connection to a server.
 * 
 * @author Tony
 *
 */
public interface Client extends Connection {

	/**
	 * Attempts to connect to the remove host
	 * 
	 * @param timeout
	 * @param host
	 * @throws IOException
	 * @return true if connected
	 */
	public boolean connect(int timeout, InetSocketAddress host) throws IOException;
	
	/**
	 * Sets the keep alive time
	 * 
	 * @param keepAliveMillis (msec)
	 */
	public void setKeepAlive(int keepAliveMillis);
	
}
