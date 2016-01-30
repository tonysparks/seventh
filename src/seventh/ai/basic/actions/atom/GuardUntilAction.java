/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import leola.vm.Leola;
import leola.vm.types.LeoObject;
import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class GuardUntilAction extends AdapterAction {
	
	private LeoObject isFinished;
	private LeoObject result;
	
	/**
	 * @param isFinished 
	 */
	public GuardUntilAction(LeoObject isFinished) {
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
		result = isFinished.xcall(Leola.toLeoObject(timeStep));		
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		if(brain.getTargetingSystem().hasTarget()) {
			return true;
		}
		
		return LeoObject.isTrue(result);
	}
}
