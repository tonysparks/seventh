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
public class NetGamePartialStats implements NetMessage {
    public NetPlayerPartialStat[] playerStats;
    public NetTeamStat[] teamStats;
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        byte len = buffer.getByte();
        if(len > 0) {
            playerStats = new NetPlayerPartialStat[len];
            for(byte i = 0; i < len; i++) {
                playerStats[i] = new NetPlayerPartialStat();
                playerStats[i].read(buffer);
            }
        }
        
        len = buffer.getByte();
        if( len > 0 ) {
            teamStats = new NetTeamStat[len];
            for(byte i = 0; i < len; i++) {
                teamStats[i] = new NetTeamStat();
                teamStats[i].read(buffer);
            }
        }
        
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        if(playerStats != null) {
            byte len = (byte)playerStats.length;
            buffer.putByte(len);
            for(byte i = 0; i < len; i++) {
                playerStats[i].write(buffer);
            }
        }
        else {
            buffer.putByte( (byte)0 );
        }
        
        if ( teamStats != null ) {
            byte len = (byte)teamStats.length;
            buffer.putByte(len);
            for(byte i = 0; i < len; i++) {
                teamStats[i].write(buffer);
            }
        }
        else {
            buffer.putByte( (byte)0 );
        }
        
    }
}

