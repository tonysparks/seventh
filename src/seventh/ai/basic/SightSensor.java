/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.List;

import seventh.game.PlayerEntity;
import seventh.shared.TimeStep;

/**
 * The bots vision
 * 
 * @author Tony
 *
 */
public class SightSensor implements Sensor {

	public static final String SIGHT = "sight";
//	private static final long SIGHT_MEMORY = 5000;
	private static final long REFRESH_TIME = 1500;
	
	private Memory memory;
	private PlayerEntity entity;	
	private World world;
	
	private long timeToSee;
//	private long timeToRemember;
		
	private List<PlayerEntity> entitiesInView;
	
	/**
	 * @param width
	 * @param height
	 */
	public SightSensor(Brain brain, int width, int height) {
		this.memory = brain.getMemory();
		this.entity = brain.getEntityOwner();
		this.world = brain.getWorld();
				
		this.entitiesInView = new ArrayList<PlayerEntity>();	
	}
	
	/**
	 * @return the entitiesInView
	 */
	public List<PlayerEntity> getEntitiesInView() {
		return entitiesInView;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.Sensor#reset()
	 */
	@Override
	public void reset(Brain brain) {
		this.entitiesInView.clear();
		this.entity = brain.getEntityOwner();
	}
	
	/* (non-Javadoc)
	 * @see palisma.ai.Sensor#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		if(this.timeToSee > 0) {
			this.timeToSee -= timeStep.getDeltaTime();
		}
		
		/*
		if(this.timeToRemember > 0) {
			this.timeToRemember -= timeStep.getDeltaTime();
		}		
		else {
			memory.store(SIGHT, LeoNull.LEONULL);
			this.timeToRemember = SIGHT_MEMORY;
		}*/	
		
		see();
		
	}

	/**
	 * Looks for anything interesting in the 
	 * current view port
	 */
	private void see() {	
		if(timeToSee <= 0) {
			this.entitiesInView.clear();
			this.world.getPlayersInLineOfSight(this.entitiesInView, this.entity);
			
			
			memory.store(SIGHT, this.entitiesInView);
			this.timeToSee = REFRESH_TIME;
		}		
	}
}
