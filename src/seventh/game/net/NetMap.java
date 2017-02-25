/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.network.messages.BufferIO;

/**
 * @author Tony
 *
 */
public class NetMap implements NetMessage {
    public int id;
    public String path;
    public String name;
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        id = buffer.getInt();
        path = BufferIO.readString(buffer);
        name = BufferIO.readString(buffer);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        buffer.putInt(id);
        BufferIO.write(buffer, path);
        BufferIO.write(buffer, name);
    }
}
