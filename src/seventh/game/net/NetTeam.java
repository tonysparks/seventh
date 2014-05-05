/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;

/**
 * @author Tony
 *
 */
public class NetTeam implements NetMessage {
	public byte id;
	public int[] playerIds;
	
	protected byte bits;
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {
		bits = buffer.get();
		id = buffer.get();
		
		if((bits & 1) != 0) {
			byte len = buffer.get();
			playerIds = new int[len];
			for(byte i = 0; i < len; i++) {
				playerIds[i] = buffer.getUnsignedByte();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {
		bits = 0;
		
		if(playerIds != null && playerIds.length > 0) {
			bits |= 1;
		}
		
		buffer.put(bits);
		buffer.put(id);
		
		if( bits > 0 ) {
			buffer.put( (byte)playerIds.length);
			for(int i = 0; i < playerIds.length; i++) {
				buffer.putUnsignedByte(playerIds[i]);
			}
		}
	}
}
