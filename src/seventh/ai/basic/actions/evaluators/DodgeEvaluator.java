/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.TargetingSystem;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Goals;
import seventh.ai.basic.actions.StrafeAction;
import seventh.game.PlayerEntity;

/**
 * @author Tony
 *
 */
public class DodgeEvaluator extends ActionEvaluator {

	
	private StrafeAction strafeAction;
	
	/**
	 * @param goals
	 * @param characterBias
	 */
	public DodgeEvaluator(Goals goals, double characterBias, double keepBias) {
		super(goals, characterBias, keepBias);
		this.strafeAction = new StrafeAction();
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double calculateDesirability(Brain brain) {
		double score = 0;
		TargetingSystem system = brain.getTargetingSystem();
		if(system.hasTarget()) {
			
			PlayerEntity enemy = system.getCurrentTarget();
			if(enemy.isFiring()) {
				score += brain.getRandomRange(0.4, 0.6);
			}
			
			
			score *= getCharacterBias();
		}
		
		
		return score;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
	 */
	@Override
	public Action getAction(Brain brain) {
//		return getGoals().chargeEnemy(getGoals(), brain, brain.getTargetingSystem().getCurrentTarget());
		this.strafeAction.reset(brain);
		return this.strafeAction; 
	}

}
