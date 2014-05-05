/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.map.PathFeeder;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Gives the entity a path to move about
 * 
 * @author Tony
 *
 */
public class MoveAction extends AdapterAction {

	private Vector2f destination;
	
	/**
	 * @param destination
	 */
	public MoveAction(Vector2f destination) {	
		this.destination = destination;
	}
	
	/* (non-Javadoc)
	 * @see palisma.ai.Action#start(palisma.ai.Brain)
	 */
	@Override
	public void start(Brain brain) {
		Vector2f position = brain.getEntityOwner().getPos();
		
		PathFeeder<?> feeder = brain.getWorld().getGraph().findPath(position, destination);
		brain.getMotion().setPathFeeder(feeder);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
	 */
	@Override
	public void resume(Brain brain) {
		start(brain);
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#end(palisma.ai.Brain)
	 */
	@Override
	public void end(Brain brain) {
		brain.getMotion().emptyPath();
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {				
		PathFeeder<?> path = brain.getMotion().getPathFeeder();
		return path!=null ? path.atDestination() : true;
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#update(palisma.ai.Brain, leola.live.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		if(isFinished(brain)) {
			this.getActionResult().setSuccess();
		}
		else {
			this.getActionResult().setFailure();
		}
	}

}
