/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Goals;
import seventh.ai.basic.actions.MoveToAction;
import seventh.game.Entity;
import seventh.game.weapons.Bullet;
import seventh.math.Vector2f;

/**
 * If the bot is shot, see if it can defend itself
 * 
 * @author Tony
 *
 */
public class DefendSelfActionEvaluator extends ActionEvaluator {

	private MoveToAction moveToAction;
	
	/**
	 * @param goals
	 * @param characterBias
	 */
	public DefendSelfActionEvaluator(Goals goals, double characterBias, double keepBias) {
		super(goals, characterBias, keepBias);
		this.moveToAction = new MoveToAction(new Vector2f());
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double calculateDesirability(Brain brain) {
		double desirability = 0.0;
				
		Entity attacker = brain.getSensors().getFeelSensor().getMostRecentAttacker();
		if(attacker != null) {
			desirability += brain.getRandomRange(0.90f, 1.0f);
		}
		
		desirability *= getCharacterBias();
		
		return desirability;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
	 */
	@Override
	public Action getAction(Brain brain) {		
		Entity attacker = brain.getSensors().getFeelSensor().getMostRecentAttacker();
		if(attacker != null) {
			if(attacker instanceof Bullet) {
				Bullet bullet = (Bullet)attacker;
				Entity owner = bullet.getOwner();
				this.moveToAction.reset(brain, owner.getCenterPos());
				brain.getMotion().lookAt(this.moveToAction.getDestination());
			}
			else {
				
				this.moveToAction.reset(brain, attacker.getCenterPos());
				brain.getMotion().lookAt(this.moveToAction.getDestination());
			}
		}		
		return this.moveToAction;
	}

}
