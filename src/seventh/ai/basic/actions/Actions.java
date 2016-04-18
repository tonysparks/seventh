/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import java.util.ArrayList;
import java.util.List;

import leola.vm.Leola;
import leola.vm.lib.LeolaMethod;
import leola.vm.types.LeoArray;
import leola.vm.types.LeoObject;
import seventh.ai.AISystem;
import seventh.ai.basic.AIConfig;
import seventh.ai.basic.AttackDirection;
import seventh.ai.basic.Brain;
import seventh.ai.basic.Cover;
import seventh.ai.basic.DefaultAISystem;
import seventh.ai.basic.Zone;
import seventh.ai.basic.actions.atom.AvoidMoveToAction;
import seventh.ai.basic.actions.atom.DefendAttackDirectionsAction;
import seventh.ai.basic.actions.atom.DefendZoneAction;
import seventh.ai.basic.actions.atom.DefuseBombAction;
import seventh.ai.basic.actions.atom.EnterVehicleAction;
import seventh.ai.basic.actions.atom.EvaluateAttackDirectionsAction;
import seventh.ai.basic.actions.atom.FindClosestBombTarget;
import seventh.ai.basic.actions.atom.FindSafeDistanceFromActiveBombAction;
import seventh.ai.basic.actions.atom.FollowEntityAction;
import seventh.ai.basic.actions.atom.GuardAction;
import seventh.ai.basic.actions.atom.GuardUntilAction;
import seventh.ai.basic.actions.atom.MoveToAction;
import seventh.ai.basic.actions.atom.MoveToBombAction;
import seventh.ai.basic.actions.atom.MoveToBombTargetToPlantAction;
import seventh.ai.basic.actions.atom.MoveToFlagAction;
import seventh.ai.basic.actions.atom.MoveToVehicleAction;
import seventh.ai.basic.actions.atom.PlantBombAction;
import seventh.ai.basic.actions.atom.SecureZoneAction;
import seventh.ai.basic.actions.atom.SupressFireUntilAction;
import seventh.ai.basic.actions.atom.body.LookAtAction;
import seventh.ai.basic.actions.atom.body.MoveAction;
import seventh.ai.basic.actions.atom.body.ShootAction;
import seventh.ai.basic.actions.evaluators.DoNothingEvaluator;
import seventh.ai.basic.actions.evaluators.GrenadeEvaluator;
import seventh.ai.basic.actions.evaluators.MeleeEvaluator;
import seventh.ai.basic.actions.evaluators.MoveTowardEnemyEvaluator;
import seventh.ai.basic.actions.evaluators.ShootWeaponEvaluator;
import seventh.ai.basic.actions.evaluators.TakeCoverEvaluator;
import seventh.game.BombTarget;
import seventh.game.Flag;
import seventh.game.PlayerEntity;
import seventh.game.vehicles.Vehicle;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Randomizer;


/**
 * A factory for creating compound Actions
 * 
 * @author Tony
 *
 */
public class Actions {
	private Leola runtime;
	
	private Randomizer random;
	private AIConfig config;
	
	private DefaultAISystem aiSystem;
	
	/**
	 * @param aiSystem
	 * @param runtime
	 */
	public Actions(AISystem aiSystem, Leola runtime) {
		this.aiSystem = (DefaultAISystem)aiSystem;
		this.runtime = runtime;
		
		this.random = aiSystem.getRandomizer();
		this.config = aiSystem.getConfig();
	}
	
	/*--------------------------------------------------------------------------
	 *                      Factory of Atom Actions
	  --------------------------------------------------------------------------*/
	 

	public Action shootAtAction(Vector2f destination) {
		return new SequencedAction("shootAt")
				.addNext(new LookAtAction(destination))
				.addNext(new ShootAction());
	}
	
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
	
	public MoveToFlagAction moveToFlagAction(Flag flag) {
		return new MoveToFlagAction(flag);
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
	
	public MoveToBombTargetToPlantAction moveToBombTargetToPlantAction(BombTarget bomb) {
		return new MoveToBombTargetToPlantAction(bomb);
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
	
	public FindSafeDistanceFromActiveBombAction findSafeDistanceFromActiveBombAction(BombTarget target) {
		return new FindSafeDistanceFromActiveBombAction(target);
	}
	
	public EvaluateAttackDirectionsAction evaluateAttackDirectionsAction() {
		return new EvaluateAttackDirectionsAction();
	}
	
	public DefendAttackDirectionsAction defendAttackDirectionsAction(List<AttackDirection> attackDirs, long timeToDefend) {
		return new DefendAttackDirectionsAction(attackDirs, timeToDefend);
	}

	public WaitAction waitAction(long timeToWaitMSec) {
		return new WaitAction(timeToWaitMSec);
	}
	
	public GuardAction guardAction() {
		return new GuardAction();
	}
	
	public GuardUntilAction guardUntilAction(LeoObject isFinished) {
		return new GuardUntilAction(isFinished);
	}
	
	public SupressFireUntilAction supressFireUntilAction(Vector2f target, LeoObject isFinished) {
		return new SupressFireUntilAction(isFinished, target);
	}
	

	/**
	 * Get an {@link Action} defined in a {@link Leola} script
	 * 
	 * @param action
	 * @return the {@link Action} if found, otherwise this will return a {@link WaitAction}
	 */
	@LeolaMethod(alias="action")
	public Action getScriptedAction(String action) {
		LeoObject actionFunction  = runtime.get(action);
		if(LeoObject.isTrue(actionFunction)) {
			LeoObject gen = actionFunction.call();			
			ScriptedAction goal = new ScriptedAction(action, gen );
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
	
	public Action moveToRandomSpot(Brain brain) {		
		return new MoveAction(brain.getWorld().getRandomSpot(brain.getEntityOwner()));
	}
	
	public Action takeCover(Vector2f attackDir) {
		Action action = getScriptedAction("takeCover");
		action.getActionResult().setValue(attackDir);

		return new ConcurrentAction(action, new WeightedAction(this.config, "moveToCover",
					   	new ShootWeaponEvaluator(this, random.getRandomRangeMin(0.8), 0.8),
					   	new MeleeEvaluator(this, random.getRandomRange(0.2, 0.4), 0),
					   	new DoNothingEvaluator(this, random.getRandomRangeMin(0.6), 0),
						new GrenadeEvaluator(this, random.getRandomRangeMin(0.5), 0)
		));		
		
	}
	
	public Action moveToCover(Cover cover) {
		Action action = getScriptedAction("moveToCover");
		action.getActionResult().setValue(cover);
		
		return new ConcurrentAction(action, new WeightedAction(config, "moveToCover",
										   	new ShootWeaponEvaluator(this, random.getRandomRangeMin(0.93), 0.8),
										   	new MeleeEvaluator(this, 1, 0),
										   	new DoNothingEvaluator(this, random.getRandomRange(0.1, 0.35), 0),
											new GrenadeEvaluator(this, random.getRandomRangeMin(0.5), 0)
		));
	}
	
	public Action operateVehicle(Vehicle vehicle) {
	    CompositeAction goal = new SequencedAction("operateVehicle");
	    goal.addLastAction(new MoveToVehicleAction(vehicle));
	    goal.addLastAction(new EnterVehicleAction(vehicle));
	    return goal;
	}
	
	public Action wander() {
		Action action = getScriptedAction("wander");
		return action;
	}
	
	
	public Action chargeEnemy(PlayerEntity enemy) {
		return new ConcurrentAction(decideAttackMethod(), new FollowEntityAction(enemy)
			/*, new WeightedGoal(brain, new DodgeEvaluator(goals, brain.getRandomRange(0.4, 0.8)),
			                            new DoNothingEvaluator(goals, brain.getRandomRange(0, 0)) )
			 */
	   );
	}
	
	/**
	 * Goal which decides which attack method to use
	 * 
	 * @param goals
	 * @param brain
	 * @return the goal
	 */
	public CompositeAction decideAttackMethod() {
		return new WeightedAction(config, "decideAttackMethod",
				//new DoNothingEvaluator(goals, 1, 1)
				new ShootWeaponEvaluator(this, random.getRandomRangeMin(0.8), 0.8),
				new MeleeEvaluator(this, random.getRandomRangeMin(0.95), 0),
				new GrenadeEvaluator(this, random.getRandomRangeMin(0.2), 0.2)
		);
	}
		
	
	public CompositeAction enemyEncountered() {
		return new WeightedAction(config, "enemyEncountered",
				new MoveTowardEnemyEvaluator(this, random.getRandomRangeMin(0.68), 0.8),
				new TakeCoverEvaluator(this, random.getRandomRangeMin(0.31), 0.7)
		);
	}
	
	public Action surpressFire(Vector2f position) {
//		return new WeightedGoal(brain, "surpressFire",
////				new MoveTowardEnemyEvaluator(goals, brain.getRandomRangeMin(0.4), 0.8),
//				new SurpressFireEvaluator(goals, brain.getRandomRangeMin(0.3), 0.9, position)
//		);
		
		Action action = getScriptedAction("surpressFire");
		action.getActionResult().setValue(position);
		return action;
	}
	
	public Action guard(Vector2f position) {
	    SequencedAction goal = new SequencedAction("guard: " + position);
	    return goal.addNext(new MoveToAction(position)).addNext(new GuardAction());	    
	}
	
	public Action returnFlag(Flag flag) {
		SequencedAction goal = new SequencedAction("returnFlag: " + flag.getType());
		// TODO
	    return goal.addNext(new MoveToFlagAction(flag));
	}
	
	public Action captureFlag(Flag flag, Vector2f homebase) {
		SequencedAction goal = new SequencedAction("captureFlag: " + flag.getType());
		
		Zone flagZone = aiSystem.getWorld().getZone(flag.getSpawnLocation());
		Zone homeBaseZone = aiSystem.getWorld().getZone(homebase);
		
		List<Zone> zonesToAvoid = new ArrayList<>();
		Zone[] zones = aiSystem.getStats().getTop5DeadliesZones();
		for(int i = 0; i < zones.length;i++) {
			Zone zone = zones[i];
			if(zone!=null&&flagZone!=zone&&homeBaseZone!=zone) {
				zonesToAvoid.add(zone);				
			}
		}
		//AvoidMoveToAction
	    return goal.addNext(new MoveToFlagAction(flag, zonesToAvoid))
	    		   .addNext(new AvoidMoveToAction(homebase, zonesToAvoid));
	}
	
	public Action defendFlag(Flag flag, Rectangle homebase) {
		SequencedAction goal = new SequencedAction("defendFlag: " + flag.getType());		
	    return goal.addNext(new MoveToFlagAction(flag)); // TODO .addNext(defend(zone));
	}
}
