/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.game.BombTarget;
import seventh.game.PlayerEntity;
import seventh.shared.TimeStep;

/**
 * Plants or defuses a bomb
 * 
 * @author Tony
 *
 */
public class BombAction extends AdapterAction {

	private boolean plant;	
	private BombTarget bombTarget;
	/**
	 * 
	 */
	public BombAction(BombTarget bombTarget, boolean plant) {
		this.bombTarget = bombTarget;
		this.plant = plant;		
		
		getActionResult().setFailure();
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#end(palisma.ai.Brain)
	 */
	@Override
	public void end(Brain brain) {	
		brain.getEntityOwner().unuse();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
	 */
	@Override
	public void interrupt(Brain brain) {
		brain.getEntityOwner().unuse();
		if(isFinished()) {
			getActionResult().setSuccess();
		}
	}

	/**
	 * @return true if the bomb is planted/defused
	 */
	protected boolean isFinished() {
		boolean isFinished = plant ? bombTarget.bombActive()||!bombTarget.isAlive() : !bombTarget.isBombAttached();
		return isFinished;
	}
	
	/* (non-Javadoc)
	 * @see palisma.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return isFinished();
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#update(palisma.ai.Brain, leola.live.TimeStep)
	 */	
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		PlayerEntity player = brain.getEntityOwner();
		player.use();
		
		if(isFinished()) {
			getActionResult().setSuccess();
		}
	}

	@Override
	public DebugInformation getDebugInformation() {	
		return super.getDebugInformation()
				.add("target", this.bombTarget.getId())
				.add("plant", plant);
	}
}
