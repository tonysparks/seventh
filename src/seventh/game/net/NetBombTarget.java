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

    public boolean isRotated;
    
    public NetBombTarget() {
        this.type = Type.BOMB_TARGET;
    }
    
    public boolean rotated90() {
        return isRotated;
    }
    
    public void rotate90() {
        isRotated=true;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        isRotated = buffer.getBooleanBit();
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        buffer.putBooleanBit(isRotated);
    }
}
