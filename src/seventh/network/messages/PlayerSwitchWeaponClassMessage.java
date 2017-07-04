/*
 * The Seventh
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.game.entities.Entity.Type;

/**
 * @author Tony
 *
 */
public class PlayerSwitchWeaponClassMessage extends AbstractNetMessage {
    public Type weaponType;
    
    /**
     * 
     */
    public PlayerSwitchWeaponClassMessage() {
        super(BufferIO.PLAYER_WEAPON_CLASS_CHANGE);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        weaponType = BufferIO.readType(buffer);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        BufferIO.writeType(buffer, weaponType);
    }
}
