/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.teamstrategy.TeamStrategy;

/**
 * @author Tony
 *
 */
public class StrategyEvaluator extends ActionEvaluator {

	private TeamStrategy teamStrategy;
	
	/**
	 * @param goals
	 * @param characterBias
	 */
	public StrategyEvaluator(TeamStrategy teamStrategy, Actions goals, double characterBias, double keepBias) {
		super(goals, characterBias, keepBias);
		this.teamStrategy = teamStrategy;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double calculateDesirability(Brain brain) {
		double score = 0.35;
		score *= getCharacterBias();
		return score;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
	 */
	@Override
	public Action getAction(Brain brain) {
		return teamStrategy.getGoal(brain);
	}

}
