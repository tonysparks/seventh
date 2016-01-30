/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.ai.basic.actions.Action;
import seventh.ai.basic.teamstrategy.TeamStrategy;
import seventh.game.PlayerEntity;
import seventh.game.PlayerInfo;
import seventh.graph.GraphNode;
import seventh.map.Tile;
import seventh.math.Vector2f;
import seventh.shared.DebugDraw;
import seventh.shared.Debugable;
import seventh.shared.TimeStep;

/**
 * An AI Brain, handles all of the sensory inputs, locomotion, memory and
 * thought processes.
 * 
 * @author Tony
 *
 */
public class Brain implements Debugable {
		
	private Locomotion motion;
	private Memory memory;
	private Sensors sensors;
	private ThoughtProcess thoughtProcess;
	private Communicator communicator;
	
	private World world;
	
	private PlayerInfo player;
	private PlayerEntity entityOwner;			
	
	private TargetingSystem targetingSystem;
	
	/**
	 * @param runtime
	 * @param world
	 */
	public Brain(TeamStrategy strategy, World world, PlayerInfo player) {
		this.world = world;
		this.player = player;
		
		this.entityOwner = player.getEntity();
		
		this.memory = new Memory(this);
		
		this.motion = new Locomotion(this);
		this.sensors = new Sensors(this);
		this.thoughtProcess = new WeightedThoughtProcess(strategy, this); 
//				new SimpleThoughtProcess(new ReactiveThinkListener(strategy, world.getGoals()), this);
		this.communicator = new Communicator(world);
		
		this.targetingSystem = new TargetingSystem(this);
	}
	
	/**
	 * Called after the player is all set, and this entity
	 * is ready to roll
	 */
	public void spawned(TeamStrategy strategy) {
		this.entityOwner = player.getEntity();
		
		this.targetingSystem.reset(this);
		this.communicator.reset(this);
		this.sensors.reset(this);
		this.motion.reset(this);				
				
		/* if this is a Dummy bot, make it just sit there */
		if(player.isDummyBot()) {		
			this.thoughtProcess = DummyThoughtProcess.getInstance();
		}
		else {
			this.thoughtProcess = new WeightedThoughtProcess(strategy, this); 
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
	 * @return the config
	 */
	public AIConfig getConfig() {
		return this.world.getConfig();
	}
	
	/**
	 * @return the player
	 */
	public PlayerInfo getPlayer() {
		return player;
	}
	
	public double getRandomRange(double min, double max) {
		return this.world.getRandom().getRandomRange(min, max);
	}
	
	
	public double getRandomRangeMin(double min) {
		return getRandomRange(min, 1.0);
	}
	
	public double getRandomRangeMax(double max) {
		return getRandomRange(0.0, max);
	}
	
	/**
	 * Broadcasts a command
	 * 
	 * @param cmd
	 */
	public void broadcastCommand(Action cmd) {
		this.communicator.broadcastAction(this, cmd);
	}
		
	/**
	 * Lets the brain think for a game tick
	 * 
	 * @param timeStep
	 */
	public void update(TimeStep timeStep) {
		/* We can't rely on the player's isAlive method because
		 * we might have respawned with a new entity, in which 
		 * case the Brain.spawned method will be invoked, but
		 * for now we must rely on the entity that is currently
		 * bound to this Brain
		 */
		if(entityOwner!=null&&entityOwner.isAlive()) {			
			this.memory.update(timeStep);
			this.sensors.update(timeStep);		
			this.motion.update(timeStep);
			this.thoughtProcess.think(timeStep, this);
			
			this.targetingSystem.update(timeStep);
			
			debugDraw();
			//debugDrawPathPlanner();
		}		
	}
	
	
	@SuppressWarnings("unused")
	private void debugDraw() {
		Cover cover = world.getCover(entityOwner, entityOwner.getPos());
		//DebugDraw.fillRectRelative( (int)cover.getCoverPos().x, (int)cover.getCoverPos().y, 5, 5, 0xff00ff00);
		
		/*List<AttackDirection> attackDirections = world.getAttackDirections(entityOwner);
		for(AttackDirection dir : attackDirections) {
			DebugDraw.drawLineRelative(entityOwner.getPos(), dir.getDirection(), 0xff00ff00);
		}*/
		
		//DebugDraw.drawString(this.thoughtProcess.toString(), new Vector2f(-120, 700), 0xff00ffff);
		
		//Vector2f p = new Vector2f(entityOwner.getPos().x-250, entityOwner.getPos().y + 40);
		//DebugDraw.drawStringRelative(motion.getDebugInformation().toString(), p, 0xff00ffff);
		//DebugDraw.drawString(this.thoughtProcess.toString(), p, 0xff00ffff);
		
		
		Vector2f p = new Vector2f(entityOwner.getPos().x-50, entityOwner.getPos().y + 40);
		String str = this.thoughtProcess.toString();
		String[] sections = str.split("\\{");
		for(String section : sections) {
			String[] attributes = section.split(":");
			for(String att : attributes) {
				
				DebugDraw.drawStringRelative(att, p, 0xff00ffff);
				p.y += 12;
			}
			p.x += 10;
		}
		
		/*for(BombTarget target : world.getBombTargetsWithActiveBombs()) {
			if( target.getBomb() != null ) {
				Bomb bomb = target.getBomb();
				Rectangle b = bomb.getBlastRadius();
				DebugDraw.fillRectRelative(b.x, b.y, b.width, b.height, 0xaf00ffff);
			}
		}*/
	}
	
	@SuppressWarnings("unused")
	private void debugDrawPathPlanner() {
		int x = (int) entityOwner.getCenterPos().x;
		int y = (int) entityOwner.getCenterPos().y;
		GraphNode<Tile, ?> snode = world.getGraph().getNodeByWorld(x,y);
		if(snode != null) {
			DebugDraw.fillRectRelative(snode.getValue().getX(), snode.getValue().getY(), snode.getValue().getWidth(), snode.getValue().getHeight(), 0x8f00ff00);
		}
		
		Tile t = world.getMap().getWorldTile(0, x, y);
		if(t!=null) {
			DebugDraw.fillRectRelative(t.getX(), t.getY(), t.getWidth(), t.getHeight(), 0x8f0000ff);
		}
		
//		MapGraph<?> tt = world.getGraph();
//		for(int yy = 0; yy < tt.graph.length; yy++) {
//			for(int xx = 0; xx < tt.graph[0].length; xx++) {
//				snode =  (GraphNode<Tile, ?>) tt.graph[yy][xx];
//				if(snode != null) {
//					DebugDraw.fillRectRelative(snode.getValue().getX(), snode.getValue().getY(), snode.getValue().getWidth(), snode.getValue().getHeight(), 0x1fffff00);
//					DebugDraw.drawRectRelative(snode.getValue().getX(), snode.getValue().getY(), snode.getValue().getWidth(), snode.getValue().getHeight(), 0xffffffff);
//				}
//			}
//		}
		
		
		PathPlanner<?> pathPlanner = motion.getPathPlanner();
		if(pathPlanner != null) {
			for(GraphNode<Tile, ?> node : pathPlanner.getPath()) {
				Tile tile = node.getValue();
				if(tile != null) {
					DebugDraw.fillRectRelative(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight(), 0x1f00ff00);
				}
			}
		}		
	}

	/**
	 * @return the targetingSystem
	 */
	public TargetingSystem getTargetingSystem() {
		return targetingSystem;
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
		
	
	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation me = new DebugInformation();
		me.add("entity_id", (this.entityOwner!=null) ? getEntityOwner().getId() : null)
		  .add("locomotion", this.motion)
		  .add("thoughts", getThoughtProcess());
		return me;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDebugInformation().toString();
	}
}
