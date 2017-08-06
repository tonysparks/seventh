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

    public abstract void start(AIGroup aIGroup);
    public abstract void end(AIGroup aIGroup);
    public abstract void cancel(AIGroup aIGroup);
    public abstract boolean isFinished(AIGroup aIGroup);
    public abstract Action getAction(AIGroup aIGroup);

}
