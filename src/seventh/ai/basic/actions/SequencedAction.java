/*
 * see license.txt
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;

/**
 * @author Tony
 *
 */
public class SequencedAction extends CompositeAction {

    private boolean started;
    
    /**
     * @param name
     */
    public SequencedAction(String name) {
        super(name);
        this.started = false;
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.CompositeAction#start(seventh.ai.basic.Brain)
     */
    @Override
    public void start(Brain brain) {    
        super.start(brain);
        this.started = true;
    }
    
    public SequencedAction addNext(Action action) {
        this.addLastAction(action);
        return this;
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.Goal#isFinished(seventh.ai.basic.Brain)
     */
    @Override
    public boolean isFinished(Brain brain) {
        if(!this.started) {
            return false;
        }
        
        Action action = this.currentAction();
        if(action!=null && action.isFinished(brain)) {
            if(action.getActionResult().isFailure()) {
                getActionResult().setFailure();
                cancel();
                return true;
            }
        }
        return super.isFinished(brain);
    }
}
