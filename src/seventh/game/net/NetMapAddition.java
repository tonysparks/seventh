/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;

/**
 * A tile addition, specifies the location of the
 * tile and type of tile
 * 
 * @author Tony
 *
 */
public class NetMapAddition implements NetMessage {

    public int tileX, tileY;
    public byte type;

    public NetMapAddition() {     
    }
    
    public NetMapAddition(int tileX, int tileY, byte type) {
        this.tileX = tileX;
        this.tileY = tileY;
        this.type = type;
    }

    @Override
    public void read(IOBuffer buffer) {
        this.tileX = buffer.getUnsignedByte();
        this.tileY = buffer.getUnsignedByte();  
        this.type = buffer.getByteBits(4);
    }

    @Override
    public void write(IOBuffer buffer) {
        buffer.putUnsignedByte(this.tileX);
        buffer.putUnsignedByte(this.tileY);
        buffer.putByteBits(this.type, 4);
    }

}
