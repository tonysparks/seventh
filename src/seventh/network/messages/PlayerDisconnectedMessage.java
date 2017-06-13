/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;


/**
 * @author Tony
 *
 */
public class PlayerDisconnectedMessage extends AbstractNetMessage {
    public int playerId;    
    
    /**
     * 
     */
    public PlayerDisconnectedMessage() {
        super(BufferIO.PLAYER_DISCONNECTED);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        playerId = BufferIO.readPlayerId(buffer);
    }
    
     /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        BufferIO.writePlayerId(buffer, playerId);
    }
}
