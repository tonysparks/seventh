/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.game.vehicles.Vehicle;

/**
 * @author Tony
 *
 */
public class MoveToVehicleAction extends MoveToAction {
	
    private Vehicle vehicle;
	/**
	 * @param target 
	 */
	public MoveToVehicleAction(Vehicle vehicle) {
		super(vehicle.getCenterPos());
		this.vehicle = vehicle;
	}	
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.MoveToAction#isFinished(seventh.ai.basic.Brain)
	 */
	@Override
	public boolean isFinished(Brain brain) {
	    getActionResult().setFailure();
	    if(!this.vehicle.isAlive()) {
	        return true;
	    }
	    
	    if(this.vehicle.hasOperator()) {
	        return true;
	    }
	    
	    if(this.vehicle.canOperate(vehicle)) {
	        getActionResult().setSuccess();
	        return true;
	    }
	    
	    return super.isFinished(brain);
	}
}
