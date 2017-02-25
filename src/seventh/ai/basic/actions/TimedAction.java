/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class TimedAction extends WaitAction {

    /**
     * 
     */
    public TimedAction(long timeToWait) {
        super(timeToWait);
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
     */
    @Override
    public void update(Brain brain, TimeStep timeStep) {
        super.update(brain, timeStep);
        doAction(brain, timeStep);
    }
    
    /**
     * Do the action
     * 
     * @param brain
     * @param timeStep
     */
    protected void doAction(Brain brain, TimeStep timeStep) {
    }
    
    
}
