/*
 * see license.txt 
 */
package seventh.ai.basic.strategy;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.ai.basic.Memory;
import seventh.game.BombTarget;

/**
 * A very simple strategy for a bot
 * 
 * @author Tony
 *
 */
public class PlantBombStrategy extends BombStrategy {

	/* (non-Javadoc)
	 * @see seventh.ai.BombStrategy#arrivedAtBomb(seventh.ai.Brain, seventh.game.BombTarget)
	 */
	@Override
	protected void arrivedAtBomb(Brain brain, BombTarget bomb) {				
		brain.getMotion().plantBomb(bomb);
		brain.getMemory().store("planting", true);	
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.BombStrategy#checkIfJobCompleted(seventh.ai.Brain, seventh.game.BombTarget)
	 */
	@Override
	protected boolean checkIfJobCompleted(Brain brain, BombTarget bomb) {
		if(!bomb.isAlive() || bomb.bombActive()) {
			Memory mem = brain.getMemory();
			mem.store("bomb_target", null);
			mem.store("planting", null);
			return true;
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.BombStrategy#isValidBombTarget(seventh.ai.Brain, seventh.game.BombTarget)
	 */
	@Override
	protected boolean isValidBombTarget(Brain brain, BombTarget bomb) {
		return bomb.isAlive() && !bomb.isBombAttached() && !bomb.bombActive() && !bomb.bombPlanting();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.BombStrategy#moveTowardsBomb(seventh.ai.Brain, seventh.game.BombTarget)
	 */
	@Override
	protected void moveTowardsBomb(Brain brain, BombTarget bomb) {
		Object defusing = brain.getMemory().get("planting");
		
		if(defusing == null) {
			Locomotion motion = brain.getMotion();
			motion.lookAt(bomb.getCenterPos());
			motion.moveTo(bomb.getCenterPos());							
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.BombStrategy#shouldFindTarget(seventh.ai.Brain)
	 */
	@Override
	protected boolean shouldFindTarget(Brain brain) {
		if(brain.getEntityOwner().isPlantingBomb()) {
			brain.getMemory().store("planting", null);
			return false;
		}
		return true;
	}
	
}
