/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import java.util.Random;

import seventh.ai.basic.Brain;
import seventh.game.Entity;
import seventh.game.Entity.State;
import seventh.game.PlayerEntity;
import seventh.game.weapons.Weapon;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class FireAtAction extends AdapterAction {

	private Entity fireAtMe;		
	private long firingTime;
	
	/**
	 * @param fireAtMe 
	 */
	public FireAtAction(Entity fireAtMe) {
		reset(fireAtMe);
	}

	/**
	 * @param fireAtMe the fireAtMe to set
	 */
	public void reset(Entity fireAtMe) {
		this.fireAtMe = fireAtMe;		
		this.firingTime = 0;
		this.getActionResult().setFailure();
	}
	
	/* (non-Javadoc)
	 * @see palisma.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return !this.fireAtMe.isAlive() || brain.getSensors().getSightSensor().timeSeenAgo(fireAtMe) > 15000;
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#update(palisma.ai.Brain, leola.live.TimeStep)
	 */	
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		PlayerEntity player = brain.getEntityOwner();
		
		if( brain.getSensors().getSightSensor().inView(this.fireAtMe) ) {
			this.firingTime += timeStep.getDeltaTime();
			
			float dist = Vector2f.Vector2fDistanceSq(this.fireAtMe.getPos(), player.getPos());
			Weapon weapon = player.getInventory().currentItem();
			if(weapon!=null) {				
				if(dist<1500) {						
					weapon.meleeAttack();						
				}
				else if(weapon.canFire()) {
					
					/* if we've been firing at this guy for
					 * over X amount of time, switch
					 * our strategy
					 */					
					Random random = brain.getWorld().getRandom();
					if(this.firingTime > random.nextInt(1_000) + 1_000) {
						
						/* if the enemy if crouching behind something, try throwing a grenade at 
						 * them
						 */
						if(this.fireAtMe.getCurrentState()==State.CROUCHING) {			
							Vector2f dest = this.fireAtMe.getCenterPos().createClone();
							int x = random.nextBoolean() ? -1 : 1;
							int y = random.nextBoolean() ? -1 : 1;
							Vector2f.Vector2fAdd(dest, new Vector2f(random.nextInt(128) * x, random.nextInt(128) * y), dest);
							brain.getMotion().throwGrenade(dest);
							this.firingTime = 0;
						
						}
						
						/*
						 * Otherwise, we'll need to figure out something else
						 */
						else {					
							player.beginFire();
							player.endFire();
						}
					}
					else {					
						player.beginFire();
						player.endFire();
					}
				}
				else if (!weapon.isLoaded()) {
					player.reload();
				}
			}
		}
		else {
			this.firingTime = 0;				
		}		
		
		if(!this.fireAtMe.isAlive()) {
			this.getActionResult().setSuccess();
		}
	}

	
	@Override
	public DebugInformation getDebugInformation() {	
		return super.getDebugInformation().add("fireAt", this.fireAtMe);
	}
}
