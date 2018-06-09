package seventh.ai.basic.group;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.AttackDirection;
import seventh.ai.basic.World;
import seventh.math.Vector2f;

public class DefendStart implements Start {
    
    
    private Vector2f defendPosition;
    private List<AttackDirection> directionsToDefend;
    
    public DefendStart(Vector2f Position) {
        this.defendPosition = Position;
        this.directionsToDefend = new ArrayList<>();
    }
    
    public void start(AIGroup aIGroup) {
        World world = aIGroup.getWorld();
        float radius = (float)world.getRandom().getRandomRange(100f, 150f);
        directionsToDefend.addAll(world.getAttackDirections(this.defendPosition, radius, 12));

    }
}
