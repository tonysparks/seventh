/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import java.util.List;

import seventh.ai.basic.Brain;
import seventh.game.Entity;
import seventh.game.PlayerEntity;
import seventh.game.weapons.Weapon;
import seventh.map.PathFeeder;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Gives the entity a path to move about
 * 
 * @author Tony
 *
 */
public class FollowEntityAction extends AdapterAction {
	
	private Entity followMe;	
	private Vector2f previousPosition;
	
	private long lastVisibleTime;
	private final long timeSinceLastSeenExpireMSec;
	
	/**
	 * @param feeder
	 */
	public FollowEntityAction(Entity followMe) {
		this.followMe = followMe;
		this.previousPosition = new Vector2f();
		
		timeSinceLastSeenExpireMSec = 2_000;
	}
	
	/* (non-Javadoc)
	 * @see palisma.ai.Action#end(palisma.ai.Brain)
	 */
	@Override
	public void end(Brain brain) {		
		brain.getMotion().emptyPath();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
	 */
	@Override
	public void interrupt(Brain brain) {
		brain.getMotion().emptyPath();
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {		
		return !this.followMe.isAlive() || this.lastVisibleTime > timeSinceLastSeenExpireMSec;
	}
	
	/**
	 * Determine if we should melee the enemy
	 * @param ent
	 * @return
	 */
	protected boolean shouldMelee(PlayerEntity bot, Entity followed) {
		Weapon weapon = bot.getInventory().currentItem();
		if(weapon != null) {
			return weapon.getBulletsInClip() == 0;
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#update(palisma.ai.Brain, leola.live.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		
		List<PlayerEntity> entitiesInView = brain.getSensors().getSightSensor().getEntitiesInView();
		if(!entitiesInView.contains(this.followMe)) {		
			this.lastVisibleTime += timeStep.getDeltaTime();
		}
		else {
			this.lastVisibleTime = 0;
		}
		
		PathFeeder<?> feeder = brain.getMotion().getPathFeeder();
		if(!feeder.hasPath() || !feeder.onFirstNode()) {
			Vector2f newPosition = this.followMe.getPos();
			Vector2f start = brain.getEntityOwner().getPos();
			
			if(shouldMelee(brain.getEntityOwner(), followMe)) {
				if(Vector2f.Vector2fDistanceSq(start, newPosition) > 1_000) {
					feeder.findPath(start, newPosition);								
				}
			}
			else if(Vector2f.Vector2fDistanceSq(start, newPosition) > 10_000) {
				//if ( Vector2f.Vector2fDistanceSq(newPosition, previousPosition) > 1500 ) 
				{					
					feeder.findPath(start, newPosition);								
				}
			}
			else {
				/* stop the agent */				
				feeder.clearPath();				
			}
			
			previousPosition.set(newPosition);
		}		
	}

}
