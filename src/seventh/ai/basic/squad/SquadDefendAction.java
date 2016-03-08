/*
 * see license.txt
 */
package seventh.ai.basic.squad;

import java.util.List;

import seventh.ai.basic.AttackDirection;
import seventh.ai.basic.Brain;
import seventh.ai.basic.World;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.WaitAction;
import seventh.ai.basic.teamstrategy.Roles;
import seventh.ai.basic.teamstrategy.Roles.Role;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class SquadDefendAction extends SquadAction {

    private Vector2f defendPosition;
    private List<AttackDirection> directionsToDefend;
    
    /**
     * @param position
     */
    public SquadDefendAction(Vector2f position) {
        this.defendPosition = position;
    }

    @Override
    public void start(Squad squad) {
        World world = squad.getWorld();
        float radius = (float)world.getRandom().getRandomRange(100f, 150f);
        directionsToDefend = world.getAttackDirections(this.defendPosition, radius, 12);

    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.squad.SquadAction#getAction(seventh.ai.basic.squad.Squad)
     */
    @Override
    public Action getAction(Squad squad) {
    	if(squad.squadSize() > 0 ) {
    		World world = squad.getWorld();
	        Brain[] members = squad.getMembers();
	        Roles roles = squad.getRoles();
	        
	        int squadSize = squad.squadSize();
	        
	        if(!directionsToDefend.isEmpty() && squadSize>0) {
	        	int increment = 1;
	        	if(directionsToDefend.size()> squadSize) {
	        		increment = directionsToDefend.size() / squadSize;
	        	}
	        	
	            int i = 0;
	        	for(int j = 0; j < members.length; j++) {
	        		Brain member = members[j];
	        		if(member!=null) {
	        			if(roles.getAssignedRole(member.getPlayer()) != Role.None) {
			                AttackDirection dir = directionsToDefend.get( (i += increment) % directionsToDefend.size());
			                Vector2f position = new Vector2f(dir.getDirection());
			                //Vector2f.Vector2fMA(defendPosition, dir.getDirection(), 10f + world.getRandom().nextInt(100), position);
			                
			                return (world.getGoals().guard(position));
	        			}
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
