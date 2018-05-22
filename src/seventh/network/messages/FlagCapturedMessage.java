/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;

import static org.junit.Assert.*;
import org.junit.Test;

/**
 * @author Tony
 *
 */
public class FlagCapturedMessage extends AbstractNetMessage {
    public int flagId;
    public int capturedBy;
    
    /**
     * 
     */
    public FlagCapturedMessage() {
        super(BufferIO.FLAG_CAPTURED);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        this.flagId = buffer.getUnsignedByte();
        
        //test if flagID is unsigned
        assertTrue(this.flagId >= 0);
        
        this.capturedBy = buffer.getUnsignedByte();
        
        //test if capturedBy is unsigned
        assertTrue(this.capturedBy >= 0);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        buffer.putUnsignedByte(flagId);
        buffer.putUnsignedByte(this.capturedBy);
    }
}