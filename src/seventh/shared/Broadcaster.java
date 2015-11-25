/*
 * see license.txt
 */
package seventh.shared;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * A simple tool to broadcast a message to a group address using multicast.
 * 
 * @author Tony
 *
 */
public class Broadcaster implements AutoCloseable {

    public static void main(String[] args) throws Exception {
        try(Broadcaster caster = new Broadcaster(1500, "224.0.0.44", 4888)) {
            caster.broadcastMessage("Hello Cruel World");
            caster.broadcastMessage("Bye Cruel World");
        }
    }
    
    private DatagramSocket socket;
    private InetAddress groupAddress;
    private final int port;
    private final int MTU;
    
    /**
     * @param mtu
     * @param groupAddress
     * @param port
     * @throws Exception
     */
    public Broadcaster(int mtu, String groupAddress, int port) throws Exception {
        this.MTU = mtu;
        this.port = port;
                
        this.groupAddress = InetAddress.getByName(groupAddress);   
        this.socket = new DatagramSocket();
    }
    
    
    /**
     * Broadcast a message to the group address
     * 
     * @param message
     * @throws Exception
     */
    public void broadcastMessage(String message) throws Exception {
        
        byte[] msg = message.getBytes();
        if(msg.length > this.MTU) {
            throw new IllegalArgumentException("The supplied message exceeds the MTU: '" + this.MTU + "' < '" + msg.length +"'");
        }
        
        DatagramPacket packet = new DatagramPacket(msg, msg.length, this.groupAddress, this.port);
        this.socket.send(packet);
    }
    
    
    @Override
    public void close() throws Exception {
        if(this.socket != null) {
            this.socket.close();
        }        
    }
    
}
