/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import java.util.List;

import seventh.ai.basic.Brain;
import seventh.game.Entity;
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
	private long lastContactTime;
	/**
	 * 
	 */
	public FireAtAction(Entity fireAtMe) {
		this.fireAtMe = fireAtMe;
		this.getActionResult().setFailure();
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return !this.fireAtMe.isAlive() || this.lastContactTime > 15000;
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#update(palisma.ai.Brain, leola.live.TimeStep)
	 */	
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		PlayerEntity player = brain.getEntityOwner();
		
		List<PlayerEntity> entitiesInView = brain.getSensors().getSightSensor().getEntitiesInView();
		if(entitiesInView.contains(this.fireAtMe)) {
			float dist = Vector2f.Vector2fDistanceSq(this.fireAtMe.getPos(), player.getPos());
			Weapon weapon = player.getInventory().currentItem();
			if(weapon!=null) {				
				if(dist<1200) {						
					weapon.meleeAttack();						
				}
				else if(weapon.canFire()) {
					player.beginFire();
					player.endFire();						
				}
				else if (!weapon.isLoaded()) {
					player.reload();
				}
			}
		}
		else {
			this.lastContactTime += timeStep.getDeltaTime();	
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
