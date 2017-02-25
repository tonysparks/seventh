/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.TargetingSystem;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.atom.body.MeleeAction;
import seventh.game.entities.PlayerEntity;
import seventh.game.weapons.Weapon;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class MeleeEvaluator extends ActionEvaluator {

    private MeleeAction meleeAction;
    
    /**
     * @param goals
     * @param characterBias
     */
    public MeleeEvaluator(Actions goals, double characterBias, double keepBias) {
        super(goals, characterBias, keepBias);
        this.meleeAction = new MeleeAction();
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
            
            Weapon weapon = bot.getInventory().currentItem();
            if (weapon != null && weapon.canMelee()) {
                float distanceAwaySq = Vector2f.Vector2fDistanceSq(bot.getCenterPos(), system.getCurrentTarget().getCenterPos());
                final double MaxMeleeDistance = 1500.0;
                if(distanceAwaySq < MaxMeleeDistance) {
                    desire = Math.max(0.9 - (distanceAwaySq/MaxMeleeDistance), 0);
                    desire *= getCharacterBias();
                }
            }
        }
        
        return desire;
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
     */
    @Override
    public Action getAction(Brain brain) {
        return this.meleeAction;
    }

}
