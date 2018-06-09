package seventh.ai.basic.actions;

import java.util.List;
import java.util.ArrayList;
import seventh.ai.basic.Brain;
import seventh.shared.TimeStep;

public abstract class ActionSubject {
    private List<Action> actions = new ArrayList<Action>();
    public void attach(Action action) { actions.add(action); }
    public void detach(Action action) { actions.remove(action); }
    public void notifyActions(Brain brain, TimeStep timeStep) {
        for(Action action : actions)
            action.update(brain, timeStep);
    }
}
