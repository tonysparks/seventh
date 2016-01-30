/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import java.util.List;

import seventh.ai.basic.AttackDirection;
import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;

/**
 * @author Tony
 *
 */
public class EvaluateAttackDirectionsAction extends AdapterAction {

	/**
	 * 
	 */
	public EvaluateAttackDirectionsAction() {
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		List<AttackDirection> attackDirs = brain.getWorld().getAttackDirections(brain.getEntityOwner());		
		this.getActionResult().setSuccess(attackDirs);
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
