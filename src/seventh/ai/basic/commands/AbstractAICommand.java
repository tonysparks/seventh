/*
 * see license.txt 
 */
package seventh.ai.basic.commands;

import harenet.IOBuffer;

/**
 * @author Tony
 *
 */
public abstract class AbstractAICommand implements AICommand {

	private byte id;
	/**
	 * 
	 */
	public AbstractAICommand(byte id) {
		this.id = id;
	}

	/* (non-Javadoc)
	 * @see harenet.messages.NetMessage#read(harenet.IOBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {		
	}

	/* (non-Javadoc)
	 * @see harenet.messages.NetMessage#write(harenet.IOBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {
	}

	/* (non-Javadoc)
	 * @see seventh.ai.commands.AICommand#getId()
	 */
	@Override
	public byte getId() {
		return id;
	}
}
