/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import java.util.List;

import seventh.ai.basic.AttackDirection;
import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.Entity;
import seventh.game.PlayerEntity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class DefendAttackDirectionsAction extends AdapterAction {

	private List<AttackDirection> attackDirs;
	private long lookTime;
	private long timeToDefend, originalTimeToDefend;
	private int currentDirection;
	private Vector2f dir;
	private boolean interrupted;
	
	
	
	/**
	 * 
	 */
	public DefendAttackDirectionsAction(List<AttackDirection> attackDirs, long timeToDefend) {
		this.attackDirs = attackDirs;
		this.timeToDefend = timeToDefend;
		this.originalTimeToDefend  = timeToDefend;
		
		this.dir = new Vector2f();
		if(this.attackDirs.isEmpty()) {
			this.interrupted = true;
		}
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		this.timeToDefend = this.originalTimeToDefend;
		
		if(!this.interrupted && this.attackDirs.size() > 0) {
			this.currentDirection = (currentDirection+1) % this.attackDirs.size();
			this.dir.set(this.attackDirs.get(currentDirection).getDirection());
		}
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
	 */
	@Override
	public void resume(Brain brain) {
		if(this.attackDirs.isEmpty()) {
			this.interrupted = true;
		}
		else {
			this.interrupted = false;
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
	 */
	@Override
	public void interrupt(Brain brain) {
		this.interrupted = true;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		lookTime += timeStep.getDeltaTime();
		timeToDefend -= timeStep.getDeltaTime();
		
		PlayerEntity ent = brain.getEntityOwner();
		
		if(lookTime > 2_000 && !this.interrupted && this.attackDirs.size() > 0) {
			this.currentDirection = (currentDirection+1) % this.attackDirs.size();
			this.dir.set(this.attackDirs.get(currentDirection).getDirection());
			lookTime = 0;
		}
		
		float destinationOrientation = (float)Entity.getAngleBetween(dir, ent.getCenterPos());
		ent.setOrientation(destinationOrientation);
				
//		float destinationDegree = (float)Math.toDegrees(destinationOrientation);
//			
//		float currentDegree = (float)Math.toDegrees(ent.getOrientation());
//		float deltaDegree = destinationDegree - currentDegree;
//		
//		/* find the normalized delta */
//		if(deltaDegree>0) {
//			deltaDegree=1;
//		}
//		else if(deltaDegree < 0) {
//			deltaDegree=-1;
//		}
//		else {
//			deltaDegree=0;
//		}
//		
//		float rotationSpeed = 150.0f;
//		currentDegree += deltaDegree * rotationSpeed * timeStep.asFraction();		
//		ent.setOrientation( (float)Math.toRadians(currentDegree));
	
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished(seventh.ai.basic.Brain)
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return interrupted || timeToDefend <= 0;
	}
}
