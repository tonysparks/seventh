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
public class ConnectAcceptedMessage extends AbstractNetMessage {
    public int playerId;
    public NetGameState gameState;
    
    /**
     * 
     */
    public ConnectAcceptedMessage() {
        super(BufferIO.CONNECT_ACCEPTED);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        super.read(buffer);
        
        this.playerId = BufferIO.readPlayerId(buffer);
        
        gameState = new NetGameState();
        gameState.read(buffer);
    }

    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {        
        super.write(buffer);
        BufferIO.writePlayerId(buffer, playerId);
        
        gameState.write(buffer);
    }    
}
