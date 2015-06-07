/*
 * see license.txt 
 */
package seventh.game;

import java.util.List;

import seventh.game.events.SoundEmittedEvent;
import seventh.game.events.SoundEventPool;
import seventh.game.net.NetEntity;
import seventh.game.net.NetPlayer;
import seventh.game.net.NetPlayerPartial;
import seventh.game.vehicles.Tank;
import seventh.game.vehicles.Vehicle;
import seventh.game.weapons.GrenadeBelt;
import seventh.game.weapons.Kar98;
import seventh.game.weapons.M1Garand;
import seventh.game.weapons.MP40;
import seventh.game.weapons.MP44;
import seventh.game.weapons.Pistol;
import seventh.game.weapons.Risker;
import seventh.game.weapons.RocketLauncher;
import seventh.game.weapons.Shotgun;
import seventh.game.weapons.Springfield;
import seventh.game.weapons.Thompson;
import seventh.game.weapons.Weapon;
import seventh.map.Map;
import seventh.map.Tile;
import seventh.map.Tile.SurfaceType;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Geom;
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * A controllable {@link Entity} by either AI or a Player.  
 * 
 * @author Tony
 *
 */
public class PlayerEntity extends Entity implements Controllable {

	/**
	 * The keys/actions that a player can make
	 * 
	 * @author Tony
	 *
	 */
	public static enum Keys {
		UP		(1<<0),
		DOWN	(1<<1),
		LEFT	(1<<2),
		RIGHT	(1<<3),
		WALK	(1<<4),
		FIRE	(1<<5),
		RELOAD	(1<<6),
		WEAPON_SWITCH_UP(1<<7),
		WEAPON_SWITCH_DOWN(1<<8),
		
		THROW_GRENADE(1<<9),
		
		SPRINT(1<<10),
		CROUCH(1<<11),
		
		USE(1<<12),
		DROP_WEAPON(1<<13),
		MELEE_ATTACK(1<<14),
		;
		
		private Keys(int value) {
			this.value = value;
		}
		
		private int value;
		/**
		 * @return the value
		 */
		public int getValue() {
			return value;
		}
		
		public boolean isDown(int keys) {
			return (value & keys) > 0;
		}
	}
	
	public static final int PLAYER_HEARING_RADIUS = 900;
	public static final int PLAYER_WIDTH = 24;//16;
	public static final int PLAYER_HEIGHT = 24;
	
	public static final int PLAYER_SPEED = 120;
	public static final int PLAYER_MIN_SPEED = 20;
	private static final int RUN_DELAY_TIME = 300;
	private static final int SPRINT_DELAY_TIME = 200;
	
	public static final float WALK_SPEED_FACTOR = 0.484f;
	public static final float SPRINT_SPEED_FACTOR = 1.60f; // 1.95f
	
	private static final int ENTERING_VEHICLE_TIME = 2500;
	private static final int EXITING_VEHICLE_TIME = 2000;
	
	private static final int RECOVERY_TIME = 2000;
	
	public static final byte MAX_STAMINA = 100;
	public static final float STAMINA_DECAY_RATE = 2;
	public static final float STAMINA_RECOVER_RATE = 0.5f;
	
	private NetPlayer player;
	private NetPlayerPartial partialPlayer;
	private Team team;
		
	private int previousKeys;
	private float previousOrientation;
	
	private Inventory inventory;
		
	private long invinceableTime;	
	private int lineOfSight;
	private int hearingRadius;
	private Rectangle hearingBounds, visualBounds;
	
	private float stamina;
	private boolean completedRecovery;
	
	protected Vector2f inputVel;
	private boolean firing;
	private long runTime, recoveryTime;
	private boolean wasSprinting;
	
	private Vector2f enemyDir;
	
	private BombTarget bombTarget;
	private Vehicle operating;
	
	private boolean isFlashlightOn;
	private long vehicleTime;
	
	
	/**
	 * @param position
	 * @param speed
	 * @param game
	 */
	public PlayerEntity(int id, Vector2f position, Game game) {
		super(id, position, PLAYER_SPEED, game, Type.PLAYER);
		
		this.player = new NetPlayer();
		this.player.id = id;
		
		this.partialPlayer = new NetPlayerPartial();
		this.partialPlayer.id = id;
				
		this.bounds.set(position, PLAYER_WIDTH, PLAYER_HEIGHT);
		this.inputVel = new Vector2f();
		this.enemyDir = new Vector2f();
				
		this.inventory = new Inventory();						
		this.hearingBounds = new Rectangle();
		
		this.stamina = MAX_STAMINA;
		
		this.visualBounds = new Rectangle(5000, 5000);
		
		setLineOfSight(WeaponConstants.DEFAULT_LINE_OF_SIGHT);
		setHearingRadius(PLAYER_HEARING_RADIUS);
	}
	
	/**
	 * Sets the players weapon class -- their default
	 * inventory.
	 * 
	 * @param weaponClass
	 */
	public void setWeaponClass(Type weaponClass) {	
		Weapon weapon = null;
		switch(weaponClass) {		
			case KAR98:
				weapon = new Kar98(game, this);
				break;		
			case MP40:
				weapon = new MP40(game, this);
				break;
			case MP44:
				weapon = new MP44(game, this);
				break;		
			case ROCKET_LAUNCHER:
				weapon = new RocketLauncher(game, this);
				break;
			case SHOTGUN:
				weapon = new Shotgun(game, this);
				break;
			case SPRINGFIELD:
				weapon = new Springfield(game, this);
				break;		
			case M1_GARAND:
				weapon = new M1Garand(game, this);
				break;
			case THOMPSON:
				weapon = new Thompson(game, this);
				break;			
			case RISKER: 
				weapon = new Risker(game, this);
				break;
			default:
				if(team != null) {
					if(team.getId() == Team.ALLIED_TEAM_ID) {
						weapon = new Thompson(game, this);
					}
					else {
						weapon = new MP40(game, this);
					}
				}
				
				break;		
		}
		
		this.inventory.clear();
		this.inventory.addItem(weapon);
		
		setupCommonWeapons();
		checkLineOfSightChange();
	}
		
	private void setupCommonWeapons() {		
		this.inventory.addItem(new GrenadeBelt(game, this));
		this.inventory.addItem(new Pistol(game, this));
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#kill(seventh.game.Entity)
	 */
	@Override
	public void kill(Entity killer) {	
		super.kill(killer);
		
		unuse();
		
		/* suicides don't leave weapons */
		if(killer != this) {
			dropItem(false);
		}
	}
	
	/**
	 * Drops the currently held item
	 * @param makeSound
	 */
	public void dropItem(boolean makeSound) {
		Weapon weapon = this.inventory.currentItem();
		if(weapon != null) {
			this.inventory.removeItem(weapon);
			this.inventory.nextItem();
			
			Vector2f weaponPos = new Vector2f(getFacing());
			Vector2f.Vector2fMA(getCenterPos(), weaponPos, 40, weaponPos);
			game.newDroppedItem(weaponPos, weapon);
			
			if(makeSound) {
				game.emitSound(getId(), SoundType.WEAPON_DROPPED, weaponPos);
			}
		}
	}
	
	/**
	 * Picks up an item
	 * 
	 * @param item
	 */
	public void pickupItem(Weapon weapon) {		
		Type type = weapon.getType();
		if(inventory.hasItem(type)) {			
			Weapon myWeapon = inventory.getItem(type);
			myWeapon.addAmmo(weapon.getTotalAmmo());
			game.emitSound(getId(), SoundType.AMMO_PICKUP, getPos());		
		}
		else {				
			inventory.addItem(weapon);
			weapon.setOwner(this);
			game.emitSound(getId(), SoundType.WEAPON_PICKUP, getPos());			
		}
	}
	
	/**
	 * @param lineOfSight the lineOfSight to set
	 */
	public void setLineOfSight(int lineOfSight) {
		this.lineOfSight = lineOfSight;
	}
	
	/**
	 * @param radius the hearing range
	 */
	public void setHearingRadius(int radius) {
		this.hearingRadius = radius;
	}
	
	/**
	 * @return the hearingRadius
	 */
	public int getHearingRadius() {
		return hearingRadius;
	}
	
	/**
	 * @return the lineOfSight
	 */
	public int getLineOfSight() {
		return lineOfSight;
	}
	
	/**
	 * @return the current held weapons distance
	 */
	public int getCurrentWeaponDistance() {
		Weapon weapon = inventory.currentItem();
		if(weapon != null) {
			return weapon.getBulletRange();
		}
		return 0;
	}
	
	/**
	 * @return the current held weapons distance squared
	 */
	public int getCurrentWeaponDistanceSq() {
		Weapon weapon = inventory.currentItem();
		if(weapon != null) {
			return weapon.getBulletRange() * weapon.getBulletRange();
		}
		return 0;
	}
	
	/**
	 * @return the stamina
	 */
	public byte getStamina() {
		return (byte)stamina;
	}
	
	/**
	 * @return the isFlashlightOn
	 */
	public boolean isFlashlightOn() {
		return isFlashlightOn;
	}
	
	/**
	 * @param invinceableTime the invinceableTime to set
	 */
	public void setInvinceableTime(long invinceableTime) {
		this.invinceableTime = invinceableTime;
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.Entity#damage(palisma.game.Entity, int)
	 */
	@Override
	public void damage(Entity damager, int amount) {
		if(this.invinceableTime<=0) {
			super.damage(damager, amount);
		}
	}

	/* (non-Javadoc)
	 * @see palisma.game.Entity#update(leola.live.TimeStep)
	 */
	@Override
	public boolean update(TimeStep timeStep) {
		updateInvincibleTime(timeStep);
		updateVehicleTime(timeStep);
		
		boolean blocked = false;		
		if(!isOperatingVehicle()) {
			
			updateVelocity(timeStep);
			blocked = super.update(timeStep);
					
			updateMovementSounds(timeStep);		
			updateStamina(timeStep);
			updateWeapons(timeStep);						
			updateBombTargetUse(timeStep);
		}
		else {
			moveTo(this.operating.getCenterPos());
		}
		
		return blocked;
	}

	/**
	 * Updates the {@link Entity#vel} with the inputs 
	 * 
	 * @param timeStep
	 */
	protected void updateVelocity(TimeStep timeStep) {
		this.vel.set(inputVel);
	}
	
	/**
	 * Handles the invincible time
	 * @param timeStep
	 */
	protected void updateInvincibleTime(TimeStep timeStep) {
		if(this.invinceableTime>0) {
			this.invinceableTime-=timeStep.getDeltaTime();
		}
	}
	
	/**
	 * Handles the vehicle time
	 * @param timeStep
	 */
	protected void updateVehicleTime(TimeStep timeStep) {
		if(this.vehicleTime>0) {
			this.vehicleTime -= timeStep.getDeltaTime();
		}		
		else {
			if(currentState == State.ENTERING_VEHICLE) {
				currentState = State.OPERATING_VEHICLE;					
			}
			if(currentState == State.EXITING_VEHICLE) {
				
				Vehicle vehicle = getVehicle();
				Vector2f newPos = game.findFreeRandomSpotNotIn(this, vehicle.getBounds(), vehicle.getOBB());
				
				/* we can't find an area around the tank to exit it,
				 * so lets just keep inside the tank
				 */
				if(newPos == null) {
					currentState = State.OPERATING_VEHICLE;
				}
				else {
					
					this.operating.stopOperating(this);
					this.operating = null;		
					setCanTakeDamage(true);
					
					currentState = State.IDLE;
					moveTo(newPos);
				}
			}
		}
	}
	
	/**
	 * Handles stamina
	 * 
	 * @param timeStep
	 */
	protected void updateStamina(TimeStep timeStep) {		
		if(currentState != State.SPRINTING) {
			stamina += STAMINA_RECOVER_RATE;
			if(stamina > MAX_STAMINA) {
				stamina = MAX_STAMINA;
			}
			
			/* if are not sprinting anymore, 
			 * start recovering 
			 */			
			if(recoveryTime > 0) {
				recoveryTime -= timeStep.getDeltaTime();
				if(recoveryTime <= 0) {
					completedRecovery = true;
				}
			}			
		}
		else {
			stamina -= STAMINA_DECAY_RATE;
			if(stamina < 0) {
				stamina = 0;
				currentState = State.RUNNING;
				
				recoveryTime = RECOVERY_TIME;
			}
			
//			float recoveryTimeNeeded = MAX_STAMINA/2f; 
//			if(stamina < recoveryTimeNeeded) {
//				// Take percentage of the stamina to time to recovery and adjust the
//				// recovery time accordingly
//				recoveryTime = RECOVERY_TIME - (long)(RECOVERY_TIME * (stamina / recoveryTimeNeeded)); 				
//			}
		}
				
	}
	
	
	/**
	 * Handles the weapon updates
	 * 
	 * @param timeStep
	 */
	protected void updateWeapons(TimeStep timeStep) {
		Weapon weapon = inventory.currentItem();
		if(weapon!=null) {
			if(firing) {
				beginFire();
			}			
			weapon.update(timeStep);
		}
		
		if(inventory.hasGrenades()) {
			GrenadeBelt grenades = inventory.getGrenades();
			grenades.update(timeStep);			
		}	
	}
	
	/**
	 * Checks to see if we need to stop planting/disarming a {@link BombTarget}
	 * 
	 * @param timeStep
	 */
	protected void updateBombTargetUse(TimeStep timeStep) {
		// if we are planting/defusing, make sure
		// we are still over the bomb
		if(this.bombTarget != null) {
			if(!this.bombTarget.isAlive()) {
				this.bombTarget = null;
			}
			else {
				Bomb bomb = this.bombTarget.getBomb();
				if(bomb != null && (bomb.isPlanting() || bomb.isDisarming()) ) {
					
					// if we are not, stop planting/defusing
					if(!bounds.intersects(this.bombTarget.getBounds())) {
						unuse();
					}				
				}
			}
		}
	}
	
	/**
	 * Handles making movement sounds
	 * 
	 * @param timeStep
	 */
	protected void updateMovementSounds(TimeStep timeStep) {
		/* make walking sounds */
		if( !inputVel.isZero() && (currentState==State.RUNNING||currentState==State.SPRINTING) ) {
			if ( runTime <= 0 ) {				
				Vector2f soundPos = getCenterPos();
				int x = (int)soundPos.x;
				int y = (int)soundPos.y;
				
				SurfaceType surface = game.getMap().getSurfaceTypeByWorld(x, y);
				if(surface != null) {
					game.emitSound(getId(), SurfaceTypeToSoundType.toSurfaceSoundType(surface) , soundPos);
				}
				else {
					game.emitSound(getId(), SoundType.SURFACE_NORMAL , soundPos);
				}
				
				if(currentState == State.SPRINTING) {
					if(stamina > 0) {
						runTime = SPRINT_DELAY_TIME;
						game.emitSound(getId(), SoundType.RUFFLE, soundPos);						
					}
					else {						
						runTime = RUN_DELAY_TIME;	
					}
					
				}
				else {
					runTime = RUN_DELAY_TIME;
				}
				
				
				/* if we are near end of stamina, breadth hard */
				if (stamina <= STAMINA_DECAY_RATE) {
					game.emitSound(getId(), SoundType.BREATH_HEAVY, soundPos);
				}
				
				/* if we have recovered enough stamina, let out a lite breadth */
				if(completedRecovery) {
					game.emitSound(getId(), SoundType.BREATH_LITE, soundPos);
					completedRecovery = false;
				}								
			}
			else {
				runTime -= timeStep.getDeltaTime();
			}
		}
		else {
			runTime=0;
		}
	}
	
	
	/* (non-Javadoc)
	 * @see palisma.game.Entity#calculateMovementSpeed()
	 */
	@Override
	protected int calculateMovementSpeed() {
		/* The player's speed is impacted by:
		 * 1) the state of the player (walking, running or sprinting)
		 * 2) the weapon he is currently wielding
		 */
		
		
		int mSpeed = this.speed;
		if(currentState==State.WALKING) {
			mSpeed = (int)( (float)this.speed * WALK_SPEED_FACTOR);
		}
		else if(currentState == State.SPRINTING) {
			if(stamina > 0) {
				mSpeed = (int)( (float)this.speed * SPRINT_SPEED_FACTOR);
			}
		}

		Weapon weapon = inventory.currentItem();
		if(weapon!=null) {
			mSpeed -= (weapon.getWeaponWeight());			
		}
		
		if(mSpeed < PLAYER_MIN_SPEED) {
			mSpeed = PLAYER_MIN_SPEED;
		}
		
		return mSpeed;
	}
	
	/**
	 * @return the inventory
	 */
	public Inventory getInventory() {
		return inventory;
	}
	
	/**
	 * @return the team
	 */
	public Team getTeam() {
		return team;
	}
	
	/**
	 * @param team the team to set
	 */
	public void setTeam(Team team) {
		this.team = team;
		if(this.team != null) {
			setWeaponClass(Type.UNKNOWN);
		}
	}
		
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#handleUserCommand(seventh.game.UserCommand)
	 */
	@Override
	public void handleUserCommand(int keys, float orientation) {
		if(isOperatingVehicle()) {
			
			/* The USE key is the one that enters/exit
			 * the vehicles
			 */
			if(Keys.USE.isDown(previousKeys) && !Keys.USE.isDown(keys)) {
				if(currentState==State.OPERATING_VEHICLE) {
					leaveVehicle();
				}
			}
			else {
			
				/* this additional check makes sure we are not
				 * entering/exiting the vehicle and controlling
				 * it
				 */
				if(currentState==State.OPERATING_VEHICLE) {
					this.operating.handleUserCommand(keys, orientation);
				}
			}
			
			previousKeys = keys;
		}
		else {
					
//			int previousKeys = (previousCommand != null) ? previousCommand.getKeys() : 0;
//			float prevOrientation = (previousCommand != null) ? previousCommand.getOrientation() : -1; 		
			
			if( Keys.FIRE.isDown(keys) ) {
				firing = true;
			}
			else if(Keys.FIRE.isDown(previousKeys)) {
				endFire();
				firing = false;
			}		
			
			if(Keys.THROW_GRENADE.isDown(keys)) {
				pullGrenadePin();
			}
			
			if(Keys.THROW_GRENADE.isDown(previousKeys) && !Keys.THROW_GRENADE.isDown(keys)) {
				throwGrenade();			
			}
			
			if(Keys.USE.isDown(keys)) {
				use();
			}
			else /*if (Keys.USE.isDown(previousKeys))*/ {
				unuse();	
			}
			
			if(Keys.MELEE_ATTACK.isDown(keys)) {
				meleeAttack();
			}
			else if(Keys.MELEE_ATTACK.isDown(previousKeys)) {
				doneMeleeAttack();
			}
			
			if(Keys.DROP_WEAPON.isDown(previousKeys) && !Keys.DROP_WEAPON.isDown(keys)) {			
				dropItem(true);
			}
			
			if(Keys.RELOAD.isDown(keys)) {
				reload();
			}
			
			if(Keys.WALK.isDown(keys)) {
				walk();
			}
			else {
				stopWalking();
			}
			
			
			/* ============================================
			 * Handles the movement of the character
			 * ============================================
			 */
						
			if(Keys.UP.isDown(keys)) {
				moveUp();
			}
			else if(Keys.DOWN.isDown(keys)) {
				moveDown();
			}
			else {
	//			inputVel.y = 0;
				noMoveY();
			}
			
			if(Keys.LEFT.isDown(keys)) {
				moveLeft();
			}
			else if (Keys.RIGHT.isDown(keys)) {
				moveRight();
			}
			else {
	//			inputVel.x = 0;
				noMoveX();
			}
				
			if(Keys.SPRINT.isDown(keys)) {
				if(!inputVel.isZero() && currentState != State.WALKING) {										
					sprint();
				}
			}
			else {
				stopSprinting();
//				this.wasSprinting = false;
//				
//				if(!inputVel.isZero() && currentState != State.WALKING) {
//					currentState = State.RUNNING;
//				}			
			}
			
			if(Keys.CROUCH.isDown(keys)) {
				crouch();
			}
			else {
				standup();
			}
			
			if(Keys.WEAPON_SWITCH_UP.isDown(keys)) {
				nextWeapon();
			}
			else if (Keys.WEAPON_SWITCH_DOWN.isDown(keys)) {
				prevWeapon();
			}
			
			if(previousOrientation != orientation) {
				setOrientation(orientation);
			}
			
			this.previousKeys = keys;
			this.previousOrientation = orientation;
		}
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#setOrientation(float)
	 */
	@Override
	public void setOrientation(float orientation) {
		if(inventory != null) {
			Weapon weapon = inventory.currentItem();
			if(weapon==null || !weapon.isMeleeAttacking()) {
				super.setOrientation(orientation);
			}
		}
		else {
			super.setOrientation(orientation);
		}
	}
	
	/**
	 * Go in the walking position (makes no sounds)
	 */	
	public void walk() {
		if(currentState!=State.DEAD) {
			currentState = State.WALKING;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#stopWalking()
	 */
	public void stopWalking() {
		if(currentState==State.WALKING) {
			currentState = State.RUNNING;
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#crouch()
	 */
	public void crouch() { 
		if(currentState == State.IDLE) {			
			game.emitSound(getId(), SoundType.RUFFLE, getCenterPos());								
			this.currentState = State.CROUCHING;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#standup()
	 */
	public void standup() {
		if(currentState==State.CROUCHING) {
			game.emitSound(getId(), SoundType.RUFFLE, getCenterPos());
			this.currentState = State.IDLE;
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#sprint()
	 */
	public void sprint() {
		
		/*
		 * We only allow sprinting in very special cases:
		 * 1) you are not dead
		 * 2) you have enough stamina
		 * 3) you are not firing your weapon
		 * 4) you are not currently using a Rocket Launcher
		 * 5) recovery time has been met
		 */
		
		if(currentState!=State.DEAD &&				
		   stamina > 0 &&
		   !firing &&
		   !wasSprinting &&		   
		   recoveryTime <= 0) {		
		
			Weapon weapon = this.inventory.currentItem();
			if(weapon == null || !weapon.getType().equals(Type.ROCKET_LAUNCHER)) {			
				if(currentState!=State.SPRINTING) {
					game.emitSound(getId(), SoundType.RUFFLE, getCenterPos());
				}
				
				currentState = State.SPRINTING;				
				return;
			}
		}
		
		wasSprinting = true;
		currentState = State.RUNNING;
	}
	
	public void stopSprinting() {
		this.wasSprinting = false;
		
		if(!inputVel.isZero() && currentState != State.WALKING) {
			currentState = State.RUNNING;
		}	
	}
	
	/**
	 * @return true if we are walking
	 */
	public boolean isWalking() {
		return this.currentState == State.WALKING;
	}
	
	/**
	 * @return true if we are running
	 */
	public boolean isRunning() {
		return this.currentState == State.RUNNING;
	}
	
	/**
	 * @return true if we are sprinting
	 */
	public boolean isSprinting() {
		return this.currentState == State.SPRINTING;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#reload()
	 */	
	public boolean reload() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon!=null) {
			return weapon.reload();
		}
		return false;
	}
	
	/**
	 * @return true if we are currently reloading a weapon
	 */
	public boolean isReloading() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon!=null) {
			return weapon.isReloading();
		}
		return false;
	}
	
	/**
	 * @return true if we are currently switching weapons
	 */
	public boolean isSwitchingWeapon() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon!=null) {
			return weapon.isSwitchingWeapon();
		}
		return false;
	}
	
	/**
	 * @return true if we are currently melee attacking
	 */
	public boolean isMeleeAttacking() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon!=null) {
			return weapon.isMeleeAttacking();
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#meleeAttack()
	 */
	public boolean meleeAttack() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon != null) {
			return weapon.meleeAttack();
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#doneMeleeAttack()
	 */
	public void doneMeleeAttack() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon != null) {
			weapon.doneMelee();	
		}
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#beginFire()
	 */
	public boolean beginFire() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon!=null) {
			return weapon.beginFire();
		}
		return false;
	}
	
		
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#endFire()
	 */	
	public boolean endFire() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon!=null) {
			return weapon.endFire();
		}
		return false;
	}
	
	/**
	 * @return true if the weapon is being fired
	 */
	public boolean isFiring() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon!=null) {
			return weapon.isFiring();
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#pullGrenadePin()
	 */	
	public boolean pullGrenadePin() {
		if(inventory.hasGrenades()) {
			GrenadeBelt grenades = inventory.getGrenades();
			return grenades.pullPin();			
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#throwGrenade()
	 */	
	public boolean throwGrenade() {
		if(inventory.hasGrenades()) {
			GrenadeBelt grenades = inventory.getGrenades();
			return grenades.throwGrenade();			
		}
		return false;
	}
	
	/**
	 * @return true if the current weapon can fire
	 */
	public boolean canFire() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon!=null) {
			return weapon.canFire();
		}
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#moveOrientation(float)
	 */	
	public void moveOrientation(float value) {
		this.setOrientation(value);
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#noMoveY()
	 */	
	public void noMoveY() {
		vel.y = 0;
		inputVel.y = 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#moveUp()
	 */	
	public void moveUp() {
		vel.y = -1;
		inputVel.y = -1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#moveDown()
	 */	
	public void moveDown() {
		vel.y = 1;
		inputVel.y = 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#noMoveX()
	 */	
	public void noMoveX() {
		vel.x = 0;
		inputVel.x = 0;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#moveLeft()
	 */	
	public void moveLeft() {
		vel.x = -1;
		inputVel.x = -1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#moveRight()
	 */	
	public void moveRight() {
		vel.x = 1;
		inputVel.x = 1;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#nextWeapon()
	 */	
	public void nextWeapon() {
		Weapon weapon = inventory.nextItem();
		if(weapon!=null) {
			weapon.setSwitchingWeaponState();
			checkLineOfSightChange();
		}
		
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#prevWeapon()
	 */	
	public void prevWeapon() {
		Weapon weapon = inventory.prevItem();
		if(weapon!=null) {
			weapon.setSwitchingWeaponState();
			checkLineOfSightChange();
		}
		
	}
	
	
	/**
	 * Determines if this entity is currently planting a bomb
	 * @return true if planting.
	 */
	public boolean isPlantingBomb() {
		if(this.bombTarget != null) {
			Bomb bomb = this.bombTarget.getBomb();
			if(bomb != null && bomb.isPlanting() ) {				
				return bounds.intersects(this.bombTarget.getBounds());	
			}
		}
		return false;
	}
	
	/**
	 * Determines if this entity is currently defusing a bomb
	 * @return true if defusing.
	 */
	public boolean isDefusingBomb() {
		if(this.bombTarget != null) {
			Bomb bomb = this.bombTarget.getBomb();
			if(bomb != null && bomb.isDisarming() ) {				
				return bounds.intersects(this.bombTarget.getBounds());	
			}
		}
		return false;
	}
	
	/**
	 * Determines if this entity is currently throwing a grenade (the pin has been
	 * pulled)
	 * @return true if pin is pulled on the grenade
	 */
	public boolean isThrowingGrenade() {
		return this.inventory.getGrenades().isPinPulled();
	}
	
	/**
	 * Use can be used for either planting a bomb, or disarming it.
	 */	
	public void use() {
		if(isOperatingVehicle()) {	
			if(vehicleTime <= 0) {
				leaveVehicle();				
			}
		}
		else {
		
			if(this.bombTarget == null) {		
				this.bombTarget = game.getCloseBombTarget(this);
			}
			
			if(this.bombTarget == null) {
				Vehicle vehicle = game.getCloseOperableVehicle(this);
				if(vehicle != null) {
					if(vehicleTime <= 0) {
						operateVehicle(vehicle);
					}
				}
			}
		}		
	}
	
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.Controllable#unuse()
	 */	
	public void unuse() {
		if(this.bombTarget != null) {
			Bomb bomb = this.bombTarget.getBomb();
			if(bomb != null) {
				if(bomb.isPlanting()) {
					bomb.stopPlanting();
					bomb.softKill();
					
					this.bombTarget.reset();
				}
				else if(bomb.isDisarming()) {
					bomb.stopDisarming();
				}				
			}
			
			this.bombTarget = null;
		}
	}
	
	
	/**
	 * Updates the line of sight (LOS) depending
	 * on the current {@link Weapon}
	 */
	private void checkLineOfSightChange() {
		Weapon weapon = inventory.currentItem();
		if(weapon !=null) {
			setLineOfSight(weapon.getLineOfSight());
		}
	}
	
	/**
	 * Determines if both {@link PlayerEntity} are on the same team.
	 * @param other
	 * @return true if both {@link PlayerEntity} are on the same team
	 */
	public boolean isOnTeamWith(PlayerEntity other) {
		Team othersTeam = other.getTeam();
		if(othersTeam != null) {
			if(this.team != null) {
				return this.team.getId() == othersTeam.getId();
			}
		}
		
		return false;
	}
	
	/**
	 * If this {@link PlayerEntity} is operating a {@link Vehicle}
	 * @return true if operating a {@link Vehicle}
	 */
	public boolean isOperatingVehicle() {
		return this.operating != null && this.operating.isAlive();
	}
	
	private void beginLeaveVehicle() {
		this.vehicleTime = EXITING_VEHICLE_TIME;
		this.currentState = State.EXITING_VEHICLE;
	}
	
	/**
	 * Leaves the {@link Vehicle}
	 */
	public void leaveVehicle() {
		beginLeaveVehicle();
	}
	
	/**
	 * Operates the vehicle
	 * 
	 * @param vehicle
	 */
	public void operateVehicle(Vehicle vehicle) {
		this.operating = vehicle;
		this.operating.operate(this);
		this.vehicleTime = ENTERING_VEHICLE_TIME;
		this.currentState = State.ENTERING_VEHICLE;
		setCanTakeDamage(false);
	}
	
	/**
	 * @return the {@link Vehicle} this {@link PlayerEntity} is operating
	 */
	public Vehicle getVehicle() {
		return this.operating;
	}
	
	/**
	 * Retrieves the sounds heard by an Entity
	 * @param soundEvents
	 * @param soundsHeard (the out parameter)
	 * @return the same instance as soundsHeard, just returned for convenience
	 */
	public List<SoundEmittedEvent> getHeardSounds(SoundEventPool soundEvents, List<SoundEmittedEvent> soundsHeard) {
		
		Vector2f pos = getCenterPos();
		// NOTE: this is for performance reasons only, this is not thread-safe!!!
		int radius = getHearingRadius();
		this.hearingBounds.set( (int)pos.x - (radius/2), (int)pos.y - (radius/2), radius, radius);
		
		int size = soundEvents.numberOfSounds();		
		for(int i = 0; i < size; i++) {
			SoundEmittedEvent event = soundEvents.getSound(i);
			if(this.hearingBounds.contains(event.getPos())) {
				soundsHeard.add(event);
			}
		}
				
		return soundsHeard;
	}
	
	/**
	 * Given the game state, retrieve the {@link Entity}'s in the current entities view.
	 * @param game
	 * @return a list of {@link Entity}s that are in this players view
	 */
	public List<Entity> getEntitiesInView(Game game) {
		/*
		 * Calculate all the visuals this player can see
		 */
		Map map = game.getMap();
		Entity[] entities = game.getEntities();
		List<Entity> entitiesInView = game.aEntitiesInView;
		
		Vector2f centerPos = getCenterPos();
		if(isOperatingVehicle()) {
			// TODO clean up
			Tank vehicle = (Tank)getVehicle();
			// TODO vehicle line of sight
			Geom.calculateLineOfSight(game.aTiles, vehicle.getCenterPos(), vehicle.getTurretFacing(), WeaponConstants.TANK_DEFAULT_LINE_OF_SIGHT, map, getHeightMask());
		}
		else {
							
			Geom.calculateLineOfSight(game.aTiles, centerPos, getFacing(), getLineOfSight(), map, getHeightMask());						
		}
		this.visualBounds.centerAround(centerPos);
		
		for(int i = 0; i < entities.length; i++) {
			Entity ent = entities[i];
			if(ent==null /*|| !ent.isAlive()*/) {
				continue;
			}
			
			Type entType = ent.getType();
			boolean isCalculatedEntity = entType==Type.PLAYER;//||entType==Type.GRENADE||entType==Type.NAPALM_GRENADE;
			
			if(isCalculatedEntity && game.isEnableFOW()) {				
				if(ent.getId() != id) {
					Vector2f pos = ent.getCenterPos();
					
					Vector2f.Vector2fSubtract(pos, centerPos, this.enemyDir);
					Vector2f.Vector2fNormalize(this.enemyDir, this.enemyDir);
					
					if(!game.isEntityReachable(ent, centerPos, this.enemyDir)) {
						continue;
					}
					
//					if(map.lineCollides(pos, centerPos)) {
//						continue;
//					}								
					
					int px = (int)pos.x;
					int py = (int)pos.y;
					
					// check center of entity
					Tile tile = map.getWorldTile(0, px, py);
					if(tile!=null) {
						if (tile.getMask() > 0) {
							entitiesInView.add(ent);
							continue;							
						}
					}												
											
					// make this a rectangle
					int width = ent.getBounds().width; // 4
					int height = ent.getBounds().height; // 4
					
					// offset a bit to because for whatever
					// reason entities butted against the lower right
					// corner become hidden
					px = (int)ent.getPos().x;//width/4; // 3
					py = (int)ent.getPos().y;//+height/4;
					
					
					// check upper right corner
					tile = map.getWorldTile(0, px+width, py);
					if(tile!=null) {
						if (tile.getMask() > 0) {
							//tile.getCollisionMask().pointCollide(hearingBounds, px, py)
							entitiesInView.add(ent);
							continue;
						}
					}		
						
					// check lower right corner
					tile = map.getWorldTile(0, px+width, py+height);
					if(tile!=null) {
						if (tile.getMask() > 0) {
							entitiesInView.add(ent);
							continue;
				
						}
					}		
					
					// check lower left corner
					tile = map.getWorldTile(0, px, py+height);
					if(tile!=null) {
						if (tile.getMask() > 0) {
							entitiesInView.add(ent);
							continue;
						}
					}											
					
					// check upper left corner
					tile = map.getWorldTile(0, px, py);
					if(tile!=null) {
						if (tile.getMask() > 0) {
							entitiesInView.add(ent);
							continue;				
						}
					}		
				
				}
			}
			else /*if (!game.isEnableFOW())*/ {		
				/* We don't always send every entity over the wire */
				Type type = ent.getType();
				switch(type) {
					case BOMB: 
						Bomb bomb = (Bomb)ent;
						if(bomb.isPlanted()) {
							entitiesInView.add(ent);
						}
						break;
					case LIGHT_BULB:
					case BOMB_TARGET:
						/* don't add */
						break;
					default: {
						if(visualBounds.intersects(ent.getBounds())) {
							entitiesInView.add(ent); 
						}
					}
				}
			}					
		}
		
		return entitiesInView;
	}
		
	/* (non-Javadoc)
	 * @see palisma.game.Entity#getNetEntity()
	 */
	@Override
	public NetEntity getNetEntity() {
		return getNetPlayerPartial();
	}
	
	
	/**
	 * Read the state
	 * @return the {@link NetPlayer}
	 */
	public NetPlayer getNetPlayer() {				
		setNetEntity(player);
		player.orientation = (short) Math.toDegrees(this.orientation);

		player.state = currentState.netValue();				
		player.grenades = (byte)inventory.getGrenades().getNumberOfGrenades();
		
		player.health = (byte)getHealth(); 
//		player.events = (byte)getEvents();
		player.stamina = getStamina();
		setEvents(0);
		
		player.isOperatingVehicle = isOperatingVehicle();
		if(player.isOperatingVehicle) {
			player.vehicleId = this.operating.getId(); 
		}
		
		
		Weapon weapon = inventory.currentItem();
		if(weapon!=null) {
			player.weapon = weapon.getNetWeapon();
		}
		else {
			player.weapon = null;
		}
		
		return player;
	}	
	
	
	/**
	 * Gets the Partial network update.  The {@link NetPlayerPartial} is used for Players
	 * that are NOT the local player
	 * @return the {@link NetPlayerPartial}
	 */
	public NetPlayerPartial getNetPlayerPartial() {
		setNetEntity(partialPlayer);
		partialPlayer.orientation = getNetOrientation();
		
		partialPlayer.state = currentState.netValue();								
		partialPlayer.health = (byte)getHealth(); 
		
		player.isOperatingVehicle = isOperatingVehicle();
		if(player.isOperatingVehicle) {
			player.vehicleId = this.operating.getId(); 
		}
		
		Weapon weapon = inventory.currentItem();
		if(weapon!=null) {
			partialPlayer.weapon = weapon.getNetWeapon();
		}
		else {
			partialPlayer.weapon = null;
		}
		
		return partialPlayer;
	}
}
