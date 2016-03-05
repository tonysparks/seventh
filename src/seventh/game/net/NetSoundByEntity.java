/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.math.Vector2f;

/**
 * A sound in which is sourced/attached to an entity
 * 
 * @author Tony
 *
 */
public class NetSoundByEntity extends NetSound {	
	public int entityId;
		
	public NetSoundByEntity() {	
	}
	
	/**
	 * @param pos
	 * @param entityId
	 */
	public NetSoundByEntity(Vector2f pos, int entityId) {
		/* NOTE: this does not set the enabled position flag */
		setPos(pos);
		this.entityId = entityId;
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		this.entityId = buffer.getUnsignedByte();
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {
		super.write(buffer);
		buffer.putUnsignedByte(this.entityId);
	}		
}
