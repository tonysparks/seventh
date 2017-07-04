/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;

/**
 * @author Tony
 *
 */
public class PlayerSpeechMessage extends AbstractNetMessage {
    public int playerId;
    public int posX;
    public int posY;
    public byte speechCommand;
        
    /**
     * 
     */
    public PlayerSpeechMessage() {
        super(BufferIO.PLAYER_SPEECH);
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
        speechCommand = buffer.getByteBits(4);
        
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
        buffer.putByteBits(speechCommand, 4);
    }

}
