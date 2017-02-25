/*
 * The Seventh
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;

/**
 * @author Tony
 *
 */
public class PlayerSwitchTeamMessage extends AbstractNetMessage {
    public int playerId;
    public byte teamId;
    
    /**
     * 
     */
    public PlayerSwitchTeamMessage() {
        super(BufferIO.PLAYER_SWITCH_TEAM);
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        playerId = buffer.getUnsignedByte();
        teamId = buffer.get();
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        buffer.putUnsignedByte(playerId);
        buffer.put(teamId);
    }
}
