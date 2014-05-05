/*
 * see license.txt 
 */
package seventh.ai.basic.commands;

import harenet.IOBuffer;

/**
 * @author Tony
 *
 */
public class AICommandFactory {
	
	public static final byte NO_ACTION_COMMAND = 1;
	public static final byte DEFEND_COMMAND = 2;
	public static final byte PLANT_BOMB_COMMAND = 3;
	public static final byte DEFUSE_BOMB_COMMAND = 4;
	public static final byte INFILTRATE_COMMAND = 5;
	
	
	public static AICommand newCommand(IOBuffer buffer, byte cmdId) {
		AICommand command = null;
		switch(cmdId) {
//			case DEFAULT_COMMAND: command = new DefaultAICommand();
//				break;
//			case DEFEND_COMMAND: command = new DefendAICommand();
//				break;
//			case PLANT_BOMB_COMMAND: command = new PlantBombAICommand();
//				break;
//			case DEFUSE_BOMB_COMMAND: command = new DefuseBombAICommand();
//				break;
			default: {
				command = new NoActionAICommand();
			}
		}
		
		if(command != null) {
			command.read(buffer);
		}
		
		return command;
	}
}
