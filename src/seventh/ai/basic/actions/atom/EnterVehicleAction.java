/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.entities.PlayerEntity;
import seventh.game.entities.vehicles.Vehicle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Enters a vehicle
 * 
 * @author Tony
 *
 */
public class EnterVehicleAction extends AdapterAction {

    private Vehicle vehicle;
    /**
     * 
     */
    public EnterVehicleAction(Vehicle vehicle) {
        this.vehicle = vehicle;
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
    
    /**
     * @return true if the bot is in the vehicle
     */
    protected boolean isInVehicle(PlayerEntity bot) {        
        if(this.vehicle.hasOperator() && bot.isOperatingVehicle()) {
            return this.vehicle == bot.getVehicle();
        }
        
        return false;
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
     */
    @Override
    public void update(Brain brain, TimeStep timeStep) {
        PlayerEntity bot = brain.getEntityOwner();
        if(isInVehicle(bot)) {
            this.getActionResult().setSuccess();
        }
        else if(!vehicle.canOperate(brain.getEntityOwner())) {                        
            Locomotion motion = brain.getMotion();
            Vector2f dest = motion.getDestination();
            if(dest == null || !dest.equals(vehicle.getCenterPos())) {
                motion.moveTo(vehicle.getCenterPos());
            }
            getActionResult().setFailure();
        }    
        else if( !bot.isEnteringVehicle() ) {
        	Locomotion motion = brain.getMotion();
            bot.use();
            getActionResult().setFailure();
        }            
        else{
        	Locomotion motion = brain.getMotion();
        	getActionResult().setFailure();
        }        
    }
    

    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#isFinished()
     */
    @Override
    public boolean isFinished(Brain brain) {
        getActionResult().setFailure();
        
        /* error cases, we must finish then */
        if(!vehicle.isAlive()) {
            return true;
        }
        
        if(vehicle.hasOperator()) {
            return true;
        }
        
        PlayerEntity bot = brain.getEntityOwner();
        if(isInVehicle(bot)) {
            getActionResult().setSuccess();
            return true;
        }
        
        return false;
    }
    
    @Override
    public DebugInformation getDebugInformation() {    
        return super.getDebugInformation().add("vehicle", this.vehicle.getId());
    }
}
