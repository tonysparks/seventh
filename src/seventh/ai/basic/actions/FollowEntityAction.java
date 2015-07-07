/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.ai.basic.PathPlanner;
import seventh.ai.basic.SightSensor;
import seventh.ai.basic.memory.SightMemory.SightMemoryRecord;
import seventh.game.Entity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Follows the supplied {@link Entity}.
 * 
 * @author Tony
 *
 */
public class FollowEntityAction extends AdapterAction {
	
	private Entity followMe;	
	private Vector2f previousPosition;
	
	private long timeSinceLastSeenExpireMSec;
	
	/**
	 * @param feeder
	 */
	public FollowEntityAction(Entity followMe) {
		this.followMe = followMe;
		if(followMe == null) {
			throw new NullPointerException("The followMe entity is NULL!");
		}
		
		this.previousPosition = new Vector2f();
		
		timeSinceLastSeenExpireMSec = 2_000;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		this.timeSinceLastSeenExpireMSec = 2_000;
		this.timeSinceLastSeenExpireMSec += brain.getWorld().getRandom().nextInt(3) * 1000;
	}
	
	/* (non-Javadoc)
	 * @see palisma.ai.Action#end(palisma.ai.Brain)
	 */
	@Override
	public void end(Brain brain) {		
		brain.getMotion().emptyPath();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
	 */
	@Override
	public void interrupt(Brain brain) {
		brain.getMotion().emptyPath();
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {		
		SightSensor sight = brain.getSensors().getSightSensor();
		SightMemoryRecord mem = sight.getMemoryRecordFor(followMe);
		
		
		boolean isFinished = true;
		if(mem != null && mem.isValid()) {
			isFinished = mem.getTimeSeenAgo() > timeSinceLastSeenExpireMSec;
		}
		
		/* if the entity is out of our sensory memory or is Dead,
		 * expire this action
		 */
		return !this.followMe.isAlive() || isFinished;
	}
	

	/* (non-Javadoc)
	 * @see palisma.ai.Action#update(palisma.ai.Brain, leola.live.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
						
		PathPlanner<?> feeder = brain.getMotion().getPathPlanner();
		SightSensor sight = brain.getSensors().getSightSensor();
		if(!feeder.hasPath() || !feeder.onFirstNode()) {

			SightMemoryRecord mem = sight.getMemoryRecordFor(followMe);
			if(mem != null && mem.isValid()) {
				Vector2f newPosition = mem.getLastSeenAt();
				Vector2f start = brain.getEntityOwner().getPos();
				
				// if the entity we are following is a certain distance away,
				// recalculate the path to it
			    if(Vector2f.Vector2fDistanceSq(start, newPosition) > 10_000) {
					feeder.findPath(start, newPosition);								
				}
				else {
					feeder.clearPath();	
				}
				
				previousPosition.set(newPosition);
			}
		}		
	}

}
