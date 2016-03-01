/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;

/**
 * @author Tony
 *
 */
public class FlagCapturedMessage extends AbstractNetMessage {
	public int flagId;
	public int capturedBy;
	
	/**
	 * 
	 */
	public FlagCapturedMessage() {
		super(BufferIO.FLAG_CAPTURED);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		this.flagId = buffer.getUnsignedByte();
		this.capturedBy = buffer.getUnsignedByte();
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
		buffer.putUnsignedByte(flagId);
		buffer.putUnsignedByte(this.capturedBy);
	}
}
