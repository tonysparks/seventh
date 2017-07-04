/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.game.entities.Entity.State;
import seventh.game.entities.Entity.Type;
import seventh.network.messages.BufferIO;

/**
 * The full player state.  This is message is for the local player.
 * 
 * @author Tony
 *
 */
public class NetPlayer extends NetEntity {

    public static final int HAS_WEAPON = 2;
    public static final int IS_OPERATING_VEHICLE = 4;    
    public static final int IS_SMOKE_GRENADES = 8;
    
    public NetPlayer() {
        this.type = Type.PLAYER;
    }

    public State state;
    public byte grenades;
    public byte health;    
    public byte stamina;
    
    public boolean isOperatingVehicle;
    public boolean isSmokeGrenades;
    
    public int vehicleId;
    
    protected byte bits;
    public NetWeapon weapon;
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#checkBits()
     */
    
    protected void checkBits() {        
        if(weapon!=null) {
            bits |= HAS_WEAPON;
        }
        
        if(isOperatingVehicle) {
            bits = 0; /* clear the weapon isRotated */
            bits |= IS_OPERATING_VEHICLE;
        }                
        
        if(isSmokeGrenades) {
            bits |= IS_SMOKE_GRENADES;
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        bits = buffer.getByte();
        
        orientation = BufferIO.readAngle(buffer);
        state = BufferIO.readState(buffer);
        grenades = buffer.getByteBits(4);
        health = buffer.getByteBits(7);
        stamina = buffer.getByteBits(7);
        
        if((bits & HAS_WEAPON) != 0) {            
            weapon = new NetWeapon();
            weapon.read(buffer);
        }
                
        if((bits & IS_OPERATING_VEHICLE) != 0) {
            isOperatingVehicle = true;
            vehicleId = buffer.getUnsignedByte();
        }        
        
        if((bits & IS_SMOKE_GRENADES) != 0) {
            isSmokeGrenades = true;
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
        buffer.putByte(bits);
        
        BufferIO.writeAngle(buffer, orientation);
        BufferIO.writeState(buffer, state);
        buffer.putByteBits(grenades, 4);
        buffer.putByteBits(health, 7);
        buffer.putByteBits(stamina, 7);        
        
        if(weapon != null && !isOperatingVehicle) {
            weapon.write(buffer);
        }
        
        if(isOperatingVehicle) {
            buffer.putUnsignedByte(vehicleId);
        }
    }
}
