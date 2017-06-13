/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;


/**
 * @author Tony
 *
 */
public class PlayerConnectedMessage extends AbstractNetMessage {
    public int playerId;
    public String name;
    
    /**
     * 
     */
    public PlayerConnectedMessage() {
        super(BufferIO.PLAYER_CONNECTED);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        playerId = BufferIO.readPlayerId(buffer);
        name = BufferIO.readString(buffer);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        BufferIO.writePlayerId(buffer, playerId);
        BufferIO.writeString(buffer, name != null ? name : "");
    }
}
