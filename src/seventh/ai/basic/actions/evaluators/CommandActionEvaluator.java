/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Communicator;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Goals;

/**
 * @author Tony
 *
 */
public class CommandActionEvaluator extends ActionEvaluator {

	
	/**
	 * @param goals
	 * @param characterBias
	 */
	public CommandActionEvaluator(Goals goals, double characterBias, double keepBias) {
		super(goals, characterBias, keepBias);
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#isRepeatable()
	 */
	@Override
	public boolean isRepeatable() {
		return true;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double calculateDesirability(Brain brain) {
		double desirability = 0.0;
		
		Communicator comms = brain.getCommunicator();
		if(comms.hasPendingCommands()) {
			desirability += brain.getRandomRange(0.6, 0.8);
		}
		
		desirability *= getCharacterBias();
				
		return desirability;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
	 */
	@Override
	public Action getAction(Brain brain) {
		return brain.getCommunicator().poll();
	}

}
