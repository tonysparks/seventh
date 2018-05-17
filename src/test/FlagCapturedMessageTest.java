package test;

import static org.junit.Assert.*;

import org.junit.Test;
import harenet.IOBuffer;
import seventh.network.messages.FlagCapturedMessage;

public class FlagCapturedMessageTest {

    @Test
    public void readtest(IOBuffer buffer) {
        super.read(buffer);
        this.flagId = buffer.getUnsignedByte();
        
        //test if flagID is unsigned
        assertTrue(this.flagId >= 0);
        
        this.capturedBy = buffer.getUnsignedByte();
        
        //test if capturedBy is unsigned
        assertTrue(this.capturedBy >= 0);
    }

}
