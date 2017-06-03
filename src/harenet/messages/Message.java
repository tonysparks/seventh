/*
 * see license.txt 
 */
package harenet.messages;

import harenet.Transmittable;


/**
 * A message is a protocol level way is distributing isRotated of information from Host to Peer.
 * 
 * Messages are packed into UDP datagram packets and sent to the Peer.  Clients of the Harenet
 * API should not use this interface to create custom messages, instead they should use the {@link NetMessage}.
 * 
 * @see NetMessage
 * @author Tony
 *
 */
public interface Message extends Transmittable {

    /**
     * @return the timeSent
     */
    public long getTimeSent();
    
    /**
     * @return the message id
     */
    public int getMessageId();
    
    /**
     * @param messageId
     */
    public void setMessageId(int messageId);
    
    /**
     * @param timeReceived
     */
    public void setTimeReceived(long timeReceived);
    
    /**
     * @return the time this message was received
     */
    public long getTimeReceived();
    
    /**
     * @param timeSent the timeSent to set
     */
    public void setTimeSent(long timeSent);
    
    /**
     * @return true if this message has been sent at least once
     */
    public boolean hasBeenSent();
    
    /**
     * @return the sequenceNumberSent
     */
    public int getSequenceNumberSent();
    
    /**
     * @param sequenceNumberSent the sequenceNumberSent to set
     */
    public void setSequenceNumberSent(int sequenceNumberSent);
    
    /**
     * Adds the number attempts this was sent
     */
    public void addSequenceNumberSent();
    
    /**
     * @return the sequencesSent
     */
    public int getSequencesSent();
    
    /**
     * @return true if a reliable message
     */
    public boolean isReliable();
    
    
    /**
     * @return the messages data
     */
    public NetMessage getMessage();
    
    /**
     * @return the size of this message
     */
    public short getSize();

    /**
     * @return the number of times this message
     * missed transporting because of its size 
     * (not fitting into the packet)
     */
    public int getNumberOfDelays();
    
    /**
     * Delay this message from being sent
     * out -- this is due to not fitting
     * within the available Packet space.
     */
    public void delay();
    
    /**
     * @return a copy of this message
     */
    public Message copy();

}
