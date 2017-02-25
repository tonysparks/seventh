/*
 * see license.txt
 */
package harenet.api.impl;

import harenet.Host;
import harenet.Host.MessageListener;
import harenet.NetConfig;
import harenet.Peer;
import harenet.api.Connection;
import harenet.api.Server;
import harenet.messages.Message;
import harenet.messages.NetMessage;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.List;

/**
 * Harenet {@link Server} implementation
 * @author Tony 
 */
public class HareNetServer extends HareNetEndpoint implements Server {

    private Host host;
    private Connection[] connections;
    
    private MessageListener listener;
    
    private class ServerMessageListener implements MessageListener {
        
        /* (non-Javadoc)
         * @see netspark.Host.MessageListener#onConnected(netspark.Peer)
         */
        @Override
        public void onConnected(Peer peer) {

            byte peerId = peer.getId();
            
            Connection conn = connections[peerId];
            if(conn != null) {
                if(conn.getId() != peerId) {
                    conn.close();                        
                }
            }
            
            connections[peerId] = new HareNetClient(getNetConfig(), peerId, host, peer);                
            fireOnConnectionEvent(connections[peerId]);            
        }
        
        /* (non-Javadoc)
         * @see netspark.Host.MessageListener#onDisconnected(netspark.Peer)
         */
        @Override
        public void onDisconnected(Peer peer) {            
            byte peerId = peer.getId();
            
            Connection conn = connections[peerId];
            if(conn != null) {
                fireOnDisconnectionEvent(conn);
            }        
        }
        
        /* (non-Javadoc)
         * @see netspark.Host.MessageListener#onMessage(netspark.Peer, netspark.messages.Message)
         */
        @Override
        public void onMessage(Peer peer, Message message) {
            byte peerId = peer.getId();
            
            Connection conn = connections[peerId];
            if(conn != null) {
                fireOnReceivedEvent(conn, message);
            }    
        }
    }
    
    /**
     * 
     */
    public HareNetServer(NetConfig netConfig) {
        super(netConfig);
        this.connections = new Connection[netConfig.getMaxConnections()];        
        this.listener = new ServerMessageListener();
    }
    
    /* (non-Javadoc)
     * @see harenet.api.Server#reserveId(int)
     */
    @Override
    public int reserveId() {
        if(host != null) {
            return host.reserveId();
        }
        return -1;
    }
    
    /* (non-Javadoc)
     * @see net.jenet.api.Endpoint#close()
     */
    @Override
    public void close() {                        
        try {
            if(host!=null) {                
                host.destroy();
            }
        }
        catch(Exception ignore) {                    
        }
        
    
    }
    
    /* (non-Javadoc)
     * @see net.jenet.api.Server#isRunning()
     */
    @Override
    public boolean isRunning() {    
        return ! this.isShutdown();
    }

    /* (non-Javadoc)
     * @see net.jenet.api.Endpoint#update(int)
     */
    @Override
    public void update(int timeout) {
        try {
            this.host.update(this.listener, timeout);
        } catch (IOException e) {
            this.host.getLogger().error(e.toString());
        }
    }

    /* (non-Javadoc)
     * @see net.jenet.api.Server#bind(int)
     */
    @Override
    public void bind(int port) throws IOException {
        InetSocketAddress address=new InetSocketAddress(port);        
        this.host=new Host(getNetConfig(), address);        
    }

    /* (non-Javadoc)
     * @see net.jenet.api.Server#getConnections()
     */
    @Override
    public List<Connection> getConnections() {
        return Arrays.asList(this.connections);
    }

    /* (non-Javadoc)
     * @see net.jenet.api.Server#sendToAll(int, java.lang.Object)
     */
    @Override
    public void sendToAll(int protocolFlags, NetMessage msg) throws IOException {
        if(this.host != null) {
            Message message = writeMessage(protocolFlags, msg);
            this.host.sendToAll(message);
        }
    }

    /* (non-Javadoc)
     * @see net.jenet.api.Server#sendToAllExcept(int, java.lang.Object, int)
     */
    @Override
    public void sendToAllExcept(int protocolFlags, NetMessage msg, int connectionId) throws IOException {
        if(this.host != null) {
            Message message = writeMessage(protocolFlags, msg);
            this.host.sendToAllExcept(message, (byte) connectionId);
        }
    }

    /* (non-Javadoc)
     * @see net.jenet.api.Server#sendTo(int, java.lang.Object, int)
     */
    @Override
    public void sendTo(int protocolFlags, NetMessage msg, int connectionId) throws IOException {
        if(this.host != null) {
            Message message = writeMessage(protocolFlags, msg);
            this.host.sendTo(message, (byte) connectionId);
        }
    }

}
