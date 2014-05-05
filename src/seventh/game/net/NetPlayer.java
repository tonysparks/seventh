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
public class NetPlayer extends NetEntity {

	public static final int HAS_WEAPON = 2;
	public static final int FLASHLIGHT_ON = 4;
	public static final int IS_MECH = 8;
	
	public NetPlayer() {
		this.type = Type.PLAYER.netValue();
	}

	public byte state;
	public byte grenades;
	public byte health;	
	public byte stamina;
	
	public boolean flashLightOn;
	public boolean isMech;
	
	protected byte bits;
	public NetWeapon weapon;
	
	/* (non-Javadoc)
	 * @see seventh.game.net.NetEntity#checkBits()
	 */
	
	protected void checkBits() {		
		if(weapon!=null) {
			bits |= HAS_WEAPON;
		}
		
		if(flashLightOn) {
			bits |= FLASHLIGHT_ON;
		}
		
		if(isMech) {
			bits |= IS_MECH;
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		bits = buffer.get();
		
		orientation = buffer.getShort();
		state = buffer.get();
		grenades = buffer.get();
		health = buffer.get();
		stamina = buffer.get();
		
		if((bits & HAS_WEAPON) != 0) {			
			weapon = new NetWeapon();
			weapon.read(buffer);
		}
		
		if((bits & FLASHLIGHT_ON) != 0) {
			flashLightOn = true;
		}
		
		if((bits & IS_MECH) != 0) {
			isMech = true;
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
		
		bits = 0;
		checkBits();
		buffer.put(bits);
		
		buffer.putShort(orientation);
		buffer.put(state);
		buffer.put(grenades);
		buffer.put(health);
		buffer.put(stamina);		
		
		if(weapon != null) {
			weapon.write(buffer);
		}
	}
}
