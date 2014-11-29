/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.List;

import seventh.ai.basic.SimpleThoughtProcess.ThinkListener;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.teamstrategy.TeamStrategy;
import seventh.game.BombTarget;
import seventh.game.Entity;
import seventh.game.PlayerEntity;
import seventh.game.events.SoundEmittedEvent;
import seventh.game.weapons.Bullet;
import seventh.shared.DebugDraw;
import seventh.shared.TimeStep;

/**
 * Handles reactionary AI.  This reacts to any game world events of interest
 * 
 * @author Tony
 *
 */
public class ReactiveThinkListener implements ThinkListener {
	
	private Goal longTermGoal;	
	private TeamStrategy strategy;
	private boolean isInterrupted;
	
//	enum State {
//		None,
//		Combat,
//		SeekingCover,
//		Hiding,
//		Wandering,
//		PlantingBomb,
//		DefusingBomb,
//		Defending,		
//		MovingAway,
//	}
	
	/**
	 * 
	 */
	public ReactiveThinkListener(TeamStrategy strategy) {
		this.strategy = strategy;
		this.longTermGoal = new Goal();		
	}
	
	
	/**
	 * Interrupt the current goal to react to
	 * a world event
	 * 
	 * @param brain
	 */
	private void interrupt(Brain brain) {
		if(!this.isInterrupted) {
			longTermGoal.interrupt(brain);
			this.isInterrupted = true;
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.strategy.SimpleStrategy.ThinkListener#onDeath(seventh.ai.basic.Brain)
	 */
	@Override
	public void onDeath(Brain brain) {		
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.strategy.SimpleStrategy.ThinkListener#onSpawned(seventh.ai.basic.Brain)
	 */
	@Override
	public void onSpawned(Brain brain) {
		brain.getMotion().pickWeapon();
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.strategy.SimpleStrategy.ThinkListener#onBeginThink(seventh.shared.TimeStep, seventh.ai.basic.Brain)
	 */
	@Override
	public boolean onBeginThink(TimeStep timeStep, Brain brain) {		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.strategy.SimpleStrategy.ThinkListener#onEndThink(seventh.shared.TimeStep, seventh.ai.basic.Brain)
	 */
	@Override
	public void onEndThink(TimeStep timeStep, Brain brain) {
				
		/* are we receiving orders? if so make these highest priority */
		Action command = brain.getCommunicator().receiveAction(brain);
		if(command != null) {			
			this.longTermGoal.end(brain);
			this.longTermGoal.addFirstAction(command);
		}
		else {
			
			/* if we were interrupted, we should 
			 * be able to resume our goal now
			 */
			if(this.isInterrupted) {
				this.longTermGoal.resume(brain);
				this.isInterrupted = false;
			}
			
			
			/* if we don't have any goals left,
			 * let's ping the TeamStrategy to see
			 * if there is anything we should be doing
			 */
			if(this.longTermGoal.isFinished(brain)) {		
				if(this.strategy != null) {
					this.strategy.onGoaless(brain);
				}
			}
			
			
			/* if all else fails, start wandering around
			 * looking for bad guys
			 */
			if(this.longTermGoal.isFinished(brain)) {
				
				Locomotion motion = brain.getMotion();
				if(!motion.isMoving() && !motion.isAttacking()) {
					motion.wander();
				}
			}
			else {
				this.longTermGoal.update(brain, timeStep);
			}
		}
		
//		debugDraw();
	}
	
	@SuppressWarnings("unused")
	private void debugDraw() {

		Action action = longTermGoal.currentAction();
		if(action != null) {
			DebugDraw.drawString("Goal Action: " + action.getClass().getSimpleName(), 800, 50, 0xff00ff00);
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.strategy.SimpleStrategy.ThinkListener#onStuck(seventh.shared.TimeStep, seventh.ai.basic.Brain)
	 */
	@Override
	public boolean onStuck(TimeStep timeStep, Brain brain) {
		Locomotion motion = brain.getMotion();
		PlayerEntity bot = brain.getEntityOwner();

		motion.moveTo(brain.getWorld().getRandomSpot(bot));
		motion.scanArea();
		return true;		
//		return false;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.strategy.SimpleStrategy.ThinkListener#onTouched(seventh.shared.TimeStep, seventh.ai.basic.Brain, seventh.game.Entity)
	 */
	@Override
	public boolean onTouched(TimeStep timeStep, Brain brain, Entity attacker) {

		Locomotion motion = brain.getMotion();
		boolean attacking = motion.isAttacking(); 
		
		
		/* first check if we are getting shot
		 * if so, lets look at the attacker.
		 * if they are an enemy, we will attack (this will happen next frame)
		 */
		//if( !attacking ) 
		{
			
			if(attacker instanceof Bullet) {	
				interrupt(brain);
				motion.lookAt(((Bullet) attacker).getOwner().getCenterPos());	
				attacking = true;
			}
		}
		
		return attacking;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.strategy.SimpleStrategy.ThinkListener#onSeeEnemies(seventh.shared.TimeStep, seventh.ai.basic.Brain, java.util.List)
	 */
	@Override
	public boolean onSeeEnemies(TimeStep timeStep, Brain brain, List<PlayerEntity> enemies) {
		
		Locomotion motion = brain.getMotion();
		boolean attacking = motion.isAttacking(); 
		
		// Now lets check to see if any enemies
		// are in our sights.  If so, attack! 
		
		/* reaffirm our attacking strategy */
		Entity enemy = brain.getEntityOwner().getClosest(enemies);
		if(enemy != null) {
			interrupt(brain);
			motion.attack(enemy);
			attacking = true;
		}
		
		
		return attacking;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.strategy.SimpleStrategy.ThinkListener#onClosestSound(seventh.shared.TimeStep, seventh.ai.basic.Brain, seventh.game.events.SoundEmittedEvent)
	 */
	@Override
	public boolean onClosestSound(TimeStep timeStep, Brain brain, SoundEmittedEvent closestSound) {
		Locomotion motion = brain.getMotion();
		boolean attacking = motion.isAttacking() || motion.isMoving(); 
		
		// Finally, lets listen for any sounds, and go
		// to the closest one
		if(!attacking) {
			interrupt(brain);
			
			motion.moveTo(closestSound.getPos());
			motion.scanArea();
			attacking = true;
		}
		
		return attacking;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.SimpleThoughtProcess.ThinkListener#onTooCloseToActiveBomb(seventh.shared.TimeStep, seventh.ai.basic.Brain, seventh.game.BombTarget)
	 */
	@Override
	public boolean onTooCloseToActiveBomb(TimeStep timeStep, Brain brain, BombTarget target) {	
		Locomotion motion = brain.getMotion();
		if(!motion.isMoving()) {
			motion.wander();
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation me = new DebugInformation();
		me.add("type", getClass().getSimpleName())
		  .add("is_interrupted", this.isInterrupted)
		  .add("long_term_goal", this.longTermGoal);		  
		return me;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDebugInformation().toString();
	}	
}
