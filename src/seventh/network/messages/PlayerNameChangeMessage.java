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
public class PlayerNameChangeMessage extends AbstractNetMessage {	
	public String name;
	
	/**
	 * 
	 */
	public PlayerNameChangeMessage() {
		super(BufferIO.PLAYER_NAME_CHANGE);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#read(java.nio.ByteBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {	
		super.read(buffer);
		name = BufferIO.readString(buffer);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#write(java.nio.ByteBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);		
		BufferIO.write(buffer, name);
	}
}
