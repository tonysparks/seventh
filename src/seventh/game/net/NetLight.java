/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.game.entities.Entity.Type;


/**
 * @author Tony
 *
 */
public class NetLight extends NetEntity {    

    public short r, g, b;
    public short luminacity;
    public short size;
    
    /**
     */
    public NetLight() {
        this.type = Type.LIGHT_BULB.netValue();
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        
        r = (short)(buffer.getUnsignedByte());
        g = (short)(buffer.getUnsignedByte());
        b = (short)(buffer.getUnsignedByte());
        
        luminacity = (short)(buffer.getUnsignedByte());
        size = buffer.getShort();
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        
        buffer.putUnsignedByte(r);
        buffer.putUnsignedByte(g);
        buffer.putUnsignedByte(b);
        
        buffer.putUnsignedByte(luminacity);
        buffer.putShort(size);
    }
}
