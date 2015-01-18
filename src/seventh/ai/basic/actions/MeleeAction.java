/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.game.PlayerEntity;
import seventh.shared.TimeStep;

/**
 * Melee attack action
 * 
 * @author Tony
 *
 */
public class MeleeAction extends AdapterAction {

	/**
	 */
	public MeleeAction() {
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished(seventh.ai.basic.Brain)
	 */
	@Override
	public boolean isFinished(Brain brain) {	
		return !brain.getEntityOwner().isMeleeAttacking();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		PlayerEntity entity = brain.getEntityOwner();
		if(! entity.meleeAttack() ) {
			getActionResult().setFailure();
		}
		else {
			getActionResult().setSuccess();
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#end(seventh.ai.basic.Brain)
	 */
	@Override
	public void end(Brain brain) {
		PlayerEntity entity = brain.getEntityOwner();
		entity.doneMeleeAttack();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		PlayerEntity entity = brain.getEntityOwner();
		entity.doneMeleeAttack();
		
	}
}
