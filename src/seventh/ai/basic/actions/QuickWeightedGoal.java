/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.evaluators.ActionEvaluator;
import seventh.ai.basic.actions.evaluators.Evaluators;

/**
 * Determines the best course of action to take based on {@link ActionEvaluator}s.  It will
 * make a decision once and that is it.
 * 
 * 
 * @author Tony
 *
 */
public class QuickWeightedGoal extends Goal {

	private ActionEvaluator[] evaluators;
	private ActionEvaluator currentActiveEvaluator;
		
	/**
	 * @param brain
	 * @param name
	 * @param evaluators
	 */
	public QuickWeightedGoal(Brain brain, String name, ActionEvaluator ... evaluators) {
		super(name);
		this.evaluators = evaluators;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.Goal#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		this.currentActiveEvaluator = Evaluators.evaluate(brain, evaluators);
		replace(this.currentActiveEvaluator.getAction(brain));
	}			
}
