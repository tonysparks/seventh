/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.game.Entity;
import seventh.game.Entity.OnDamageListener;
import seventh.game.PlayerEntity;
import seventh.shared.TimeStep;

/**
 * Anything touching us
 * 
 * @author Tony
 *
 */
public class FeelSensor implements Sensor, OnDamageListener {

	public static final String FEEL = "feelSensor";
	private static final long FEEL_MEMORY = 1; /* remember being shot for 5 seconds -- thug life */
	private Entity damager;
	private Memory memory;
	private long timeToFeel;
	
	/**
	 * @param brain
	 */
	public FeelSensor(Brain brain) {
		memory = brain.getMemory();
		PlayerEntity ent = brain.getEntityOwner();
		if(ent != null) {
			ent.onDamage = this;
		}		
	}
	
	/**
	 * @return the damager
	 */
	public Entity getDamager() {
		return damager;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.Sensor#reset(seventh.ai.Brain)
	 */
	@Override
	public void reset(Brain brain) {
		this.damager = null;
		brain.getEntityOwner().onDamage = this;				
	}
	
	/**
	 * Test for anything touching this entity.
	 */
	private void feel() {
		this.memory.store(FEEL, this.damager);
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Sensor#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		if(this.timeToFeel > 0) {
			this.timeToFeel -= timeStep.getDeltaTime();
		}
		else {
			this.damager = null;
		}
		
		feel();
		
	}
	
	/**
	 * Registered callback
	 * @param damager
	 * @param amount
	 */
	@Override
	public void onDamage(Entity damager, int amount) {
		this.damager = damager;
		this.timeToFeel = FEEL_MEMORY;
	}
}
