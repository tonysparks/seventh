/*
 * see license.txt
 */
package seventh.shared;

import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * A very simple multicast server socket, that listens for messages broadcast to a particular group address and port.
 * 
 * @author Tony
 *
 */
public class BroadcastListener implements AutoCloseable {
    
    
    /**
     * Message was received from a broadcast message
     * 
     * @author Tony
     *
     */
    public static interface OnMessageReceivedListener {
        public void onMessage(DatagramPacket packet);
    }
        
    private MulticastSocket socket;
    private InetAddress groupAddress;
    private int port;
    private AtomicBoolean active;
    private final int MTU;
    
    private List<OnMessageReceivedListener> listeners;


    
    /**
     * @param mtu
     * @param groupAddress
     * @param port
     * @throws Exception
     */
    public BroadcastListener(int mtu, String groupAddress, int port) {
        this.MTU = mtu;        
        this.port = port;
        
                
        this.listeners = new ArrayList<BroadcastListener.OnMessageReceivedListener>();
        this.active = new AtomicBoolean();
        
        try {
            this.groupAddress = InetAddress.getByName(groupAddress); 
        }
        catch(Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Adds a {@link OnMessageReceivedListener}
     * 
     * @param l
     */
    public void addOnMessageReceivedListener(OnMessageReceivedListener l) {
        this.listeners.add(l);
    }
    
    /**
     * Starts listening on a port for broadcast messages.
     * 
     * @param groupAddress
     * @param port
     */
    public void start() throws Exception {
        if(!this.active.get()) {            
            this.active.set(true);
            
            socket = null;
            try {
                socket = new MulticastSocket(this.port);
                socket.joinGroup(this.groupAddress);    
                
                byte[] buffer = new byte[this.MTU];
                while(this.active.get()) {
                    
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);               
                       socket.receive(packet);               
                    
                    for(OnMessageReceivedListener l : this.listeners) {
                        l.onMessage(packet);
                    }
                }
            }
            catch(SocketException e) {
                if(!e.getMessage().equals("socket closed")) {
                    throw e;
                }
            }
            finally {
                if(socket != null && !socket.isClosed()) {
                    socket.leaveGroup(this.groupAddress);
                    socket.close();
                }  
            }
        }
    }
    
    @Override
    public void close() throws Exception {
        this.active.set(false);
        if(socket != null && !socket.isClosed()) {
            socket.leaveGroup(this.groupAddress);
            socket.close();
        }
    }
}
