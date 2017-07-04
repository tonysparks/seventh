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
public class NetFlag extends NetEntity {

    public int carriedBy;
    
    /**
     */
    public NetFlag() {
    }
    
    /**
     * The type of flag this is
     * @param type
     */
    public NetFlag(Type type) {
        this.type = type;
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#read(harenet.IOBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        this.carriedBy = BufferIO.readPlayerId(buffer);
    }

    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#write(harenet.IOBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        super.write(buffer);
        BufferIO.writePlayerId(buffer, carriedBy);
    }
}
