/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.game.Entity;
import seventh.game.PlayerEntity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;


/**
 * @author Tony
 *
 */
public class LookAtAction extends AdapterAction {


	/**
	 * Full circle
	 */
	private static final float fullCircle = (float)Math.PI * 2f;
	
	private float destinationOrientation;
	
	public LookAtAction(float orientation) {
		this.destinationOrientation = orientation;		
	}
	
	public LookAtAction(Entity entity, Vector2f position) {		
		reset(entity, position);
	}

	/**
	 * Sets the orientation to move in order for the Entity to look at the
	 * dest vector.
	 * 
	 * @param me
	 * @param dest
	 */
	public void reset(Entity me, Vector2f dest) {
		this.destinationOrientation = Entity.getAngleBetween(dest, me.getPos());
	}
	
	/* (non-Javadoc)
	 * @see palisma.ai.Action#start(palisma.ai.Brain)
	 */
	@Override
	public void start(Brain brain) {				
		//brain.getEntityOwner().setOrientation(this.destinationOrientation);
		//this.getActionResult().setSuccess();
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		PlayerEntity ent = brain.getEntityOwner();
		
		float currentOrientation = ent.getOrientation();
		double currentDegree = Math.toDegrees(currentOrientation);
		double destDegree = Math.toDegrees(destinationOrientation);
		
		// TODO: Work out Looking at something (being shot while takingCover)
		return Math.abs(currentDegree - destDegree) < 2;
		//return true;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		PlayerEntity ent = brain.getEntityOwner();
		
		float currentOrientation = ent.getOrientation();
		
		// Thank you: http://dev.bennage.com/blog/2013/03/05/game-dev-03/
		float deltaOrientation = (destinationOrientation - currentOrientation);
		float deltaOrientationAbs = Math.abs(deltaOrientation);
		if(deltaOrientationAbs > Math.PI) {
			deltaOrientation = fullCircle - deltaOrientationAbs;
			//deltaOrientation = deltaOrientationAbs - fullCircle;
		}
		
		final double movementSpeed = Math.toRadians(15.0f);
		
		if(deltaOrientation != 0) {
			float direction = deltaOrientation / deltaOrientationAbs;
			currentOrientation += (direction * Math.min(movementSpeed, deltaOrientationAbs));			
		}
		currentOrientation %= fullCircle;
		
		ent.setOrientation( currentOrientation );	
	}

	
	@Override
	public DebugInformation getDebugInformation() {	
		return super.getDebugInformation().add("orientation", Math.toDegrees(destinationOrientation));
	}
}
