/*
 * see license.txt 
 */
package seventh.ai.basic.actions.atom;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.ai.basic.actions.AdapterAction;
import seventh.game.PlayerEntity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public class StrafeAction extends AdapterAction {

	private Timer strafeTimer;
	private Vector2f dest;
	private int direction;
	/**
	 * 
	 */
	public StrafeAction() {		
		this.strafeTimer = new Timer(true, 1000);
		this.dest = new Vector2f();
		this.direction = 1;
	}
	
	/**
	 * @param destination the destination to set
	 */
	public void reset(Brain brain) {
		this.strafeTimer.stop();
		this.strafeTimer.start();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#start(seventh.ai.basic.Brain)
	 */
	@Override
	public void start(Brain brain) {
		this.strafeTimer.start();
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#interrupt(seventh.ai.basic.Brain)
	 */
	@Override
	public void interrupt(Brain brain) {
		this.strafeTimer.stop();
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
		Locomotion motion = brain.getMotion();
		if(motion.isMoving()) {
			getActionResult().setSuccess();
		}		
		
		PlayerEntity bot = brain.getEntityOwner();
		Vector2f vel = bot.getFacing();
		Vector2f.Vector2fPerpendicular(vel, dest);
		Vector2f.Vector2fMult(dest, direction, dest);
		bot.getVel().set(dest);
		
		System.out.println(dest);
		
		this.strafeTimer.update(timeStep);
		if(this.strafeTimer.isTime()) {
			this.strafeTimer.setEndTime(brain.getWorld().getRandom().nextInt(1000) + 500);
			this.strafeTimer.start();
			this.direction *= -1;
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.actions.AdapterAction#isFinished()
	 */
	@Override
	public boolean isFinished(Brain brain) {
		return !brain.getMotion().isMoving();	
	}
}
