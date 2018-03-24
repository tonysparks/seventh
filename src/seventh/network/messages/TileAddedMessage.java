/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.game.net.NetMapAddition;


/**
 * A tile has been added to the world
 * 
 * @author Tony
 *
 */
public class TileAddedMessage extends AbstractNetMessage {
    public NetMapAddition tile;
    
    public TileAddedMessage() {
        super(BufferIO.TILE_ADDED);
    }
    
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        this.tile = new NetMapAddition();
        this.tile.read(buffer);
    }
    
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        tile.write(buffer);
    }
}
