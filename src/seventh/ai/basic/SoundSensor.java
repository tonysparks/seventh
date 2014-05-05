/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.List;

import seventh.game.PlayerEntity;
import seventh.game.events.SoundEmittedEvent;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class SoundSensor implements Sensor {

	public static final String HEARING = "hearing";
//	private static final long SIGHT_MEMORY = 5000;
	private static final long REFRESH_TIME = 500;
	
	private Memory memory;
	private PlayerEntity entity;	
	private World world;
	
	private long timeToHear;
//	private long timeToRemember;
			
	private List<SoundEmittedEvent> sounds;
	/**
	 * @param width
	 * @param height
	 */
	public SoundSensor(Brain brain, int width, int height) {
		this.memory = brain.getMemory();
		this.entity = brain.getEntityOwner();
		this.world = brain.getWorld();
				
		this.sounds = new ArrayList<SoundEmittedEvent>();
	}
	
	/**
	 * @return the sounds
	 */
	public List<SoundEmittedEvent> getSounds() {
		return sounds;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.Sensor#reset(seventh.ai.Brain)
	 */
	@Override
	public void reset(Brain brain) {
		this.sounds.clear();
		this.entity = brain.getEntityOwner();
	}

	/* (non-Javadoc)
	 * @see palisma.ai.Sensor#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		if(this.timeToHear > 0) {
			this.timeToHear -= timeStep.getDeltaTime();
		}		
	}

	/**
	 * Listen for sounds
	 */
	public void listen() {	
		if(timeToHear <= 0) 
		{			
			this.sounds.clear();
			List<SoundEmittedEvent> emittedSounds = world.getSoundEvents();
			if(!emittedSounds.isEmpty()) {
				this.entity.getHeardSounds(emittedSounds, this.sounds);
			}
			
			memory.store(HEARING, this.sounds);
			this.timeToHear = REFRESH_TIME;
		}
	}
}
