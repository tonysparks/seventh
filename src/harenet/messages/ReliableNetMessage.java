/*
 * see license.txt 
 */
package harenet.messages;

import harenet.MessageHeader;

/**
 * A Reliable message is one that is guaranteed to make it to the peer.  This message
 * can contain a user defined {@link NetMessage} to transfer.
 * 
 * @author Tony
 *
 */
public class ReliableNetMessage extends AbstractReliableMessage {
		
	/**
	 */
	public ReliableNetMessage() {
		super(MessageHeader.RELIABLE_NETMESSAGE);	
	}

	public ReliableNetMessage(NetMessage message, short length) {
		super(MessageHeader.RELIABLE_NETMESSAGE, message, length);
	}	
	
	/* (non-Javadoc)
	 * @see netspark.messages.Message#copy()
	 */
	@Override
	public Message copy() {	
		return new ReliableNetMessage(getMessage(), this.sizeInBytes);
	}
}
