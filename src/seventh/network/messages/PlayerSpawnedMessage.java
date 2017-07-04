/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;

/**
 * @author Tony
 *
 */
public class PlayerSpawnedMessage extends AbstractNetMessage {
    public int playerId;
    public int posX;
    public int posY;
        
    /**
     * 
     */
    public PlayerSpawnedMessage() {
        super(BufferIO.PLAYER_SPAWNED);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        playerId = BufferIO.readPlayerId(buffer);
        posX = buffer.getIntBits(13); // max X & Y of 256x256 tiles (32x32 tiles) ~8191 
        posY = buffer.getIntBits(13);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        BufferIO.writePlayerId(buffer, playerId);
        buffer.putIntBits(posX, 13);
        buffer.putIntBits(posY, 13);
    }

}
