/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom.body;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.Brain;
import seventh.ai.basic.PathPlanner;
import seventh.ai.basic.Zone;
import seventh.ai.basic.actions.AdapterAction;
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
	private List<Zone> zonesToAvoid;
	
	/**
	 */
	public MoveAction() {
		this(new Vector2f());
	}
	
	/**
	 * @param destination
	 */
	public MoveAction(Vector2f destination) {
		this.destination = destination;
		this.zonesToAvoid = new ArrayList<Zone>();
	}
	
	/**
	 * @param destination the destination to set
	 */
	public void setDestination(Vector2f destination) {
		this.destination.set(destination);
	}
		
	/**
	 * @param zonesToAvoid the zonesToAvoid to set
	 */
	public void setZonesToAvoid(List<Zone> zonesToAvoid) {
		this.zonesToAvoid.addAll(zonesToAvoid);
	}
	
	/**
	 * Clears out the zones to avoid 
	 */
	public void clearAvoids() {
		this.zonesToAvoid.clear();
	}
	
	/* (non-Javadoc)
	 * @see palisma.ai.Action#start(palisma.ai.Brain)
	 */
	@Override
	public void start(Brain brain) {
		Vector2f position = brain.getEntityOwner().getCenterPos();
		
		PathPlanner<?> feeder = brain.getMotion().getPathPlanner(); 
				
		if(this.zonesToAvoid.isEmpty()) { 
			feeder.findPath(position, this.destination);
		}
		else {
			feeder.findAvoidancePath(position, this.destination, this.zonesToAvoid);
		}					
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
		this.zonesToAvoid.clear();
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {				
		PathPlanner<?> path = brain.getMotion().getPathPlanner();
		return !path.hasPath() || path.atDestination();
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

	@Override
	public DebugInformation getDebugInformation() {	
		return super.getDebugInformation().add("destination", this.destination);
	}
}
