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
public abstract class ActionEvaluator {

	private double characterBias, keepBias;
	private Goals goals;
	/**
	 * @param characterBias 
	 * @param keepBias
	 */
	public ActionEvaluator(Goals goals, double characterBias, double keepBias) {
		this.goals = goals;
		this.characterBias = characterBias;
		this.keepBias = keepBias;
	}
	
	/**
	 * @return the goals
	 */
	public Goals getGoals() {
		return goals;
	}
	
	/**
	 * @return the characterBias
	 */
	public double getCharacterBias() {
		return characterBias;
	}

	/**
	 * @return the bias to keep doing this action
	 */
	public double getKeepBias() {
		return keepBias;
	}
	
	/**
	 * Calculates the desirability
	 * 
	 * @param brain
	 * @return the desirability score between 0 and 1
	 */
	public abstract double calculateDesirability(Brain brain);
	
	
	/**
	 * 
	 * @return
	 */
	public abstract Action getAction(Brain brain);
	
	
}
