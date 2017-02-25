/*
 * see license.txt
 */
package harenet.api;

import harenet.messages.NetMessage;

import java.io.IOException;
import java.util.List;


/**
 * Manages remote connections from {@link Client}s
 * 
 * @author Tony
 *
 */
public interface Server extends Endpoint {

    /**
     * @return true if running, false otherwise
     */
    public boolean isRunning();
    
    /**
     * Binds the server to a port
     * @param port
     * @throws IOException
     */
    public void bind(int port) throws IOException;
    
    /**
     * Reserves the specified ID.  If this ID is already claimed
     * nothing happens.
     * 
     * @return the reserved id
     */
    public int reserveId();
    
    
    /**
     * @return a list of active {@link Connection} to this {@link Server}
     */
    public List<Connection> getConnections();
    
    /**
     * Sends the message to all active connections
     * @param protocolFlags
     * @param msg
     * @throws IOException
     */
    public void sendToAll(int protocolFlags, NetMessage msg) throws IOException;
    
    /**
     * Sends the message to all active connections except the supplied connectionId
     * @param protocolFlags
     * @param msg
     * @param connectionId
     * @throws IOException
     */
    public void sendToAllExcept(int protocolFlags, NetMessage msg, int connectionId) throws IOException;
    
    /**
     * Sends the message to the connection
     * @param protocolFlags
     * @param msg
     * @param connectionId
     * @throws IOException
     */
    public void sendTo(int protocolFlags, NetMessage msg, int connectionId) throws IOException;
}
