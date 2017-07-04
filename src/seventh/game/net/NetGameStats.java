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
public class NetGameStats implements NetMessage {
    public NetPlayerStat[] playerStats;
    
    public NetTeamStat alliedTeamStats;
    public NetTeamStat axisTeamStats;
    
    
    /**
     * 
     */
    public NetGameStats() {        
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        byte len = buffer.getByteBits(4);
        if(len > 0) {
            playerStats = new NetPlayerStat[len];
            for(byte i = 0; i < len; i++) {
                playerStats[i] = new NetPlayerStat();
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
            buffer.putByteBits(len, 4);
            for(byte i = 0; i < len; i++) {
                playerStats[i].write(buffer);
            }
        }
        else {
            buffer.putByteBits( (byte)0, 4 );
        }
        
        alliedTeamStats.write(buffer);
        axisTeamStats.write(buffer);
        
    }
}

