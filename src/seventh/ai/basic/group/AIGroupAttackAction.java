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
    }

    @Override
    public void start(AIGroup aIGroup) {
        World world = aIGroup.getWorld();

        this.attackDirections.addAll(world.getAttackDirections(attackPosition, 150f, aIGroup.groupSize()));        
        if(attackDirections.isEmpty()) {
            attackDirections.add(new AttackDirection(attackPosition));
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.group.AIGroupAction#getAction(seventh.ai.basic.group.AIGroup)
     */
    @Override
    public Action getAction(AIGroup aIGroup) {
        if(aIGroup.groupSize()>0) {
            World world = aIGroup.getWorld();
            Brain[] members = aIGroup.getMembers();
            Roles roles = aIGroup.getRoles();
            
            int j = 0;
            for(int i = 0; i < members.length; i++) {
                Brain member = members[i];
                if(member!=null) {
                    if(roles.getAssignedRole(member.getPlayer()) != Role.None) {                    
                        AttackDirection dir = attackDirections.get( (j+=1) % attackDirections.size());
                        return new SequencedAction("squadAttack")
                                    .addNext(world.getGoals().moveToAction(dir.getDirection()));
                    }
                }
            }
        }
        
        return new WaitAction(500);
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
