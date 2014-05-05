/*
 * see license.txt 
 */
package seventh.game;

import java.util.List;

import seventh.game.events.SoundEmittedEvent;
import seventh.game.net.NetEntity;
import seventh.game.net.NetPlayer;
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
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Geom;
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * @author Tony
 *
 */
public class PlayerEntity extends Entity {

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
	
	private UserCommand previousCommand;
	
	private Inventory inventory;
	
	public static final int PLAYER_HEARING_RADIUS = 900;
	public static final int PLAYER_WIDTH = 24;//16;
	public static final int PLAYER_HEIGHT = 24;
	
	public static final int PLAYER_SPEED = 120;
	public static final int PLAYER_MIN_SPEED = 20;
	private static final int RUN_DELAY_TIME = 300;
	private static final int SPRINT_DELAY_TIME = 200;
	
	public static final byte MAX_STAMINA = 100;
	public static final float STAMINA_DECAY_RATE = 2;
	public static final float STAMINA_RECOVER_RATE = 0.5f;
	
	private NetPlayer player;
	private Team team;
	
	private long invinceableTime;	
	private int lineOfSight;
	private int hearingRadius;
	private Rectangle hearingBounds, visualBounds;
	
	private float stamina;
	
	protected Vector2f inputVel;
	private boolean firing;
	private long runTime;
	
	private Vector2f enemyDir;
	
	private BombTarget bombTarget;
	
	private boolean isFlashlightOn;
	private long flashlightToggleTime;
	
	/**
	 * @param position
	 * @param speed
	 * @param game
	 */
	public PlayerEntity(int id, Vector2f position, Game game) {
		super(id, position, PLAYER_SPEED, game, Type.PLAYER);
		
		this.player = new NetPlayer();
		this.player.id = id;
				
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
					if(team.getId() == Team.ALLIED_TEAM) {
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
		if(killer != this) 
		{
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
		if(this.invinceableTime>0) {
			this.invinceableTime-=timeStep.getDeltaTime();
		}
		
		if(this.flashlightToggleTime>0) {
			this.flashlightToggleTime-=timeStep.getDeltaTime();
		}
		
		this.vel.set(inputVel);
				
		boolean blocked = super.update(timeStep);
		
		
		makeMovementSounds(timeStep);
		
		if(currentState !=State.SPRINTING) {
			stamina += STAMINA_RECOVER_RATE;
			if(stamina > MAX_STAMINA) {
				stamina = MAX_STAMINA;
			}
		}
		else {
			stamina -= STAMINA_DECAY_RATE;
			if(stamina < 0) {
				stamina = 0;
			}
		}
		
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
		
		return blocked;
	}

	/**
	 * Handles making movement sounds
	 * 
	 * @param timeStep
	 */
	protected void makeMovementSounds(TimeStep timeStep) {
		/* make walking sounds */
		if( !inputVel.isZero() && (currentState==State.RUNNING||currentState==State.SPRINTING) ) {
			if ( runTime <= 0 ) {				
				Vector2f cpos = getCenterPos();
				int x = (int)cpos.x;
				int y = (int)cpos.y;
				
				Tile tile = game.getMap().getWorldTile(0, x, y);
				if(tile != null) {
					game.emitSound(getId(), SurfaceTypeToSoundType.toSoundType(tile.getSurfaceType()) , cpos);
				}
				if(currentState == State.SPRINTING) {
					if(stamina > 0) {
						runTime = SPRINT_DELAY_TIME;
						game.emitSound(getId(), SoundType.RUFFLE, getCenterPos());
					}
					else {
						runTime = RUN_DELAY_TIME;	
					}
				}
				else {
					runTime = RUN_DELAY_TIME;
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
		int mSpeed = this.speed;
		if(currentState==State.WALKING) {
			mSpeed = (int)( (float)this.speed * 0.484f);
		}
		else if(currentState == State.SPRINTING) {
			if(stamina > 0) {
				mSpeed = (int)( (float)this.speed * 1.35f);
			}
//			else {
//				mSpeed = (int)(this.speed * 0.8);	
//			}
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
		
	/**
	 * Handles the users input commands
	 * 
	 * @param command
	 */
	public void handleUserCommand(UserCommand command) {
		int keys = command.getKeys();
		int previousKeys = (previousCommand != null) ? previousCommand.getKeys() : 0;
		float prevOrientation = (previousCommand != null) ? previousCommand.getOrientation() : -1; 		
		
		if( Keys.FIRE.isDown(keys) ) {
//			if(Keys.FIRE.isDown(previousKeys)) {
//				beginFire();
//			}
			firing = true;
		}
		else if(Keys.FIRE.isDown(previousKeys)) {
			endFire();
			firing = false;
		}
		//else firing = false;
		
		
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
			
		
//		boolean strafe = Keys.STRAFE.isDown(keys);
		if(Keys.UP.isDown(keys)) {
			moveUp();
		}
		else if(Keys.DOWN.isDown(keys)) {
			moveDown();
		}
		else inputVel.y = 0;
		
		if(Keys.LEFT.isDown(keys)) {
			moveLeft();
		}
		else if (Keys.RIGHT.isDown(keys)) {
			moveRight();
		}
		else inputVel.x = 0;
		
		

		if(Keys.SPRINT.isDown(keys)) {
			if(!inputVel.isZero() && currentState != State.WALKING) {
				sprint();
			}
		}
		else {
			if(!inputVel.isZero() && currentState != State.WALKING) {
				currentState = State.RUNNING;
			}			
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
		
		if(prevOrientation != command.getOrientation())
			setOrientation(command.getOrientation());
		
		this.previousCommand = command;
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
	
	/**
	 * Stop walking
	 */
	public void stopWalking() {
		if(currentState==State.WALKING) {
			currentState = State.RUNNING;
		}
	}
	
	
	/**
	 * Go in the crouching position
	 */
	public void crouch() { 
		if(currentState == State.IDLE) {			
			game.emitSound(getId(), SoundType.RUFFLE, getCenterPos());								
			this.currentState = State.CROUCHING;
		}
	}
	
	/**
	 * Stop crouching
	 */
	public void standup() {
		if(currentState==State.CROUCHING) {
			game.emitSound(getId(), SoundType.RUFFLE, getCenterPos());
			this.currentState = State.IDLE;
		}
	}
	
	public void sprint() {
		if(currentState!=State.DEAD && stamina > 0) {		
			if(currentState!=State.SPRINTING) {
				game.emitSound(getId(), SoundType.RUFFLE, getCenterPos());
			}
			
			currentState = State.SPRINTING;			
		}
	}
	
	public void reload() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon!=null) {
			weapon.reload();
		}
	}
	
	public boolean meleeAttack() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon != null) {
			return weapon.meleeAttack();
		}
		return false;
	}
	
	public void doneMeleeAttack() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon != null) {
			weapon.doneMelee();	
		}
	}
	
	public boolean beginFire() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon!=null) {
			return weapon.beginFire();
		}
		return false;
	}
	
		
	public boolean endFire() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon!=null) {
			return weapon.endFire();
		}
		return false;
	}
	
	public boolean pullGrenadePin() {
		if(inventory.hasGrenades()) {
			GrenadeBelt grenades = inventory.getGrenades();
			return grenades.pullPin();			
		}
		return false;
	}
	
	public boolean throwGrenade() {
		if(inventory.hasGrenades()) {
			GrenadeBelt grenades = inventory.getGrenades();
			return grenades.throwGrenade();			
		}
		return false;
	}
	
	public boolean canFire() {
		Weapon weapon = this.inventory.currentItem();
		if(weapon!=null) {
			return weapon.canFire();
		}
		return false;
	}
	
	public void moveOrientation(float value) {
		this.setOrientation(value);
	}
	

	public void noMoveY() {
		vel.y = 0;
		inputVel.y = 0;
	}
	
	public void moveUp() {
		vel.y = -1;
		inputVel.y = -1;
	}
	
	public void moveDown() {
		vel.y = 1;
		inputVel.y = 1;
	}
	
	public void noMoveX() {
		vel.x = 0;
		inputVel.x = 0;
	}
	
	public void moveLeft() {
		vel.x = -1;
		inputVel.x = -1;
	}
	
	public void moveRight() {
		vel.x = 1;
		inputVel.x = 1;
	}
	
	public void nextWeapon() {
		Weapon weapon = inventory.nextItem();
		if(weapon!=null) {
			weapon.setSwitchingWeaponState();
			checkLineOfSightChange();
		}
		
	}
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
	 * Use can be used for either planting a bomb, or disarming it.
	 */
	public void use() {
//		boolean shouldToggleFlashlight = true;
		
		if(this.bombTarget == null) {		
			List<BombTarget> targets = game.getBombTargets();
			int size = targets.size();
			for(int i = 0; i < size; i++) {
				BombTarget target = targets.get(i);
				if(bounds.intersects(target.getBounds())) {
										
					if(target.bombActive()) {
						Bomb bomb = target.getBomb();
						bomb.disarm(this);						
												
						game.emitSound(getId(), SoundType.BOMB_DISARM, getPos());						
					}
					else {
						if(!target.isBombAttached()) {							
							Bomb bomb = game.newBomb(target); 
							bomb.plant(this, target);
							target.attachBomb(bomb);
							game.emitSound(getId(), SoundType.BOMB_PLANT, getPos());
						}												
					}
					
					this.bombTarget = target;
//					shouldToggleFlashlight = false;
					break;
				}
			}
		}
		
//		if(shouldToggleFlashlight) {	
//			if(flashlightToggleTime<=0) {
//				isFlashlightOn = !isFlashlightOn;
//				flashlightToggleTime = 300;
//			}
//		}
	}
	
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
	 * Retrieves the sounds heard by an Entity
	 * @param soundEvents
	 * @param soundsHeard (the out parameter)
	 * @return the same instance as soundsHeard, just returned for convenience
	 */
	public List<SoundEmittedEvent> getHeardSounds(List<SoundEmittedEvent> soundEvents, List<SoundEmittedEvent> soundsHeard) {
		
		Vector2f pos = getCenterPos();
		// NOTE: this is for performance reasons only, this is not thread-safe!!!
		int radius = getHearingRadius();
		this.hearingBounds.set( (int)pos.x - (radius/2), (int)pos.y - (radius/2), radius, radius);
		
		int size = soundEvents.size();
		for(int i = 0; i < size; i++) {
			SoundEmittedEvent event = soundEvents.get(i);
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
		Geom.calculateLineOfSight(game.aTiles, centerPos, getFacing(), getLineOfSight(), map, getHeightMask());						
		
		this.visualBounds.centerAround(centerPos);
		
		for(int i = 0; i < entities.length; i++) {
			Entity ent = entities[i];
			if(ent==null /*|| !ent.isAlive()*/) {
				continue;
			}
			
			Type entType = ent.getType();
			boolean isCalculatedEntity = entType==Type.PLAYER;//||entType==Type.GRENADE||entType==Type.NAPALM_GRENADE;
			
			if(isCalculatedEntity && game.isEnableFOW()) {				
				if(ent.getId() == id) {
					entitiesInView.add(ent);
				}
				else {
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
	
	/**
	 * @return true if this is a mech
	 */
	public boolean isMech() {
		return getType()==Type.MECH;
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.Entity#getNetEntity()
	 */
	@Override
	public NetEntity getNetEntity() {
		return getNetPlayer();
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
		
		player.flashLightOn = isFlashlightOn;
		
		player.health = (byte)getHealth(); 
//		player.events = (byte)getEvents();
		player.stamina = getStamina();
		setEvents(0);
		
		Weapon weapon = inventory.currentItem();
		if(weapon!=null) {
			player.weapon = weapon.getNetWeapon();
		}
		else {
			player.weapon = null;
		}
		
		return player;
	}	
}
