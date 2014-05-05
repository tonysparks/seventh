/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;


/**
 * @author Tony
 *
 */
public class SpectatingPlayerMessage extends AbstractNetMessage {
	public int playerIdBeingWatched;	
	
	/**
	 * 
	 */
	public SpectatingPlayerMessage() {
		super(BufferIO.SPECTATING_PLAYER);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		playerIdBeingWatched = buffer.getUnsignedByte();
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
		buffer.putUnsignedByte(playerIdBeingWatched);
	}
}
