/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.network.messages.BufferIO;
import seventh.shared.Bits;

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
    public short assists;
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        playerId = BufferIO.readPlayerId(buffer);
        
        kills = Bits.getSignedShort(buffer.getShortBits(10), 10);
        deaths = buffer.getShortBits(10);
        assists = buffer.getShortBits(10);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        BufferIO.writePlayerId(buffer, playerId);
        
        buffer.putShortBits(Bits.setSignedShort(kills, 10), 10);
        buffer.putShortBits(deaths, 10);  
        buffer.putShortBits(assists, 10);
    }
}
