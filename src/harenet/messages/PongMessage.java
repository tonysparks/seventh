/*
 * see license.txt 
 */
package harenet.messages;

import harenet.MessageHeader;

/**
 * Ping message
 * 
 * @author Tony
 *
 */
public class PongMessage extends AbstractMessage {
	
	
	/**
	 */
	public PongMessage() {
		super(MessageHeader.PONG_MESSAGE);		
	}
	
	/* (non-Javadoc)
	 * @see netspark.messages.Message#copy()
	 */
	@Override
	public Message copy() {	
		return new PongMessage();
	}
}
