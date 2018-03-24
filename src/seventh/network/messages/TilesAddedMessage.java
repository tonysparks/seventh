/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.game.net.NetMapAdditions;


/**
 * A number of tiles have been added to the world
 * 
 * @author Tony
 *
 */
public class TilesAddedMessage extends AbstractNetMessage {
    public NetMapAdditions tiles;
    
    public TilesAddedMessage() {
        super(BufferIO.TILES_ADDED);
    }
    
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        this.tiles = new NetMapAdditions();
        this.tiles.read(buffer);
    }
    
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        this.tiles.write(buffer);
    }
}
