/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.game.entities.Entity.Type;

/**
 * A Door
 * 
 * @author Tony
 *
 */
public class NetDoor extends NetEntity {
	
	public byte hinge;
	
    public NetDoor() {
        this.type = Type.DOOR.netValue();
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        this.orientation = buffer.getShort();
        this.hinge = buffer.get();
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        buffer.putShort(this.orientation);
        buffer.put(hinge);
    }
}
