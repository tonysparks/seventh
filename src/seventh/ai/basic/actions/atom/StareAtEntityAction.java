/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.Entity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class StareAtEntityAction extends AdapterAction {

	/**
	 * Max value to offset aiming
	 */
	private static final float MAX_SLOP = (float)(Math.PI/8);
	
	private Entity stareAtMe;	
	private final long timeSinceLastSeenExpireMSec;
	
	public StareAtEntityAction(Entity stareAtMe) {
		reset(stareAtMe);
		timeSinceLastSeenExpireMSec = 1_300;
	}
	
	/**
	 * @param stareAtMe the stareAtMe to set
	 */
	public void reset(Entity stareAtMe) {
		this.stareAtMe = stareAtMe;				
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return !this.stareAtMe.isAlive() || brain.getSensors().getSightSensor().timeSeenAgo(stareAtMe) > timeSinceLastSeenExpireMSec;
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#update(palisma.ai.Brain, leola.live.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		Entity me = brain.getEntityOwner();
		Vector2f entityPos = stareAtMe.getCenterPos();				
		if( brain.getSensors().getSightSensor().inView(this.stareAtMe) ||
		    brain.getTargetingSystem().targetInLineOfFire(entityPos)) {					
			/* add some slop value so that the Agent isn't too accurate */
			float slop = brain.getWorld().getRandom().nextFloat() * (MAX_SLOP/3f);
			
			me.setOrientation(Entity.getAngleBetween(entityPos, me.getCenterPos()) + slop );			
		}				
	}

	@Override
	public DebugInformation getDebugInformation() {	
		return super.getDebugInformation().add("stareAt", this.stareAtMe);
	}
}
