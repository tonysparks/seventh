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
public class PingMessage extends AbstractMessage {
	
	/**
	 */
	public PingMessage() {
		super(MessageHeader.PING_MESSAGE);
	}
	
	/* (non-Javadoc)
	 * @see netspark.messages.Message#copy()
	 */
	@Override
	public Message copy() {	
		return new PingMessage();
	}
}
