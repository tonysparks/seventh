
/*
 * see license.txt 
 */
package harenet;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.util.Iterator;
import java.util.Queue;

import harenet.Peer.State;
import harenet.messages.ConnectionRequestMessage;
import harenet.messages.DisconnectMessage;
import harenet.messages.HeartbeatMessage;
import harenet.messages.Message;
import harenet.messages.NetMessageFactory;
import harenet.messages.PingMessage;
import harenet.messages.PongMessage;
import harenet.messages.ServerFullMessage;

/**
 * Represents a {@link Host} machine.  This is able to send and receive
 * messages from remote {@link Peer}s
 * 
 * @author Tony
 * 
 */
public class Host {

    public static final byte INVALID_PEER_ID = -1;

    private static final int SOCKET_ERROR     = -1;
    private static final int SOCKET_WAIT     = 0;    
    private static final int SOCKET_READ     = 1;
    private static final int SOCKET_WRITE     = 2;
    
    private Selector selector;
    private DatagramChannel datagramChannel;

    private InetSocketAddress receivedAddress;
    
    private NetConfig config;
    private IOBuffer readBuffer, writeBuffer;
    
    private Peer[] peers;
    private int maxConnections;
    private int numberOfConnections;
        
    private Protocol protocol;
    
    private Log log;
    private NetMessageFactory messageFactory;
    
    private boolean isServer;
    private Peer localPeer;
    
    /**
     * Listens for messages and {@link Peer} connection state events.
     * 
     * @author Tony
     *
     */
    public static interface MessageListener {
        
        /**
         * The {@link Peer} has connected to the {@link Host}
         * 
         * @param peer
         */
        public void onConnected(Peer peer);
        
        /**
         * The {@link Peer} has disconnected from the {@link Host}
         * 
         * @param peer
         */
        public void onDisconnected(Peer peer);
        
        /**
         * The server is full and is denying the peer from
         * connecting.
         * 
         * @param peer
         */
        public void onServerFull(Peer peer);
        
        /**
         * A message has been received
         * 
         * @param peer
         * @param message
         */
        public void onMessage(Peer peer, Message message);
    }

    /**
     * @param config
     * @param address
     * @throws IOException
     */
    public Host(NetConfig config, InetSocketAddress address) throws IOException {
        this.config = config;
        
        log = config.getLog();
        messageFactory = config.getMessageFactory();
        
        isServer = address != null;
        
        maxConnections = this.config.getMaxConnections();
        
        datagramChannel = DatagramChannel.open();
        datagramChannel.configureBlocking(false);

        datagramChannel.bind(address);

        selector = Selector.open();
        datagramChannel.register(selector, SelectionKey.OP_READ);

        readBuffer = config.useDirectBuffers() ? IOBuffer.Factory.allocateDirect(config.getMtu()) :
                                                 IOBuffer.Factory.allocate(config.getMtu());
        readBuffer.clear();

        writeBuffer = config.useDirectBuffers() ? IOBuffer.Factory.allocateDirect(config.getMtu()) :
                                                  IOBuffer.Factory.allocate(config.getMtu());
        writeBuffer.clear();
        
        peers = new Peer[config.getMaxConnections()];
        
        protocol = new Protocol(config.getCompressionThreshold(), config.getMtu());
    }
    
    /**
     * @return the config
     */
    public NetConfig getConfig() {
        return config;
    }
    
    /**
     * @return the {@link Log} instance
     */
    public Log getLogger() {
        return config.getLog();
    }
    
    /**
     * Reserves the next available ID slot.
     * 
     * @param id the ID which is reserved.
     */
    public int reserveId() {
        synchronized (this) {                    
            byte id = findOpenId();
            if(isValidPeerId(id)) {
                if(peers[id] == null) {
                    peers[id] = new DummyPeer(this, (byte)id);
                    numberOfConnections++;
                }
            }
            return id;
        }
    }
    
    /**
     * Destroy this host, releasing any resources and closing down connections
     */
    public void destroy() {
        for(int i = 0; i < this.maxConnections; i++) {
            Peer peer = peers[i];
            if(peer != null && peer.isConnected()) {
                peer.disconnectNow();
            }
            
            peers[i] = null;
        }
        
        this.numberOfConnections = 0;
        
        if(datagramChannel.isOpen()) {
            try {datagramChannel.close(); } catch (IOException e) {}
        }
        
        if(this.selector.isOpen()) {
            try { this.selector.close(); } catch (IOException e) {}
        }
    }
    
    /**
     * Initiates a connection to the remote address
     * @param address
     * @return the {@link Peer}
     * @throws IOException
     */
    public Peer connect(InetSocketAddress address) throws IOException {
//        Peer peer = allocatePeer(address);
        if(localPeer != null) {
            localPeer.disconnectNow();
        }
        if(datagramChannel.isConnected()) {
            datagramChannel.disconnect();
        }
        
        datagramChannel.connect(address);
        localPeer = new Peer(this, address, INVALID_PEER_ID);                
        localPeer.send(new ConnectionRequestMessage());                
        return localPeer;
    }
    
    /**
     * Allocates the {@link Peer} which doesn't actually make the connection
     * @param address
     * @return the Peer
     * @throws IOException
     */
    protected Peer allocatePeer(InetSocketAddress address) throws IOException {
        synchronized (this) {    
            if (this.numberOfConnections >= this.maxConnections) {
                sendOutOfBandMessage(writeBuffer, address, ServerFullMessage.INSTANCE);
                
                throw new IOException("Max number of connections has been met.");
            }
            Peer peer = isClientAlreadyConnected(address);
            if(peer==null) {
                byte id = findOpenId();
                if (id == INVALID_PEER_ID) {
                    throw new IOException("Unable to allocate a valid id.");
                }
        
                this.numberOfConnections++;
        
                peer = new Peer(this, address, id);
                peer.setState(State.CONNECTED);            
                peers[id] = peer;
            }
            return peer;
        }        
    }

    private Peer isClientAlreadyConnected(InetSocketAddress address) {        
        for (int i = 0; i < this.maxConnections; i++) {
            if (peers[i] != null) {                
                if(peers[i].getAddress().equals(address)) {
                    return peers[i];
                }
            }
        }
        return null;
    }
    
    /**
     * @return an open available id
     */
    private byte findOpenId() {                
        for (int i = 0; i < this.maxConnections; i++) {
            if (peers[i] == null) {
                return (byte) i;
            }
        }

        return INVALID_PEER_ID;
    }
    
    /**
     * @param peerId
     * @return true if the supplied ID is valid
     */
    private boolean isValidPeerId(byte peerId) {
        return peerId > INVALID_PEER_ID && peerId < this.maxConnections;
    }

    void disconnect(Peer peer) {
        byte id = peer.getId();
        if (isValidPeerId(id)) {
            if(peers[id] != null) {
                peers[id].send(new DisconnectMessage());
                                
                /* remove the peer if they are disconnected */
                removeDisconnetedPeer(id);
            }
        }
    }

	/**
	 * @param id
	 */
    private void removeDisconnetedPeer(byte id) {
        if(peers[id].isDisconnected()) {
		    synchronized (this) {                                            
		        peers[id] = null;
		        this.numberOfConnections--;
		        if(this.numberOfConnections < 0) {
		            this.numberOfConnections = 0;
		        }
		    }
		}
	}

    /**
     * Sends a {@link Message} to the peer
     * @param message
     * @param peerId
     */
    public void sendTo(Message message, byte peerId) {
        if(isValidPeerId(peerId)) {
            Peer peer = peers[peerId];
            if(peer != null) {
                peer.send(message);
            }
        }
    }
    
    /**
     * Sends the message to all Peers
     * @param message
     */
    public void sendToAll(Message message) {
        for(int i = 0; i < peers.length; i++) {
            Peer peer = peers[i];
            if(peer != null) {
                peer.send(message);
            }
        }
    }
    
    /**
     * Sends the message to all Peers with the exception of the supplied peerId
     * @param message
     * @param peerId
     */
    public void sendToAllExcept(Message message, byte peerId) {
        for(int i = 0; i < peers.length; i++) {
            Peer peer = peers[i];
            if(peer != null && i != peerId) {
                peer.send(message);
            } 
        }
    }
        
    /**
     * Sends out any pending packets to all connected clients
     * 
     * @throws IOException
     */
    public void sendClientPackets() throws IOException {
        if(isServer) {
            for (int i = 0; i < maxConnections; i++) {
                Peer peer = peers[i];
                sendPacket(peer);
            }
        }
        else {
            sendPacket(localPeer);
        }
    }
    
    
    /**
     * Checks to see if any bytes are received
     * 
     * @return true if packets have been received
     * @throws IOException
     */
    public boolean receiveClientPackets() throws IOException {
        
        boolean bytesReceived = false;
        
        /* now lets receive any messages */
        int numberOfBytesRecv = receive(readBuffer);
        if(numberOfBytesRecv > 0) {
            
            /* the received address is updated as
             * we receive bytes from a peer (it is updated
             * to the address that send us the bytes
             */
            if(this.receivedAddress != null) {
                parsePacket(readBuffer);
                bytesReceived = true;
            }
        }
        
        return bytesReceived;
    }
    
    /**
     * Updates the network state
     * 
     * @param listener
     * @param timeout
     * @throws IOException
     */
    public void update(MessageListener listener, int timeout) throws IOException {
        // algorithm:
        // For each Client:
        // 1) send out Packet
        // 2) read in queued up Messages from clients
        // 3) receive Packet
        // 4) parse Packet
        // 5) place Message in client Queue

        int startTime = Time.time();
        
        /* send any pending packets */
        sendClientPackets();
        
        int socketState = SOCKET_WAIT;
        do {
                
            /* if we received a packet from the client
             * we can break out
             */
            if(receiveClientPackets()) {
                break;
            }
            
            int endTime = Time.time();
            if( (endTime-startTime) > timeout) {
                break;
            }
            
            socketState = socketWait(timeout - (endTime-startTime) );
            if(socketState == SOCKET_ERROR) {
                if(log.enabled()) {
                    log.error("Error selecting from datagram channel!");
                }
            }
        } while (socketState == SOCKET_READ);
        
        dispatchMessages(listener);
        
        checkTimeouts(listener);
    }
    
    
    /**
     * Packs and sends out the packet for the peer.
     * 
     * @param peer
     */
    private void sendPacket(Peer peer) {
        if (peer != null) {                        
            writeBuffer.clear();            
            protocol.reset();
            
            /* do we need to send a ping? */
            if( this.isServer ) {
                long currentTime = System.currentTimeMillis();
                if ( currentTime - peer.getLastPingTime() >= config.getPingRate() ) {
                    peer.setLastPingTime(currentTime);
                    peer.send(PingMessage.INSTANCE);
                }
            }
            
            
            /* move the header to start writing messages,
             * we will write out the protocol header once
             * we have all the information we need
             */
            writeBuffer.position(protocol.size());
            
            if(peer.isConnecting()) {
                protocol.setPeerId(INVALID_PEER_ID);
            }
            else {
                protocol.setPeerId(peer.getId());
            }
                        
            byte numberOfMessages = 0;
            
            /* always give priority to the reliable packets */
            numberOfMessages += packReliableMessages(writeBuffer, protocol, peer);            
            
            /* if there is space, go ahead and put in the unreliable packets */
            numberOfMessages += packUnreliableMessages(writeBuffer, protocol, peer);            
            protocol.setNumberOfMessages(numberOfMessages);
            

            /* if we had any messages, lets send it out */
            if(numberOfMessages > 0) {
                
//                protocol.setSentTime(Time.time());            
                protocol.setSendSequence(peer.nextSequenceNumber());
                protocol.setAcknowledge(peer.getRemoteSequence());
                protocol.setAckHistory(peer.getAckHistory());
                
                protocol.writeTo(writeBuffer);
                
                peer.addNumberOfBytesCompressed(protocol.getNumberOfBytesCompressed());
                
                send(writeBuffer, peer);
            }
            else {
                long amountOfTimeSinceLastPacket = System.currentTimeMillis() - peer.getLastSendTime();
                if(amountOfTimeSinceLastPacket >= config.getHeartbeatTime()) {                    
                    peer.send(HeartbeatMessage.INSTANCE);
                }
            }                                            
        }
    }
    
    /**
     * Empties the unreliable queue
     * @param writeBuffer
     * @param protocol
     * @param peer
     * @return the number of messages packed
     */
    private int packUnreliableMessages(IOBuffer writeBuffer, Protocol protocol, Peer peer) {
        int numberOfMessagesSent = 0;
        if (peer != null) {

            Queue<Message> messages = peer.getOutgoingMessages();
            int numberOfMessages = messages.size();
            if((numberOfMessages+protocol.getNumberOfMessages()) > Byte.MAX_VALUE) {
                numberOfMessages = Byte.MAX_VALUE - protocol.getNumberOfMessages();
            }
            
            if (!messages.isEmpty()) {

                while (numberOfMessages > 0) {
                    numberOfMessages--;

                    Message msg = messages.peek();

                    /*
                     * check and see if the message can fit
                     */
                    if (!fitsInPacket(msg)) {
                        msg.delay();
                        break;
                    }

                    messages.poll();

                    if(log.enabled()) {
                        log.debug("Sending unreliable message: " 
                                + (peer.getSendSequence()+1) 
                                + " with Ack: " + peer.getRemoteSequence());
                    }
                    
                    msg.writeTo(writeBuffer);
                    numberOfMessagesSent++;
                }

            }

        }
        
        return (numberOfMessagesSent);
    }
    
    /**
     * Packs as many reliable messages as possible
     * @param writeBuffer
     * @param protocol
     * @param peer
     * @return the number of packed messages
     */
    private int packReliableMessages(IOBuffer writeBuffer, Protocol protocol, Peer peer) {
        int numberOfMessagesSent = 0;
        
        if (peer != null) {
            Queue<Message> reliableMessages = peer.getReliableOutgoingMessages();
                        
            if(!reliableMessages.isEmpty()) {                                                                
                for (Message msg : reliableMessages) {                                                            
                    /* check and see if the message can
                     * fit
                     */
                    if(! fitsInPacket(msg)) {
                        msg.delay();
                        break;
                    }
                        
                                            
                    /* note when it was sent out, so we can time it out */
                    if(!msg.hasBeenSent()) {
                        
                        
                        /* mark which packet number this reliable message
                        * was sent out
                        */
                        int seq = peer.getSendSequence()+1;
                        msg.setSequenceNumberSent(seq);
                        msg.setMessageId(peer.nextMessageId());
                        
                        msg.setTimeSent(System.currentTimeMillis());
                    }                    
                    
                    msg.addSequenceNumberSent();
                    
                                    
                    msg.writeTo(writeBuffer);
                    
                    if(log.enabled()) {
                        log.debug("Sending reliable message: " + msg.getClass().getSimpleName() 
                                + " Sequence: " + msg.getSequenceNumberSent() 
                                + " with Ack: " + peer.getRemoteSequence());
                    }
                    
                    numberOfMessagesSent++;
                }
            }
        }
        return (numberOfMessagesSent);
    }
        
    
    /**
     * Dispatches any queued up messages
     * @param listener
     */
    private void dispatchMessages(MessageListener listener) {
        for (int i = 0; i < maxConnections; i++) {
            Peer peer = peers[i];

            if (peer != null) {
                peer.receiveMessages(listener);
//                peer.checkReliableMessages();
            }
        }
    }
    
    /**
     * Writes an out of band message to the remote address.  This is one way communication
     * 
     * @param ioBuffer
     * @param remoteAddress
     * @param msg
     * @return the number of bytes written out
     */
    private int sendOutOfBandMessage(IOBuffer ioBuffer, InetSocketAddress remoteAddress, Message msg) {
        protocol.reset();
        protocol.setNumberOfMessages( (byte)1 );
        protocol.writeTo(ioBuffer);
        msg.writeTo(ioBuffer);
        
        ByteBuffer buffer = ioBuffer.sendSync().asByteBuffer();        
        buffer.flip();        
        try {                       
            return datagramChannel.send(buffer, remoteAddress);
        } 
        catch (Exception e) {        
            if(log.enabled()) log.error("Error sending bytes to peer: " + e);
            return -1;
        }
    }
    
    /**
     * Sends the bytes to the remote Peer.
     * @param buffer
     * @param peer
     * @return the number of bytes transfered
     */
    private int send(IOBuffer ioBuffer, Peer peer) {
        ByteBuffer buffer = ioBuffer.sendSync().asByteBuffer();        
        buffer.flip();        
        try {
            peer.setLastSendTime(System.currentTimeMillis());
            peer.addNumberOfBytesSent(buffer.limit());
            return datagramChannel.send(buffer, peer.getAddress());
        } 
        catch (Exception e) {        
            if(log.enabled()) log.error("Error sending bytes to peer: " + e);
            return -1;
        }
    }
    
    /**
     * Receives bytes from a remote peer.  If bytes are received the "receivedAddress"
     * will be updated to the address that send the bytes.
     * 
     * @param buffer
     * @return the number of bytes received
     */
    private int receive(IOBuffer ioBuffer) {
        ByteBuffer buffer = ioBuffer.clear().asByteBuffer();
        buffer.clear();
        try {
            this.receivedAddress = (InetSocketAddress) datagramChannel.receive(buffer);            
            buffer.flip();  
            
            ioBuffer.receiveSync();
            
            return buffer.limit();            
        }
        catch(Exception e) {
            if(log.enabled()) log.error("Error receiving bytes: " + e);
            return -1;
        }
    }
    
    /**
     * Parses the packet; reads in the protocol headers and queuing up Messages
     * for the Peer.
     * 
     * @param buffer
     * @throws IOException
     */
    private void parsePacket(IOBuffer buffer) throws IOException {
        if(log.enabled()) {
            log.debug("Receiving " + buffer.limit() + " of bytes from " + receivedAddress);
        }
        
        protocol.reset();        
        protocol.readFrom(buffer, messageFactory);
        
        /* if this packet isn't from our
         * network protocol ignore it
         */
        if(protocol.isValid()) {                        
            byte peerId = protocol.getPeerId();
            
            /* this is the first time this client
             * is connecting to our server
             */
            if(peerId == INVALID_PEER_ID) {                
                peerId = handleConnectionRequest(buffer, protocol);
            }
        
            
            /* lets read in the messages */    
            if(isValidPeerId(peerId)) {

                /* if this isn't a server we reassign the peer
                 * to the correct id (since it was assigned from
                 * the server
                 */
                if(!isServer && peers[peerId] == null) {
                    peers[peerId] = localPeer;
                    peers[peerId].setState(State.CONNECTED);
                    peers[peerId].receive(new ConnectionRequestMessage());
                }
                
                Peer peer = peers[peerId];
                                
                if(peer == null) {
                                                            
                    if(log.enabled()) {
                        log.error("The PeerId: " + peerId + " is no longer valid, ignoring packet.");
                    }    
                }
                else {
                    /* there are times were the remote address will
                     * change, so we just always update the address to 
                     * keep in sync
                     */
                    peer.setId(peerId);
                    peer.setAddress(receivedAddress);
                    peer.setLastReceivedTime(System.currentTimeMillis());
                    peer.addNumberOfBytesRecv(buffer.limit());    
                    peer.addNumberOfBytesCompressed(protocol.getNumberOfBytesCompressed());
                    
                    int seqNumber = protocol.getSendSequence();
                    
                    /* if this is a newer packet, lets go ahead
                     * and process it
                     */
                    if(peer.isSequenceMoreRecent(seqNumber)) {
                        int numberOfDroppedPackets = seqNumber - peer.getRemoteSequence();
                        if(numberOfDroppedPackets > 1) {
                            peer.addDroppedPacket(numberOfDroppedPackets-1);
                        }
                        
                        peer.setRemoteSequence(seqNumber);        
                        peer.setRemoteAck(protocol.getAckHistory(), protocol.getAcknowledge());
                        parseMessages(peer, buffer, protocol);
                    }
                    else {
                        peer.addDroppedPacket();
                        
                        if(log.enabled()) {
                            log.error("Out of order packet:" + seqNumber + " should be: " + peer.getRemoteSequence());
                        }
                    }
                }
                
            }
            else {
                if(log.enabled()) {
                    log.error("Invalid PeerId: " + peerId + " ignoring the rest of the packet.");
                }
            }
        }
        
    }
    
    /**
     * Handles a new connection request
     * 
     * @param buffer
     * @param protocol
     * @return the PeerId
     */
    private byte handleConnectionRequest(IOBuffer buffer, Protocol protocol) throws IOException {
        Peer peer = allocatePeer(receivedAddress);        
        if(peer ==null) {
            if(log.enabled()) {
                log.error("Unable to allocate peer for: " + receivedAddress);
            }
            
            return INVALID_PEER_ID;
        }
//        else {
//            peer.send(new ConnectionAcceptedMessage());
//        }
        
        return peer.getId();
    }
    
    
    /**
     * Parses the messages
     * 
     * @param peer
     * @param buffer
     * @param protocol
     */
    private void parseMessages(Peer peer, IOBuffer buffer, Protocol protocol) {
        byte numberOfMessages = protocol.getNumberOfMessages();                                                
        while(numberOfMessages > 0 && buffer.hasRemaining()) {
            numberOfMessages--;
            
            Message message = MessageHeader.readMessageHeader(buffer, messageFactory);
            if(message == null) {
                if(log.enabled()) {
                    log.error("Error reading the message header from: " + receivedAddress);
                }            
            }
            else if(message.isReliable()) {
//                if(log.enabled()) {
//                    log.debug("Receiving message: " + protocol.getSendSequence() + " with Ack: " + protocol.getAcknowledge());
//                }
                if(!peer.isDuplicateMessage(message)) {
                    peer.receive(message);
                }
            }
            else {
                if(message instanceof PingMessage) {
                    peer.send(PongMessage.INSTANCE);
                }
                else if(message instanceof PongMessage) {
                    peer.pongMessageReceived();
                }
                else {
                    peer.receive(message);
                }
            }
            
            
        }
    }
    
    /**
     * @param msg
     * @return true if the Message fits in the Packet
     */
    private boolean fitsInPacket(Message msg) {
        return this.writeBuffer.remaining() > msg.getSize();
    }
    
    /**
     * Does multiplex on the Datagram channel
     * @param timeout
     * @return what state the socket is in
     * @throws IOException
     */
    private int socketWait(int timeout) {
        int result = SOCKET_WAIT;
        
        int numKeys = 0;
        try {
            numKeys = (timeout > 0) ? selector.select(timeout) : selector.selectNow();
        } 
        catch (IOException e) {
            if(log.enabled()) {
                log.error("*** Error in select : " + e);
            }
            result = SOCKET_ERROR;
        }
        
        
        if (numKeys > 0) {
            Iterator<SelectionKey> selectedKeys = selector.selectedKeys().iterator();
            while (selectedKeys.hasNext()) {
                SelectionKey key = selectedKeys.next();
                selectedKeys.remove();

                if (!key.isValid()) {
                    continue;
                }

                if (key.isReadable()) {                    
                    result = SOCKET_READ;
                    break;
                } else if (key.isWritable()) {    
                    result = SOCKET_WRITE;
                    break;
                }
            }
        }
        
        return result;
    }
    
    
    /**
     * Checks for client time outs
     * @param listener
     */
    private void checkTimeouts(MessageListener listener) {
        
        long currentTime = System.currentTimeMillis();
        long messageTimeout = config.getReliableMessageTimeout() + 500;
        for(int i = 0; i < peers.length; i++) {
            Peer peer = peers[i];
            if(peer != null && peer.isConnected()) {
                if(peer.checkIfTimedOut(currentTime, config.getTimeout())) {
                    listener.onDisconnected(peer);
                    peer.disconnectNow();
                }
                else {
                    peer.timeoutDuplicates(currentTime, messageTimeout);
                }
            }
            else if(peer!=null && peer.isDisconnecting()) {                
                peer.disconnectNow();                
            }
        }
    }
}
