/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;

/**
 * @author Tony
 *
 */
public class FlagReturnedMessage extends AbstractNetMessage {
	public int flagId;
	public int returnedBy;
	
	/**
	 * 
	 */
	public FlagReturnedMessage() {
		super(BufferIO.FLAG_RETURNED);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		this.flagId = buffer.getUnsignedByte();
		this.returnedBy = buffer.getUnsignedByte();
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
		buffer.putUnsignedByte(flagId);
		buffer.putUnsignedByte(returnedBy);
	}
}
