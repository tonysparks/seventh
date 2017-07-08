/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.game.PlayerAwardSystem.Award;

/**
 * @author Tony
 *
 */
public class PlayerAwardMessage extends AbstractNetMessage {
    public int playerId;
    public Award award;
    public byte killStreak;
    
    public PlayerAwardMessage() {
        super(BufferIO.PLAYER_AWARD);
    }
    
    @Override
    public void read(IOBuffer buffer) {    
        super.read(buffer);
        playerId = BufferIO.readPlayerId(buffer);
        award = Award.fromNetValue(buffer.getByteBits(Award.numOfBits()));
        switch(award) {            
            case KillRoll:
            case KillStreak:
                killStreak = buffer.getByteBits(4);
                break;
            default:
                break;
        
        }
    }
    
    @Override
    public void write(IOBuffer buffer) {    
        super.write(buffer);
        BufferIO.writePlayerId(buffer, playerId);
        buffer.putByteBits(award.netValue(), Award.numOfBits());
        switch(award) {            
            case KillRoll:
            case KillStreak:
                buffer.putByteBits(killStreak, 4);
                break;
            default:
                break;    
        }
    }
}
