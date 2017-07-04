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
public class NetGameTypeInfo implements NetMessage {

    public long maxTime;
    
    public int maxScore;
    public NetTeam alliedTeam;
    public NetTeam axisTeam;
    
    public byte type;
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        maxTime = buffer.getLong();
        maxScore = buffer.getInt();
        type = buffer.getByte();
        
        alliedTeam = new NetTeam();
        alliedTeam.read(buffer);
        alliedTeam.id = 1;
        
        axisTeam = new NetTeam();
        axisTeam.read(buffer);
        axisTeam.id = 2;
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        buffer.putLong(maxTime);
        buffer.putInt(maxScore);
        buffer.putByte(type);
        
        alliedTeam.write(buffer);
        axisTeam.write(buffer);
    }
}
