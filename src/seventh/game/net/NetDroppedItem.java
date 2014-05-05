/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.game.Entity.Type;

/**
 * A dropped item
 * 
 * @author Tony
 *
 */
public class NetDroppedItem extends NetEntity {

	public NetDroppedItem() {
		this.type = Type.DROPPED_ITEM.netValue();
	}
	public byte droppedItem;
	
	/* (non-Javadoc)
	 * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		droppedItem = buffer.get();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
		buffer.put(droppedItem);
	}
}
