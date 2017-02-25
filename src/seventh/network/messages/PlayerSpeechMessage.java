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
    public short posX;
    public short posY;
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
        playerId = buffer.getUnsignedByte();        
        posX = buffer.getShort();
        posY = buffer.getShort();
        speechCommand = buffer.get();
        
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);

        buffer.putUnsignedByte(playerId);
        buffer.putShort(posX);
        buffer.putShort(posY);
        buffer.put(speechCommand);
    }

}
