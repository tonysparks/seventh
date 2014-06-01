/*
 * see license.txt 
 */
package seventh.ai;

import seventh.network.messages.BufferIO;
import harenet.IOBuffer;
import harenet.messages.NetMessage;

/**
 * Directs a bot to do something
 * 
 * @author Tony
 *
 */
public class AICommand implements NetMessage {

	private String message;
	
	/**
	 */
	public AICommand() {
		this("");
	}
	
	/**
	 * @param message
	 */
	public AICommand(String message) {
		this.message = message;
	}

	/* (non-Javadoc)
	 * @see harenet.messages.NetMessage#read(harenet.IOBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {
		this.message = BufferIO.readString(buffer);
	}

	/* (non-Javadoc)
	 * @see harenet.messages.NetMessage#write(harenet.IOBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {
		BufferIO.write(buffer, message);
	}
	
	/**
	 * @return the message
	 */
	public String getMessage() {
		return message;
	}
}
