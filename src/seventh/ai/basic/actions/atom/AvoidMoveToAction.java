/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import java.util.List;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Zone;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class AvoidMoveToAction extends MoveToAction {
	
	private List<Zone> zonesToAvoid;
	/**
	 * 
	 */
	public AvoidMoveToAction(Vector2f dest, List<Zone> zonesToAvoid) {		
		super(dest);
		this.zonesToAvoid = zonesToAvoid;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {		
		brain.getMotion().avoidMoveTo(getDestination(), zonesToAvoid);
	}		
}
