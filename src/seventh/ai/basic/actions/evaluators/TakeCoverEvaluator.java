/*
 * see license.txt 
 */
package seventh.ai.basic.actions.evaluators;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Cover;
import seventh.ai.basic.TargetingSystem;
import seventh.ai.basic.actions.Action;
import seventh.ai.basic.actions.Goals;
import seventh.game.PlayerEntity;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class TakeCoverEvaluator extends ActionEvaluator {

	private Cover cover;
	/**
	 * @param goals
	 * @param characterBias
	 */
	public TakeCoverEvaluator(Goals goals, double characterBias, double keepBias) {
		super(goals, characterBias, keepBias);
		this.cover = new Cover(new Vector2f(), new Vector2f());
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#calculateDesirability(seventh.ai.basic.Brain)
	 */
	@Override
	public double calculateDesirability(Brain brain) {
		double score = 0;
		TargetingSystem system = brain.getTargetingSystem();
		if(system.hasTarget()) {
			PlayerEntity bot = brain.getEntityOwner();
			
			final double tweaker = 1.0;
			
			final PlayerEntity enemy = system.getCurrentTarget();
			
			if(enemy.isOperatingVehicle()) {
				score = 1.0;
			}
			else {
				score = tweaker 
						* 1.0 - Evaluators.healthScore(bot) 
						* 1.0 - Evaluators.currentWeaponAmmoScore(bot)
						* 1.0 - Evaluators.weaponDistanceScore(bot, enemy)
						;
			}
			Vector2f lastSeenAt = system.getLastRemeberedPosition();
			if(lastSeenAt != null) {
//				Vector2f coverPosition = brain.getWorld().getClosestCoverPosition(bot, system.getLastRemeberedPosition());
//				this.cover.setCoverPos(coverPosition);
//				
//				DebugDraw.drawRectRelative( (int)coverPosition.x, (int)coverPosition.y, 10, 10, 0xff00ff00);
//				float distanceToCoverSq = Vector2f.Vector2fDistanceSq(bot.getCenterPos(), coverPosition);
//				final float MaxCoverDistance = (32*4) * (32*4);
//				
//				if(distanceToCoverSq < MaxCoverDistance) {
//					score *= 1.0 - distanceToCoverSq / MaxCoverDistance;
//				}
//				else {
//					score = 0;
//				}
			}
			
			
			if(!system.currentTargetInLineOfFire()) {
				score *= 0.9;
			}
			
			score *= getCharacterBias();
		}
		
		
		return score;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.evaluators.ActionEvaluator#getAction(seventh.ai.basic.Brain)
	 */
	@Override
	public Action getAction(Brain brain) {
		
		TargetingSystem system = brain.getTargetingSystem();
		Vector2f attackDir = system.getLastRemeberedPosition() != null ? system.getLastRemeberedPosition() : 
							 system.hasTarget() ? system.getCurrentTarget().getCenterPos() : brain.getEntityOwner().getFacing();
							 
		Vector2f coverPosition = brain.getWorld().getClosestCoverPosition(brain.getEntityOwner(), attackDir);
		
		this.cover.setCoverPos(coverPosition);
		this.cover.setAttackDir(attackDir);
		return getGoals().moveToCover(this.cover, brain);
	}

}
