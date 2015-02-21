/*
 * see license.txt 
 */
package seventh.ai.basic.memory;

import seventh.game.Entity;
import seventh.shared.SeventhConstants;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

/**
 * @author Tony
 *
 */
public class FeelMemory implements Updatable {

	/**
	 * Feeling memory record
	 * 
	 * @author Tony
	 *
	 */
	public static class FeelMemoryRecord {
		private final long expireTime;
		
		private Entity damager;			
		private long timeFelt;
		private long timeFeltAgo;
		
		private boolean isValid;
		
		/**
		 * @param expireTime
		 */
		public FeelMemoryRecord(long expireTime) {
			this.expireTime = expireTime;						
			this.isValid = false;
		}
		
		
		/**
		 * Feel the pain
		 * 
		 * @param timeStep
		 * @param damager
		 */
		public void feelDamage(TimeStep timeStep, Entity damager) {
			this.damager = damager;
			this.timeFelt = timeStep.getGameClock();			
			this.isValid = true;
			
		}
		
		/**
		 * @return the isValid
		 */
		public boolean isValid() {
			return isValid;
		}
		
		/**
		 * Checks if this memory record has expired
		 * 
		 * @param timeStep
		 */
		public void checkExpired(TimeStep timeStep) {
			this.timeFeltAgo = timeStep.getGameClock() - this.timeFelt;
			if(isExpired(timeStep)) {
				expire();
			}
		}
		
		/**
		 * Expire this record
		 */
		public void expire() {
			this.damager = null;														
			this.isValid = false;
		}
		
		/**
		 * If this entity is expired
		 * 
		 * @param timeStep
		 * @return
		 */
		public boolean isExpired(TimeStep timeStep) {
			return this.damager == null ||				  
				  (timeStep.getGameClock() - this.timeFelt > this.expireTime);
		}
		
		/**
		 * @return the damager
		 */
		public Entity getDamager() {
			return damager;
		}
		
		/**
		 * @return the timeFelt
		 */
		public long getTimeFelt() {
			return timeFelt;
		}
		
		/**
		 * @return the timeFeltAgo
		 */
		public long getTimeFeltAgo() {
			return timeFeltAgo;
		}
		
	}
	
	private FeelMemoryRecord[] feelingRecords;
	
	
	
	/**
	 * 
	 */
	public FeelMemory(long expireTime) {	
		this.feelingRecords = new FeelMemoryRecord[SeventhConstants.MAX_PLAYERS];
		for(int i = 0; i < this.feelingRecords.length; i++) {
			this.feelingRecords[i] = new FeelMemoryRecord(expireTime);
		}		
	}
	
	

	/* (non-Javadoc)
	 * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		updateFeelingMemory(timeStep);				
	}
	
	/**
	 * Expire this memory
	 */
	public void clear() {
		for(int i = 0; i < this.feelingRecords.length; i++) {
			this.feelingRecords[i].expire();			
		}
	}
	
	private void updateFeelingMemory(TimeStep timeStep) {
		for(int i = 0; i < this.feelingRecords.length; i++) {
			this.feelingRecords[i].checkExpired(timeStep);			
		}
	}
	
	/**
	 * @return the feel records
	 */
	public FeelMemoryRecord[] getFeelRecords() {
		return feelingRecords;
	}
	
	
	/**
	 * Feel damage
	 * 
	 * @param timeStep
	 * @param event
	 */
	public void feel(TimeStep timeStep, Entity damager) {
		long oldest = 0;
		int oldestIndex = 0;
		
		for(int i = 0; i < this.feelingRecords.length; i++) {
			if(!this.feelingRecords[i].isValid()) {
				oldestIndex = i;
				break;
			}
			else {
				/* replace with oldest sound */					
				if( oldest < this.feelingRecords[i].getTimeFeltAgo()) {
					oldest = this.feelingRecords[i].getTimeFeltAgo();
					oldestIndex = i;
				}
			}
		}
		
		this.feelingRecords[oldestIndex].feelDamage(timeStep, damager);
	}

}
