/*
 * see license.txt
 */
package seventh.ai.basic.squad;

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
public class SquadAttackAction extends SquadAction {

    private Vector2f attackPosition;
    private List<AttackDirection> attackDirections;
    
    /**
     * @param position
     */
    public SquadAttackAction(Vector2f position) {
        this.attackPosition = position;
        this.attackDirections = new ArrayList<>();
    }

    @Override
    public void start(Squad squad) {
        World world = squad.getWorld();

        this.attackDirections.addAll(world.getAttackDirections(attackPosition, 150f, squad.squadSize()));        
        if(attackDirections.isEmpty()) {
            attackDirections.add(new AttackDirection(attackPosition));
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.squad.SquadAction#getAction(seventh.ai.basic.squad.Squad)
     */
    @Override
    public Action getAction(Squad squad) {
        if(squad.squadSize()>0) {
            World world = squad.getWorld();
            Brain[] members = squad.getMembers();
            Roles roles = squad.getRoles();
            
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
     * @see seventh.ai.basic.squad.SquadAction#end(seventh.ai.basic.squad.Squad)
     */
    @Override
    public void end(Squad squad) {
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.squad.SquadAction#cancel(seventh.ai.basic.squad.Squad)
     */
    @Override
    public void cancel(Squad squad) {        
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.squad.SquadAction#isFinished(seventh.ai.basic.squad.Squad)
     */
    @Override
    public boolean isFinished(Squad squad) {
        return false;
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
    }

}
