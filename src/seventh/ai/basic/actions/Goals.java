/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import leola.vm.Leola;
import leola.vm.types.LeoObject;
import seventh.ai.basic.Brain;
import seventh.ai.basic.Cover;
import seventh.ai.basic.Zone;
import seventh.ai.basic.actions.evaluators.GrenadeEvaluator;
import seventh.ai.basic.actions.evaluators.MeleeEvaluator;
import seventh.ai.basic.actions.evaluators.MoveTowardEnemyEvaluator;
import seventh.ai.basic.actions.evaluators.ShootWeaponEvaluator;
import seventh.ai.basic.actions.evaluators.TakeCoverEvaluator;
import seventh.game.BombTarget;
import seventh.game.Entity;
import seventh.game.PlayerEntity;
import seventh.math.Vector2f;


/**
 * A factory for creating compound goal objectives
 * 
 * @author Tony
 *
 */
public class Goals {
	private Leola runtime;
	
	public Goals(Leola runtime) {
		this.runtime = runtime;
	}
	
	
	/**
	 * Get an {@link Action} defined in a {@link Leola} script
	 * @param action
	 * @return
	 */
	public Action getScriptedAction(String action) {
		LeoObject actionFunction  = runtime.get(action);
		if(LeoObject.isTrue(actionFunction)) {
			LeoObject gen = this.runtime.execute(actionFunction);			
			ScriptedGoal goal = new ScriptedGoal(runtime, gen);
			return goal;
		}				
		return new WaitAction(1_000);
	}
	
	public Action defuseBomb() {
		return getScriptedAction("defuseBomb");
	}
	
	public Action plantBomb() {
		return getScriptedAction("plantBomb");
	}
		
	public Action infiltrate(Zone zone) {
		Action action = getScriptedAction("infiltrate");
		action.getActionResult().setValue(zone);
		return action;
	}
	
	public Action defend(Zone zone) {
		Action action = getScriptedAction("defend");
		action.getActionResult().setValue(zone);
		return action;
	}
	
	public Action defendPlantedBomb(BombTarget target) {
		Action action = getScriptedAction("defendPlantedBomb");
		action.getActionResult().setValue(target);
		return action;
	}
	
	public Action goToRandomSpot(Brain brain) {		
		return new MoveAction(brain.getWorld().getRandomSpot(brain.getEntityOwner()));
	}
	
	public Action takeCover(Vector2f attackDir) {
		Action action = getScriptedAction("takeCover");
		action.getActionResult().setValue(attackDir);
		return action;
	}
	
	public Action moveToCover(Cover cover) {
		Action action = getScriptedAction("moveToCover");
		action.getActionResult().setValue(cover);
		return action;
	}
	
	public Action moveToRandomSpot() {
		Action action = getScriptedAction("moveToRandomSpot");
		return action;
	}
	
	public Action onTouched(Entity attacker) {
		Action action = getScriptedAction("onTouched");
		action.getActionResult().setValue(attacker);
		return action;
	}
	
	public Action attack(Entity enemy) {
		Action action = getScriptedAction("attack");
		action.getActionResult().setValue(enemy);
		return action;
	}
	
//	public Action scanArea() {
//		Action action = getScriptedAction("scanArea");
//		return action;
//	}
	
	
	public Action chargeEnemy(Goals goals, Brain brain, PlayerEntity enemy) {
		return new ConcurrentGoal(decideAttackMethod(goals, brain), new FollowEntityAction(enemy));
	}
	
	/**
	 * Goal which decides which attack method to use
	 * 
	 * @param goals
	 * @param brain
	 * @return the goal
	 */
	public Goal decideAttackMethod(Goals goals, Brain brain) {
		return new WeightedGoal(brain, 
				new ShootWeaponEvaluator(goals, brain.getRandomRangeMin(0.8)),
				new MeleeEvaluator(goals, brain.getRandomRangeMin(0.5)),
				new GrenadeEvaluator(goals, brain.getRandomRangeMin(0.2))
		);
	}
	
	
	public Goal enemyEncountered(Goals goals, Brain brain) {
		return new WeightedGoal(brain, 
				new MoveTowardEnemyEvaluator(goals, brain.getRandomRangeMin(0.4)),
				new TakeCoverEvaluator(goals, brain.getRandomRangeMin(0.3))
		);
	}
	
}
