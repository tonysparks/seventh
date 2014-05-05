/*
 * see license.txt 
 */
package seventh.ai.basic.commands;

import harenet.messages.NetMessage;
import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;

/**
 * Directs a bot to do something
 * 
 * @author Tony
 *
 */
public interface AICommand extends NetMessage {

	/**
	 * @return the id of this AICommand (used for serialization)
	 */
	public byte getId();
	
	/**
	 * @param brain
	 * @return the {@link Action} to take
	 */
	public Action getAction(Brain brain);
}
