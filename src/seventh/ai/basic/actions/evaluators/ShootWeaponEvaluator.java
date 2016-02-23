/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.TargetingSystem;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.atom.ShootAtAction;
import seventh.game.weapons.Weapon;

/**
 * @author Tony
 *
 */
public class ShootWeaponEvaluator extends ActionEvaluator {

	
	private ShootAtAction shootAction;
	
	/**
	 * @param goals
	 * @param characterBias
	 */
	public ShootWeaponEvaluator(Actions goals, double characterBias, double keepBias) {
		super(goals, characterBias, keepBias);
		
		this.shootAction = new ShootAtAction();
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double calculateDesirability(Brain brain) {
		double desire = 0.0;
		
		TargetingSystem system = brain.getTargetingSystem();
		if(system.hasTarget()) {
			Weapon weapon = brain.getEntityOwner().getInventory().currentItem();
			if (weapon != null) {
				double weaponScore = Evaluators.currentWeaponAmmoScore(brain.getEntityOwner());
				double distanceScore = Evaluators.weaponDistanceScore(brain.getEntityOwner(), system.getCurrentTarget());
								
				
				desire  = (weaponScore + distanceScore) / 2.0;
				desire *= getCharacterBias();
			}
		}
		
		return desire;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
	 */
	@Override
	public Action getAction(Brain brain) {
		return this.shootAction;
	}

}
