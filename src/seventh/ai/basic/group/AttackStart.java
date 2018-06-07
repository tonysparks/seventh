package seventh.ai.basic.group;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.AttackDirection;
import seventh.ai.basic.World;
import seventh.math.Vector2f;

public class AttackStart implements Start {

    private Vector2f attackPosition;
    private List<AttackDirection> attackDirections;
    
    public AttackStart(Vector2f Position) {
        this.attackPosition = Position;
        this.attackDirections = new ArrayList<>();
    }
    
    public void start(AIGroup aIGroup) {
        World world = aIGroup.getWorld();

        this.attackDirections.addAll(world.getAttackDirections(attackPosition, 150f, aIGroup.groupSize()));        
        if(attackDirections.isEmpty()) {
            attackDirections.add(new AttackDirection(attackPosition));
        }
    }
}
