/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;

/**
 * @author Tony
 *
 */
public class PlayerKilledMessage extends AbstractNetMessage {
    public int playerId;
    public int killedById;
    public byte deathType;
    public short posX;
    public short posY;
    
    /**
     * 
     */
    public PlayerKilledMessage() {
        super(BufferIO.PLAYER_KILLED);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        playerId = buffer.getUnsignedByte();
        killedById = buffer.getUnsignedByte();
        deathType = buffer.get();        
        posX = buffer.getShort();
        posY = buffer.getShort();
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        buffer.putUnsignedByte(playerId);
        buffer.putUnsignedByte(killedById);
        buffer.put(deathType);
        buffer.putShort(posX);
        buffer.putShort(posY);
    }
}
