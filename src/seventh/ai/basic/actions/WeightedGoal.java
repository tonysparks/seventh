/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.evaluators.ActionEvaluator;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * Determines the best course of action to take based on {@link ActionEvaluator}s
 * 
 * 
 * @author Tony
 *
 */
public class WeightedGoal extends Goal {

	private List<ActionEvaluator> evaluators;
	private Timer updateEval;
	
	
	private ActionEvaluator currentActiveEvaluator;
	private double currentScore;
	private double bestEvalScore;
	
	/**
	 * 
	 */
	public WeightedGoal(Brain brain, ActionEvaluator ... evaluators) {
		this.evaluators = new ArrayList<ActionEvaluator>();
		for(ActionEvaluator e : evaluators) {
			this.evaluators.add(e);
		}
		
		this.updateEval = new Timer(true, brain.getConfig().getEvaluationPollTime());	
		this.updateEval.start();
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.Goal#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		this.currentActiveEvaluator = evaluate(brain);
		replace(this.currentActiveEvaluator.getAction(brain));
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.Goal#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		this.updateEval.update(timeStep);
		if(this.updateEval.isTime()) {
			ActionEvaluator currentEvaluator = evaluate(brain);
			
			if( this.currentActiveEvaluator == null ||
				this.isFinished(brain) ) {								
				
				boolean shouldChangeAction = false;
				if(this.bestEvalScore > this.currentScore) {
					double delta = this.bestEvalScore - this.currentScore;
					if (delta > 0.05) {
						shouldChangeAction = true;
					}
				}
				
				if( ((currentEvaluator.getKeepBias() > this.currentActiveEvaluator.getKeepBias() || shouldChangeAction) &&
				    (currentEvaluator != this.currentActiveEvaluator)) ) { 
				
					this.currentActiveEvaluator = currentEvaluator;
					this.currentScore = this.bestEvalScore;
					Action action = this.currentActiveEvaluator.getAction(brain);
	//					
					if(!(action instanceof WaitAction))
						System.out.println(action.getClass().getSimpleName());
					this.replace(action);
				}
			}
			
			//System.out.println("Best: " + this.bestEvalScore + " vs. " + "Current: " + this.currentScore);
		}
		
		super.update(brain, timeStep);
	}
	
	private ActionEvaluator evaluate(Brain brain) {
		double highestDesire = 0;
		ActionEvaluator bestEval = null;
		
		int size = this.evaluators.size();
		for(int i = 0; i < size; i++) {
			ActionEvaluator eval = this.evaluators.get(i); 
			double desire = eval.calculateDesirability(brain);
			if(bestEval == null || desire > highestDesire) {
				bestEval = eval;
				highestDesire = desire;
			}
		}
		
		this.bestEvalScore = highestDesire;
						
		return bestEval;
	}
}
