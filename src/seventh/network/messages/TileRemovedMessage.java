/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;


/**
 * A tile has been removed from the world
 * 
 * @author Tony
 *
 */
public class TileRemovedMessage extends AbstractNetMessage {
    /** Should be viewed as Unsigned Bytes */
    public int x, y;
    
    public TileRemovedMessage() {
        super(BufferIO.TILE_REMOVED);
    }
    
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        this.x = buffer.getUnsignedByte();
        this.y = buffer.getUnsignedByte();
    }
    
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        buffer.putUnsignedByte(this.x);
        buffer.putUnsignedByte(this.y);
    }
}
