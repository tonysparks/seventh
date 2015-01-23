/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.List;

import leola.vm.Leola;
import leola.vm.lib.LeolaIgnore;
import leola.vm.lib.LeolaLibrary;
import leola.vm.types.LeoArray;
import leola.vm.types.LeoNamespace;
import leola.vm.types.LeoObject;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.AvoidMoveToAction;
import seventh.ai.basic.actions.DefendAttackDirectionsAction;
import seventh.ai.basic.actions.DefendZoneAction;
import seventh.ai.basic.actions.DefuseBombAction;
import seventh.ai.basic.actions.EvaluateAttackDirectionsAction;
import seventh.ai.basic.actions.FindClosestBombTarget;
import seventh.ai.basic.actions.FindSafeDistanceFromActiveBombAction;
import seventh.ai.basic.actions.Goals;
import seventh.ai.basic.actions.MoveToAction;
import seventh.ai.basic.actions.MoveToBombAction;
import seventh.ai.basic.actions.PlantBombAction;
import seventh.ai.basic.actions.SecureZoneAction;
import seventh.ai.basic.actions.WaitAction;
import seventh.game.BombTarget;
import seventh.math.Vector2f;

/**
 * AI Library
 * 
 * @author Tony
 *
 */
public class AILeolaLibrary implements LeolaLibrary {

	private Leola runtime;
	private Goals goals;
	
	/**
	 * 
	 */
	public AILeolaLibrary() {
	}

	/* (non-Javadoc)
	 * @see leola.vm.lib.LeolaLibrary#init(leola.vm.Leola, leola.vm.types.LeoNamespace)
	 */
	@LeolaIgnore
	@Override
	public void init(Leola leola, LeoNamespace namespace) throws Exception {
		this.runtime = leola;
		this.runtime.putIntoNamespace(this, namespace);
		
		this.goals = new Goals(this.runtime);
		namespace.setObject("goals", Leola.toLeoObject(this.goals));
	}
	
		
	/*--------------------------------------------------------------------------
	 *                      Math functions
	  --------------------------------------------------------------------------*/
	
	
	/*--------------------------------------------------------------------------
	 *                      Factory of Atom Actions
	  --------------------------------------------------------------------------*/
	 

	public MoveToAction moveToAction(Vector2f destination) {
		return new MoveToAction(destination);
	}
	
	public AvoidMoveToAction avoidMoveToAction(Vector2f destination, LeoArray zonesToAvoid) {
		List<Zone> zones = new ArrayList<>(zonesToAvoid.size());
		for(LeoObject obj : zonesToAvoid) {
			zones.add( (Zone)obj.getValue());
		}
		return new AvoidMoveToAction(destination, zones);
	}	
	
	public SecureZoneAction secureZoneAction(Zone zone) {
		return new SecureZoneAction(zone);
	}
	
	public DefendZoneAction defendZoneAction(Zone zone) {
		return new DefendZoneAction(zone);
	}
	
	public MoveToBombAction moveToBombAction(BombTarget bomb) {
		return new MoveToBombAction(bomb);
	}
	
	public PlantBombAction plantBombAction(BombTarget bomb) {
		return new PlantBombAction(bomb);
	}
	
	public DefuseBombAction defuseBombAction(BombTarget bomb) {
		return new DefuseBombAction(bomb);
	}
	
	public FindClosestBombTarget findClosestBombTargetToPlant() {
		return new FindClosestBombTarget(false);
	}
	
	public FindClosestBombTarget findClosestBombTargetToDefuse() {
		return new FindClosestBombTarget(true);
	}
	
	public FindSafeDistanceFromActiveBombAction findSafeDistanceFromActiveBombAction(BombTarget bomb) {
		return new FindSafeDistanceFromActiveBombAction(bomb);
	}
	
	public EvaluateAttackDirectionsAction evaluateAttackDirectionsAction() {
		return new EvaluateAttackDirectionsAction();
	}
	
	public DefendAttackDirectionsAction defendAttackDirectionsAction(List<AttackDirection> attackDirs) {
		return new DefendAttackDirectionsAction(attackDirs);
	}

	public WaitAction waitAction(long timeToWaitMSec) {
		return new WaitAction(timeToWaitMSec);
	}
	
	/**
	 * Loads a function with a generator into an {@link Action} so that
	 * they can be called within scripts
	 * 
	 * @param action
	 * @return the {@link Action}
	 */
	public Action action(String action) {
		return this.goals.getScriptedAction(action);
	}
	
}
