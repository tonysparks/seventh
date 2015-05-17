/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import seventh.ai.basic.memory.SoundMemory;
import seventh.ai.basic.memory.SoundMemory.SoundMemoryRecord;
import seventh.game.PlayerEntity;
import seventh.game.SoundType;
import seventh.game.Team;
import seventh.game.events.SoundEmittedEvent;
import seventh.game.events.SoundEventPool;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * Listens for sounds
 * 
 * @author Tony
 *
 */
public class SoundSensor implements Sensor {

	/**
	 * Less important sounds
	 */
	private static final EnumSet<SoundType> lessImportant 
		= EnumSet.of(SoundType.IMPACT_DEFAULT
				   , SoundType.IMPACT_FOLIAGE
				   , SoundType.IMPACT_METAL
				   , SoundType.IMPACT_WOOD);
	
	private SoundMemory memory;
	private PlayerEntity entity;	
	private World world;
	
	private Timer updateEar;
			
	private List<SoundEmittedEvent> sounds;
	
	
	/**
	 * @param brain
	 */
	public SoundSensor(Brain brain) {
		this.memory = brain.getMemory().getSoundMemory();
		this.entity = brain.getEntityOwner();
		this.world = brain.getWorld();
				
		this.updateEar = new Timer(true, brain.getConfig().getSoundPollTime());
		this.updateEar.start();
		
		this.sounds = new ArrayList<SoundEmittedEvent>();
	}
	
	/**
	 * Gets the closest most interesting sound
	 * @return
	 */
	public SoundEmittedEvent getClosestSound() {		
		Team myTeam = entity.getTeam();				
		SoundMemoryRecord[] sounds = memory.getSoundRecords();
		
		SoundEmittedEvent closestSound = null;
		for(int i = 0; i < sounds.length; i++) {
			if(sounds[i].isValid()) {							
				SoundEmittedEvent sound = sounds[i].getSound();
				long id = sound.getId();
				
				/* ignore your own sounds */
				if(entity.getId() == id) {
					continue;
				}
				
				/* ignore your friendly sounds (this is a cheat, but what'evs) */
				PlayerEntity personMakingSound = world.getPlayerById(id);
				if(personMakingSound != null && personMakingSound.getTeam().equals(myTeam)) {
					continue;
				}
				
				Vector2f soundPos = sound.getPos();
				
				/* make the first sound the closest */
				if(closestSound == null) {
					closestSound = sound;
				}
				else {
					
					/* now lets check and see if there is a higher priority sound (such as foot steps, gun fire, etc.) */
					if(lessImportant.contains(closestSound.getSoundType()) && !lessImportant.contains(sound.getSoundType())) {
						closestSound = sound;
					}
					else {
						
						/* short of there being a higher priority sound, check the distance */
						if(closestSound.getPos().lengthSquared() > soundPos.lengthSquared()) {					
							closestSound = sound;
						}		
					}						
				}
			}
		}
		
		return closestSound;
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
		this.updateEar.update(timeStep);
		if(this.updateEar.isTime()) {		
			listen(timeStep);
		}
	}

	/**
	 * Listen for sounds
	 */
	private void listen(TimeStep timeStep) {	
			
		this.sounds.clear();
		SoundEventPool emittedSounds = world.getSoundEvents();
		if(emittedSounds.hasSounds()) {
			this.entity.getHeardSounds(emittedSounds, this.sounds);
			this.memory.hear(timeStep, sounds);
		}
		
	}
}
