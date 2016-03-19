/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.TargetingSystem;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.game.PlayerEntity;

/**
 * @author Tony
 *
 */
public class MoveTowardEnemyEvaluator extends ActionEvaluator {

	/**
	 * @param goals
	 * @param characterBias
	 */
	public MoveTowardEnemyEvaluator(Actions goals, double characterBias, double keepBias) {
		super(goals, characterBias, keepBias);
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double calculateDesirability(Brain brain) {
		double score = 0;
		TargetingSystem system = brain.getTargetingSystem();
		if(system.hasTarget()) {
			PlayerEntity bot = brain.getEntityOwner();
			
			final double tweaker = brain.getPersonality().aggressiveness;
			final PlayerEntity enemy = system.getCurrentTarget();
			
			if(enemy.isOperatingVehicle()) {				
				score = 0;
			}
			else {
				score = tweaker 
						+ Evaluators.healthScore(bot) 
						+ Evaluators.currentWeaponAmmoScore(bot)
						+ Evaluators.weaponDistanceScore(bot, enemy)
						;
				score = score / 4.0;
			}
			
			
			if(!system.currentTargetInLineOfFire()) {
				score *= 0.9;
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
		if(brain.getTargetingSystem().hasTarget()) {
			return getGoals().chargeEnemy(brain.getTargetingSystem().getCurrentTarget());
		}
		return null;
	}

}
