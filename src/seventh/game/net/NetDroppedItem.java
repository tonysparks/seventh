/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.game.entities.Entity.Type;
import seventh.network.messages.BufferIO;

/**
 * A dropped item
 * 
 * @author Tony
 *
 */
public class NetDroppedItem extends NetEntity {

    public NetDroppedItem() {
        this.type = Type.DROPPED_ITEM;
    }
    public Type droppedItem;
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        droppedItem = BufferIO.readType(buffer);
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        BufferIO.writeType(buffer, droppedItem);
    }
}
