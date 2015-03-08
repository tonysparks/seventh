/*
 * see license.txt 
 */
package seventh.ai.basic;

import seventh.ai.basic.memory.FeelMemory;
import seventh.ai.basic.memory.FeelMemory.FeelMemoryRecord;
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

	private FeelMemory memory;
	private TimeStep timeStep;
	private Brain brain;
	
	/**
	 * @param brain
	 */
	public FeelSensor(Brain brain) {
		this.brain = brain;
		this.memory = brain.getMemory().getFeelMemory();
		
		PlayerEntity ent = brain.getEntityOwner();
		if(ent != null) {
			ent.onDamage = this;
		}		
	}
	
	/**
	 * Retrieves the most recent attacker
	 * 
	 * @return Retrieves the most recent attacker
	 */
	public Entity getMostRecentAttacker() {
		FeelMemoryRecord[] records = memory.getFeelRecords();
		Entity recentAttacker = null;
		long mostRecentTime = Long.MAX_VALUE;
		for(int i = 0; i < records.length; i++) {
			if(records[i].isValid()) {
				
				if (recentAttacker == null ||
				    mostRecentTime > records[i].getTimeFeltAgo()) {
					
					mostRecentTime = records[i].getTimeFeltAgo();
					recentAttacker = records[i].getDamager();
				}
			}
		}
		
		return recentAttacker;
	}
		
	/* (non-Javadoc)
	 * @see seventh.ai.Sensor#reset(seventh.ai.Brain)
	 */
	@Override
	public void reset(Brain brain) {
		this.memory.clear();
		brain.getEntityOwner().onDamage = this;				
	}
	
	

	/* (non-Javadoc)
	 * @see palisma.ai.Sensor#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.timeStep  = timeStep;
	}
	
	/**
	 * Registered callback
	 * @param damager
	 * @param amount
	 */
	@Override
	public void onDamage(Entity damager, int amount) {
		if( damager != null && !this.brain.getPlayer().isTeammateWith(damager.getId())) {		
			this.memory.feel(this.timeStep, damager);
		}
	}
}
