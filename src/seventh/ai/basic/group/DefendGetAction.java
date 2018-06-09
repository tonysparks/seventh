package seventh.ai.basic.group;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.AttackDirection;
import seventh.ai.basic.Brain;
import seventh.ai.basic.World;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.WaitAction;
import seventh.math.Vector2f;

public class DefendGetAction implements GetAction {

    private Vector2f defendPosition;
    private List<AttackDirection> directionsToDefend;
    
    public DefendGetAction(Vector2f Position) {
        this.defendPosition = Position;
        this.directionsToDefend = new ArrayList<>();
    }

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
    
}
