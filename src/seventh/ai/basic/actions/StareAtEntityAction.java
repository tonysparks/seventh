/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import java.util.List;

import seventh.ai.basic.Brain;
import seventh.game.Entity;
import seventh.game.PlayerEntity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class StareAtEntityAction extends AdapterAction {

	private static final int MAX_SLOP = (int)((Math.PI/30.0) * 100.0);
	
	private Entity stareAtMe;
	private long lastVisibleTime;
	private final long timeSinceLastSeenExpireMSec;
	
	public StareAtEntityAction(Entity stareAtMe) {
		this.stareAtMe = stareAtMe;
		timeSinceLastSeenExpireMSec = 1_000;
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return !this.stareAtMe.isAlive() || this.lastVisibleTime > timeSinceLastSeenExpireMSec;
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#update(palisma.ai.Brain, leola.live.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		Entity me = brain.getEntityOwner();
		Vector2f entityPos = stareAtMe.getPos();				
		List<PlayerEntity> entitiesInView = brain.getSensors().getSightSensor().getEntitiesInView();
		if(entitiesInView.contains(this.stareAtMe)) {
			this.lastVisibleTime = 0;
			float slop = 0.0f;//brain.getWorld().getRandom().nextInt(MAX_SLOP+25) / 100.0f;
			me.setOrientation(Entity.getAngleBetween(entityPos, me.getPos()) + slop );		
		}
		else {
			this.lastVisibleTime += timeStep.getDeltaTime();
		}
				
	}

}
