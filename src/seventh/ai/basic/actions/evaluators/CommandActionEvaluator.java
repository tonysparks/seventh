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
	public CommandActionEvaluator(Goals goals, double characterBias) {
		super(goals, characterBias);
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double calculateDesirability(Brain brain) {
		double desirability = 0.0;
		
		Communicator comms = brain.getCommunicator();
		if(comms.hasPendingCommands()) {
			desirability += brain.getRandomRange(0.8, 0.9);
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
