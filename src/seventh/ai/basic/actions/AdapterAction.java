/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.shared.TimeStep;

/**
 * An empty implementation for ease of inheritance use.
 * 
 * @author Tony
 *
 */
public class AdapterAction implements Action {

    private ActionResult result;
    
    /**
     */
    public AdapterAction() {
        this.result = new ActionResult();
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.Action#cancel()
     */
    @Override
    public void cancel() {
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.Action#start(seventh.ai.basic.Brain)
     */
    @Override
    public void start(Brain brain) {
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.Action#end(seventh.ai.basic.Brain)
     */
    @Override
    public void end(Brain brain) {
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.Action#isFinished()
     */
    @Override
    public boolean isFinished(Brain brain) {
        return true;
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.Action#interrupt(seventh.ai.basic.Brain)
     */
    @Override
    public void interrupt(Brain brain) {    
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.Action#resume(seventh.ai.basic.Brain)
     */
    @Override
    public void resume(Brain brain) {    
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.Action#getActionResult()
     */
    @Override
    public ActionResult getActionResult() {
        return this.result;
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.Action#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
     */
    @Override
    public void update(Brain brain, TimeStep timeStep) {
    }

    /* (non-Javadoc)
     * @see seventh.shared.Debugable#getDebugInformation()
     */
    @Override
    public DebugInformation getDebugInformation() {
        DebugInformation me = new DebugInformation();
        me.add("type", getClass().getSimpleName());
        return me;
    }
    
    /* (non-Javadoc)
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return getDebugInformation().toString();
    }
}
