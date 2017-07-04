/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import harenet.messages.NetMessage;

/**
 * @author Tony
 *
 */
public class AbstractNetMessage implements NetMessage {

    protected byte type;
    
    
    /**
     * @param type
     */
    public AbstractNetMessage(byte type) {
        this.type = type;
    }

    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        // type is read from the Header
    }

    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        buffer.putByteBits(type, 6); // must match SeventhNetMessageFactory
    }

}
