/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.AIConfig;
import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.evaluators.ActionEvaluator;
import seventh.ai.basic.actions.evaluators.Evaluators;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * Determines the best course of action to take based on {@link ActionEvaluator}s
 * 
 * 
 * @author Tony
 *
 */
public class WeightedAction extends CompositeAction {

	private List<ActionEvaluator> evaluators;
	private Timer updateEval;
	
	
	private ActionEvaluator currentActiveEvaluator;
		
	private Action currentAction;
	
	/**
	 * 
	 */
	public WeightedAction(AIConfig config, String name, ActionEvaluator ... evaluators) {
		super(name);
		
		this.evaluators = new ArrayList<ActionEvaluator>();
		for(ActionEvaluator e : evaluators) {
			this.evaluators.add(e);
		}
		
		this.updateEval = new Timer(true, config.getEvaluationPollTime());	
		this.updateEval.start();
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.Goal#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		this.currentActiveEvaluator = Evaluators.evaluate(brain, evaluators);
		replace(this.currentActiveEvaluator.getAction(brain));
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.Goal#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		this.updateEval.update(timeStep);
		if(this.updateEval.isTime()) {
			ActionEvaluator newEvaluator = Evaluators.evaluate(brain, evaluators);
			
			boolean isFinished = isFinished(brain);
			if( this.currentActiveEvaluator == null ||
				isFinished || 
				newEvaluator.isRepeatable() ||
				((newEvaluator.getKeepBias() > this.currentActiveEvaluator.getKeepBias() ) &&
				    (newEvaluator != this.currentActiveEvaluator)) ) { 
				
				Action action = newEvaluator.getAction(brain);
				if(action!=null && (isFinished||this.currentAction!=action) ) {
					this.currentActiveEvaluator = newEvaluator;
					
					if(this.currentActiveEvaluator.isContinuable()) {
						interrupt(brain);										
						addFirstAction(action);					
					}
					else {			
						this.replace(action);
					}
					
					if(newEvaluator.isRepeatable()) {
						action.start(brain);
					}
					
					this.currentAction = action;
				}
			}
		}
		
		super.update(brain, timeStep);
	}
}
