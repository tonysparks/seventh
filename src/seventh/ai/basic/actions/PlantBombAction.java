/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.game.BombTarget;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class PlantBombAction extends AdapterAction {

	private BombTarget bomb;	
	
	/**
	 * 
	 */
	public PlantBombAction(BombTarget bomb) {
		this.bomb = bomb;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {				
//		if(!bomb.isTouching(brain.getEntityOwner())) {								
//			brain.getMotion().moveTo(bomb.getCenterPos());
//		}
//		
//		brain.getMotion().plantBomb(bomb);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#end(seventh.ai.basic.Brain)
	 */
	@Override
	public void end(Brain brain) {
		brain.getMotion().stopUsingHands();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
	 */
	@Override
	public void interrupt(Brain brain) {
		brain.getMotion().stopUsingHands();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#resume(seventh.ai.basic.Brain)
	 */
	@Override
	public void resume(Brain brain) {
		start(brain);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		if(bomb.bombActive()) {
			getActionResult().setSuccess();
		}
		else {
			Locomotion motion = brain.getMotion();
//			if(!bomb.isTouching(brain.getEntityOwner())) {
//				Vector2f dest = motion.getDestination();
//				if(dest == null || !dest.equals(bomb.getCenterPos())) {
//					if(!motion.isMoving()) {
//						motion.moveTo(bomb.getCenterPos());
//					}
//				}
//			}
//			else 
				if(!bomb.bombPlanting() || !motion.isPlanting()) {
				motion.plantBomb(bomb);
			}
			
			getActionResult().setFailure();
		}
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		/* error cases, we must finish then */
		if(!bomb.isAlive()) {
			return true;
		}
		
		/* check and see if we planted the bomb */
		if(bomb.bombActive()) {
			return true;
		}
		
		/* still working on being the hero */
		return false;
	}
	
	@Override
	public DebugInformation getDebugInformation() {	
		return super.getDebugInformation().add("target", this.bomb.getId());
	}
}
