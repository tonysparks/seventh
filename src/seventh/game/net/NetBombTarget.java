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
public class NetBombTarget extends NetEntity {

    public byte bits;
    
    public NetBombTarget() {
        this.type = Type.BOMB_TARGET.netValue();
    }
    
    public boolean rotated90() {
        return bits > 0;
    }
    
    public void rotate90() {
        bits=1;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        bits = buffer.get();
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        buffer.put(bits);
    }
}
