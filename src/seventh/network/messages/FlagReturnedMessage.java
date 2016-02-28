/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.game.net.NetFlag;

/**
 * @author Tony
 *
 */
public class FlagReturnedMessage extends AbstractNetMessage {
	public NetFlag flag;
	public int playerId;
	
	/**
	 * 
	 */
	public FlagReturnedMessage() {
		super(BufferIO.FLAG_RETURNED);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		this.flag = new NetFlag();
		this.flag.read(buffer);
		this.playerId = buffer.getUnsignedByte();
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
		this.flag.write(buffer);
		this.playerId = buffer.getUnsignedByte();
	}
}
