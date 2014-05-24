/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;



/**
 * @author Tony
 *
 */
public class BombPlantedMessage extends AbstractNetMessage {
	public int bombTargetId;
	
	
	/**
	 * @param bombTargetId
	 */
	public BombPlantedMessage(int bombTargetId) {
		this();
		this.bombTargetId = bombTargetId;
	}

	/**
	 * 
	 */
	public BombPlantedMessage() {
		super(BufferIO.BOMB_PLANTED);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#read(harenet.IOBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		bombTargetId = buffer.getUnsignedByte();
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#write(harenet.IOBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
		buffer.putUnsignedByte(bombTargetId);
	}
}
