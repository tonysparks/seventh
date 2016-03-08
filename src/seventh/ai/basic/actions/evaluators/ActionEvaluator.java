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
public abstract class ActionEvaluator {

	private double characterBias, keepBias;
	private Actions goals;
	/**
	 * @param characterBias 
	 * @param keepBias
	 */
	public ActionEvaluator(Actions goals, double characterBias, double keepBias) {
		this.goals = goals;
		this.characterBias = characterBias;
		this.keepBias = keepBias;
	}
	
	/**
	 * @return the goals
	 */
	public Actions getGoals() {
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
	 * If this Evaluator repeats different actions that should be considered 'new' after
	 * each calculation
	 * @return true if to be considered 'new' after an evaluation
	 */
	public boolean isRepeatable() {
		return false;
	}
	
	public boolean isContinuable() {
		return false;
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
