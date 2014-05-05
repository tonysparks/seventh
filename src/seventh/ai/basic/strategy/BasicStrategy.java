/*
 * see license.txt 
 */
package seventh.ai.basic.strategy;

import java.util.EnumSet;
import java.util.List;
import java.util.Random;

import seventh.ai.basic.Brain;
import seventh.ai.basic.Locomotion;
import seventh.ai.basic.ThoughtProcess;
import seventh.game.Entity;
import seventh.game.PlayerEntity;
import seventh.game.SoundType;
import seventh.game.Team;
import seventh.game.events.SoundEmittedEvent;
import seventh.game.weapons.Bullet;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * A very simple strategy for a bot
 * 
 * @author Tony
 *
 */
public class BasicStrategy implements ThoughtProcess {

	/**
	 * Less important sounds
	 */
	private static final EnumSet<SoundType> lessImportant = EnumSet.of(SoundType.IMPACT_DEFAULT
																     , SoundType.IMPACT_FOLIAGE
																     , SoundType.IMPACT_METAL
																     , SoundType.IMPACT_WOOD);
	
	private Timer stuckTimer;
	private Vector2f previousPosition;
	
	/**
	 */
	public BasicStrategy() {
		this.stuckTimer = new Timer(false, 2000);
		this.previousPosition = new Vector2f();		
	}

	/* (non-Javadoc)
	 * @see seventh.ai.basic.Strategy#onKilled(seventh.ai.basic.Brain)
	 */
	@Override
	public void onKilled(Brain brain) {
		// TODO Auto-generated method stub
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.Strategy#onSpawn(seventh.ai.basic.Brain)
	 */
	@Override
	public void onSpawn(Brain brain) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Check if we are being attacked
	 * 
	 * @param brain
	 */
	protected void checkIfAttacked(Brain brain) {		
		Locomotion motion = brain.getMotion();
		
		Entity damager = brain.getSensors().getFeelSensor().getDamager();
		if( damager instanceof Bullet ) {
			Bullet bullet = (Bullet)damager;
			Entity owner = bullet.getOwner();
			motion.lookAt(owner.getPos());
			
//			mem.store(FeelSensor.FEEL, null);					
		}
	}
	
	
	/**
	 * Check if anything is in sight
	 * 
	 * @param brain
	 * @return true if something is attackable
	 */
	protected boolean checkSight(Brain brain) {
		Random random = brain.getWorld().getRandom();		
		Locomotion motion = brain.getMotion();
		PlayerEntity bot = brain.getEntityOwner();
		
		Team myTeam = bot.getTeam();
		
		boolean engagingEnemy = false;		
		
		List<PlayerEntity> entitiesInView = brain.getSensors().getSightSensor().getEntitiesInView();
		int size = entitiesInView.size();
		for(int i = 0; i < size; i++) {
			PlayerEntity playerEnt = entitiesInView.get(i);
			
			float dist = Vector2f.Vector2fDistanceSq(bot.getPos(), playerEnt.getPos());
			
			Team otherTeam = playerEnt.getTeam();
			if(otherTeam==null || myTeam==null || otherTeam.getId() != myTeam.getId()) {
				if(dist > 32*32*10 && dist < 32*32*14 ) {						
					if(random.nextInt(5) == 4 ) {
						motion.throwGrenade(playerEnt.getCenterPos());
					}							
				}						
				
				motion.attack(playerEnt);
				
				engagingEnemy = true;
				break;
			}
		
		}
		
		
		return engagingEnemy;
	}
	
	
	/**
	 * Check to see if any sounds are occurring by us
	 * 
	 * @param brain
	 * @return true if we are inspecting a sound
	 */
	protected boolean checkSounds(Brain brain) {		
		Locomotion motion = brain.getMotion();
		PlayerEntity bot = brain.getEntityOwner();
		Team myTeam = bot.getTeam();
		
		
		boolean investigatingSounds = false;		
		
		List<SoundEmittedEvent> sounds = brain.getSensors().getSoundSensor().getSounds();
		int size = sounds.size();
		
		SoundEmittedEvent closestSound = null;
		for(int i = 0; i < size; i++) {
			SoundEmittedEvent sound = sounds.get(i);
			long id = sound.getId();
			if( bot.getId() == id) {
				continue;
			}
			
			PlayerEntity personMakingSound = brain.getWorld().getPlayerById(id);
			if(personMakingSound != null && personMakingSound.getTeam().equals(myTeam)) {
				continue;
			}
			
			
			Vector2f soundPos = sound.getPos();
			if(closestSound == null) {
				closestSound = sound;
			}
			else {
				if(lessImportant.contains(closestSound.getSoundType()) && !lessImportant.contains(sound.getSoundType())) {
					closestSound = sound;
				}
				else {
					if(closestSound.getPos().lengthSquared() > soundPos.lengthSquared()) {					
						closestSound = sound;
					}		
				}						
			}
		}
		
		if(closestSound!=null) {
			motion.moveTo(closestSound.getPos());
			motion.lookAt(closestSound.getPos());
			
			investigatingSounds = true;
		}
	
		
		return investigatingSounds;
	}
	
	
	/**
	 * Check to see if our agent is stuck
	 * 
	 * @param timeStep
	 * @param brain
	 */
	protected void checkIfStuck(TimeStep timeStep, Brain brain) {
		Locomotion motion = brain.getMotion();
		PlayerEntity bot = brain.getEntityOwner();
		
		// anti stuck check
		if(Vector2f.Vector2fApproxEquals(previousPosition, bot.getPos(), 3.0f)) {						
			this.stuckTimer.update(timeStep);
			
			if(this.stuckTimer.isTime()) {
				motion.moveTo(brain.getWorld().getRandomSpot(bot));
				motion.scanArea();	
				
				this.stuckTimer.reset();
			}						
		}
		else {
			this.stuckTimer.reset();
		}
		this.previousPosition.set(bot.getPos());
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.ai.Strategy#think(seventh.shared.TimeStep, seventh.ai.Brain)
	 */	
	@Override
	public void think(TimeStep timeStep, Brain brain) {
		
		Locomotion motion = brain.getMotion();
				
		// #1
		// first check if we are getting shot
		// if so, lets look at the attacker.
		// if they are an enemy, we will attack 
		// (this will happen next frame)
		checkIfAttacked(brain);
		
		boolean engagingEnemy = false;
		boolean investigatingSounds = false;
				
		
		// if we are already engaged in action, lets skip this
		if(motion.handsInUse() && motion.isMoving() && motion.isStaringAtEntity()) {
			engagingEnemy = true;
		}
		
		// #2
		// Now lets check to see if any enemies
		// are in our sights.  If so, attack! 
		if(!engagingEnemy) {
			engagingEnemy = checkSight(brain);
		}
		
		// #3
		// Finally, lets listen for any sounds, and go
		// to the closest one
		if(!engagingEnemy) {
			investigatingSounds = checkSounds(brain);
		}
				
		// if we are not engaging an enemy or investigating sounds
		// lets move to a random spot on the map
		if(!engagingEnemy && !investigatingSounds) {
			if(!motion.isMoving()) {
				motion.wander();
			}
		}
		
		checkIfStuck(timeStep, brain);
	}
}
