/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.game.type.cmd.FireTeam;
import seventh.network.messages.BufferIO;

/**
 * @author Tony
 *
 */
public class NetFireTeam implements NetMessage {

    public int id;
    public int teamLeaderPlayerId;
    public int[] memberPlayerIds;    
    
    public NetFireTeam() {
        this.memberPlayerIds = new int[FireTeam.MAX_MEMBERS-1];
    }

    @Override
    public void read(IOBuffer buffer) {
        this.id = buffer.getIntBits(2);
        this.teamLeaderPlayerId = BufferIO.readPlayerId(buffer);
        this.memberPlayerIds[0] = BufferIO.readPlayerId(buffer);
        this.memberPlayerIds[1] = BufferIO.readPlayerId(buffer);
        this.memberPlayerIds[2] = BufferIO.readPlayerId(buffer);
        
    }
    
    @Override
    public void write(IOBuffer buffer) {
        buffer.putIntBits(this.id, 2);
        BufferIO.writePlayerId(buffer, this.teamLeaderPlayerId);
        BufferIO.writePlayerId(buffer, this.memberPlayerIds[0]);
        BufferIO.writePlayerId(buffer, this.memberPlayerIds[1]);
        BufferIO.writePlayerId(buffer, this.memberPlayerIds[2]);
    }
}
