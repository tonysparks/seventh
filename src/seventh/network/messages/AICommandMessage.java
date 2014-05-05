/*
 * see license.txt 
 */
package seventh.network.messages;

import harenet.IOBuffer;
import seventh.ai.basic.commands.AICommand;
import seventh.ai.basic.commands.AICommandFactory;



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
		super(BufferIO.AI_COMMAND);
	}	
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#read(harenet.IOBuffer)
	 */
	@Override
	public void read(IOBuffer buffer) {
		super.read(buffer);
		botId = buffer.getUnsignedByte();
		byte cmdId = buffer.get();
		command = AICommandFactory.newCommand(buffer, cmdId);
	}
	
	/* (non-Javadoc)
	 * @see seventh.network.messages.AbstractNetMessage#write(harenet.IOBuffer)
	 */
	@Override
	public void write(IOBuffer buffer) {	
		super.write(buffer);
		buffer.putUnsignedByte(botId);
		
		if(command!=null) {
			buffer.put(command.getId());
			command.write(buffer);
		}
		else {
			buffer.put((byte)0);
		}
	}
}
