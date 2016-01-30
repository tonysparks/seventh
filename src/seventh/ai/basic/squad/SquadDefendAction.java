/*
 * see license.txt
 */
package seventh.ai.basic.squad;

import java.util.List;

import seventh.ai.basic.AttackDirection;
import seventh.ai.basic.World;
import seventh.ai.basic.actions.atom.GuardAction;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class SquadDefendAction extends SquadAction {

    private Vector2f defendPosition;
    /**
     * 
     */
    public SquadDefendAction(Vector2f position) {
        this.defendPosition = position;
    }

    @Override
    public void start(Squad squad) {
        World world = squad.getWorld();
        List<AttackDirection> directionsToDefend = world.getAttackDirections(this.defendPosition, 50f, 12);

        if(!directionsToDefend.isEmpty()) {
            int i = 0;
            for(SquadMember member : squad.getMembers()) {
                AttackDirection dir = directionsToDefend.get(i++ % directionsToDefend.size());
                Vector2f position = new Vector2f(dir.getDirection());
                //Vector2f.Vector2fMA(defendPosition, dir.getDirection(), 10f + world.getRandom().nextInt(100), position);
                
                member.getBot().getCommunicator().makeTopPriority(world.getGoals().guard(position));
            
            }
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.squad.SquadAction#end(seventh.ai.basic.squad.Squad)
     */
    @Override
    public void end(Squad squad) {
        // TODO Auto-generated method stub
        
    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.squad.SquadAction#cancel(seventh.ai.basic.squad.Squad)
     */
    @Override
    public void cancel(Squad squad) {
        // TODO Auto-generated method stub
        
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
