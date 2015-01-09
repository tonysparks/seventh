/*
 * see license.txt 
 */
package harenet;

import harenet.Host.MessageListener;
import harenet.messages.ConnectionRequestMessage;
import harenet.messages.DisconnectMessage;
import harenet.messages.Message;
import harenet.messages.ReliableNetMessage;
import harenet.messages.UnReliableNetMessage;

import java.net.InetSocketAddress;
import java.util.Iterator;
import java.util.Map;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * A {@link Peer} is a representation of a remote client.  The {@link Host}
 * can send and receive messages from a {@link Peer}.
 * 
 * @author Tony
 *
 */
public class Peer {

	enum State {
		CONNECTING,
		CONNECTED,
		DISCONNECTING,
		DISCONNECTED,
		
		ZOMBIE,
	}
	
	private byte id;
	private InetSocketAddress address;
	
	private Host host;
	
	private State state;
	
	private NetConfig config;
	private Log log;
	
	private Queue<Message> outgoingMessages;
	private Queue<Message> reliableOutgoingMessages;
	private Queue<Message> inboundMessages;
		
//	private Queue<Message> outgoingMessagesCache;
	
	private Map<Integer, Message> receivedReliableMessages;
	
	private long roundTripTime;
	
	/* the packet number */
	private int sendSequence;
	
	/* the remote packet sequence number */
	private int remoteSequence;
	
	/* what the other connection received from us */
	private int remoteAck;
		
	/* last time this peer received a message */
	private long lastReceivedTime;
	
	/* last time this peer sent a message */
	private long lastSendTime;
	
	/* last time we pinged this peer */
	private long lastPingTime;
	
	private long numberOfBytesSent;
	private long numberOfBytesRecv;
	private long numberOfDroppedPackets;
	
	private int[] ackBuffer;
	private int ackBufferIndex;
	
	private int messageIdGen;
	
	private long timeConnected;
	
	/**
	 * @param host
	 * @param address
	 * @param id
	 */
	public Peer(Host host, InetSocketAddress address, byte id) {
		this.host = host;
		this.address = address;
		this.id = id;
		
		this.config = host.getConfig();
		this.log = config.getLog();
		
		this.state = State.CONNECTING;
		this.outgoingMessages = new ConcurrentLinkedQueue<Message>();
		this.reliableOutgoingMessages = new ConcurrentLinkedQueue<Message>();
		this.inboundMessages = new ConcurrentLinkedQueue<Message>();
//		this.outgoingMessagesCache = new ConcurrentLinkedQueue<Message>();		
		
		this.receivedReliableMessages = new ConcurrentHashMap<>();
		
		this.ackBuffer = new int[32];
		this.ackBufferIndex = 0;
		
		this.timeConnected = System.currentTimeMillis();
	}
	
	/**
	 * @param state the state to set
	 */
	void setState(State state) {
		this.state = state;
	}
	
	/**
	 * @param id the id to set
	 */
	void setId(byte id) {
		this.id = id;
	}
	
	/**
	 * @return the lastPingTime
	 */
	public long getLastPingTime() {
		return lastPingTime;
	}
	
	/**
	 * @param lastPingTime the lastPingTime to set
	 */
	public void setLastPingTime(long lastPingTime) {
		this.lastPingTime = lastPingTime;
	}
	public void pongMessageReceived() {		
		long ping = System.currentTimeMillis() - this.lastPingTime;
		this.roundTripTime = (this.roundTripTime + ping) / 2;
	}
	
	/**
	 * @return the numberOfBytesRecv
	 */
	public long getNumberOfBytesRecv() {
		return numberOfBytesRecv;
	}
	
	/**
	 * @return the numberOfBytesSent
	 */
	public long getNumberOfBytesSent() {
		return numberOfBytesSent;
	}
	
	public void addNumberOfBytesRecv(int bytes) {
		this.numberOfBytesRecv += bytes;
	}
	
	public void addNumberOfBytesSent(int bytes) {
		this.numberOfBytesSent += bytes;
	}
	
	public void addDroppedPacket() {
		this.numberOfDroppedPackets++;
	}
	
	public long getAvgBitsPerSecRecv() {
		long totalTimeConnected = System.currentTimeMillis() - this.timeConnected;
		return (long)( (numberOfBytesRecv*8) / (totalTimeConnected/1000) );
	}
	
	
	public long getAvgBitsPerSecSent() {
		long totalTimeConnected = System.currentTimeMillis() - this.timeConnected;
		return (long)( (numberOfBytesSent*8) / (totalTimeConnected/1000) );
	}
	
	
	/**
	 * @return the numberOfDroppedPackets
	 */
	public long getNumberOfDroppedPackets() {
		return numberOfDroppedPackets;
	}
	
	/**
	 * @return the next message id
	 */
	public int nextMessageId() {
		return this.messageIdGen++;
	}
	
	/**
	 * @return the new Sequence number of this Peer next packet
	 */
	public int nextSequenceNumber() {
		if( this.sendSequence >= Integer.MAX_VALUE ) {
			this.sendSequence = 0;
		}
		return ++this.sendSequence;
	}
	
	/**
	 * @return the sendSequence
	 */
	public int getSendSequence() {
		return sendSequence;
	}
		
	/**
	 * Checks the reliable message queue
	 */
	public void checkReliableMessages(int ackHistory) {
				
		if(!this.reliableOutgoingMessages.isEmpty()) {
			long currentTime = System.currentTimeMillis();
			Iterator<Message> it = this.reliableOutgoingMessages.iterator();
			while(it.hasNext()) {
				
				Message msg = it.next();
				/* if the sent packet has been acknowledged, we can
				 * go ahead and discard it.
				 */
				if(isAcknowledged(ackHistory, msg)) {					
					it.remove();
					if(log.enabled()) {
						log.debug("Reliable message received: " 
										+ msg.getClass().getSimpleName() 
										+ " Sequence: " + msg.getSequenceNumberSent()
										+ " MessageId: " + msg.getMessageId()
										+ " #Of Resends: " + msg.getSequencesSent());
					}					
				}
				else {
					long timeSent = msg.getTimeSent();
					if(timeSent > -1) {
						long dt = currentTime - timeSent;
						if(dt > config.getReliableMessageTimeout()) {
							if(log.enabled()) {
								log.debug("Reliable message timed out: " + msg.getClass().getSimpleName() 
										+ " Sequence: " + msg.getSequenceNumberSent() 
										+ " Number of Delays: " + msg.getNumberOfDelays()
										+ " MessageId: " + msg.getMessageId()
										+ " #Of Resends: " + msg.getSequencesSent());
							}
							
							it.remove();
						}
					}
				}
			}
		}
	}
	
	/**
	 * @param remoteSequence the remoteSequence to set
	 */
	public void setRemoteSequence(int remoteSequence) {				
		this.remoteSequence = remoteSequence;			
		
		this.ackBuffer[this.ackBufferIndex] = remoteSequence;
		this.ackBufferIndex = (this.ackBufferIndex + 1) % this.ackBuffer.length;
	}
	
	/**
	 * @param remoteAck the remoteAck to set
	 */
	public void setRemoteAck(int ackHistory, int remoteAck) {
		this.remoteAck = remoteAck;

//		this.ackBuffer[this.ackBufferIndex] = this.remoteAck;
//		this.ackBufferIndex = (this.ackBufferIndex + 1) % this.ackBuffer.length;
				
		checkReliableMessages(ackHistory);
	}
	
	/**
	 * @param sequenceNumber
	 * @return true if the sequence number was acknowledged
	 */
	public boolean isAcknowledged(int ackHistory, Message msg) {
		
//		int ackHistory = getAckHistory();
		int sequenceNumber = msg.getSequenceNumberSent();
		int numberOfTimesSent = msg.getSequencesSent();

		if(log.enabled()) {
			log.debug("checking if acknowledged: Hist:" + Integer.toBinaryString(ackHistory) 
					+ " RemoteSeq: " + this.remoteSequence 
					+ " RemoteAck: " + this.remoteAck
					+ " Seq:" + sequenceNumber);
		}
		
		boolean wasAck = false;
		
		while(numberOfTimesSent > 0 && !wasAck) {
		
			if(sequenceNumber == this.remoteAck) {
				wasAck = true;
			}
			else if (sequenceNumber < this.remoteAck) {
				int ackPosition = Math.max(this.remoteAck - sequenceNumber - 1, 0);						
				wasAck = ((ackHistory >>> ackPosition) & 1) != 0;
			}
			
			sequenceNumber--;
			numberOfTimesSent--;
		}
		
		return wasAck;
				
	}
	
	/**
	 * @return the lastSendTime
	 */
	public long getLastSendTime() {
		return lastSendTime;
	}
	
	/**
	 * @param lastSendTime the lastSendTime to set
	 */
	public void setLastSendTime(long lastSendTime) {
		this.lastSendTime = lastSendTime;
	}
	
	/**
	 * @return the lastReceivedTime
	 */
	public long getLastReceivedTime() {
		return lastReceivedTime;
	}
	
	/**
	 * @param lastReceivedTime the lastReceivedTime to set
	 */
	public void setLastReceivedTime(long lastReceivedTime) {
		this.lastReceivedTime = lastReceivedTime;
	}
	
	/**
	 * @param currentTime the current time
	 * @param timeout time in msec to consider this connection timed out
	 * @return true if this Peer has timed out
	 */
	public boolean checkIfTimedOut(long currentTime, long timeout) {
		long dt = currentTime - this.lastReceivedTime;
		return dt > timeout;
	}
	
	/**
	 * @return the remoteSequence
	 */
	public int getRemoteSequence() {
		return remoteSequence;
	}
	
	/**
	 * @return the ackHistory
	 */
	public int getAckHistory() {
		int ackHistory = 0;
		for(int i = 0; i < ackBuffer.length; i++) {
			int previusSeq = ackBuffer[(ackBufferIndex + i) % ackBuffer.length];
			if(previusSeq < remoteSequence) {
				int newAckDelta =  remoteSequence - previusSeq;
				if(newAckDelta > 0 && newAckDelta < 32) {
					ackHistory = ackHistory | (1<<newAckDelta);
				}
			}
		}
		
//		int ackHistory = 0;
//		for(int i = 0; i < ackBuffer.length; i++) {			
//			int newAckDelta =  this.remoteSequence - ackBuffer[(ackBufferIndex + i) % ackBuffer.length];
//			ackHistory = ackHistory | (1<<newAckDelta);
//		}
		
//		if(log.enabled()) {
//			log.debug("AckHistory: " + Integer.toBinaryString(ackHistory));
//		}
		
		return ackHistory;
	}
		
	/**
	 * @return the roundTripTime
	 */
	public long getRoundTripTime() {
		return roundTripTime;
	}
	
//	/**
//	 * @return the outgoingMessagesCache
//	 */
//	public Queue<Message> getOutgoingMessagesCache() {
//		this.outgoingMessagesCache.clear();
//		
//		this.outgoingMessagesCache.addAll(this.reliableOutgoingMessages);
//		this.outgoingMessagesCache.addAll(this.outgoingMessages);
//		
//		return outgoingMessagesCache;
//	}
	
	/**
	 * @return the reliableOutgoingMessages
	 */
	public Queue<Message> getReliableOutgoingMessages() {
		return reliableOutgoingMessages;
	}
	
	/**
	 * @return the outgoingMessages
	 */
	public Queue<Message> getOutgoingMessages() {
		return outgoingMessages;
	}
	
	/**
	 * @return the inboundMessages
	 */
	public Queue<Message> getInboundMessages() {
		return inboundMessages;
	}
	
	
	/**
	 * @return the address
	 */
	public InetSocketAddress getAddress() {
		return address;
	}
	
	/**
	 * @param address the address to set
	 */
	public void setAddress(InetSocketAddress address) {
		this.address = address;
	} 
	
	/**
	 * @return the id
	 */
	public byte getId() {
		return id;
	}
	
	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * @param seqNum - the sequence number to compare against
	 * @return true if seqNum is a newer sequence number than the stored sequence number
	 */
	public boolean isSequenceMoreRecent( int seqNum ) {		
        return (( seqNum >= this.remoteSequence ) && ( seqNum - this.remoteSequence <= Integer.MAX_VALUE/2 )) ||
               (( this.remoteSequence >= seqNum ) && ( this.remoteSequence - seqNum >  Integer.MAX_VALUE/2  ));
        
//        return ( this.remoteSequence > seqNum ) && ( this.remoteSequence - seqNum <= Integer.MAX_VALUE/2 ) ||
//               ( seqNum > this.remoteSequence ) && ( seqNum - this.remoteSequence >  Integer.MAX_VALUE/2  );
    }
	
	public boolean isConnected() {
		return state==State.CONNECTED;
	}
	public boolean isConnecting() {
		return state==State.CONNECTING;
	}
	
	public boolean isDisconnected() {
		return state==State.DISCONNECTED;
	}
	
	public boolean isDisconnecting() {
		return state == State.DISCONNECTING;
	}
	
	/**
	 * @return true if in zombie state
	 */
	public boolean isZombie() {
		return state==State.ZOMBIE;
	}
	
	/**
	 * Signal a Disconnect message to
	 * the host.
	 */
	public void disconnect() {
		this.state = State.DISCONNECTING;
		this.host.disconnect(this);
	}
	
	public void disconnectNow() {
		this.state = State.DISCONNECTED;
		this.host.disconnect(this);
		this.inboundMessages.clear();
		this.outgoingMessages.clear();
	}
	
	/**
	 * Sends a Message
	 * @param message
	 */
	public void send(Message message) {
		if(message.isReliable()) {			
			this.reliableOutgoingMessages.add(message.copy());
		}
		else {
			this.outgoingMessages.add(message);
		}
	}
	
	/**
	 * Removes stored reliable messages
	 * @param currentTime
	 */
	public void timeoutDuplicates(long currentTime, long timeout) {
		for(Map.Entry<Integer, Message> e : this.receivedReliableMessages.entrySet()) {
			if(currentTime - e.getValue().getTimeReceived() > timeout) {
				this.receivedReliableMessages.remove(e.getKey());
			}
		}		
	}
	
	/**
	 * Checks and temporarily stores the message to check for duplicates
	 * @param msg
	 * @return true if this message is a duplicate
	 */
	public boolean isDuplicateMessage(Message msg) {
		boolean isDup = this.receivedReliableMessages.containsKey(msg.getMessageId());
		if ( !isDup ) {
			this.receivedReliableMessages.put(msg.getMessageId(), msg);
			msg.setTimeReceived(System.currentTimeMillis());			
		}
		
		return isDup;
	}
	
	/**
	 * Receives a message
	 * @param message
	 */
	public void receive(Message message) {		
		this.inboundMessages.add(message);
	}

	/**
	 * Receives messages
	 * 
	 * @param listener
	 */
	public void receiveMessages(MessageListener listener) {
		while(!this.inboundMessages.isEmpty()) {
			Message message = this.inboundMessages.poll();
			
			if(message instanceof UnReliableNetMessage) {
				listener.onMessage(this, message);
			}
			else if(message instanceof ReliableNetMessage) {			
				listener.onMessage(this, message);
			}
			if(message instanceof ConnectionRequestMessage) {
				listener.onConnected(this);
			}
			else if(message instanceof DisconnectMessage) {
				listener.onDisconnected(this);
			}			
		}
	}
}
