/*
 * see license.txt 
 */
package harenet.messages;

import harenet.MessageHeader;

/**
 * A network message that is <b>NOT</b> guaranteed to arrive at the peer. This message
 * can contain a user defined {@link NetMessage} to transfer.
 * 
 * @author Tony
 *
 */
public class UnReliableNetMessage extends AbstractMessage {
    /**
     */
    public UnReliableNetMessage() {
        super(MessageHeader.UNRELIABLE_NETMESSAGE);
    }

    public UnReliableNetMessage(NetMessage message, short length) {
        super(MessageHeader.UNRELIABLE_NETMESSAGE, message, length);
    }    
    
    /* (non-Javadoc)
     * @see netspark.messages.Message#copy()
     */
    @Override
    public Message copy() {    
        return new UnReliableNetMessage(getMessage(), this.sizeInBytes);
    }
}
