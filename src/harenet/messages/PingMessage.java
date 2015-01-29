/*
 * see license.txt 
 */
package harenet.messages;

import harenet.MessageHeader;

/**
 * Ping message.  One side of the connection can initiate
 * a {@link PingMessage} and the other side will respond back with
 * a {@link PongMessage}.  These messages are used to calculate the 
 * round trip time of a {@link Message}.
 * 
 * @author Tony
 *
 */
public class PingMessage extends AbstractMessage {
	
	/**
	 * Cached instance to reduce GC load
	 */
	public static final PingMessage INSTANCE = new PingMessage();
	
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
		return INSTANCE;
	}
}
