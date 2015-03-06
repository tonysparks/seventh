/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

import seventh.ai.basic.actions.Action;
import seventh.ai.basic.teamstrategy.TeamStrategy;
import seventh.game.Player;
import seventh.game.PlayerEntity;
import seventh.math.Rectangle;


/**
 * Allows for giving an Agent to be given orders/Goals from another 
 * source (either a {@link TeamStrategy}, remote command, another agent, etc) 
 * 
 * @author Tony
 *
 */
public class Communicator {

	private Queue<Action> commands;
	private World world;
	
	private Rectangle broadcastBounds;
	
	/**
	 * @param world
	 */
	public Communicator(World world) {
		this.world = world;
		this.commands = new ConcurrentLinkedQueue<Action>();
		this.broadcastBounds = new Rectangle(1000, 1000);
	}
	
	public void reset(Brain brain) {
		this.commands.clear();
	}
	
	/**
	 * Receives any pending {@link Action}s.
	 * 
	 */
	public Action poll() {
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
	
	
	/**
	 * Broadcasts an action for another entity to pick up
	 * 
	 * @param cmd
	 */
	public void broadcastAction(Brain brain, Action cmd) {
		if(brain.getPlayer().isAlive()) {
			broadcastBounds.centerAround(brain.getEntityOwner().getCenterPos());
			List<Player> teammates = world.getTeammates(brain);
			for(int i = 0; i < teammates.size(); i++) {
				Player player = teammates.get(i);
				if(player.isBot() && player.isAlive()) {
					
					PlayerEntity ent = player.getEntity();
					if(broadcastBounds.intersects(ent.getBounds())) {
						Brain other = world.getBrain(player.getId());
						other.getCommunicator().post(cmd);
					}
					
				}
			}
		}
	}
}
