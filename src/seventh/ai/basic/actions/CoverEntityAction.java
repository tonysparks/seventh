/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.ai.basic.PathPlanner;
import seventh.game.Entity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Cover a friend
 * 
 * @author Tony
 *
 */
public class CoverEntityAction extends AdapterAction {
	
	private Entity followMe;
	
	private long lastVisibleTime;
	private final long timeSinceLastSeenExpireMSec;
	
	/**
	 * @param feeder
	 */
	public CoverEntityAction(Entity followMe) {
		this.followMe = followMe;
		this.timeSinceLastSeenExpireMSec = 7_000;
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
	    boolean isfinished = !this.followMe.isAlive() || this.lastVisibleTime > timeSinceLastSeenExpireMSec;
		return isfinished;
	}
	
	/* (non-Javadoc)
	 * @see palisma.ai.Action#update(palisma.ai.Brain, leola.live.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		
		
		if(! brain.getSensors().getSightSensor().inView(this.followMe) ) {		
			this.lastVisibleTime += timeStep.getDeltaTime();
		}
		else {
			this.lastVisibleTime = 0;
		}
		
		PathPlanner<?> feeder = brain.getMotion().getPathPlanner();
		if(!feeder.hasPath() || !feeder.onFirstNode()) {
			Vector2f newPosition = this.followMe.getPos();
			Vector2f start = brain.getEntityOwner().getPos();
			float distance = Vector2f.Vector2fDistanceSq(start, newPosition);
			
			
			if(distance > 15_000) {
				feeder.findPath(start, newPosition);						
			}			
			else {
				/* stop the agent */
				if(feeder != null) {
					feeder.clearPath();
					brain.getMotion().scanArea();
				}
			}
		}		
	}

}
