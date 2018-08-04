/*
 * The Seventh
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;

/**
 * @author Tony
 *
 */
public class PlayerSwitchTileMessage extends AbstractNetMessage {
    public int newTileId;
    
    /**
     * 
     */
    public PlayerSwitchTileMessage() {
        super(BufferIO.PLAYER_SWITCH_TEAM);
    }
    
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        newTileId = BufferIO.readTileType(buffer);
        
    }
    
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        BufferIO.writeTileType(buffer, newTileId);
    }
}
