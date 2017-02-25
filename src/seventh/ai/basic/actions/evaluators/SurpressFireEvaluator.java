/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.TimedAction;
import seventh.game.weapons.Weapon;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class SurpressFireEvaluator extends ActionEvaluator {

    
    private TimedAction surpressAction;
    private Vector2f target;
    /**
     * @param goals
     * @param characterBias
     */
    public SurpressFireEvaluator(Actions goals, double characterBias, double keepBias, final Vector2f target) {
        super(goals, characterBias, keepBias);
        
        this.target = target;
        
        // TODO: Make time variable/fuzzy
        this.surpressAction = new TimedAction(3500) {
            
            /* (non-Javadoc)
             * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
             */
            @Override
            public void start(Brain brain) {
                super.start(brain);
                Locomotion motion = brain.getMotion();
                
                
                motion.lookAt(target);
                motion.shoot();
            }
            
            protected void doAction(Brain brain, seventh.shared.TimeStep timeStep) {
                
            }
            
            /* (non-Javadoc)
             * @see seventh.ai.basic.actions.AdapterAction#end(seventh.ai.basic.Brain)
             */
            @Override
            public void end(Brain brain) {            
                super.end(brain);
                brain.getMotion().stopShooting();
            }
        };
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
     */
    @Override
    public double calculateDesirability(Brain brain) {
        double desire = 0.0;
        
        Weapon weapon = brain.getEntityOwner().getInventory().currentItem();
        if (weapon != null) {
            double weaponScore = Evaluators.currentWeaponAmmoScore(brain.getEntityOwner());
            double distanceScore = Evaluators.weaponDistanceScore(brain.getEntityOwner(), this.target);
            
            desire  = (weaponScore + distanceScore) / 2.0;
            desire *= getCharacterBias();
        }
        
        
        return desire;
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
     */
    @Override
    public Action getAction(Brain brain) {
        return this.surpressAction;
    }

}
