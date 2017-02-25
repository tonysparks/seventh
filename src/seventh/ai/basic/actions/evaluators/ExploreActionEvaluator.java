/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.atom.body.MoveAction;

/**
 * @author Tony
 *
 */
public class ExploreActionEvaluator extends ActionEvaluator {

    private MoveAction moveAction;
    
    /**
     * @param goals
     * @param characterBias
     */
    public ExploreActionEvaluator(Actions goals, double characterBias, double keepBias) {
        super(goals, characterBias, keepBias);
        this.moveAction = new MoveAction();
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
     */
    @Override
    public double calculateDesirability(Brain brain) {
        double desirability = 0.35;
        
        desirability *= getCharacterBias();
        
        return desirability;
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
     */
    @Override
    public Action getAction(Brain brain) {
        brain.getMotion().scanArea();
        this.moveAction.setDestination(brain.getWorld().getRandomSpot(brain.getEntityOwner()));
        return this.moveAction;
//        return getGoals().goToRandomSpot(brain);
    }

}
