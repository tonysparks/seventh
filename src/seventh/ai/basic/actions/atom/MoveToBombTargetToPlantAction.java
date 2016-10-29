/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import seventh.ai.basic.Brain;
import seventh.game.entities.Bomb;
import seventh.game.entities.BombTarget;

/**
 * Moves to a bomb target with the intention of planting a {@link Bomb}.  This is slightly different
 * from {@link MoveToBombAction} in that it will fail/stop if the target is already being planted.
 * 
 * @author Tony
 *
 */
public class MoveToBombTargetToPlantAction extends MoveToAction {
	
	private BombTarget target;
	
	
	/**
	 * @param target
	 */
	public MoveToBombTargetToPlantAction(BombTarget target) {
		super(target.getCenterPos());
		
		this.target = target;
	}	

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.MoveToAction#isFinished(seventh.ai.basic.Brain)
	 */
	@Override
	public boolean isFinished(Brain brain) {
		if(this.target.bombPlanting() || this.target.bombActive()) {
			getActionResult().setFailure();
			return true;
		}
		
		return super.isFinished(brain);
	}
}
