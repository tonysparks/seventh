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
public class NetGameTypeInfo implements NetMessage {

	public long maxTime;
	
	public int maxScore;
	public NetTeam[] teams;
	
	public byte type;
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {
		maxTime = buffer.getLong();
		maxScore = buffer.getInt();
		type = buffer.get();
		
		byte len = buffer.get();
		if(len > 0 ) {
			teams= new NetTeam[len];
			for(byte i = 0; i < len; i++) {
				teams[i] = new NetTeam();
				teams[i].read(buffer);
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.NetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {
		buffer.putLong(maxTime);
		buffer.putInt(maxScore);
		buffer.put(type);
		
		if(teams != null) {
			byte len = (byte)teams.length;
			buffer.put(len);
			for(byte i = 0; i < len; i++) {
				teams[i].write(buffer);
			}
		}
		else {
			buffer.put( (byte)0 );
		}
	}
}
