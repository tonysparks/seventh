/*
 * The Seventh
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.game.PlayerEntity;
import seventh.map.PathFeeder;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class HeadScanAction extends AdapterAction {

	/**
	 * Full circle
	 */
	private static final float fullCircle = (float)Math.toRadians(360);
	
	private long sampleTime;	
	private Vector2f destination;
	
	/**
	 */
	public HeadScanAction() {		
		this.destination = new Vector2f();
	}


	/**
	 * Rest this action
	 */
	public void reset() {
		this.sampleTime = 0;
		this.destination.zeroOut();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return false;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.Action#update(seventh.ai.Brain, leola.live.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		PlayerEntity ent = brain.getEntityOwner();
		
		/* only update the destination once in a while to 
		 * avoid the jitters				
		 */
		this.sampleTime -= timeStep.getDeltaTime();
		if(this.sampleTime < 0) {
			PathFeeder<?> feeder = brain.getMotion().getPathFeeder();
			
			Vector2f dest = null;
			if(feeder != null) {
				dest = feeder.nextDestination(ent);			
			}
			else {
				dest = ent.getMovementDir();
			}
			
			Vector2f.Vector2fNormalize(dest, dest);
			
			destination.set(dest);
			this.sampleTime = 200;
		}
		
		
		float currentOrientation = ent.getOrientation();
		float destinationOrientation = (float)(Math.atan2(destination.y, destination.x));
		
		// Thank you: http://dev.bennage.com/blog/2013/03/05/game-dev-03/
		float deltaOrientation = (destinationOrientation - currentOrientation);
		float deltaOrientationAbs = Math.abs(deltaOrientation);
		if(deltaOrientationAbs > Math.PI) {
			deltaOrientation = deltaOrientationAbs - fullCircle;
		}
		
		final double movementSpeed = Math.toRadians(15.0f);
		
		if(deltaOrientation != 0) {
			float direction = deltaOrientation / deltaOrientationAbs;
			currentOrientation += (direction * Math.min(movementSpeed, deltaOrientationAbs));
			currentOrientation %= fullCircle;
		}
		
		ent.setOrientation( currentOrientation );										
	}

}
