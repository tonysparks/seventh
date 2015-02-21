/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.game.PlayerEntity;
import seventh.game.weapons.Weapon;

/**
 * @author Tony
 *
 */
public class Evaluators {

	/**
	 * Health score between 0 and 1
	 * @param ent
	 * @return
	 */
	public static double healthScore(PlayerEntity ent) {
		return (double) ent.getHealth() / (double) ent.getMaxHealth();
	}
	
	/**
	 * Current weapon score (only accounts for bullets in clip)
	 * @param ent
	 * @return
	 */
	public static double currentWeaponScore(PlayerEntity ent) {
		
		double score = 0;
		Weapon weapon = ent.getInventory().currentItem();
		if(weapon != null) {
			return (double)weapon.getBulletsInClip() / (double)weapon.getClipSize();
		}
		
		return score;
	}
}
