/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.game.net.NetGameStats;

/**
 * @author Tony
 *
 */
public class GameEndedMessage extends AbstractNetMessage {    
    public NetGameStats stats;
    
    /**
     * 
     */
    public GameEndedMessage() {
        super(BufferIO.GAME_ENDED);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        stats = new NetGameStats();
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
