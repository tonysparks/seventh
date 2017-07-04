/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.game.entities.Entity.Type;
import seventh.game.weapons.Weapon.WeaponState;
import seventh.network.messages.BufferIO;

/**
 * @author Tony
 *
 */
public class NetWeapon implements NetMessage {    
    public Type type;
    public short ammoInClip;
    public short totalAmmo;    
    public WeaponState weaponState;
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        type = BufferIO.readType(buffer);
        ammoInClip = (short)buffer.getUnsignedByte();
        totalAmmo = buffer.getShort();
        weaponState = BufferIO.readWeaponState(buffer);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        BufferIO.writeType(buffer, type);
        buffer.putUnsignedByte(ammoInClip);
        buffer.putShort(totalAmmo);
        BufferIO.writeWeaponState(buffer, weaponState);
    }
}
