/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;

/**
 * Determines if a Player is converted to or from a Commander
 * 
 * @author Tony
 *
 */
public class PlayerCommanderMessage extends AbstractNetMessage {
	public int playerId;
	public boolean isCommander;
	
	/**
	 * 
	 */
	public PlayerCommanderMessage() {
		super(BufferIO.PLAYER_COMMANDER);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		playerId = buffer.getUnsignedByte();
		isCommander = (buffer.get() > 0);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
		buffer.putUnsignedByte(playerId);
		buffer.put(isCommander ? (byte)1 : (byte)0);
	}
}
