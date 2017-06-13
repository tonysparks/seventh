/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.network.messages.BufferIO;

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
    
    private boolean hasPlayers;
    public boolean isAttacker;
    public boolean isDefender;
    
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        id = BufferIO.readTeamId(buffer);
        
        hasPlayers = buffer.getBooleanBit();
        isAttacker = buffer.getBooleanBit();
        isDefender = !isAttacker;
        
        if(hasPlayers) {
            byte len = buffer.getByteBits(4);
            playerIds = new int[len];
            for(byte i = 0; i < len; i++) {
                playerIds[i] = buffer.getUnsignedByte();
            }
        }
                
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        hasPlayers = playerIds != null && playerIds.length > 0;
        
        BufferIO.writeTeamId(buffer, id);
        buffer.putBooleanBit(hasPlayers);
        buffer.putBooleanBit(isAttacker);
                
        if(hasPlayers) {
            buffer.putByteBits( (byte)playerIds.length, 4);
            for(int i = 0; i < playerIds.length; i++) {
                buffer.putUnsignedByte(playerIds[i]);
            }
        }
    }
}
