/*
 * see license.txt 
 */
package seventh.game.net;

import harenet.IOBuffer;
import harenet.messages.NetMessage;
import seventh.network.messages.BufferIO;
import seventh.shared.Bits;

/**
 * @author Tony
 *
 */
public class NetPlayerStat implements NetMessage {
    
    public int playerId;
    public String name;
    public short kills;
    public short deaths;
    public short assists;
    public byte hitPercentage;
    public short ping;
    public int joinTime;
    public byte teamId;
    
    public boolean isBot;
    public boolean isCommander;
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
     */
    @Override
    public void read(IOBuffer buffer) {
        playerId = BufferIO.readPlayerId(buffer);
        name = BufferIO.readString(buffer);
        kills = Bits.getSignedShort(buffer.getShortBits(10), 10);
        deaths = buffer.getShortBits(10);
        assists = buffer.getShortBits(10);
        hitPercentage = buffer.getByteBits(7);
        ping = buffer.getShortBits(9);
        joinTime = buffer.getInt();
        teamId = BufferIO.readTeamId(buffer);
        
        isBot = buffer.getBooleanBit();
        isCommander = buffer.getBooleanBit();        
    }
    
    /* (non-Javadoc)
     * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
     */
    @Override
    public void write(IOBuffer buffer) {
        BufferIO.writePlayerId(buffer, playerId);
        BufferIO.writeString(buffer, name != null ? name : "");
        buffer.putShortBits(Bits.setSignedShort(kills, 10), 10);
        buffer.putShortBits(deaths, 10);
        buffer.putShortBits(assists, 10);
        buffer.putByteBits(hitPercentage, 7);
        buffer.putShortBits(ping, 9);
        buffer.putInt(joinTime);
        BufferIO.writeTeamId(buffer, teamId);
        
        buffer.putBooleanBit(isBot);
        buffer.putBooleanBit(isCommander);
    }
}
