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
import seventh.ai.basic.actions.WaitAction;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class AIGroupDefendAction extends AIGroupAction {

    private Vector2f defendPosition;
    private List<AttackDirection> directionsToDefend;
    
    /**
     * @param position
     */
    public AIGroupDefendAction(Vector2f position) {
        this.defendPosition = position;
        this.directionsToDefend = new ArrayList<>();
    }

    @Override
    public void start(AIGroup aIGroup) {
        World world = aIGroup.getWorld();
        float radius = (float)world.getRandom().getRandomRange(100f, 150f);
        directionsToDefend.addAll(world.getAttackDirections(this.defendPosition, radius, 12));

    }
    
    /* (non-Javadoc)
     * @see seventh.ai.basic.group.AIGroupAction#getAction(seventh.ai.basic.group.AIGroup)
     */
    @Override
    public Action getAction(AIGroup aIGroup) {
        if(aIGroup.groupSize() > 0 ) {
            World world = aIGroup.getWorld();
            Brain[] members = aIGroup.getMembers();
//            Roles roles = aIGroup.getRoles();
            
            int squadSize = aIGroup.groupSize();
            
            if(!directionsToDefend.isEmpty() && squadSize>0) {
                int increment = 1;
                if(directionsToDefend.size()> squadSize) {
                    increment = directionsToDefend.size() / squadSize;
                }
                
                int i = 0;
                for(int j = 0; j < members.length; j++) {
                    Brain member = members[j];
                    if(member!=null) {
                        //if(roles.getAssignedRole(member.getPlayer()) != Role.None) 
                        {
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
