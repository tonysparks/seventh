/*
 * see license.txt 
 */
package seventh.game;

import seventh.shared.State;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public abstract class EntityState implements State {

	private byte netValue;
	private final long durationMSec;
	private long timeMSec;
	
	/**
	 * 
	 */
	public EntityState(byte netValue, long durationMSec) {
		this.netValue = netValue;
		this.durationMSec =durationMSec;
		this.timeMSec = 0;
	}

	/* (non-Javadoc)
	 * @see palisma.shared.State#enter()
	 */
	@Override
	public void enter() {
		this.timeMSec = this.durationMSec;
	}


	/* (non-Javadoc)
	 * @see palisma.shared.State#exit()
	 */
	@Override
	public void exit() {
	}
	
	/* (non-Javadoc)
	 * @see palisma.shared.State#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.timeMSec -= timeStep.getDeltaTime();
	}
	
	/**
	 * @return the netValue
	 */
	public byte getNetValue() {
		return netValue;
	}
	
	/**
	 * @return this state is completed
	 */
	public boolean isDone() {
		return this.timeMSec <= 0;
	}


}
