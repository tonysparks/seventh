/*
 * see license.txt
 */
package seventh.ai.basic.group;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.AttackDirection;
import seventh.ai.basic.Brain;
import seventh.ai.basic.World;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.SequencedAction;
import seventh.ai.basic.actions.WaitAction;
import seventh.ai.basic.teamstrategy.Roles;
import seventh.ai.basic.teamstrategy.Roles.Role;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class AIGroupAttackAction extends AIGroupAction {

    private Vector2f attackPosition;
    private List<AttackDirection> attackDirections;
    
    /**
     * @param position
     */
    public AIGroupAttackAction(Vector2f position) {
        this.attackPosition = position;
        this.attackDirections = new ArrayList<>();
        action = new AttackGetAction(position);
        start = new AttackStart(position);
    }

    
    /* (non-Javadoc)
     * @see seventh.ai.basic.group.AIGroupAction#end(seventh.ai.basic.group.AIGroup)
     */
    @Override
    public void end(AIGroup aIGroup) {
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.group.AIGroupAction#cancel(seventh.ai.basic.group.AIGroup)
     */
    @Override
    public void cancel(AIGroup aIGroup) {        
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.group.AIGroupAction#isFinished(seventh.ai.basic.group.AIGroup)
     */
    @Override
    public boolean isFinished(AIGroup aIGroup) {
        return false;
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
    }

}
