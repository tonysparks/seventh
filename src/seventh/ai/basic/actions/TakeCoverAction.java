/*
 * see license.txt 
 */
package seventh.ai.basic.actions;


import seventh.ai.basic.Brain;
import seventh.ai.basic.Cover;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class TakeCoverAction extends AdapterAction {

	private Vector2f targetPos;
	
	/**
	 * 
	 */
	public TakeCoverAction(Vector2f targetPos) {
		this.targetPos = targetPos;
	}
		
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		
		// TODO :: move to leola script
		
//		Cover cover = brain.getWorld().getCover(brain.getEntityOwner(), targetPos);
//		if(!cover.getCoverPos().isZero()) {
//			brain.getMotion().lookAt(cover.getAttackDir());
//			brain.getGoals().addFirstAction(new MoveToAction(cover.getCoverPos()));			
//		}
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
