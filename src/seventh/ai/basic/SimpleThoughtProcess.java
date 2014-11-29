/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.List;

import seventh.game.Bomb;
import seventh.game.BombTarget;
import seventh.game.Entity;
import seventh.game.PlayerEntity;
import seventh.game.SoundType;
import seventh.game.Team;
import seventh.game.events.SoundEmittedEvent;
import seventh.math.Vector2f;
import seventh.shared.Debugable;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * A simple thinking strategy which delegates interesting events to the {@link ThinkListener}
 * 
 * @author Tony
 *
 */
public class SimpleThoughtProcess implements ThoughtProcess {

	/**
	 * Listens for interesting events in a given thought process
	 * @author Tony
	 *
	 */
	public static interface ThinkListener extends Debugable {
				
		/**
		 * The agent has spawned
		 * @param brain
		 */
		void onSpawned(Brain brain);
		
		/**
		 * The agent has died
		 * 
		 * @param brain
		 */
		void onDeath(Brain brain);
		
		
		/**
		 * This agent is stuck.
		 * 
		 * @param timeStep
		 * @param brain
		 * @return true if we should stop processing
		 */
		boolean onStuck(TimeStep timeStep, Brain brain);
		
		/**
		 * This agent has been touched/attacked by another {@link Entity}
		 * @param timeStep
		 * @param brain
		 * @param toucher probably a bullet piercing this agents body
		 * @return true if we should stop processing
		 */
		boolean onTouched(TimeStep timeStep, Brain brain, Entity toucher);
		
		/**
		 * This agent sees enemies
		 * 
		 * @param timeStep
		 * @param brain
		 * @param enemies the list of enemies that want to mow down this agent
		 * @return true if we should stop processing
		 */
		boolean onSeeEnemies(TimeStep timeStep, Brain brain, List<PlayerEntity> enemies);
		
		/**
		 * The closest enemy sound to this agent
		 * @param timeStep
		 * @param brain
		 * @param closestSound the closest sound to this agent
		 * @return true if we should stop processing
		 */
		boolean onClosestSound(TimeStep timeStep, Brain brain, SoundEmittedEvent closestSound);
		
		/**
		 * The agent is within the blast radius of an active bomb
		 * @param timeStep
		 * @param brain
		 * @param target
		 * @return true if we should stop processing
		 */
		boolean onTooCloseToActiveBomb(TimeStep timeStep, Brain brain, BombTarget target);
		
		/**
		 * The start of the thinking process
		 * @param timeStep
		 * @param brain
		 * @return true if we should stop processing
		 */
		boolean onBeginThink(TimeStep timeStep, Brain brain);
		
		/**
		 * The end of thinking -- main processing logic should go here
		 * 
		 * @param timeStep
		 * @param brain
		 */
		void onEndThink(TimeStep timeStep, Brain brain);
	}
	
	/**
	 * Less important sounds
	 */
	private static final EnumSet<SoundType> lessImportant 
		= EnumSet.of(SoundType.IMPACT_DEFAULT
				   , SoundType.IMPACT_FOLIAGE
				   , SoundType.IMPACT_METAL
				   , SoundType.IMPACT_WOOD);
	
	/**
	 * Properties for checking if the bot
	 * is stuck
	 */
	private Timer stuckTimer;
	private long nextStuckCheck;
	private Vector2f previousPosition;
	
	/**
	 * Cached list of entities in view
	 */
	private List<PlayerEntity> attackableEntities;
	
	/**
	 * The actual thinking layer
	 */
	private ThinkListener thinkListener;
	
	/**
	 */
	public SimpleThoughtProcess(ThinkListener thinkListener, Brain brain) {
		this.stuckTimer = new Timer(false, 2000);
		this.previousPosition = new Vector2f();
		this.attackableEntities = new ArrayList<PlayerEntity>();
		this.thinkListener = thinkListener;
	}
	
	/**
	 * @param thinkListener the thinkListener to set
	 */
	public void setThinkListener(ThinkListener thinkListener) {
		this.thinkListener = thinkListener;
	}

	/**
	 * Sets the time between checks to see if an agent is stuck
	 * 
	 * @param timeToCheckMSec
	 */
	public void setCheckStuckMSec(long timeToCheckMSec) {
		this.stuckTimer.setEndTime(timeToCheckMSec);
		this.stuckTimer.reset();
	}
	
	/**
	 * Check if we are being attacked
	 * 
	 * @param brain
	 * @return the {@link Entity} who is attacking the agent.  Null if nothing is touching the agent.
	 */
	protected Entity checkIfAttacked(Brain brain) {				
		Entity damager = brain.getSensors().getFeelSensor().getDamager();
		return damager;
	}
	
	
	/**
	 * Check if anything is in sight
	 * 
	 * @param brain
	 * @return the list of attackable entities if something is attackable.  The list
	 * returned is cached, so future calls to this method will alter this List.
	 */
	protected List<PlayerEntity> checkSight(Brain brain) {
		PlayerEntity bot = brain.getEntityOwner();		
		Team myTeam = bot.getTeam();
			
		this.attackableEntities.clear();
		
		List<PlayerEntity> entitiesInView = brain.getSensors().getSightSensor().getEntitiesInView();
		int size = entitiesInView.size();
		for(int i = 0; i < size; i++) {
			PlayerEntity otherPlayer = entitiesInView.get(i);
			
			Team otherTeam = otherPlayer.getTeam();
			if(otherTeam==null || myTeam==null || otherTeam.getId() != myTeam.getId()) {
				this.attackableEntities.add(otherPlayer);
				break;
			}
		
		}
				
		return this.attackableEntities;
	}
	
	
	/**
	 * Check to see if any sounds are occurring by us
	 * 
	 * @param brain
	 * @return the {@link SoundEmittedEvent} of the closest non-friendly sound
	 */
	protected SoundEmittedEvent checkSounds(Brain brain) {				
		PlayerEntity bot = brain.getEntityOwner();
		Team myTeam = bot.getTeam();
		
		List<SoundEmittedEvent> sounds = brain.getSensors().getSoundSensor().getSounds();
		int size = sounds.size();
		
		SoundEmittedEvent closestSound = null;
		for(int i = 0; i < size; i++) {
			SoundEmittedEvent sound = sounds.get(i);
			long id = sound.getId();
			
			/* ignore your own sounds */
			if( bot.getId() == id) {
				continue;
			}
			
			/* ignore your friendly sounds (this is a cheat, but what'evs) */
			PlayerEntity personMakingSound = brain.getWorld().getPlayerById(id);
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
		
		return closestSound;
	}
	
	
	/**
	 * Check to see if our agent is stuck
	 * 
	 * @param timeStep
	 * @param brain
	 * @return true if we are stuck
	 */
	protected boolean checkIfStuck(TimeStep timeStep, Brain brain) {

		PlayerEntity bot = brain.getEntityOwner();
		
		this.nextStuckCheck -= timeStep.getDeltaTime();
		
		if(this.nextStuckCheck < 0) {
			this.nextStuckCheck = 1500;
			
			/* if we are still roughly within X amount of pixels from the last check (Y milliseconds),
			 * then we are declared stuck.
			 */
			if(Vector2f.Vector2fApproxEquals(previousPosition, bot.getPos(), 5.0f)) {		
				this.stuckTimer.start();
				this.stuckTimer.update(timeStep);
				
				if(this.stuckTimer.isTime()) {								
					this.stuckTimer.stop();
					return true;
				}						
			}
			else {
				this.stuckTimer.stop();
			}
			
		}
		
		this.previousPosition.set(bot.getPos());
		
		return false;
	}
	
	/**
	 * Check to see if the agent is too close to an active bomb
	 * 
	 * @param brain
	 * @return the {@link BombTarget} with an active bomb on it.  Null if not too close
	 */
	private BombTarget checkIfCloseToActiveBomb(Brain brain) {

		World world = brain.getWorld();
		if(!world.isOnOffense(brain.getEntityOwner().getTeam())) {
			return null;
		}
		
		List<BombTarget> targets = world.getBombTargetsWithActiveBombs();
		for(int i = 0; i < targets.size(); i++) {
			BombTarget target = targets.get(i);
			
			/* if this is an active bomb, let's see if
			 * we are too close for comfort
			 */													
			Zone zone = world.getZone(target.getCenterPos());
			if(zone != null) {
				Bomb bomb = target.getBomb();
				
				/* we are in the blast radius, so let's move to a safer location */
				if(bomb.getBlastRadius().intersects(brain.getEntityOwner().getBounds())) {
					return target;
				}
			}
			
		}		
		
		return null;
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.Strategy#onKilled(seventh.ai.basic.Brain)
	 */
	@Override
	public void onKilled(Brain brain) {		
		this.thinkListener.onDeath(brain);
	}
	
	/* (non-Javadoc)
	 * @see seventh.ai.basic.Strategy#onSpawn(seventh.ai.basic.Brain)
	 */
	@Override
	public void onSpawn(Brain brain) {
		this.thinkListener.onSpawned(brain);
	}
		
	/*
	 * (non-Javadoc)
	 * @see seventh.ai.Strategy#think(seventh.shared.TimeStep, seventh.ai.Brain)
	 */	
	@Override
	public void think(TimeStep timeStep, Brain brain) {

		/* if we are stuck, lets try to move some where */
		if (checkIfStuck(timeStep, brain) ) {
			if( this.thinkListener.onStuck(timeStep, brain) ) {
				return;
			}
		}

		/* let the thinker do some pre-processing */
		if(this.thinkListener.onBeginThink(timeStep, brain)) {
			return;
		}
		
		
		/* notify we are being attacked */
		Entity attacker = checkIfAttacked(brain);
		if(attacker != null) {
			if( this.thinkListener.onTouched(timeStep, brain, attacker)) {
				return;
			}
		}
		
	
		/* notify we see some bad guys */
		List<PlayerEntity> enemiesInSight = checkSight(brain);
		if(!enemiesInSight.isEmpty()) {
			if(this.thinkListener.onSeeEnemies(timeStep, brain, enemiesInSight)) {
				return;
			}
		}
		
		
		/* notify we hear some bad guy making noise */
		SoundEmittedEvent sound = checkSounds(brain);
		if(sound != null) {
			if(this.thinkListener.onClosestSound(timeStep, brain, sound)) {
				return;
			}
		}
		
		BombTarget target = checkIfCloseToActiveBomb(brain);
		if(target != null) {
			if(this.thinkListener.onTooCloseToActiveBomb(timeStep, brain, target)) {
				return;
			}
		}
		
		
		/* now let the thinker do some logic/goal processing */
		this.thinkListener.onEndThink(timeStep, brain);
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override 
	public DebugInformation getDebugInformation() {
		DebugInformation me = new DebugInformation();
		me.add("type", getClass().getSimpleName())
		  .add("stuck_timer", this.stuckTimer.getRemainingTime());
		
		Object[] ids = new Object[this.attackableEntities.size()];
		for(int i = 0; i < this.attackableEntities.size(); i++) {
			ids[i] = this.attackableEntities.get(i);
		}
		me.add("attackable_entities", ids);
		me.add("think_listener", this.thinkListener);
		
		return me;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDebugInformation().toString();
	}	
}
