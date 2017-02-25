/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom.body;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class CrouchAction extends AdapterAction {
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#isFinished(seventh.ai.basic.Brain)
     */
    @Override
    public boolean isFinished(Brain brain) {
        return false;
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
     */
    @Override
    public void update(Brain brain, TimeStep timeStep) {
        brain.getEntityOwner().crouch();
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.AdapterAction#end(seventh.ai.basic.Brain)
     */
    @Override
    public void end(Brain brain) {
        brain.getEntityOwner().standup();
    }
}
