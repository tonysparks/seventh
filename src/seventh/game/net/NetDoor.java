/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.game.entities.Entity.Type;
import seventh.network.messages.BufferIO;

/**
 * A Door
 * 
 * @author Tony
 *
 */
public class NetDoor extends NetEntity {
    
    public byte hinge;
    
    public NetDoor() {
        this.type = Type.DOOR;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        this.orientation = BufferIO.readAngle(buffer);
        this.hinge = buffer.getByteBits(3);
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        BufferIO.writeAngle(buffer, orientation);
        buffer.putByteBits(hinge, 3);
    }
}
