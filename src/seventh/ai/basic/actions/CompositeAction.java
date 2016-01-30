/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import java.util.Deque;
import java.util.concurrent.ConcurrentLinkedDeque;

import seventh.ai.basic.Brain;
import seventh.shared.Debugable;
import seventh.shared.TimeStep;

/**
 * Represents a collection of {@link Action}s to be executed.
 * 
 * @author Tony
 *
 */
public class CompositeAction extends AdapterAction implements Debugable {

	private Deque<Action> actions;
	private boolean isFirstAction;
	
	private String name;
	
	/**
	 * 
	 */
	public CompositeAction(String name) {		
		this.actions = new ConcurrentLinkedDeque<>();
		this.isFirstAction = true;
		this.name = name;
	}
	
	/**
	 * Cancels all the actions
	 */
	@Override
	public void cancel() {		
		this.actions.clear();
	}
	
	/**
	 * Replaces the current actions, with the supplied action
	 * @param action
	 */
	public void replace(Action action) {
		cancel();
		addFirstAction(action);
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
	 * Updates the status of the {@link CompositeAction}
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
				
				/* remove this action because the action may have pushed another action on the queue */
				this.actions.remove(action); 
				
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
	 * Adds an {@link Action} to this {@link CompositeAction}
	 * 
	 * @param action
	 * @return this Goal for function chaining
	 */
	public CompositeAction addLastAction(Action action) {		
		if(action != null) {
			this.actions.add(action);
		}
		return this;
	}
	
	/**
	 * Adds an {@link Action} to the front of this {@link CompositeAction}
	 * 
	 * @param action
	 * @return this Goal for function chaining
	 */
	public CompositeAction addFirstAction(Action action) {		
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
		
	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation me = new DebugInformation();
		me.add("name", this.name);
		me.add("actions", this.actions.peek());
		return me;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {	
		return getDebugInformation().toString();
	}
}
