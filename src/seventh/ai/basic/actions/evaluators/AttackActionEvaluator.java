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
	public AttackActionEvaluator(Goals goals, double characterBias, double keepBias) {
		super(goals, characterBias, keepBias);
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
							* Math.max(Evaluators.healthScore(brain.getEntityOwner()), 0.7) 
							* Math.max(Evaluators.currentWeaponAmmoScore(brain.getEntityOwner()), 0.8);
			
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
