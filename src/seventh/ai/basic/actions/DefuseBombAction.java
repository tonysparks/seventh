/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.game.BombTarget;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Defuses a bomb
 * 
 * @author Tony
 *
 */
public class DefuseBombAction extends AdapterAction {

	private BombTarget bomb;
	/**
	 * 
	 */
	public DefuseBombAction(BombTarget bomb) {
		this.bomb = bomb;
		this.getActionResult().setFailure();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
	 */
	@Override
	public void interrupt(Brain brain) {
		brain.getMotion().stopUsingHands();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#end(seventh.ai.basic.Brain)
	 */
	@Override
	public void end(Brain brain) {
		brain.getMotion().stopUsingHands();
	}
	
	/**
	 * @return true if the bomb is defused
	 */
	protected boolean isDefused() {		
		/* check and see if we saved the day! */
		if(!bomb.bombActive() && !bomb.bombPlanting() && !bomb.isBombAttached()) {
			return true;
		}
		
		/* still working on being the hero */
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#update(seventh.ai.basic.Brain, seventh.shared.TimeStep)
	 */
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		if(isDefused()) {
			this.getActionResult().setSuccess();
		}
		else {
			Locomotion motion = brain.getMotion();
			if(!bomb.isTouching(brain.getEntityOwner())) {
				Vector2f dest = motion.getDestination();
				if(dest == null || !dest.equals(bomb.getCenterPos())) {
					motion.moveTo(bomb.getCenterPos());
				}
			}
			else if(!bomb.bombDisarming() || !motion.isDefusing()) {
				motion.defuseBomb(bomb);
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
		
		return isDefused();
	}
}
