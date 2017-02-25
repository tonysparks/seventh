/*
 * see license.txt 
 */
package harenet.messages;

import harenet.MessageHeader;

/**
 * A client is requesting to disconnect
 * 
 * @author Tony
 *
 */
public class DisconnectMessage extends AbstractMessage {

    
    /**
     */
    public DisconnectMessage() {
        super(MessageHeader.DISCONNECT_MESSAGE);
    }

    /* (non-Javadoc)
     * @see netspark.messages.Message#copy()
     */
    @Override
    public Message copy() {    
        return new DisconnectMessage();
    }
}
