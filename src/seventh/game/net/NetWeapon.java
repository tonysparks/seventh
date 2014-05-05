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
public class NetWeapon implements NetMessage {	
	public byte type;
	public short ammoInClip;
	public short totalAmmo;	
	public byte state;
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {
		type = buffer.get();
		ammoInClip = (short)buffer.getUnsignedByte();
		totalAmmo = buffer.getShort();
		state = buffer.get();
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {
		buffer.put(type);
		buffer.putUnsignedByte(ammoInClip);
		buffer.putShort(totalAmmo);
		buffer.put(state);
	}
}
