/*
 * see license.txt 
 */
package harenet.messages;

import harenet.IOBuffer;

/**
 * Generates {@link NetMessage}'s based off network message.
 * 
 * @author Tony
 *
 */
public interface NetMessageFactory {

    /**
     * Reads the {@link IOBuffer} and creates the corresponding {@link NetMessage}.
     * 
     * @param buffer
     * @return the {@link NetMessage} object
     */
    public NetMessage readNetMessage(IOBuffer buffer);
}
