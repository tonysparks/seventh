/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import java.util.List;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.game.entities.Door;
import seventh.game.entities.PlayerEntity;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.DebugDraw;

/**
 * Determines if a {@link Door} needs to be handled
 * 
 * @author Tony
 *
 */
public class HandleDoorActionEvaluator extends ActionEvaluator {

	private Door door;
    
    /**
     * @param characterBias
     */
    public HandleDoorActionEvaluator(Actions goals, double characterBias, double keepBias) {
        super(goals, characterBias, keepBias);
    }

    @Override
    public double calculateDesirability(Brain brain) {
        double desirability = 0;
        PlayerEntity bot = brain.getEntityOwner();
        if(bot.isRunning()||bot.isSprinting()) {
	        Rectangle futureBounds = new Rectangle(64,64);
	        //Vector2f newPos = new Vector2f();
	        //Vector2f.Vector2fMA(bot.getCenterPos(), bot.getMovementDir(), 35, newPos);
	        futureBounds.centerAround(bot.getCenterPos());
	        DebugDraw.fillRectRelative(futureBounds.x, futureBounds.y, futureBounds.width, futureBounds.height, 0xff00ffff);
	        List<Door> doors = brain.getWorld().getDoors();
	        for(int i = 0; i < doors.size(); i++) {
	        	Door door = doors.get(i);
	        	if(door.canBeHandledBy(bot)) {
	        		if(door.isTouching(futureBounds)) {
	        			this.door = door;
	        			DebugDraw.fillRectRelative(futureBounds.x, futureBounds.y, futureBounds.width, futureBounds.height, 0xffff00ff);
	        			desirability = 1.0f;
	        			break;
	        		}
	        	}
	        }
        }
                
        return desirability;
    }

    @Override
    public Action getAction(Brain brain) {
    	if(this.door==null) {
    		return getGoals().waitAction(2_000);
    	}
    	
        return getGoals().handleDoorAction(this.door);     
    }

}
