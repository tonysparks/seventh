/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.game.entities.Entity.Type;
import seventh.network.messages.BufferIO;


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
        this.type = Type.EXPLOSION;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        ownerId = BufferIO.readPlayerId(buffer);
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        BufferIO.writePlayerId(buffer, ownerId);
    }
}
