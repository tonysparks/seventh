/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;

import java.util.List;

import seventh.game.events.SoundEmittedEvent;

/**
 * @author Tony
 *
 */
public class NetSound implements NetMessage {	
	public byte type;
	public short posX, posY;
	
	private static final short TILE_WIDTH = 32;
	private static final short TILE_HEIGHT = 32;
	
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		type = buffer.get();
//		posX = buffer.getShort();
//		posY = buffer.getShort();
		
		posX = (short)(buffer.get() & 0xFF);
		posX *= TILE_WIDTH;
		
		posY = (short)(buffer.get() & 0xFF);
		posY *= TILE_WIDTH;
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {
		buffer.put(type);
//		buffer.putShort(posX);
//		buffer.putShort(posY);
		
		buffer.put( (byte)(posX/TILE_WIDTH) );
		buffer.put( (byte)(posY/TILE_HEIGHT) );
	}
	
	/**
	 * @param sounds
	 * @return converts the List of {@link SoundEmittedEvent} to the respective {@link NetSound} array
	 */
	public static NetSound[] toNetSounds(List<SoundEmittedEvent> sounds) {
		NetSound[] snds = new NetSound[sounds.size()];
		int size = sounds.size();
		for(int i = 0; i < size; i++) {
			snds[i] = sounds.get(i).getNetSound();
		}
		
		return snds;
	}
}
