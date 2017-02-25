/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;



/**
 * @author Tony
 *
 */
public class BombDisarmedMessage extends AbstractNetMessage {
    
    public int bombTargetId;
    
    public BombDisarmedMessage(int bombTargetId) {
        this();
        this.bombTargetId = bombTargetId;
    }
    
    
    public BombDisarmedMessage() {
        super(BufferIO.BOMB_DIARMED);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(harenet.IOBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        bombTargetId = buffer.getUnsignedByte();
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(harenet.IOBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        buffer.putUnsignedByte(bombTargetId);
    }
}
