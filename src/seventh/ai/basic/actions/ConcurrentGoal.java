/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.Brain;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class ConcurrentGoal implements Action {

	private List<Action> concurrentActions;
	private ActionResult result;
	
	/**
	 * 
	 */
	public ConcurrentGoal(Action ... concurrentActions) {
		this.concurrentActions = new ArrayList<Action>();
		for(Action a : concurrentActions) {
			this.concurrentActions.add(a);
		}
		
		this.result = new ActionResult();
	}
	
	/**
	 * @param index
	 * @return the Action
	 */
	public Action getAction(int index) {
		return this.concurrentActions.get(index);
	}
	
	/**
	 * Adds a concurrent action
	 * @param action
	 */
	public void addConcurrentAction(Action action) {
		this.concurrentActions.add(action);
	}
	
	/**
	 * Removes all concurrent actions
	 */
	public void cancel() {
		int size = this.concurrentActions.size();
		for(int i = 0; i < size; i++) {
			this.concurrentActions.get(i).cancel();
		}
	}
	
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.Goal#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		int size = this.concurrentActions.size();
		for(int i = 0; i < size; i++) {
			this.concurrentActions.get(i).start(brain);
		}
	}


	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.Action#end(seventh.ai.basic.Brain)
	 */
	@Override
	public void end(Brain brain) {
		int size = this.concurrentActions.size();
		for(int i = 0; i < size; i++) {
			this.concurrentActions.get(i).end(brain);
		}
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.Action#isFinished(seventh.ai.basic.Brain)
	 */
	@Override
	public boolean isFinished(Brain brain) {
		int size = this.concurrentActions.size();
		boolean isDone = true;
		for(int i = 0; i < size; i++) {
			isDone = this.concurrentActions.get(i).isFinished(brain) && isDone;
		}
		return isDone;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.Action#interrupt(seventh.ai.basic.Brain)
	 */
	@Override
	public void interrupt(Brain brain) {
		int size = this.concurrentActions.size();
		for(int i = 0; i < size; i++) {
			this.concurrentActions.get(i).interrupt(brain);
		}
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.Action#resume(seventh.ai.basic.Brain)
	 */
	@Override
	public void resume(Brain brain) {
		int size = this.concurrentActions.size();
		for(int i = 0; i < size; i++) {
			this.concurrentActions.get(i).resume(brain);
		}
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.Action#getActionResult()
	 */
	@Override
	public ActionResult getActionResult() {
		return this.result;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.Action#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		int size = this.concurrentActions.size();
		for(int i = 0; i < size; i++) {
			this.concurrentActions.get(i).update(brain, timeStep);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation info = new DebugInformation();
		info.add("goals", this.concurrentActions);
		return info;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDebugInformation().toString();
	}

}
