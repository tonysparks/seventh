/*
 * see license.txt 
 */
package harenet;

import java.net.DatagramPacket;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Create network hooks to mimic a disrupted network connection (dropping packets, or delaying them)
 * 
 * @author Tony
 *
 */
public class NetworkDisruptor {

    public static void main(String[] args) throws Exception {        
        int proxyPort = 9843;
        int minLatency = 50;
        int maxLatency = 100;
        int dropPercentage = 5;
                
        NetworkDisruptor proxy = new NetworkDisruptor(new InetSocketAddress("localhost", 9845), 
                proxyPort,
                minLatency,
                maxLatency,
                dropPercentage);
        
        proxy.run(); 
    }
    
    static class Packet {
        DatagramPacket packet;
        long receivedTime;
        
        public Packet(DatagramPacket packet) {
            this.packet = packet;
            this.receivedTime = System.currentTimeMillis();
        }
    }
    
    private DatagramChannel proxySocket;
    private AtomicBoolean isRunning;
    
    private ByteBuffer recvBuffer;
    private ByteBuffer writeBuffer;
    
    private InetSocketAddress proxiedServerAddress;
    private SocketAddress proxiedClientAddress;
    
    private List<Packet> pendingClientSendPackets;
    private List<Packet> pendingServerSendPackets;
    
    private int proxyPort;
    
    private int minLatency, maxLatency;
    private int dropPrecentage;
    
    private int biggestPacketSize;
    
    private Random random;
    
    public NetworkDisruptor(InetSocketAddress proxiedServerAddress, 
                            int proxyPort, 
                            int minLatency, 
                            int maxLatency, 
                            int dropPercentage) {
        
        this.proxyPort = proxyPort;
        this.minLatency = minLatency / 2; // we delay both server and client
        this.maxLatency = maxLatency / 2;
        this.dropPrecentage = dropPercentage;
                
        this.isRunning = new AtomicBoolean(false);
        
        this.recvBuffer = ByteBuffer.allocate(1500);
        this.writeBuffer = ByteBuffer.allocate(1500);
        
        this.proxiedServerAddress = proxiedServerAddress;
        
        this.pendingClientSendPackets = new LinkedList<>();
        this.pendingServerSendPackets = new LinkedList<>();
        
        this.random = new Random();
    }
    
    public void run() throws Exception {
        if(this.isRunning.get()) {
            return;
        }
        
        this.isRunning.set(true);
        
        this.proxySocket = DatagramChannel.open();
        this.proxySocket.configureBlocking(false);
        
        this.proxySocket.bind(new InetSocketAddress("localhost", this.proxyPort));
        
        System.out.println("Proxy server started on " + this.proxySocket.getLocalAddress());
        
        while(this.isRunning.get()) {
            this.recvBuffer.clear();
            
            SocketAddress remoteAddr = this.proxySocket.receive(this.recvBuffer);
            if(remoteAddr != null) {
                System.out.println("Received message from: " + remoteAddr);
                
                this.recvBuffer.flip();
            
                byte[] buf = copy(this.recvBuffer);
                DatagramPacket packet = new DatagramPacket(buf, buf.length, remoteAddr);
                Packet p = new Packet(packet);
                
                if(this.proxiedServerAddress.equals(remoteAddr)) {
                    this.pendingServerSendPackets.add(p);
                }
                else {
                    this.proxiedClientAddress = remoteAddr;
                    this.pendingClientSendPackets.add(p);
                }            
            }
            
            sendPackets(this.pendingServerSendPackets, this.proxiedClientAddress);
            sendPackets(this.pendingClientSendPackets, this.proxiedServerAddress);
        }
    }
    
    private byte[] copy(ByteBuffer buf) {
        int limit = buf.limit();
        byte[] cpy = new byte[limit];
        buf.get(cpy, 0, limit);
        return cpy;
    }
    
    private void sendPackets(List<Packet> pendingPackets, SocketAddress sendTo) throws Exception {
        final long now = System.currentTimeMillis();        
        final int latencyFuzz = this.maxLatency - this.minLatency;
        
        List<Packet> remove = new ArrayList<>();
        
        for(int i = 0; i < pendingPackets.size(); i++) {
            Packet packet = pendingPackets.get(i);
            
            long timePassed = now - packet.receivedTime;
            if( (timePassed - this.random.nextInt(latencyFuzz)) > this.minLatency) {
                remove.add(packet);
                
                int dropPacket = this.random.nextInt(100);
                if(dropPacket < this.dropPrecentage) {                   
                    System.out.println("*** Dropping packet from: " + packet.packet.getSocketAddress() + " to " + sendTo);
                    continue;
                }
                
                System.out.print("Sending packet from: " + packet.packet.getSocketAddress() + " to " + sendTo);
                this.writeBuffer.clear();
                this.writeBuffer.put(packet.packet.getData(), packet.packet.getOffset(), packet.packet.getLength());
                this.writeBuffer.flip();
                
                int sentBytes = this.proxySocket.send(this.writeBuffer, sendTo);
                System.out.println(" => " + sentBytes + " bytes");
                
                if(sentBytes > this.biggestPacketSize) {
                    this.biggestPacketSize = sentBytes;
                    System.out.println("^^^ High water mark packet size => " + this.biggestPacketSize);
                }
            }
        }
        
        for(Packet p: remove) {
            pendingPackets.remove(p);
        }
    }
}
