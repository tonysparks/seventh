/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.game.Entity.Type;


/**
 * @author Tony
 *
 */
public class NetExplosion extends NetEntity {	
//	public byte damage; /* currently not used */
	public int ownerId;
	
	/**
	 * 
	 */
	public NetExplosion() {
		this.type = Type.EXPLOSION.netValue();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
//		damage = buffer.get();
		ownerId = buffer.getUnsignedByte();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
//		buffer.put(damage);
		buffer.putUnsignedByte(ownerId);
	}
}
