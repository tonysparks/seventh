/*
 * see license.txt 
 */
package harenet.messages;

import harenet.MessageHeader;

/**
 * A Client is requesting to connect to the server.
 * 
 * @author Tony
 *
 */
public class ConnectionRequestMessage extends AbstractMessage {


	/**
	 */
	public ConnectionRequestMessage() {
		super(MessageHeader.CONNECTION_REQUEST_MESSAGE);
	}

	/* (non-Javadoc)
	 * @see netspark.messages.Message#copy()
	 */
	@Override
	public Message copy() {	
		return new ConnectionRequestMessage();
	}
}
