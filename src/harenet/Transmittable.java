/*
 * see license.txt 
 */
package harenet;

import harenet.messages.NetMessageFactory;


/**
 * @author Tony
 *
 */
public interface Transmittable {

	/**
	 * Writes the contents of this message to the buffer
	 * @param buffer
	 */
	public void writeTo(IOBuffer buffer);
	
	
	/**
	 * Reads from the buffer
	 * 
	 * @param buffer
	 * @param messageFactory
	 */
	public void readFrom(IOBuffer buffer, NetMessageFactory messageFactory);
}
