/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import java.util.List;

import seventh.ai.basic.Brain;
import seventh.game.BombTarget;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class FindClosestBombTarget extends AdapterAction {

	private boolean toDefuse;
	/**
	 * 
	 */
	public FindClosestBombTarget(boolean toDefuse) {
		this.toDefuse = toDefuse;
	}
	
	
	private boolean isValidBombTarget(Brain brain, BombTarget bomb) {
		return bomb.isAlive() && ( toDefuse ? bomb.bombActive() : (!bomb.isBombAttached() && !bomb.bombActive() && !bomb.bombPlanting()) );
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		List<BombTarget> targets = brain.getWorld().getBombTargets();
		
		Vector2f botPos = brain.getEntityOwner().getPos();
		
		if(!targets.isEmpty()) {
			BombTarget closestBomb = null;
			float distance = -1;
			
			/* lets find the closest bomb target */
			for(int i = 0; i < targets.size(); i++) {
				BombTarget bomb = targets.get(i);
				
				/* make sure this bomb is eligable for planting */
				if(isValidBombTarget(brain, bomb)) {
						
					float distanceToBomb = Vector2f.Vector2fDistanceSq(bomb.getPos(), botPos);
					
					/* if we haven't assigned a closest or we have a closer bomb
					 * assign it.
					 */
					if(closestBomb == null || distanceToBomb < distance) {
						closestBomb = bomb;
						distance = distanceToBomb;
					}
				}
				
			}
		
			if(closestBomb != null) {				
				this.getActionResult().setSuccess(closestBomb);
				return;
			}
		}	
		
		getActionResult().setFailure();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
	 */
	@Override
	public void resume(Brain brain) {
		start(brain);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return true;
	}

}
