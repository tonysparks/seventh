/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.game.PlayerEntity;
import seventh.shared.TimeStep;

/**
 * Sprint action
 * 
 * @author Tony
 *
 */
public class ShootAction extends AdapterAction {

	/**
	 */
	public ShootAction() {
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished(seventh.ai.basic.Brain)
	 */
	@Override
	public boolean isFinished(Brain brain) {	
		return !brain.getEntityOwner().isFiring();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#end(seventh.ai.basic.Brain)
	 */
	@Override
	public void end(Brain brain) {
		PlayerEntity entity = brain.getEntityOwner();
		entity.endFire();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		PlayerEntity entity = brain.getEntityOwner();
		entity.beginFire();
		
		if(!entity.beginFire()) {			
			this.getActionResult().setFailure();
		}
		else {
			getActionResult().setSuccess();
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		PlayerEntity entity = brain.getEntityOwner();
		entity.beginFire();		
	}
}
