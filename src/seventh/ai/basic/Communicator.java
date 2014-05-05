/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import seventh.ai.basic.actions.Action;


/**
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
	 * Drains one command per execution.
	 * 
	 * @param brain
	 */
	public Action process(Brain brain) {
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
