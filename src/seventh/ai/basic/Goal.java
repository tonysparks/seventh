/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.AdapterAction;
import seventh.shared.TimeStep;

/**
 * Represents a collection of {@link Action}s to be executed.
 * 
 * @author Tony
 *
 */
public class Goal extends AdapterAction {

	private Deque<Action> actions;
	private boolean isFirstAction;
	
	/**
	 * 
	 */
	public Goal() {		
		this.actions = new ConcurrentLinkedDeque<>();
		this.isFirstAction = true;
	}
	
	/**
	 * Cancels all the actions
	 */
	public void cancel() {		
		this.actions.clear();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.Action#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {				
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
	 */
	@Override
	public void interrupt(Brain brain) {
		Action action = currentAction();
		if(action != null) {
			action.interrupt(brain);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
	 */
	@Override
	public void resume(Brain brain) {
		Action action = currentAction();
		if(action != null) {
			action.resume(brain);
		}
	}
	
	/**
	 * Updates the status of the {@link Goal}
	 * 
	 * @param timeStep
	 */
	public void update(Brain brain, TimeStep timeStep) {
		if(!this.actions.isEmpty()) {
			Action action = this.actions.peek();
			if(isFirstAction) {
				action.start(brain);
				isFirstAction = false;
			}
			
			if(action.isFinished(brain)) {
				action.end(brain);
				this.actions.poll();
				Action nextAction = this.actions.peek();
				if(nextAction != null) {
					nextAction.start(brain);
					getActionResult().set(nextAction.getActionResult());
				}
				else {
					isFirstAction = true;
					getActionResult().set(action.getActionResult());
				}
			}
			else {
				action.update(brain, timeStep);
				getActionResult().set(action.getActionResult());
			}
		}
		else {
			isFirstAction = true;
		}
	}
	
	/**
	 * @return the current Action or null if none
	 */
	public Action currentAction() {
		return this.actions.peek();
	}
	
	/**
	 * Adds an {@link Action} to this {@link Goal}
	 * 
	 * @param action
	 * @return this Goal for function chaining
	 */
	public Goal addLastAction(Action action) {		
		if(action != null) {
			this.actions.add(action);
		}
		return this;
	}
	
	/**
	 * Adds an {@link Action} to the front of this {@link Goal}
	 * 
	 * @param action
	 * @return this Goal for function chaining
	 */
	public Goal addFirstAction(Action action) {		
		if(action != null) {
			this.actions.addFirst(action);
		}
		return this;
	}
	
	/**
	 * @return the number of actions left to be processed.
	 */
	public int numberOfActions() {
		return this.actions.size();
	}
	
	
	/**
	 * Clears any pending and current {@link Action}s
	 */
	public void end(Brain brain) {
		if(!this.actions.isEmpty()) {
			this.actions.peek().end(brain);
		}
		
		this.actions.clear();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return this.actions.isEmpty();
	}
		
}
