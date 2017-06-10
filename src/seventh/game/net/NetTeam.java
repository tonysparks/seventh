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
public class NetTeam implements NetMessage {
    public static final int HAS_PLAYERS = 1;
    public static final int IS_ATTACKER = 2;
    public static final int IS_DEFENDER = 4;
    
    public byte id;
    public int[] playerIds;
    public boolean isAttacker;
    public boolean isDefender;
    
    protected byte bits;
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        bits = buffer.getByte();
        id = buffer.getByte();
        
        if((bits & HAS_PLAYERS) != 0) {
            byte len = buffer.getByte();
            playerIds = new int[len];
            for(byte i = 0; i < len; i++) {
                playerIds[i] = buffer.getUnsignedByte();
            }
        }
        
        isAttacker = (bits & IS_ATTACKER) != 0;
        isDefender = (bits & IS_DEFENDER) != 0;
        
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        bits = 0;
        
        if(playerIds != null && playerIds.length > 0) {
            bits |= HAS_PLAYERS;
        }
        
        if(isAttacker) {
            bits |= IS_ATTACKER;
        }
        
        if(isDefender) {
            bits |= IS_DEFENDER;
        }
        
        buffer.putByte(bits);
        buffer.putByte(id);
        
        if( (bits & HAS_PLAYERS) != 0 ) {
            buffer.putByte( (byte)playerIds.length);
            for(int i = 0; i < playerIds.length; i++) {
                buffer.putUnsignedByte(playerIds[i]);
            }
        }
    }
}
