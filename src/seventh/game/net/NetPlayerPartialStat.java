/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.network.messages.BufferIO;

/**
 * A partial player statistics (updated at a higher frequency than full network stats)
 * 
 * @author Tony
 *
 */
public class NetPlayerPartialStat implements NetMessage {
    public int playerId;    
    public short kills;
    public short deaths;            
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        playerId = BufferIO.readPlayerId(buffer);
        
        kills = buffer.getShort();
        deaths = buffer.getShort();        
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        BufferIO.writePlayerId(buffer, playerId);
        
        buffer.putShort(kills);
        buffer.putShort(deaths);        
    }
}
