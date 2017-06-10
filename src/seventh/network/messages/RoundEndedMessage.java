/*
 * The Seventh
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.game.net.NetGameStats;

/**
 * @author Tony
 *
 */
public class RoundEndedMessage extends AbstractNetMessage {
    public byte winnerTeamId;
    public NetGameStats stats;
    
    /**
     * 
     */
    public RoundEndedMessage() {
        super(BufferIO.ROUND_ENDED);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        winnerTeamId = buffer.getByte();
        stats = new NetGameStats();
        stats.read(buffer);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        buffer.putByte(winnerTeamId);
        stats.write(buffer);
    }
}
