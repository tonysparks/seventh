/*
 * see license.txt
 */
package seventh.ai.basic.squad;

import java.util.List;

import seventh.ai.basic.AttackDirection;
import seventh.ai.basic.World;
import seventh.ai.basic.actions.SequencedAction;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class SquadAttackAction extends SquadAction {

    private Vector2f attackPosition;
    
    /**
     * @param position
     */
    public SquadAttackAction(Vector2f position) {
        this.attackPosition = position;
    }

    @Override
    public void start(Squad squad) {
        World world = squad.getWorld();

        
        List<SquadMember> members = squad.getMembers();
        if(!members.isEmpty()) {
        	List<AttackDirection> attackDirections = world.getAttackDirections(attackPosition, 150f, members.size());
        	if(attackDirections.isEmpty()) {
        		attackDirections.add(new AttackDirection(attackPosition));
        	}
        	int j = 0;
        	for(int i = 0; i < members.size(); i++) {
        		SquadMember member = members.get(i);
        		AttackDirection dir = attackDirections.get( (j+=1) % attackDirections.size());
        		member.getBot().doAction(
        				new SequencedAction("squadAttack")
        					.addNext(world.getGoals().moveToAction(dir.getDirection()))
        					.addNext(world.getGoals().waitAction(10_000))
        				 
        				);
        	}
        }
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
