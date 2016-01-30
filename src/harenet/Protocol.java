/*
 * see license.txt 
 */
package harenet;

import harenet.messages.NetMessageFactory;

import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;


/**
 * Simple base Harenet protocol header.  Each UDP packet will contain
 * this protocol header information.  This hold information such as
 * the protocol ID, version, peerId and sequencing mechanisms.
 * 
 * @author Tony
 *
 */
public class Protocol implements Transmittable {

	/**
	 * Protocol Id -- helps filter out garbage packets
	 */
	public static final byte PROTOCOL_ID = 0x1e;
	
	private static final int FLAG_COMPRESSED = 0x0001;
	
	/* serves as a quick filter and version# */
	private byte protocolId;
	
	/* packet flags */
	private byte flags;
	
	/* what peer this belongs to */
	private byte peerId;
	
	/* number of messages in this packet */
	private byte numberOfMessages;
	
	/* time the packet was sent */
//	private int sentTime;
	
	/* the packet number */
	private int sendSequence;
	
	/* the last acknowledged packet number */
	private int acknowledge;
	
	/* the last 32 acknowledges */
	private int ackHistory;

	/* the number of bytes we have compressed from
	 * the original message
	 */
	private int numberOfBytesCompressed;
	
	private Deflater deflater;
	private Inflater inflater;
	private IOBuffer compressionBuffer;

	private int compressionThreshold;
	
	/**
	 * @param compressionThreshold
	 * @param mtu
	 */
	public Protocol(int compressionThreshold, int mtu) {
	    this.compressionThreshold = compressionThreshold;
	    this.deflater = new Deflater(Deflater.BEST_COMPRESSION);
	    this.deflater.setStrategy(Deflater.HUFFMAN_ONLY);
	    
	    this.inflater = new Inflater();
	    
	    this.compressionBuffer = IOBuffer.Factory.allocate(mtu);
	    
		reset();
	}
	
	/**
	 * @return true if the protocol ID matches
	 */
	public boolean isValid() {
		return this.protocolId == PROTOCOL_ID;
	}
	
	/**
	 * Makes the protocol Id valid
	 */
	public void makeValid() {
		this.protocolId = PROTOCOL_ID;
	}
	
	/**
	 * Resets to initial state
	 */
	public void reset() {
		this.protocolId = 0;
		this.flags = 0;
		this.peerId = Host.INVALID_PEER_ID;
		this.numberOfMessages = 0;
//		this.sentTime = 0;
		this.sendSequence = 0;
		this.acknowledge = 0;
		this.ackHistory = 0;
		this.numberOfBytesCompressed = 0;
		
		this.compressionBuffer.clear();
		
		this.deflater.reset();
		this.inflater.reset();
	}
	
	/**
	 * @return the number of bytes the protocol header takes
	 */
	public int size() {
		return 1 + // protocolId
		       1 + // flags
			   1 + // peerdId
			   1 + // numberOf messages
//			   4 + // send time
			   4 + // send Sequence
			   4 + // acknowledge
			   4   // ackHistory
			   ;
	}
	
	/**
	 * @param sendSequence the sendSequence to set
	 */
	public void setSendSequence(int sendSequence) {
		this.sendSequence = sendSequence;
	}
	
	/**
	 * @param acknowledge the acknowledge to set
	 */
	public void setAcknowledge(int acknowledge) {
		this.acknowledge = acknowledge;
	}
	
	/**
	 * @param ackHistory the ackHistory to set
	 */
	public void setAckHistory(int ackHistory) {
		this.ackHistory = ackHistory;
	}
	
	/**
	 * @param numberOfMessages the numberOfMessages to set
	 */
	public void setNumberOfMessages(byte numberOfMessages) {
		this.numberOfMessages = numberOfMessages;
	}
	
	/**
	 * @param sentTime the sentTime to set
	 */
//	public void setSentTime(int sentTime) {
//		this.sentTime = sentTime;
//	}
	
	/**
	 * @param peerId the peerId to set
	 */
	public void setPeerId(byte peerId) {
		this.peerId = peerId;
	}
	
	/**
	 * @return the peerId
	 */
	public byte getPeerId() {
		return peerId;
	}
	
	/**
	 * @return the sentTime
	 */
//	public int getSentTime() {
//		return sentTime;
//	}
	
	/**
	 * @return the numberOfMessages
	 */
	public byte getNumberOfMessages() {
		return numberOfMessages;
	}
	
	/**
	 * @return the ackHistory
	 */
	public int getAckHistory() {
		return ackHistory;
	}
	
	/**
	 * @return the acknowledge
	 */
	public int getAcknowledge() {
		return acknowledge;
	}
	
	/**
	 * @return the sendSequence
	 */
	public int getSendSequence() {
		return sendSequence;
	}
	
	/**
     * @return the numberOfBytesCompressed
     */
    public int getNumberOfBytesCompressed() {
        return numberOfBytesCompressed;
    }
	
	/*
	 * (non-Javadoc)
	 * @see harenet.Transmittable#readFrom(harenet.IOBuffer, harenet.messages.NetMessageFactory)
	 */
	@Override
	public void readFrom(IOBuffer buffer, NetMessageFactory messageFactory) {
		this.protocolId = buffer.get();
		this.flags = buffer.get();
		
		uncompress(buffer);
		
		this.peerId = buffer.get();
		this.numberOfMessages = buffer.get();
//		this.sentTime = buffer.getInt();
		this.sendSequence = buffer.getInt();
		this.acknowledge = buffer.getInt();
		this.ackHistory = buffer.getInt();
	}
	
	/* (non-Javadoc)
	 * @see netspark.Transmittable#writeTo(java.nio.ByteBuffer)
	 */
	@Override
	public void writeTo(IOBuffer buffer) {	    
	    int endPosition = buffer.position();
	    buffer.position(0);
	    
		buffer.put(PROTOCOL_ID);
		buffer.put(this.flags); // just a place holder
		buffer.put(this.peerId);
		buffer.put(this.numberOfMessages);
//		buffer.putInt(this.sentTime);
		
		buffer.putInt(this.sendSequence);
		buffer.putInt(this.acknowledge);
		buffer.putInt(this.ackHistory);
		
		buffer.position(endPosition);
		
		compress(buffer);
		
		/* if we compressed things, mark the 
		 * flags header
		 */
		buffer.put(1, this.flags); 
	}	
	
	
	/**
	 * Compress the supplied {@link IOBuffer}
	 * 
	 * @param buffer
	 */
	private void compress(IOBuffer buffer) {
	    int size = buffer.position();
	    if(buffer.hasArray() && this.compressionThreshold > 0 && size > this.compressionThreshold) {	        	        
	        int numberOfBytesToSkip = 2; /* Skip the Protocol and Flags bytes */
	        this.deflater.setInput(buffer.array(), numberOfBytesToSkip, buffer.position()-numberOfBytesToSkip );
            this.deflater.finish();
            
            int len = deflater.deflate(this.compressionBuffer.array(), 2, this.compressionBuffer.capacity()-2, Deflater.FULL_FLUSH);            
            if(!deflater.finished()) {
                return; /* not able to compress with the amount of space given */
            }
            
            this.numberOfBytesCompressed = (size-2)-len;
            
            this.compressionBuffer.position(2);
            this.compressionBuffer.limit(len);
            
            buffer.limit(len+2);
            buffer.position(2);
            buffer.put(this.compressionBuffer);
            
            this.flags |= FLAG_COMPRESSED;
	    }
	}
	
	/**
	 * Uncompress the supplied {@link IOBuffer}
	 * 
	 * @param buffer
	 */
	private void uncompress(IOBuffer buffer) {
	    if((this.flags & FLAG_COMPRESSED) != 0) {
	        
	        int numberOfBytesToSkip = 2; /* Skip the Protocol and Flags bytes */
	        this.inflater.setInput(buffer.array(), numberOfBytesToSkip, buffer.limit()-numberOfBytesToSkip);	        
            int len = 0;
            try {
                len = this.inflater.inflate(this.compressionBuffer.array(), 2, this.compressionBuffer.capacity()-2);
            }
            catch(DataFormatException e) {
                throw new RuntimeException(e);
            }
            
            this.compressionBuffer.position(2);
            this.compressionBuffer.limit(len);
            
            buffer.limit(len+2);
            buffer.position(2);
            buffer.put(this.compressionBuffer);
	    }
	}
}
