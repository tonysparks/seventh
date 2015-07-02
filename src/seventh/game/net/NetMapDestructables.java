/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;

/**
 * @author Tony
 *
 */
public class NetMapDestructables implements NetMessage {
    public int length;
    /** Should be viewed as Unsigned Bytes, they are stored in the form
     * of even index = x, odd index = y, example: [1,0,4,7] => tiles at: (1,0) and (4,7) */
    public int[] tiles;
	
    @Override
    public void read(IOBuffer buffer) { 
        this.length = buffer.getInt();
        if(this.length > 0) {
            this.tiles = new int[this.length];
            for(int i = 0; i < this.tiles.length; i += 2) {
                int x = buffer.getUnsignedByte();
                int y = buffer.getUnsignedByte();  
                
                this.tiles[i + 0] = x;
                this.tiles[i + 1] = y;
            }
        }
        
    }
    
    @Override
    public void write(IOBuffer buffer) {    
        buffer.putInt(this.length);
        if(this.length > 0) {
            for(int i = 0; i < this.tiles.length; i += 2) {
                buffer.putUnsignedByte(this.tiles[i + 0]);
                buffer.putUnsignedByte(this.tiles[i + 1]);    
            }
        }
    }
}
