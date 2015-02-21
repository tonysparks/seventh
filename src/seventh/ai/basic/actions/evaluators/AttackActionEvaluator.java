/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Goals;

/**
 * @author Tony
 *
 */
public class AttackActionEvaluator extends ActionEvaluator {

	
	/**
	 * @param characterBias
	 */
	public AttackActionEvaluator(Goals goals, double characterBias) {
		super(goals, characterBias);
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double calculateDesirability(Brain brain) {
		double desirability = 0;
		
		if(brain.getTargetingSystem().hasTarget()) {
			final double tweaker = 1.0;
			
			desirability = tweaker 
							* Evaluators.healthScore(brain.getEntityOwner()) 
							* Evaluators.currentWeaponScore(brain.getEntityOwner());
			
			desirability *= getCharacterBias();
		}
		
		return desirability;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.ActionEvaluator#getAction()
	 */
	@Override
	public Action getAction(Brain brain) {		
		return getGoals().attack(brain.getTargetingSystem().getCurrentTarget());
	}

}
