/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.List;

import seventh.ai.basic.SimpleThoughtProcess.ThinkListener;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.teamstrategy.TeamStrategy;
import seventh.game.Bomb;
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
	
	private Goal goal;
	private TeamStrategy strategy;
	private boolean isInterrupted;
	private boolean movingAwayFromBomb;
	
	/**
	 * 
	 */
	public ReactiveThinkListener(TeamStrategy strategy) {
		this.strategy = strategy;
		this.goal = new Goal();
	}
	
	
	/**
	 * Interrupt the current goal to react to
	 * a world event
	 * 
	 * @param brain
	 */
	private void interrupt(Brain brain) {
		if(!this.isInterrupted) {
			goal.interrupt(brain);
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
		this.movingAwayFromBomb = false;
		
		/* are we receiving orders? if so make these highest priority */
		Action command = brain.getCommunicator().receiveAction(brain);
		if(command != null) {			
			this.goal.end(brain);
			this.goal.addFirstAction(command);
		}
		else {
			
			/* if we were interrupted, we should 
			 * be able to resume our goal now
			 */
			if(this.isInterrupted) {
				this.goal.resume(brain);
				this.isInterrupted = false;
			}
			
			
			/* if we don't have any goals left,
			 * let's ping the TeamStrategy to see
			 * if there is anything we should be doing
			 */
			if(this.goal.isFinished(brain)) {		
				if(this.strategy != null) {
					this.strategy.onGoaless(brain);
				}
			}
			
			
			/* if all else fails, start wandering around
			 * looking for bad guys
			 */
			if(this.goal.isFinished(brain)) {
				
				Locomotion motion = brain.getMotion();
				if(!motion.isMoving() && !motion.isAttacking()) {
					motion.wander();
				}
			}
			else {
				this.goal.update(brain, timeStep);
			}
		}
		
//		debugDraw();
	}
	
	@SuppressWarnings("unused")
	private void debugDraw() {

		Action action = goal.currentAction();
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
//				motion.lookAt(attacker.getCenterPos());
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
		return false;
		
//		World world = brain.getWorld();
//		if(movingAwayFromBomb) {
//			movingAwayFromBomb = target.isAlive() && target.bombActive();									
//			return true;
//		}
//		
//		
//		/* if we are defusing, we should be by the bomb */
//		PlayerEntity me = brain.getEntityOwner();
//		if(me.isDefusingBomb()) {
//			return false;
//		}
//		
//		/* we might be trying to kill someone by the bomb */
//		Locomotion motion = brain.getMotion();
//		if(motion.isAttacking()) {
//			return false;
//		}
//		
//		Bomb bomb = target.getBomb();
//		if(bomb==null) {
//			return false;
//		}
//
//		movingAwayFromBomb = true;
//											
//		Zone bombZone = world.getZone(target.getCenterPos());
//		Zone adjacentZone = world.findAdjacentZone(bombZone, bomb.getBlastRadius().width);
//						
//		/* if we found a safer zone, move to it */
//		if(adjacentZone != null) {
//			motion.moveTo(world.getRandomSpot(me, adjacentZone.getBounds()));
//		}
//		else {
//			/* if there isn't a close zone, just move to a random spot -- shouldn't happen */
//			motion.moveTo(world.getRandomSpot(me));
//		}
//		
//		return true;
	}

}
