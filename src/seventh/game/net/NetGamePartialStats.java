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
public class NetGamePartialStats implements NetMessage {
    public NetPlayerPartialStat[] playerStats;
    public NetTeamStat alliedTeamStats;
    public NetTeamStat axisTeamStats;
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        byte len = buffer.getByteBits(BufferIO.numPlayerIdBits());
        if(len > 0) {
            playerStats = new NetPlayerPartialStat[len];
            for(byte i = 0; i < len; i++) {
                playerStats[i] = new NetPlayerPartialStat();
                playerStats[i].read(buffer);
            }
        }
        
        alliedTeamStats = new NetTeamStat();
        alliedTeamStats.read(buffer);
        alliedTeamStats.id = 1;
        
        axisTeamStats = new NetTeamStat();
        axisTeamStats.read(buffer);
        axisTeamStats.id = 2;
        
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        if(playerStats != null) {
            byte len = (byte)playerStats.length;
            buffer.putByteBits(len, BufferIO.numPlayerIdBits());
            for(byte i = 0; i < len; i++) {
                playerStats[i].write(buffer);
            }
        }
        else {
            buffer.putByteBits( (byte)0, BufferIO.numPlayerIdBits() );
        }
        
        alliedTeamStats.write(buffer);
        axisTeamStats.write(buffer);
        
    }
}

