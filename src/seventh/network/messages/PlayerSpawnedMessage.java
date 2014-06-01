/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;

/**
 * @author Tony
 *
 */
public class PlayerSpawnedMessage extends AbstractNetMessage {
	public int playerId;
	public short posX;
	public short posY;
		
	/**
	 * 
	 */
	public PlayerSpawnedMessage() {
		super(BufferIO.PLAYER_SPAWNED);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		playerId = buffer.getUnsignedByte();
//		if((playerId & 0x80) != 0) {
//			isMech = true;
//		}
//		playerId = playerId & ~0x80;
//		
		posX = buffer.getShort();
		posY = buffer.getShort();
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
//		if(isMech) {
//			buffer.putUnsignedByte(playerId | 0x80 );
//		}
//		else 
		{
			buffer.putUnsignedByte(playerId);
		}
		buffer.putShort(posX);
		buffer.putShort(posY);
	}

}
