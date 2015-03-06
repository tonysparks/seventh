/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.game.PlayerEntity;

/**
 * @author Tony
 *
 */
public class WeaponSystem {

	private Brain brain;
	/**
	 * 
	 */
	public WeaponSystem() {
		// TODO Auto-generated constructor stub
	}

	public void takeAim() {
		PlayerEntity entity = brain.getTargetingSystem().getCurrentTarget();
		if(entity != null) {
			
		}
	}
}
