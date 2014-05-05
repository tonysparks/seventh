/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.game.Entity;
import seventh.math.Vector2f;


/**
 * @author Tony
 *
 */
public class LookAtAction extends AdapterAction {

	private float orientation;
	
	public LookAtAction(float orientation) {
		this.orientation = orientation;		
	}
	public LookAtAction(Entity entity, Vector2f position) {		
		Vector2f entityPos = entity.getPos();
		this.orientation = Entity.getAngleBetween(position, entityPos);
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#start(palisma.ai.Brain)
	 */
	@Override
	public void start(Brain brain) {				
		brain.getEntityOwner().setOrientation(this.orientation);
		this.getActionResult().setSuccess();
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return true;
	}

}
