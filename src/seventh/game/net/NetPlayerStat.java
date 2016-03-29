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
public class NetPlayerStat implements NetMessage {
    private static final byte IS_BOT       = (1<<0);
    private static final int IS_COMMANDER  = (1<<1);
    
	public int playerId;
	public String name;
	public short kills;
	public short deaths;
	public short ping;
	public int joinTime;
	public byte teamId;
	public byte flags;
	public boolean isBot;
	public boolean isCommander;
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {
		playerId = buffer.getUnsignedByte();
		name = BufferIO.readString(buffer);
		kills = buffer.getShort();
		deaths = buffer.getShort();
		ping = buffer.getShort();
		joinTime = buffer.getInt();
		teamId = buffer.get();
		flags = buffer.get();
		isBot = ((flags & IS_BOT) != 0);
		isCommander = ((flags & IS_COMMANDER) != 0);		
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {
		buffer.putUnsignedByte(playerId);
		BufferIO.write(buffer, name != null ? name : "");
		buffer.putShort(kills);
		buffer.putShort(deaths);
		buffer.putShort(ping);
		buffer.putInt(joinTime);
		buffer.put(teamId);
		
		if(isBot) {
		    flags |= IS_BOT;
		}
		
		if(isCommander) {
			flags |= IS_COMMANDER;
		}
		
		buffer.put(flags);
	}
}
