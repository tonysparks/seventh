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
public class FlagStolenMessage extends AbstractNetMessage {
	public NetFlag flag;
	
	/**
	 * 
	 */
	public FlagStolenMessage() {
		super(BufferIO.FLAG_STOLEN);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		this.flag = new NetFlag();
		this.flag.read(buffer);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
		this.flag.write(buffer);
	}
}
