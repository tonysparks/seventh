/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.WaitAction;

/**
 * @author Tony
 *
 */
public class DoNothingEvaluator extends ActionEvaluator {

    private WaitAction noAction;
    
    /**
     * @param goals
     * @param characterBias
     */
    public DoNothingEvaluator(Actions goals, double characterBias, double keepBias) {
        super(goals, characterBias, keepBias);
        noAction = new WaitAction(100);
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
     */
    @Override
    public double calculateDesirability(Brain brain) {
        double score = brain.getRandomRange(0.4, 0.8);
        return score * getCharacterBias();
    }

    /* (non-Javadoc)
     * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
     */
    @Override
    public Action getAction(Brain brain) {
        noAction.reset();
        return noAction;
    }

}
