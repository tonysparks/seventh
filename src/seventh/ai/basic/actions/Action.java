/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.shared.Debugable;
import seventh.shared.TimeStep;

/**
 * An action a bot can take, such as Follow someone, attack, etc.
 * 
 * @author Tony
 *
 */
public interface Action extends Debugable {

    /**
     * Cancels this action
     */
    public void cancel();
    
    /**
     * Starts the action, only invoked once upon starting the
     * action.
     * 
     * @param brain
     */
    public void start(Brain brain);
    
    /**
     * Ends the action, only invoked when isFinished returns true.
     * 
     * @param brain
     */
    public void end(Brain brain);
    
    /**
     * @return true if the action is completed
     */
    public boolean isFinished(Brain brain);
        
    /**
     * Interrupts this {@link Action}, allows it to halt
     * any pending activity.
     * 
     * @param brain
     */
    public void interrupt(Brain brain);
    
    /**
     * Resumes the execution of this {@link Action}.
     * 
     * @param brain
     */
    public void resume(Brain brain);
    
    
    /**
     * This becomes valid if {@link #isFinished(Brain)} returns true.
     * @return the {@link ActionResult}
     */
    public ActionResult getActionResult();
    
    
    /**
     * Updates the action
     * 
     * @param brain
     * @param timeStep
     */
    public void update(Brain brain, TimeStep timeStep);
}
