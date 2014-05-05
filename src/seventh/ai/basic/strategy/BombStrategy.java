/*
 * see license.txt 
 */
package seventh.ai.basic.strategy;

import java.util.List;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.ai.basic.Memory;
import seventh.ai.basic.ThoughtProcess;
import seventh.game.BombTarget;
import seventh.game.Entity.Type;
import seventh.game.PlayerEntity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * A very simple strategy for a bot
 * 
 * @author Tony
 *
 */
public class BombStrategy implements ThoughtProcess {

	/**
	 * Determine if we need to find another target
	 * @param brain
	 * @return
	 */
	protected boolean shouldFindTarget(Brain brain) {
		return false;
	}
	
	/**
	 * Check and see if we should stop what we are doing 
	 * @param brain
	 * @param bomb
	 * @return
	 */
	protected boolean checkIfJobCompleted(Brain brain, BombTarget bomb) {
		return false;
	}
	
	/**
	 * Invoked when we arrived at the bomb
	 * @param brain
	 * @param bomb
	 */
	protected void arrivedAtBomb(Brain brain, BombTarget bomb) {		
	}
	
	/**
	 * Invoked when we plan on moving towards  the bomb
	 * @param brain
	 * @param bomb
	 */
	protected void moveTowardsBomb(Brain brain, BombTarget bomb) {		
	}
	
	/**
	 * If we should consider this {@link BombTarget} 
	 * @param brain
	 * @param bomb
	 * @return
	 */
	protected boolean isValidBombTarget(Brain brain, BombTarget bomb) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.Strategy#onKilled(seventh.ai.basic.Brain)
	 */
	@Override
	public void onKilled(Brain brain) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.Strategy#onSpawn(seventh.ai.basic.Brain)
	 */
	@Override
	public void onSpawn(Brain brain) {
		// TODO Auto-generated method stub
		
	}
	/*
	 * (non-Javadoc)
	 * @see seventh.ai.Strategy#think(seventh.shared.TimeStep, seventh.ai.Brain)
	 */
	
	@Override
	public void think(TimeStep timeStep, Brain brain) {
		
		PlayerEntity bot = brain.getEntityOwner();
		
		/* if we are not defusing, lets go ahead 
		 * and continue to try find and defuse a bomb
		 */
		if(shouldFindTarget(brain)) {
	
			Memory mem = brain.getMemory();
			Locomotion motion = brain.getMotion();
			
			
			/* if we have a bomb target, lets plant it */			
			if(mem.has("bomb_target", BombTarget.class)) {
				BombTarget bomb = mem.getType("bomb_target");
				
				if(!checkIfJobCompleted(brain, bomb)) {																	
					float distanceToBomb = Vector2f.Vector2fDistanceSq(bomb.getPos(), bot.getPos());
					
					/* if the bomb is far away, switch to the pistol so
					 * we can move fast
					 */
					if(distanceToBomb > 1024*1024) {
						motion.changeWeapon(Type.PISTOL);
					}
					
					
					if(bomb.getBounds().intersects(bot.getBounds())) {
						motion.stopMoving();			
						arrivedAtBomb(brain, bomb);						
					}					
					else {						
						if(!motion.isMoving()) {
							moveTowardsBomb(brain, bomb);														
						}
					}
				
				}
			}
			else {

				List<BombTarget> targets = brain.getWorld().getBombTargets();
				
				
				/* we are out of bomb targets, so lets go back
				 * to our basic instincts
				 */
				if(targets.isEmpty()) {
//					brain.getThoughtProcess().setStrategy(new BasicStrategy());
				}
				else {
					BombTarget closestBomb = null;
					float distance = -1;
					
					/* lets find the closest bomb target */
					for(int i = 0; i < targets.size(); i++) {
						BombTarget bomb = targets.get(i);
						
						/* make sure this bomb is eligable for planting */
						if(isValidBombTarget(brain, bomb)) {
								
							float distanceToBomb = Vector2f.Vector2fDistanceSq(bomb.getPos(), bot.getPos());
							
							/* if we haven't assigned a closest or we have a closer bomb
							 * assign it.
							 */
							if(closestBomb == null || distanceToBomb < distance) {
								closestBomb = bomb;
								distance = distanceToBomb;
							}
						}
						
					}
					
					if(closestBomb == null) {
//						brain.getThoughtProcess().setStrategy(new BasicStrategy());
					}
					else {
						mem.store("bomb_target", closestBomb);
					}
				}
			}
		}
	}		
		
}
