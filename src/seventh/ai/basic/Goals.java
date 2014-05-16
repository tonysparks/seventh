/*
 * see license.txt 
 */
package seventh.ai.basic;

import leola.vm.Leola;
import leola.vm.types.LeoObject;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.ScriptedGoal;
import seventh.ai.basic.actions.WaitAction;
import seventh.game.BombTarget;


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
	
//	public Action scanArea() {
//		Action action = getScriptedAction("scanArea");
//		return action;
//	}
	
}
