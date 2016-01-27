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
			
			if( this.currentActiveEvaluator == null ||
				this.isFinished(brain) || 
				newEvaluator.isRepeatable() ||
				((newEvaluator.getKeepBias() > this.currentActiveEvaluator.getKeepBias() ) &&
				    (newEvaluator != this.currentActiveEvaluator)) ) { 
				
				this.currentActiveEvaluator = newEvaluator;
				Action action = this.currentActiveEvaluator.getAction(brain);

				// TODO : Figure out how to overridde the the current evaluator
				
				// TODO: -- BUG with taking command overriding shooting player
				
				
				// if it is a CommandActionEvaluator (override the command)
//					if(!(action instanceof WaitAction))
//						System.out.println(action.getClass().getSimpleName());
				this.replace(action);
				if(newEvaluator.isRepeatable()) {
					action.start(brain);
				}
			}
		}
		
		super.update(brain, timeStep);
	}
}
