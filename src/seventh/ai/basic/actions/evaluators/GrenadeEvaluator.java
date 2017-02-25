/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.TargetingSystem;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.atom.body.ThrowGrenadeAction;
import seventh.game.entities.PlayerEntity;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class GrenadeEvaluator extends ActionEvaluator {

    /**
     * @param goals
     * @param characterBias
     */
    public GrenadeEvaluator(Actions goals, double characterBias, double keepBias) {
        super(goals, characterBias, keepBias);
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
     */
    @Override
    public double calculateDesirability(Brain brain) {
        double desire = 0.0;
        
        TargetingSystem system = brain.getTargetingSystem();
        if(system.hasTarget()) {
            PlayerEntity bot = brain.getEntityOwner();
            
            if (bot.getInventory().hasGrenades() && !bot.isThrowingGrenade()) {

                float distanceAwaySq = Vector2f.Vector2fDistanceSq(bot.getCenterPos(), system.getCurrentTarget().getCenterPos());
                final double MaxGrenadeDistance = (64*6) * (64*6);
                desire = 0.1;
                if(distanceAwaySq < MaxGrenadeDistance) {
                    desire *= distanceAwaySq / MaxGrenadeDistance;
                }
                else {
                    desire *= brain.getRandomRangeMax(0.2);
                }
                desire *= getCharacterBias();
            }
        }
        
        return desire;
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
     */
    @Override
    public Action getAction(Brain brain) {
        return new ThrowGrenadeAction(brain.getEntityOwner(), brain.getTargetingSystem().getLastRemeberedPosition());
    }

}
