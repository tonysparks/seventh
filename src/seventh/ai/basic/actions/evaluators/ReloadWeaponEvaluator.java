/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.TargetingSystem;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.atom.body.ReloadAction;
import seventh.game.weapons.Weapon;

/**
 * @author Tony
 *
 */
public class ReloadWeaponEvaluator extends ActionEvaluator {

	private ReloadAction reloadAction;
	
	/**
	 * @param goals
	 * @param characterBias
	 */
	public ReloadWeaponEvaluator(Actions goals, double characterBias, double keepBias) {
		super(goals, characterBias, keepBias);
		this.reloadAction = new ReloadAction();
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double calculateDesirability(Brain brain) {
		double desire = 0.0;
		
		Weapon weapon = brain.getEntityOwner().getInventory().currentItem();
		if (weapon != null && weapon.getTotalAmmo() > 0) {
			desire = 1.0 - Evaluators.currentWeaponAmmoScore(brain.getEntityOwner());
			
			TargetingSystem system = brain.getTargetingSystem();
			if(system.hasTarget()) {								
				desire *= brain.getRandomRange(0.2, 0.5);
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
		return this.reloadAction;
	}

}
