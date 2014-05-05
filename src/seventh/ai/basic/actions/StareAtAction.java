/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.game.Entity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class StareAtAction extends AdapterAction {

	private Vector2f position;
	
	public StareAtAction(Vector2f position) {
		this.position = position;
	}
	
	/* (non-Javadoc)
	 * @see palisma.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return false;
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#update(palisma.ai.Brain, leola.live.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		Entity entity = brain.getEntityOwner();
		Vector2f entityPos = entity.getPos();
		entity.setOrientation(Entity.getAngleBetween(entityPos, this.position));
	}

}
