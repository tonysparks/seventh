/*
 * see license.txt 
 */
package harenet.messages;

import harenet.MessageHeader;

/**
 * The server is full, no connection for you!
 * 
 * @author Tony
 *
 */
public class ServerFullMessage extends AbstractMessage {

    public static final ServerFullMessage INSTANCE = new ServerFullMessage();
    
    /**
     */
    public ServerFullMessage() {
        super(MessageHeader.SERVER_FULL_MESSAGE);
    }

    @Override
    public Message copy() {    
        return new ServerFullMessage();
    }
}
