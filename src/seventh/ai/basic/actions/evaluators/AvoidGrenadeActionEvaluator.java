/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Actions;
import seventh.ai.basic.actions.atom.MoveToAction;
import seventh.game.Entity;
import seventh.game.Entity.Type;
import seventh.game.PlayerEntity;
import seventh.game.weapons.Bullet;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.SeventhConstants;

/**
 * If the bot is shot, see if it can defend itself
 * 
 * @author Tony
 *
 */
public class AvoidGrenadeActionEvaluator extends ActionEvaluator {

	private MoveToAction moveToAction;
	private Rectangle dangerArea;
	private Entity danger;
	
	/**
	 * @param goals
	 * @param characterBias
	 */
	public AvoidGrenadeActionEvaluator(Actions goals, double characterBias, double keepBias) {
		super(goals, characterBias, keepBias);
		this.moveToAction = new MoveToAction(new Vector2f());
		this.dangerArea = new Rectangle(75, 75);
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double calculateDesirability(Brain brain) {
		double desirability = 0.0;
				
		PlayerEntity me = brain.getEntityOwner();
		Entity[] entities = brain.getWorld().getEntities();
		for(int i = SeventhConstants.MAX_PERSISTANT_ENTITIES; i < entities.length; i++) {
			Entity ent = entities[i];
			if(ent!=null) {
				if(ent.getType().equals(Type.GRENADE)||
				   ent.getType().equals(Type.EXPLOSION)) {
					
					this.dangerArea.centerAround(ent.getCenterPos());
					if(this.dangerArea.intersects(me.getBounds())) {
						desirability += brain.getRandomRange(0.90f, 1.0f);
						danger = ent;
						break;
					}
				}
			}
		}
		
		desirability *= getCharacterBias();
		
		return desirability;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
	 */
	@Override
	public Action getAction(Brain brain) {		
		if(danger!=null) {
			// TODO find a spot out of the danger area
		}
		return this.moveToAction;
	}

}
