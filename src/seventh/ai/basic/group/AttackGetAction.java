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

public class AttackGetAction implements GetAction {
	private Vector2f attackPosition;
    private List<AttackDirection> attackDirections;
    
	
	public AttackGetAction(Vector2f Position) {
        this.attackPosition = Position;
        this.attackDirections = new ArrayList<>();
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

}
