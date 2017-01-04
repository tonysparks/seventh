/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;

/**
 * @author Tony
 *
 */
public class StayStillEvaluator extends ActionEvaluator {

	
	/**
	 * @param goals
	 * @param characterBias
	 */
	public StayStillEvaluator(Actions goals, double characterBias, double keepBias) {
		super(goals, characterBias, keepBias);		
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double calculateDesirability(Brain brain) {
		double score = brain.getRandomRange(0.3, 0.6);
		return score * getCharacterBias();
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
	 */
	@Override
	public Action getAction(Brain brain) {
		return getGoals().attackEnemy(brain.getTargetingSystem().getCurrentTarget());
	}

}
