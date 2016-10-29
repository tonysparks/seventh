/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom.body;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.entities.PlayerEntity;
import seventh.game.weapons.Weapon;
import seventh.shared.TimeStep;

/**
 * Shoot action
 * 
 * @author Tony
 *
 */
public class ShootAction extends AdapterAction {

	/**
	 */
	public ShootAction() {
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished(seventh.ai.basic.Brain)
	 */
	@Override
	public boolean isFinished(Brain brain) {	
		PlayerEntity ent = brain.getEntityOwner();
		Weapon weapon = ent.getInventory().currentItem();
		if(weapon == null) {
			return true;
		}
		
		if(!weapon.isLoaded()) {
			return true;
		}
		
		return !weapon.isFiring();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#end(seventh.ai.basic.Brain)
	 */
	@Override
	public void end(Brain brain) {
		PlayerEntity entity = brain.getEntityOwner();
		entity.endFire();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		PlayerEntity entity = brain.getEntityOwner();
		
		if(entity.canFire()) {
			if(!entity.beginFire()) {
				entity.endFire();
				this.getActionResult().setFailure();
			}
			else {
				getActionResult().setSuccess();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		PlayerEntity entity = brain.getEntityOwner();
		
		if(entity.canFire()) {
			if(!entity.beginFire()) {
				entity.endFire();
			}
		}
	}
}
