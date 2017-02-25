/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.game.net.NetGameState;

/**
 * @author Tony
 *
 */
public class GameReadyMessage extends AbstractNetMessage {
    public NetGameState gameState;
    
    /**
     * 
     */
    public GameReadyMessage() {
        super(BufferIO.GAME_READY);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        super.read(buffer);
        gameState = new NetGameState();
        gameState.read(buffer);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        super.write(buffer);
        gameState.write(buffer);
    }
}
