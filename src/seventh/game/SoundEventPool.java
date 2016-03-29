/*
 * see license.txt 
 */
package seventh.game;

import seventh.game.events.SoundEmittedEvent;
import seventh.math.Vector2f;
import seventh.shared.SoundType;


/**
 * Pools the available sounds
 * 
 * @author Tony
 *
 */
public class SoundEventPool {

	private SoundEmittedEvent[] events;
	private int usedIndex = 0;
	
	/**
	 * 
	 */
	public SoundEventPool(int maxSounds) {
		this.events = new SoundEmittedEvent[maxSounds];
		for(int i = 0; i < this.events.length; i++) {
			this.events[i] = new SoundEmittedEvent(events, i, SoundType.MUTE, new Vector2f());
			this.events[i].setBufferIndex(i);
		}
		
		clear();
	}

	
	/**
	 * Emits a sound
	 * 
	 * @param id
	 * @param sound
	 * @param entityId
	 */
	public void emitSound(int id, SoundType sound, int entityId) {
		emitSound(id, sound, null, entityId);
	}
	
	/**
	 * Emits the sound
	 * 
	 * @param id
	 * @param sound
	 * @param pos
	 */
	public void emitSound(int id, SoundType sound, Vector2f pos) {
		emitSound(id, sound, pos, id);
	}
	
	/**
	 * Emits the sound
	 * 
	 * @param id
	 * @param sound
	 * @param pos
	 * @param entityId
	 */
	public void emitSound(int id, SoundType sound, Vector2f pos, int entityId) {
		if(usedIndex+1 < events.length) {
		
			SoundEmittedEvent event = events[++usedIndex];
			event.setId(id);
			event.setSoundType(sound);
			event.setPos(pos);
			event.setEntityId(entityId);
		}		
	}
	
	/**
	 * Emits the sound
	 * 
	 * @param event
	 */
	public void emitSound(SoundEmittedEvent event) {
		emitSound(event.getId(), event.getSoundType(), event.getPos(), event.getEntityId());
	}
	
	/**
	 * @return the number of sounds in the pool
	 */
	public int numberOfSounds() {
		return Math.max(0, this.usedIndex+1);
	}
	
	/**
	 * Clears out the used sounds
	 */
	public void clear() {
		this.usedIndex = -1;
	}
	
	/**
	 * @return if this pool as available sounds
	 */
	public boolean hasSounds() {
		return this.usedIndex >= 0;
	}
	/**
	 * The {@link SoundEmittedEvent} at the supplied index
	 * @param index
	 * @return the event
	 */
	public SoundEmittedEvent getSound(int index) {
		return events[index];
	}
	
	/**
	 * Appends the supplied pools to this contents
	 * @param pool
	 */
	public void set(SoundEventPool pool) {		
		for(int i = 0; i < pool.numberOfSounds(); i++) {
			emitSound(pool.events[i]);
		}
	}
}
