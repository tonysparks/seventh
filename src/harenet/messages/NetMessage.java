/*
 * see license.txt 
 */
package harenet.messages;

import harenet.IOBuffer;

import java.nio.ByteBuffer;

/**
 * A network message
 * 
 * @author Tony
 *
 */
public interface NetMessage {
	
	/**
	 * Reads from the {@link ByteBuffer}
	 * @param buffer
	 */
	public void read(IOBuffer buffer);
	
	/**
	 * Writes to the {@link ByteBuffer}
	 * @param buffer
	 */
	public void write(IOBuffer buffer);
	
}
