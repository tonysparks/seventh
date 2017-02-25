/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import java.util.Collections;
import java.util.List;

import seventh.ai.basic.Zone;
import seventh.game.entities.Flag;

/**
 * @author Tony
 *
 */
public class MoveToFlagAction extends AvoidMoveToAction {
    
    private static final List<Zone> emptyZonesToAvoid = Collections.emptyList();
    
    /**
     * @param target 
     */
    public MoveToFlagAction(Flag target, List<Zone> zonesToAvoid) {
        super(target.getCenterPos(), zonesToAvoid);        
    }    
    
    public MoveToFlagAction(Flag target) {
        super(target.getCenterPos(), emptyZonesToAvoid);        
    }
}
