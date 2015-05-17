/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import leola.vm.Leola;
import leola.vm.types.LeoObject;
import seventh.ai.basic.Brain;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class GuardUntilAction extends AdapterAction {
	
	private Leola runtime;
	private LeoObject isFinished;
	/**
	 * 
	 */
	public GuardUntilAction(Leola runtime, LeoObject isFinished) {
		this.runtime = runtime;
		this.isFinished = isFinished;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		brain.getMotion().scanArea();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
	 */
	@Override
	public void resume(Brain brain) {
		start(brain);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {		
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		if(brain.getTargetingSystem().hasTarget()) {
			return true;
		}
		
		LeoObject result = this.runtime.execute(isFinished);
		return LeoObject.isTrue(result);
	}
}
