/*
 * see license.txt 
 */
package harenet.messages;

import harenet.IOBuffer;
import harenet.MessageHeader;

/**
 * Basis for {@link Message}s.  This holds all possible information and subclasses
 * may or may not use some of the information.  This strategy was chosen to simplify
 * the rest of the code -- it can assume all Messages have NetMessages or are Reliable
 * if need be. 
 * 
 * @author Tony
 *
 */
public abstract class AbstractMessage implements Message {

    /* the type is the only value sent over the wire for this class */
    protected byte type; 
    protected short sizeInBytes;
    
    protected int messageId;
    protected NetMessage message;
    private int numberOfDelays;
    
    /** Tracking the Acknowledgement of the message */
    private transient long timeSent  = -1;
    private transient int sequenceNumberSent = -1;
    private transient int sequencesSent = 0;
    private transient long timeReceived;
    
    /**
     * @param type
     */
    public AbstractMessage(byte type) {
        this.type = type;
    }
    
    /**
     * @param data
     * @param length
     * @param flags
     */
    public AbstractMessage(byte type, NetMessage message, short length) {
        this.type = type;    
        this.sizeInBytes = length;        
        this.message = message;
        this.numberOfDelays = 0;
    }
    
    /* (non-Javadoc)
     * @see netspark.Transmittable#writeTo(java.nio.ByteBuffer)
     */
    @Override
    public void writeTo(IOBuffer buffer) {
        buffer.put(type);
        
        writeHeader(buffer);
        
        if(type >= MessageHeader.RELIABLE_NETMESSAGE) {        
            message.write(buffer);
        }
    }

    /**
     * Write the header information
     * 
     * @param buffer
     */
    protected void writeHeader(IOBuffer buffer) {
    }
    
    /*
     * (non-Javadoc)
     * @see harenet.Transmittable#readFrom(harenet.IOBuffer, harenet.messages.NetMessageFactory)
     */
    @Override
    public void readFrom(IOBuffer buffer, NetMessageFactory messageFactory) {
        // NOTE: the type is read from the MessageHeader
        
        readHeader(buffer);
        
        if(type >= MessageHeader.RELIABLE_NETMESSAGE) {
            message = messageFactory.readNetMessage(buffer);            
        }
    }
    
    /**
     * Reads the header information
     * 
     * @param buffer
     */
    protected void readHeader(IOBuffer buffer) {
    }
    
    /* (non-Javadoc)
     * @see netspark.messages.Message#isReliable()
     */
    @Override
    public boolean isReliable() {    
        return false;
    }
    /* (non-Javadoc)
     * @see netspark.messages.Message#getMessageId()
     */
    @Override
    public int getMessageId() {
        return this.messageId;
    }
    
    /* (non-Javadoc)
     * @see netspark.messages.Message#setMessageId(int)
     */
    @Override
    public void setMessageId(int messageId) {
        this.messageId = messageId;
    }
    
    /**
     * @return the timeSent
     */
    public long getTimeSent() {
        return timeSent;
    }
    
    /* (non-Javadoc)
     * @see netspark.messages.Message#getTimeReceived()
     */
    @Override
    public long getTimeReceived() {    
        return this.timeReceived;
    }
    
    /* (non-Javadoc)
     * @see netspark.messages.Message#setTimeReceived(long)
     */
    @Override
    public void setTimeReceived(long timeReceived) {
        this.timeReceived = timeReceived;
    }
    
    /**
     * @param timeSent the timeSent to set
     */
    public void setTimeSent(long timeSent) {
        this.timeSent = timeSent;
    }
    
    /**
     * @return true if this message has been sent at least once
     */
    public boolean hasBeenSent() {
        return this.timeSent > -1;
    }
    
    /**
     * @return the sequenceNumberSent
     */
    public int getSequenceNumberSent() {
        return sequenceNumberSent;
    }
    
    /**
     * @param sequenceNumberSent the sequenceNumberSent to set
     */
    public void setSequenceNumberSent(int sequenceNumberSent) {
        this.sequenceNumberSent = sequenceNumberSent;
    }

    /**
     * Adds the number attempts this was sent
     */
    public void addSequenceNumberSent() {
        this.sequencesSent++;
    }
    
    /**
     * @return the sequencesSent
     */
    public int getSequencesSent() {
        return sequencesSent;
    }


    /* (non-Javadoc)
     * @see netspark.messages.Message#getMessage()
     */
    @Override
    public NetMessage getMessage() {    
        return message;
    }

    /* (non-Javadoc)
     * @see netspark.messages.Message#getSize()
     */
    @Override
    public short getSize() {
        return (short)(this.sizeInBytes + 2);
    }

    /* (non-Javadoc)
     * @see netspark.messages.Message#getNumberOfDelays()
     */
    @Override
    public int getNumberOfDelays() {
        return this.numberOfDelays;
    }

    /* (non-Javadoc)
     * @see netspark.messages.Message#delay()
     */
    @Override
    public void delay() {
        this.numberOfDelays++;
    }

}
