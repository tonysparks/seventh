/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.entities.PlayerEntity;
import seventh.shared.TimeStep;

/**
 * Exits a vehicle
 * 
 * @author Tony
 *
 */
public class ExitVehicleAction extends AdapterAction {

	
	/**
	 * 
	 */
	public ExitVehicleAction() {
	    this.getActionResult().setFailure();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
	 */
	@Override
	public void interrupt(Brain brain) {
		brain.getMotion().stopUsingHands();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#end(seventh.ai.basic.Brain)
	 */
	@Override
	public void end(Brain brain) {
		brain.getMotion().stopUsingHands();
	}
	
	
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
	    PlayerEntity bot = brain.getEntityOwner();
		if(bot.isExitingVehicle()) {
			this.getActionResult().setSuccess();
		}
		else {			
			bot.use();
			getActionResult().setFailure();
		}
	}
	

	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
	    getActionResult().setFailure();
		
		PlayerEntity bot = brain.getEntityOwner();
		if(bot.isExitingVehicle() || !bot.isOperatingVehicle()) {
		    getActionResult().setSuccess();
		    return true;
		}
		
		return false;
	}
	
}
