/*
 * see license.txt 
 */
package test.harenet;

import static org.junit.Assert.*;

import java.nio.ByteBuffer;

import org.junit.Test;

import harenet.ByteBufferIOBuffer;
import harenet.IOBuffer;
import harenet.Protocol;
import harenet.messages.ConnectionAcceptedMessage;
import harenet.messages.ConnectionRequestMessage;
import harenet.messages.NetMessage;
import harenet.messages.NetMessageFactory;

/**
 * @author Tony
 *
 */
public class ByteBufferIOBufferTest {

    @Test
    public void test() {
        IOBuffer writeBuffer = IOBuffer.Factory.allocate(1500);
        Protocol writeProtocol = new Protocol(-1, 1500);
        Protocol readProtocol = new Protocol(-1, 1500);
        IOBuffer readBuffer = IOBuffer.Factory.allocate(1500);

        int attempts = 3;
        while(attempts --> 0) {
            writeProtocol.reset();
            writeBuffer.position(writeProtocol.size());
            writeProtocol.setPeerId((byte)10);
            writeProtocol.setAckHistory(0xff);
            writeProtocol.setAcknowledge(4*attempts);
            writeProtocol.setNumberOfMessages( (byte)attempts);
            writeProtocol.setSendSequence(12);
            writeProtocol.writeTo(writeBuffer);
            
    
            writeBuffer.putInt(attempts);
            writeBuffer.put( (byte) 5);
            writeBuffer.putByteBits( (byte)12, 6);
            writeBuffer.putInt(attempts);
            
            //writeBuffer.flip();
            //~~~~~~~~~ sent packet
            ByteBuffer out = writeBuffer.sendSync().asByteBuffer();
            out.flip();
            // send buffer to socket
            //~~~~~~~~~
            
            
            //~~~~~~~~~ receive packet     
            ByteBuffer in = readBuffer.clear().asByteBuffer();
            in.clear();
            
            // read buffer from socket
            in.put(out.array(), 0, out.limit());
            
            in.flip();
            readBuffer.receiveSync();
            ///~~~~~~~~~~~~
            
            readProtocol.reset();
            readProtocol.readFrom(readBuffer, new NetMessageFactory() {
                
                @Override
                public NetMessage readNetMessage(IOBuffer buffer) {
                    return null;
                }
            });
            
            assertEquals(writeProtocol.getPeerId(), readProtocol.getPeerId());
            assertEquals(writeProtocol.getAckHistory(), readProtocol.getAckHistory());
            assertEquals(writeProtocol.getAcknowledge(), readProtocol.getAcknowledge());
            assertEquals(writeProtocol.getNumberOfMessages(), readProtocol.getNumberOfMessages());
            assertEquals(writeProtocol.getSendSequence(), readProtocol.getSendSequence());
    
    
            assertEquals(attempts, readBuffer.getInt());
            assertEquals( (byte) 5, readBuffer.get());
            assertEquals( (byte) 12, readBuffer.getByteBits(6));
            assertEquals(attempts, readBuffer.getInt());
        }
    }

    @Test
    public void testShort() {
        IOBuffer writeBuffer = IOBuffer.Factory.allocate(1500);
        writeBuffer.putIntBits( 8189, 13);
        writeBuffer.flip();
        int value = writeBuffer.getIntBits(13);// & (short)0b111111111111;
        assertEquals(8189, value);
    }
}

