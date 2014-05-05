/*
 * see license.txt
 */
package harenet.api;

import harenet.messages.NetMessage;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * A connection between two computers
 * 
 * @author Tony
 *
 */
public interface Connection extends Endpoint {
	
	/**
	 * @return this connections ID
	 */
	public int getId();
	
	/**
	 * @return true if connected
	 */
	public boolean isConnected();
	
	/**
	 * @return the return trip time in msec
	 */
	public int getReturnTripTime();
	
	public long getNumberOfBytesSent();
	public long getNumberOfBytesReceived();

	public long getAvgBitsPerSecRecv();
	public long getAvgBitsPerSecSent();
	
	/**
	 * Sends a message to the server
	 * @param protocolFlags
	 * @param msg
	 * @throws IOException
	 */
	public void send(int protocolFlags, NetMessage msg) throws IOException;
	
	/**
	 * @return the remote address
	 */
	public InetSocketAddress getRemoteAddress();

}
