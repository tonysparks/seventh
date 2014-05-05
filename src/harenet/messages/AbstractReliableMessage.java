/*
 * see license.txt 
 */
package harenet.messages;

import harenet.IOBuffer;

/**
 * @author Tony
 *
 */
public abstract class AbstractReliableMessage extends AbstractMessage {

	/**
	 * @param type
	 */
	public AbstractReliableMessage(byte type) {
		super(type);
	}

	/**
	 * @param type
	 * @param message
	 * @param length
	 */
	public AbstractReliableMessage(byte type, NetMessage message, short length) {
		super(type, message, length);
	}
	
	
	/* (non-Javadoc)
	 * @see netspark.messages.AbstractMessage#getSize()
	 */
	@Override
	public short getSize() {
		return (short)(super.getSize() + 4);
	}
	
	/* (non-Javadoc)
	 * @see netspark.messages.AbstractMessage#isReliable()
	 */
	@Override
	public boolean isReliable() {	
		return true;
	}
	
	/*
	 * (non-Javadoc)
	 * @see netspark.messages.AbstractMessage#readHeader(netspark.IOBuffer)
	 */
	protected void readHeader(IOBuffer buffer) {
		this.messageId = buffer.getInt();
	}
	
	/*
	 * (non-Javadoc)
	 * @see netspark.messages.AbstractMessage#writeHeader(netspark.IOBuffer)
	 */
	protected void writeHeader(IOBuffer buffer) {
		buffer.putInt(messageId);
	}
	
}
