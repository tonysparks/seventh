/*
 * The Seventh
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.game.PlayerClass;

/**
 * @author Tony
 *
 */
public class PlayerSwitchPlayerClassMessage extends AbstractNetMessage {
    public PlayerClass playerClass;
    
    /**
     * 
     */
    public PlayerSwitchPlayerClassMessage() {
        super(BufferIO.PLAYER_CLASS_CHANGE);
    }
    
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        playerClass = PlayerClass.fromNet(buffer.getByteBits(3));
    }
    
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        buffer.putByteBits(PlayerClass.toNet(playerClass), 3);
    }
}
