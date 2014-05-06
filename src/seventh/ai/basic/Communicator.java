/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import seventh.ai.basic.actions.Action;
import seventh.ai.basic.teamstrategy.TeamStrategy;


/**
 * Allows for giving an Agent to be given orders/Goals from another 
 * source (either a {@link TeamStrategy}, remote command, another agent, etc) 
 * 
 * @author Tony
 *
 */
public class Communicator {

	private Queue<Action> commands;
	
	/**
	 */
	public Communicator() {
		this.commands = new ConcurrentLinkedQueue<Action>();
	}
	
	public void reset(Brain brain) {
		this.commands.clear();
	}
	
	/**
	 * Receives any pending {@link Action}s.
	 * 
	 * @param brain
	 */
	public Action receiveAction(Brain brain) {
		Action cmd = commands.poll();
		return cmd;
	}
	
	/**
	 * Post a command to this bot
	 * @param cmd
	 */
	public void post(Action cmd) {
		this.commands.add(cmd);
	}
}
