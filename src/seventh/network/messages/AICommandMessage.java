/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.ai.AICommand;



/**
 * Sends an AI Command to a bot
 * 
 * @author Tony
 *
 */
public class AICommandMessage extends AbstractNetMessage {
	
	public int botId;
	public AICommand command;
	
	public AICommandMessage() {
		this(new AICommand());
	}
	
	public AICommandMessage(AICommand cmd) {
		super(BufferIO.AI_COMMAND);
		this.command = cmd;
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#read(harenet.IOBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {
		super.read(buffer);
		botId = buffer.getUnsignedByte();						
		command.read(buffer);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#write(harenet.IOBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
		buffer.putUnsignedByte(botId);		
		command.write(buffer);
	}
}
