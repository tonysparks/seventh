/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class WaitAction extends AdapterAction {

	private final long timeToWait;
	private long currentWaitTime;
	/**
	 * 
	 */
	public WaitAction(long timeToWait) {
		this.timeToWait = timeToWait;
		this.currentWaitTime = 0;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		this.currentWaitTime += timeStep.getDeltaTime();
		
		if(isFinished(brain)) {
			this.getActionResult().setSuccess();
		}		
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return this.currentWaitTime > this.timeToWait;
	}

	@Override
	public DebugInformation getDebugInformation() {	
		return super.getDebugInformation()
				.add("timeToWait", this.timeToWait)
				.add("currentWaitTime", this.currentWaitTime);
	}
}
