/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;

/**
 * Additions made to the Map by players
 * 
 * @author Tony
 *
 */
public class NetMapAdditions implements NetMessage {
      
    public NetMapAddition[] tiles;
    
    @Override
    public void read(IOBuffer buffer) { 
        int length = buffer.getInt();
        if(length > 0) {
            this.tiles = new NetMapAddition[length];
            for(int i = 0; i < this.tiles.length; i++) {
                NetMapAddition addition = new NetMapAddition();
                addition.read(buffer);
                
                this.tiles[i] = addition;
            }
        }
        
    }
    
    @Override
    public void write(IOBuffer buffer) {
        if(this.tiles != null) {
            buffer.putInt(this.tiles.length);
            for(int i = 0; i < this.tiles.length; i++) {
                NetMapAddition add = this.tiles[i];
                add.write(buffer);                
            }
        }
        else {
            buffer.putInt(0);
        }
    }
}
