/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.ai.basic.memory.SightMemory.SightMemoryRecord;
import seventh.game.Entity;
import seventh.game.Entity.Type;
import seventh.game.PlayerEntity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;
import seventh.shared.Updatable;

/**
 * Responsible for fixating on the most appropriate target (enemy).
 * 
 * @author Tony
 *
 */
public class TargetingSystem implements Updatable {

	private Brain brain;
	private PlayerEntity currentTarget;
	
	private Timer checkTimer;
	
	/**
	 * 
	 */
	public TargetingSystem(Brain brain) {
		this.brain = brain;
		this.checkTimer = new Timer(true, brain.getConfig().getTriggeringSystemPollTime());
		this.checkTimer.start();
	}

	public void reset(Brain brain) {
		this.clearTarget();		
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		checkTimer.update(timeStep);
		
		if(checkTimer.isTime()) {
		
			Sensors sensors = brain.getSensors();
			
			Entity recentAttacker = sensors.getFeelSensor().getMostRecentAttacker(); 
			PlayerEntity closestEnemyInSight = sensors.getSightSensor().getClosestEntity();
	//		SoundEmittedEvent closestSound = sensors.getSoundSensor().getClosestSound();
			
			this.currentTarget = null;
			
			/* if we are being attacked, this is fairly high priority */
			if(recentAttacker != null && recentAttacker.getType().equals(Type.PLAYER)) {
				
				if(closestEnemyInSight != null) {
					
					/* if the closest in sight and attacker are the same, then
					 * the choice is obvious
					 */
					if(closestEnemyInSight == recentAttacker) {
						this.currentTarget = closestEnemyInSight;
					}
					else {
						
						Vector2f botPos = this.brain.getEntityOwner().getCenterPos();
						float attackerDis = Vector2f.Vector2fDistanceSq(botPos, recentAttacker.getCenterPos());
						float sightDis = Vector2f.Vector2fDistanceSq(botPos, closestEnemyInSight.getCenterPos());
						
						if(sightDis < attackerDis) {
							this.currentTarget = closestEnemyInSight;
						}
						else {
							this.currentTarget = (PlayerEntity)recentAttacker;		
						}
					}
				}
				else {				
					this.currentTarget = (PlayerEntity)recentAttacker;			
				}
			}
			else {
				
				if(closestEnemyInSight != null) {
					this.currentTarget = closestEnemyInSight;
				}
				
			}
		}
	}
	
	/**
	 * If there is a target
	 * @return
	 */
	public boolean hasTarget() {
		return this.currentTarget != null;
	}
	
	public void clearTarget() {
		this.currentTarget = null;
	}
	
	/**
	 * @return the currentTarget
	 */
	public PlayerEntity getCurrentTarget() {
		return currentTarget;
	}
	
	
	/**
	 * The last remembered position
	 * 
	 * @return
	 */
	public Vector2f getLastRemeberedPosition() {
		if(hasTarget()) {
			SightMemoryRecord record = this.brain.getSensors().getSightSensor().getMemoryRecordFor(currentTarget);
			if(record.isValid()) {
				return record.getLastSeenAt();
			}
		}
		
		return null;
	}
}
