/*
 * see license.txt 
 */
package seventh.ai.basic.commands;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;

/**
 * @author Tony
 *
 */
public class NoActionAICommand extends AbstractAICommand {
	/**
	 * 
	 */
	public NoActionAICommand() {
		super(AICommandFactory.NO_ACTION_COMMAND);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.commands.AICommand#getAction(seventh.ai.basic.Brain)
	 */
	@Override
	public Action getAction(Brain brain) {
		return null;
	}

}
