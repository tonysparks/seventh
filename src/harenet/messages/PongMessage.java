/*
 * see license.txt 
 */
package harenet.messages;

import harenet.MessageHeader;

/**
 * Pong message, this is a response from a {@link PingMessage}.
 * This Ping/Pong is used to calculate round trip time (ping).
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
