/**
 * 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.ai.basic.World;
import seventh.ai.basic.Zone;
import seventh.game.Bomb;
import seventh.game.BombTarget;

/**
 * @author Tony
 * 
 */
public class FindSafeDistanceFromActiveBombAction extends AdapterAction {

	private BombTarget target;

	/**
	 * 
	 */
	public FindSafeDistanceFromActiveBombAction(BombTarget target) {
		this.target = target;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		Bomb bomb = target.getBomb();
		if(bomb==null) {
			this.getActionResult().setFailure();
			return;
		}
		
		World world = brain.getWorld();
				
		Zone bombZone = world.getZone(target.getCenterPos());
		Zone adjacentZone = world.findAdjacentZone(bombZone, bomb.getBlastRadius().width);

		/* if we found a safer zone, move to it */
		if (adjacentZone != null) {
			getActionResult().setSuccess(world.getRandomSpot(brain.getEntityOwner(), adjacentZone.getBounds()));
		} else {
			/*
			 * if there isn't a close zone, just move to a random spot --
			 * shouldn't happen
			 */
			getActionResult().setSuccess(world.getRandomSpot(brain.getEntityOwner()));
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
	 */
	@Override
	public void resume(Brain brain) {
		start(brain);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished(seventh.ai.basic.Brain)
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return true;
	}
}
