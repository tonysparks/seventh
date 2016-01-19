/*
 * see license.txt
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;

/**
 * @author Tony
 *
 */
public class SequencedGoal extends Goal {

    /**
     * @param name
     */
    public SequencedGoal(String name) {
        super(name);
    }
    
    public SequencedGoal addNext(Action action) {
        this.addLastAction(action);
        return this;
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.Goal#isFinished(seventh.ai.basic.Brain)
     */
    @Override
    public boolean isFinished(Brain brain) {
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
