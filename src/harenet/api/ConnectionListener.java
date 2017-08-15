/*
 * see license.txt
 */
package harenet.api;

/**
 * Listens for events/messages 
 * 
 * @author Tony
 *
 */
public interface ConnectionListener {    
    /**
     * A connection has been made
     * @param conn
     */
    void onConnected(Connection conn);
    
    /**
     * The connection has been terminated
     * @param conn
     */
    void onDisconnected(Connection conn);
    
    /**
     * The server is full, and this client is not allowed
     * to connect
     * 
     * @param conn
     */
    void onServerFull(Connection conn);
    
    /**
     * A message has been received
     * 
     * @param conn
     * @param msg
     */
    void onReceived(Connection conn, Object msg);
}
