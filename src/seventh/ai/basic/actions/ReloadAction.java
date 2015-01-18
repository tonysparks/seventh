/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.game.PlayerEntity;
import seventh.shared.TimeStep;

/**
 * Reload a weapon action
 * 
 * @author Tony
 *
 */
public class ReloadAction extends AdapterAction {

	/**
	 */
	public ReloadAction() {
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished(seventh.ai.basic.Brain)
	 */
	@Override
	public boolean isFinished(Brain brain) {	
		return !brain.getEntityOwner().isReloading();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		PlayerEntity entity = brain.getEntityOwner();
		if(! entity.reload() ) {
			getActionResult().setFailure();
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
	}
}
