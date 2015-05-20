/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import leola.vm.Leola;
import leola.vm.types.LeoObject;
import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class SupressFireUntilAction extends AdapterAction {
	
	private Leola runtime;
	private LeoObject isFinished;
	private LeoObject result;
	
	private Vector2f target;
	
	/**
	 * 
	 */
	public SupressFireUntilAction(Leola runtime, LeoObject isFinished, Vector2f target) {
		this.runtime = runtime;
		this.isFinished = isFinished;
		this.target = target;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		Locomotion motion = brain.getMotion();
		motion.lookAt(target);
		
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
		result = this.runtime.execute(isFinished, Leola.toLeoObject(timeStep));		
		
		Locomotion motion = brain.getMotion();
		if (brain.getEntityOwner().isFacing(target)) {
			motion.shoot();
			//motion.stopShooting();
		}
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
