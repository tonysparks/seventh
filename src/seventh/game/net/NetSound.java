/*
 * see license.txt 
 */
package seventh.game.net;

import java.util.List;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.game.events.SoundEmittedEvent;
import seventh.game.events.SoundEventPool;
import seventh.math.Vector2f;

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
	 * Converts the {@link SoundEmittedEvent} into a {@link NetSound}
	 * @param event
	 */
	public void toNetSound(SoundEmittedEvent event) {
		type = event.getSoundType().netValue();
		Vector2f pos = event.getPos();
		posX = (short)pos.x;
		posY = (short)pos.y;
	}
	
	/**
	 * @param sounds
	 * @return converts the List of {@link SoundEmittedEvent} to the respective {@link NetSound} array
	 */
	public static NetSound[] toNetSounds(NetSound[] snds, List<SoundEmittedEvent> sounds) {
		int size = sounds.size();
		for(int i = 0; i < size; i++) {
			snds[i].toNetSound(sounds.get(i));//.getNetSound();
		}
		
		return snds;
	}
	
	/**
	 * @param sounds
	 * @return converts the List of {@link SoundEmittedEvent} to the respective {@link NetSound} array
	 */
	public static NetSound[] toNetSounds(NetSound[] snds, SoundEventPool sounds) {
		int size = sounds.numberOfSounds(); 
		for(int i = 0; i < size; i++) {
			snds[i].toNetSound(sounds.getSound(i));
		}
		
		return snds;
	}
	
	
}
