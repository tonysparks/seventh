/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.game.entities.Entity.Type;

/**
 * El Bomba
 * 
 * @author Tony
 *
 */
public class NetBomb extends NetEntity {

	public NetBomb() {
		this.type = Type.BOMB.netValue();
	}
	public int timeRemaining;
	
	/* (non-Javadoc)
	 * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		timeRemaining = buffer.getInt();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
		buffer.putInt(timeRemaining);
	}
}
