/*
 * The Seventh
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.game.PlayerEntity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class HeadScanAction extends AdapterAction {

	private long sampleTime;
	private float destinationOrientation;
	private Vector2f dir, prevDir;
	
	/**
	 * 
	 */
	public HeadScanAction() {
		this.dir = new Vector2f();
		this.prevDir = new Vector2f();
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
		
		this.sampleTime -= timeStep.getDeltaTime();
		if(this.sampleTime < 0) {			
			
			/* I must do this in order to smooth out the direction of
			 * the agent -- during path finding it bounces back and forth
			 * between nodes because of movement delta's being large
			 */
			Vector2f.Vector2fSubtract(ent.getPos(), dir, dir);
			dir.x = (prevDir.x + dir.x) / 2f;
			dir.y = (prevDir.y + dir.y) / 2f;
			
			destinationOrientation = (float)Vector2f.Vector2fAngle(dir, Vector2f.RIGHT_VECTOR);
			
			prevDir.set(dir);
			dir.set(ent.getPos());
			
			float destinationDegree = -(float)Math.toDegrees(destinationOrientation);
				
			float currentDegree = (float)Math.toDegrees(ent.getOrientation());
			float deltaDegree = destinationDegree - currentDegree;
			
			/* find the normalized delta */
			if(deltaDegree>0) {
				deltaDegree=1;
			}
			else if(deltaDegree < 0) {
				deltaDegree=-1;
			}
			else {
				deltaDegree=0;
			}
			
			float rotationSpeed = 150.0f;
			currentDegree += deltaDegree * rotationSpeed * timeStep.asFraction();		
			ent.setOrientation( (float)Math.toRadians(currentDegree));
			this.sampleTime = 0;
		}			
	}

}
