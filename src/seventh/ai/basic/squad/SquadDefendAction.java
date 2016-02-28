/*
 * see license.txt
 */
package seventh.ai.basic.squad;

import java.util.List;

import seventh.ai.basic.AttackDirection;
import seventh.ai.basic.World;
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
        float radius = (float)world.getRandom().getRandomRange(100f, 150f);
        List<AttackDirection> directionsToDefend = world.getAttackDirections(this.defendPosition, radius, 12);

        List<SquadMember> members = squad.getMembers();
        if(!directionsToDefend.isEmpty() && !members.isEmpty()) {
        	int increment = 1;
        	if(directionsToDefend.size()> members.size()) {
        		increment = directionsToDefend.size() / members.size();
        	}
        	
            int i = 0;
            for(SquadMember member : members) {
                AttackDirection dir = directionsToDefend.get( (i += increment) % directionsToDefend.size());
                Vector2f position = new Vector2f(dir.getDirection());
                //Vector2f.Vector2fMA(defendPosition, dir.getDirection(), 10f + world.getRandom().nextInt(100), position);
                
                member.getBot().doAction(world.getGoals().guard(position));
            
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
