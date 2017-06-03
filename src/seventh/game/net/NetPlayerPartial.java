/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import seventh.game.entities.Entity.State;
import seventh.game.entities.Entity.Type;
import seventh.game.weapons.Weapon.WeaponState;
import seventh.network.messages.BufferIO;

/**
 * This is a partial message for other entities that are NOT the local
 * player.  
 * 
 * @author Tony
 *
 */
public class NetPlayerPartial extends NetEntity {

    
    public NetPlayerPartial() {
        this.type = Type.PLAYER_PARTIAL;
    }

    public State state;
    public byte health;
    public NetWeapon weapon;
    
    public boolean isOperatingVehicle;
    public int vehicleId;
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
                
        orientation = BufferIO.readAngle(buffer);
        state = BufferIO.readState(buffer);
        health = buffer.getByteBits(7);
        
        /* If this player is in a vehicle,
         * send the vehicle ID in lieu of 
         * weapon information
         */        
        if(state.isVehicleState()) {
            isOperatingVehicle = true;
            vehicleId = buffer.getUnsignedByte();
        }
        else {            
            readWeapon(buffer);
        }
    }
    
    /**
     * Reads in the {@link NetWeapon}
     * @param buffer
     */
    protected void readWeapon(IOBuffer buffer) {
        weapon = new NetWeapon();
        weapon.type = BufferIO.readType(buffer);
        weapon.weaponState = BufferIO.readWeaponState(buffer);
    }
    
    /* (non-Javadoc)
     * @see seventh.game.net.NetEntity#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
                
        BufferIO.writeAngle(buffer, orientation);
        BufferIO.writeState(buffer, state);        
        buffer.putByteBits(health, 7);
        

        /* If this player is in a vehicle,
         * send the vehicle ID in lieu of 
         * weapon information
         */        
        if(state.isVehicleState()) {
            buffer.putUnsignedByte(vehicleId);
        }
        else {            
            writeWeapon(buffer);
        }
    }
    
    /**
     * Writes out the {@link NetWeapon}
     * @param buffer
     */
    protected void writeWeapon(IOBuffer buffer) {
        if(weapon != null) {
            BufferIO.writeType(buffer, weapon.type);
            BufferIO.writeWeaponState(buffer, weapon.weaponState);
        }
        else {
            BufferIO.writeType(buffer, Type.UNKNOWN);
            BufferIO.writeWeaponState(buffer, WeaponState.UNKNOWN);
        }
    }
}
