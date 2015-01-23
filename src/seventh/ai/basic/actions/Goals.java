/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import leola.vm.Leola;
import leola.vm.types.LeoObject;
import seventh.ai.basic.Brain;
import seventh.ai.basic.Zone;
import seventh.game.BombTarget;
import seventh.game.Entity;
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
	
	public Action moveToRandomSpot() {
		Action action = getScriptedAction("moveToRandomSpot");
		return action;
	}
	
	public Action onTouched(Entity attacker) {
		Action action = getScriptedAction("onTouched");
		action.getActionResult().setValue(attacker);
		return action;
	}
	
//	public Action scanArea() {
//		Action action = getScriptedAction("scanArea");
//		return action;
//	}
	
}
