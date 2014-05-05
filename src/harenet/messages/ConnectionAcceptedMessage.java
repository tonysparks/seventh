/*
 * see license.txt 
 */
package harenet.messages;

import harenet.MessageHeader;

/**
 * The connection has been accepted by the server
 * 
 * @author Tony
 *
 */
public class ConnectionAcceptedMessage extends AbstractReliableMessage {

	/**
	 */
	public ConnectionAcceptedMessage() {
		super(MessageHeader.CONNECTION_ACCEPTED_MESSAGE);
	}
	
	/* (non-Javadoc)
	 * @see netspark.messages.Message#copy()
	 */
	@Override
	public Message copy() {
		return new ConnectionAcceptedMessage();
	}

}
