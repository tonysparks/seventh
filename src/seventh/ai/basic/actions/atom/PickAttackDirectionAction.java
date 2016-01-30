/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import java.util.List;

import seventh.ai.basic.AttackDirection;
import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class PickAttackDirectionAction extends AdapterAction {

	private List<AttackDirection> attackDirs;
	
	/**
	 * 
	 */
	public PickAttackDirectionAction(List<AttackDirection> attackDirs) {
		this.attackDirs = attackDirs;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		if(!attackDirs.isEmpty()) {
			Vector2f targetPos = attackDirs.get(brain.getWorld().getRandom().nextInt(attackDirs.size())).getDirection();
			this.getActionResult().setSuccess(targetPos);
		}
		
		else {
			this.getActionResult().setFailure();
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
