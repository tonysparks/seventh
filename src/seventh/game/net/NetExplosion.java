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
public class NetExplosion extends NetEntity {    
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
        ownerId = buffer.getUnsignedByte();
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        buffer.putUnsignedByte(ownerId);
    }
}
