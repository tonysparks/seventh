/*
 * see license.txt 
 */
package seventh.ai.basic.actions;

import seventh.ai.basic.Brain;
import seventh.game.PlayerEntity;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public class ShootAtAction extends AdapterAction {

	private Timer shouldCheckLOF;
	private boolean inLOF;
	
	/**
	 */
	public ShootAtAction() {
		this.shouldCheckLOF = new Timer(true, 200);
		this.shouldCheckLOF.start();
		this.inLOF = false;
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#cancel()
	 */
	@Override
	public void cancel() {
		this.inLOF = false;
	}
		
	/* (non-Javadoc)
	 * @see palisma.ai.Action#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#update(palisma.ai.Brain, leola.live.TimeStep)
	 */	
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		PlayerEntity player = brain.getEntityOwner();
		
		this.shouldCheckLOF.update(timeStep);
		if(this.shouldCheckLOF.isTime()) {
			this.inLOF = brain.getTargetingSystem().targetInLineOfFire();
		}
		
		if(this.inLOF) {
			if(player.canFire()) {
				player.beginFire();
			}
			else {
				player.endFire();
			}
		}		
	}

	
	@Override
	public DebugInformation getDebugInformation() {	
		return super.getDebugInformation();
	}
}
