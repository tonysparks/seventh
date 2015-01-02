/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.map.PathFeeder;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class MoveToAction extends AdapterAction {

	private Vector2f destination;
	
	/**
	 * 
	 */
	public MoveToAction( Vector2f dest) {		
		this.destination = dest;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {		
		brain.getMotion().moveTo(destination);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
	 */
	@Override
	public void interrupt(Brain brain) {
		brain.getMotion().stopMoving();
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
		Locomotion motion = brain.getMotion();
		if(!motion.isMoving()) {
			resume(brain);
		}
		
		if( isFinished(brain) ) {
			getActionResult().setSuccess();
		}
		else {
			getActionResult().setFailure();
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		PathFeeder<?> path = brain.getMotion().getPathFeeder();
		if(path != null) {
			return path.atDestination();
		}
		return false;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {	
		return super.getDebugInformation().add("destination", this.destination);
	}
}
