/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;

/**
 * @author Tony
 *
 */
public class NetTeamStat implements NetMessage {
    public byte id;
    public int score;
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
    //    id = BufferIO.readTeamId(buffer); is assigned by order in packet
        score = buffer.getShort();
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
    //    BufferIO.writeTeamId(buffer, id);
        buffer.putShort( (short) score);
    }
}
