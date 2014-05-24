/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.game.Entity.Type;

/**
 * @author Tony
 *
 */
public class NetPlayerPartial extends NetEntity {

	
	public NetPlayerPartial() {
		this.type = Type.PLAYER_PARTIAL.netValue();
	}

	public byte state;
	public byte health;
	public NetWeapon weapon;
	
	
	/* (non-Javadoc)
	 * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
				
		orientation = buffer.getShort();
		state = buffer.get();		
		health = buffer.get();
		readWeapon(buffer);
	}
	
	/**
	 * Reads in the {@link NetWeapon}
	 * @param buffer
	 */
	protected void readWeapon(IOBuffer buffer) {
		weapon = new NetWeapon();
		weapon.type = buffer.get();
		weapon.state = buffer.get();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
				
		buffer.putShort(orientation);
		buffer.put(state);		
		buffer.put(health);
		writeWeapon(buffer);
	}
	
	/**
	 * Writes out the {@link NetWeapon}
	 * @param buffer
	 */
	protected void writeWeapon(IOBuffer buffer) {
		if(weapon != null) {
			buffer.put(weapon.type);
			buffer.put(weapon.state);
		}
		else {
			buffer.put( (byte)-1);
			buffer.put( (byte)0);
		}
	}
}
