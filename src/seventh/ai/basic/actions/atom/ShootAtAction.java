/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import seventh.ai.basic.Brain;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.PlayerEntity;
import seventh.game.weapons.Weapon;
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
		Weapon weapon = brain.getEntityOwner().getInventory().currentItem();
		return !this.inLOF || (weapon==null || weapon.getBulletsInClip()==0);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		this.inLOF = brain.getTargetingSystem().currentTargetInLineOfFire();
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Action#update(palisma.ai.Brain, leola.live.TimeStep)
	 */	
	@Override
	public void update(Brain brain, TimeStep timeStep) {
		PlayerEntity player = brain.getEntityOwner();
		
		this.shouldCheckLOF.update(timeStep);
		if(this.shouldCheckLOF.isTime()) {
			this.inLOF = brain.getTargetingSystem().currentTargetInLineOfFire();
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
