/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.game.net.NetGamePartialStats;

/**
 * Just a partial game statistics update
 * 
 * @author Tony
 *
 */
public class GamePartialStatsMessage extends AbstractNetMessage {
    public NetGamePartialStats stats;
    
    /**
     * 
     */
    public GamePartialStatsMessage() {
        super(BufferIO.GAME_PARTIAL_STATS);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        stats = new NetGamePartialStats();
        stats.read(buffer);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        stats.write(buffer);
    }
}
