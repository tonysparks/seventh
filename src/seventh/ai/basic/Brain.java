/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.List;

import seventh.ai.basic.teamstrategy.TeamStrategy;
import seventh.game.PlayerEntity;
import seventh.game.PlayerInfo;
import seventh.graph.GraphNode;
import seventh.map.PathFeeder;
import seventh.map.Tile;
import seventh.shared.DebugDraw;
import seventh.shared.TimeStep;

/**
 * An AI Brain, handles all of the sensory inputs, locomotion, memory and
 * thought processes.
 * 
 * @author Tony
 *
 */
public class Brain {
		
	private Locomotion motion;
	private Memory memory;
	private Sensors sensors;
	private ThoughtProcess thoughtProcess;
	private Communicator communicator;
	
	private World world;
	
	private PlayerInfo player;
	private PlayerEntity entityOwner;
	
	private TeamStrategy strategy;
	
	private Goal goals;
	
	/**
	 * @param runtime
	 * @param world
	 */
	public Brain(TeamStrategy strategy, World world, PlayerInfo player) {
		this.strategy = strategy;
		this.world = world;
		this.player = player;
		
		this.entityOwner = player.getEntity();
		
		this.memory = new Memory();
		
		this.motion = new Locomotion(this);
		this.sensors = new Sensors(this);
		this.thoughtProcess = new SimpleThoughtProcess(new ReactiveThinkListener(strategy), this);
		this.communicator = new Communicator();
		this.goals = new Goal();

	}
	
	/**
	 * Called after the player is all set, and this entity
	 * is ready to roll
	 */
	public void spawned() {
		this.entityOwner = player.getEntity();
		
		this.communicator.reset(this);
		this.sensors.reset(this);
		this.motion.reset(this);
		
		this.goals.cancel();
				
		/* if this is a Dummy bot, make it just sit there */
		if(player.isDummyBot()) {		
			this.thoughtProcess = DummyThoughtProcess.getInstance();
		}
		else {
			this.thoughtProcess = new SimpleThoughtProcess(new ReactiveThinkListener(strategy), this);			
		}
		
		this.thoughtProcess.onSpawn(this);		
	}
	
	
	/**
	 * Called when the agent dies
	 */
	public void killed() {
		this.thoughtProcess.onKilled(this);
	}
	
	/**
	 * @return the player
	 */
	public PlayerInfo getPlayer() {
		return player;
	}
		
	/**
	 * Lets the brain think for a game tick
	 * 
	 * @param timeStep
	 */
	public void update(TimeStep timeStep) {
		if(player.isAlive()) {			
			this.sensors.update(timeStep);		
			this.motion.update(timeStep);
			this.thoughtProcess.think(timeStep, this);
			
			
			debugDraw();
		}		
	}
	
	
	@SuppressWarnings("unused")
	private void debugDraw() {
		world.getCover(entityOwner, entityOwner.getPos());
		
		List<AttackDirection> attackDirections = world.getAttackDirections(entityOwner);
		for(AttackDirection dir : attackDirections) {
			DebugDraw.drawLineRelative(entityOwner.getPos(), dir.getDirection(), 0xff00ff00);
		}
		
		
//		PathFeeder<?> pathPlanner = motion.getPathFeeder();
//		if(pathPlanner != null) {
//			for(GraphNode<Tile, ?> node : pathPlanner.getPath()) {
//				Tile tile = node.getValue();
//				if(tile != null) {
//					DebugDraw.drawRectRelative(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight(), 0xff00ff00);
//				}
//			}
//		}
	}

	/**
	 * @return the motion
	 */
	public Locomotion getMotion() {
		return motion;
	}

	/**
	 * @return the memory
	 */
	public Memory getMemory() {
		return memory;
	}

	/**
	 * @return the sensors
	 */
	public Sensors getSensors() {
		return sensors;
	}

	/**
	 * @return the thoughtProcess
	 */
	public ThoughtProcess getThoughtProcess() {
		return thoughtProcess;
	}

	/**
	 * @return the communicator
	 */
	public Communicator getCommunicator() {
		return communicator;
	}

	/**
	 * @return the world
	 */
	public World getWorld() {
		return world;
	}

	/**
	 * @return the entityOwner
	 */
	public PlayerEntity getEntityOwner() {
		return entityOwner;
	}
	
	
	/**
	 * @return the goals
	 */
	public Goal getGoals() {
		return goals;
	}
}
