/*
 * see license.txt 
 */
package harenet.messages;

import harenet.MessageHeader;

/**
 * Simple message to let the other side know we are still alive.
 * 
 * @author Tony
 *
 */
public class HeartbeatMessage extends AbstractMessage {
	
	/**
	 * Cached instance to reduce GC load
	 */
	public static final HeartbeatMessage INSTANCE = new HeartbeatMessage();
	
	/**
	 */
	public HeartbeatMessage() {
		super(MessageHeader.HEARTBEAT_MESSAGE);
	}
	
	/* (non-Javadoc)
	 * @see netspark.messages.Message#copy()
	 */
	@Override
	public Message copy() {	
		return INSTANCE;
	}
}
