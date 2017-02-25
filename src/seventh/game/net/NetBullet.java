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
public class NetBullet extends NetEntity {
//    public float targetVelX; /* target velocity isn't used yet, so disabling */
//    public float targetVelY;
    public byte damage;
    public int ownerId;
        
    /**
     * 
     */
    public NetBullet() {
        this.type = Type.BULLET.netValue();
    }
    
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
//        targetVelX = buffer.getShort() / 100.0f;
//        targetVelY = buffer.getShort() / 100.0f;
//        damage = buffer.get();
        ownerId = buffer.getUnsignedByte();    
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
//        buffer.putShort( (short)(targetVelX * 100.0f) );
//        buffer.putShort( (short)(targetVelY * 100.0f) );
//        buffer.put(damage);
        buffer.putUnsignedByte(ownerId);
    }
}
