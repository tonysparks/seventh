/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.shared.TimeStep;

/**
 * Allows for wrapping an action 
 * 
 * @author Tony
 *
 */
public class DecoratorAction extends AdapterAction {

	private Action action;	
	private Brain brain;
	
	public DecoratorAction(Brain brain) {
		this(brain, null);
	}
	
	/**
	 * @param the decorated action
	 */
	public DecoratorAction(Brain brain, Action action) {
		super();
		
		this.brain = brain;
		setAction(action);
	}
	
	/**
	 * @param action the action to set
	 */
	public void setAction(Action action) {		
//		if(hasAction()) {
//			this.action.end(brain);
//		}
		
		this.action = action;
		
		if(this.action != null) {
			this.action.start(brain);
		}
	}
	
	/**
	 * @return the action
	 */
	public Action getAction() {
		return action;
	}
	
	/**
	 * Determines if this is of another type of Action
	 * @param action
	 * @return true if the current action is of the supplied type
	 */
	public boolean is(Class<? extends Action> action) {
		if(hasAction()) {
			return this.action.getClass().equals(action);
		}
		
		return false;
	}
	
	/**
	 * @return true if there is an action bound
	 */
	public boolean hasAction() {
		return this.action != null && !this.action.isFinished(brain);
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.Action#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		if(this.action != null) {
			this.action.start(brain);
		}
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.Action#end(seventh.ai.basic.Brain)
	 */
	@Override
	public void end(Brain brain) {
		if(this.action != null) {
			this.action.end(brain);		
		}
		this.action = null;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
	 */
	@Override
	public void interrupt(Brain brain) {
		if(this.action != null) {
			this.action.interrupt(brain);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
	 */
	@Override
	public void resume(Brain brain) {
		if(this.action != null) {
			this.action.resume(brain);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#getActionResult()
	 */
	@Override
	public ActionResult getActionResult() {
		if(this.action != null) {
			return this.action.getActionResult();
		}
		
		return super.getActionResult();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return this.action != null ? this.action.isFinished(brain) : true;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.Action#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		if(this.action != null) {
			this.action.update(brain, timeStep);
		}
	}

}
