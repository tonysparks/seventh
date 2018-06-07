/*
 * see license.txt
 */
package seventh.ai.basic.group;

import seventh.ai.basic.actions.Action;
import seventh.shared.Updatable;

/**
 * @author Tony
 *
 */
public abstract class AIGroupAction implements Updatable {

	protected Start start;
	protected GetAction action;
	
    
    public abstract void end(AIGroup aIGroup);
    public abstract void cancel(AIGroup aIGroup);
    public abstract boolean isFinished(AIGroup aIGroup);
    
    public void start(AIGroup aIGroup) {
    	start.start(aIGroup);
    }
    public Action getAction(AIGroup aIGroup) {
    	return action.getAction(aIGroup);
    }

}
