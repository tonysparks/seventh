/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;

/**
 * Client is attempting to connect to the server
 * 
 * @author Tony
 *
 */
public class ConnectRequestMessage extends  AbstractNetMessage {

    /**
     * Client name
     */
    public String name;    
    
    /**
     * 
     */
    public ConnectRequestMessage() {
        super(BufferIO.CONNECT_REQUEST);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        name = BufferIO.readString(buffer);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        BufferIO.writeString(buffer, name);
    }
}
