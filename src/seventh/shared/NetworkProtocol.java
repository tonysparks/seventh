/*
 * see license.txt 
 */
package seventh.shared;

import harenet.api.Connection;
import harenet.api.ConnectionListener;
import harenet.api.Endpoint;
import harenet.messages.NetMessage;

import java.io.IOException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;



/**
 * @author Tony
 *
 */
public abstract class NetworkProtocol implements ConnectionListener {

    public static enum MessageType {
        ALL_CLIENTS,
        ALL_EXCEPT,
        ONE,
    }
    
    
    private class InboundMessage {
        Connection conn;
        NetMessage msg;
        /**
         * @param conn
         * @param msg
         */
        public InboundMessage(Connection conn, NetMessage msg) {            
            this.conn = conn;
            this.msg = msg;
        }                
    }
    
    private Queue<InboundMessage> messageQ;
    private Endpoint endpoint;    
    
    /**
     * 
     */
    public NetworkProtocol(Endpoint endpoint) {
        this.endpoint = endpoint;
        this.messageQ = new ConcurrentLinkedQueue<InboundMessage>();
    }


    /*
     * (non-Javadoc)
     * @see net.ConnectionListener#onReceived(net.Connection, java.lang.Object)
     */
    @Override
    public void onReceived(Connection conn, Object message) {
        if(message instanceof NetMessage) {
            queueInboundMessage(conn, (NetMessage)message);
        }
        else {
            //Cons.println("Received a non Message object type: " + message.getClass());
        }
    }
    
    protected void queueInboundMessage(Connection conn, NetMessage msg) {
        this.messageQ.add(new InboundMessage(conn, msg));
    }
    
    /**
     * Close out the connection
     */
    public void close() {
        try { this.endpoint.stop(); } catch(Exception e) {}
        try { this.endpoint.close(); } catch(Exception e) {}
    }
    
    
    /**
     * Post any Queued up messages
     */
    public abstract void postQueuedMessages();
    
    /**
     * Process game specific messages
     * 
     * @param conn
     * @param message
     */
    protected abstract void processMessage(Connection conn, NetMessage message) throws IOException;
    
    /**
     * Reads/writes to the network buffers
     * @param timeStep
     */
    public void updateNetwork(TimeStep timeStep) {        
        int maxMessage = 100;
        while(!this.messageQ.isEmpty() && maxMessage > 0) {
            maxMessage--;
            InboundMessage p = this.messageQ.poll();
            try {
                processMessage(p.conn, p.msg);
            } catch (IOException e) {
                Cons.println("Failed to send message: " + e);
            }
        }
    }
    
}
