/*
 * see license.txt 
 */
package seventh.game.weapons;

import java.util.Random;

import leola.vm.exceptions.LeolaRuntimeException;
import leola.vm.types.LeoMap;
import leola.vm.types.LeoObject;
import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.game.entities.Entity.Type;
import seventh.game.net.NetWeapon;
import seventh.math.Vector2f;
import seventh.shared.Config;
import seventh.shared.Cons;
import seventh.shared.SoundType;
import seventh.shared.TimeStep;

/**
 * Represents a Weapon.
 * 
 * @author Tony
 *
 */
public abstract class Weapon {
	
	public enum State {
		READY,
		FIRING,
		WAITING,
		RELOADING,
		FIRE_EMPTY,
		
		MELEE_ATTACK,
		SWITCHING,
		
		UNKNOWN
		;
		
		public byte netValue() {
			return (byte)ordinal();
		}
		
		private static State[] values = values();
		
		public static State fromNet(byte value) {
			if(value < 0 || value >= values.length) {
				return UNKNOWN;
			}
			
			return values[value];
		}
	}
	
	protected long weaponTime;
	protected int damage;
	
	protected long reloadTime;
	
	protected int weaponWeight;
			
	protected int clipSize;
	protected int bulletsInClip;
	protected int totalAmmo;
	protected int spread;
	protected int lineOfSight;
	protected int bulletRange;
	
	private State state;
	
	protected Entity owner;
	
	protected Game game;
	
	protected NetWeapon netWeapon;
	
	private Random random;
	private final Type type;
	
	private GunSwing gunSwing;
	
	private float bulletSpawnDistance, 
				  rocketSpawnDistance, 
				  grenadeSpawnDistance;
	
	/**	 
	 * @param game
	 * @param owner
	 * @param type
	 */
	public Weapon(Game game, Entity owner, Type type) {
		this.type = type;
		this.game = game;
		this.owner = owner;
		this.netWeapon = new NetWeapon();
		this.netWeapon.type = -1;
		this.random = new Random();
		this.state = State.READY;	
		
		this.gunSwing = new GunSwing(game, owner);		
		
		this.bulletSpawnDistance = 35.0f;
		this.rocketSpawnDistance = 40.0f;
		this.grenadeSpawnDistance = 50.0f;
		
		this.bulletRange = 5000;
	}
	
	/**
	 * Sets the weapons properties.
	 * @param weaponName
	 * @return the attributes if available, otherwise null
	 */
	protected LeoMap applyScriptAttributes(String weaponName) {	
		Config config = game.getConfig().getConfig();
				
		try {
			LeoObject values = config.get("weapons", weaponName);
			if(values!=null&&values.isMap()) {
				LeoMap attributes = values.as();
			
				this.damage = attributes.getInt("damage");
				this.reloadTime = attributes.getInt("reload_time");
				this.clipSize = attributes.getInt("clip_size");
				this.totalAmmo = attributes.getInt("total_ammo");
				this.bulletsInClip = this.clipSize;
						
				this.spread = attributes.getInt("spread");
				this.bulletRange = attributes.getInt("bullet_range");
				return attributes;
			}
		}
		catch(LeolaRuntimeException e) {
			Cons.print("*** Error reading the weapons configuration: " + e);
		}
		
		return null;
	}
	
	/**
	 * @param owner the owner to set
	 */
	public void setOwner(Entity owner) {
		this.owner = owner;
		this.gunSwing.setOwner(owner);
	}
	
	/**
	 * @param bulletsInClip the bulletsInClip to set
	 */
	public void setBulletsInClip(int bulletsInClip) {
		this.bulletsInClip = bulletsInClip;
	}
	
	/**
	 * @param totalAmmo the totalAmmo to set
	 */
	public void setTotalAmmo(int totalAmmo) {
		this.totalAmmo = totalAmmo;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * @return the weaponWeight in pounds
	 */
	public int getWeaponWeight() {
		return weaponWeight;
	}
	
	/**
	 * @return the weaponSightDistance
	 */
	public int getLineOfSight() {
		return lineOfSight;
	}
	
	public int getOwnerId() {
		return this.owner.getId();
	}
	
	/**
	 * @return the bulletRange
	 */
	public int getBulletRange() {
		return bulletRange;
	}
	
	/**
	 * @return the owners center position
	 */
	public Vector2f getPos() {
		return this.owner.getCenterPos();
	}
	

	public void update(TimeStep timeStep) {
		if ( weaponTime > 0 ) {
			weaponTime -= timeStep.getDeltaTime();			
		}
		else if (!this.isLoaded() && totalAmmo > 0) {
			reload();
		}
		else {
			setReadyState();			
		}
		
		gunSwing.update(timeStep);
	}
	
	/**
	 * Swing the gun for a melee attack
	 * @return true if we were able to swing
	 */
	public boolean meleeAttack() {
		if(state == State.READY) {
			if(this.gunSwing.beginSwing()) {
				setMeleeAttack();
				this.gunSwing.endSwing();
				return true;
			}
		}
		
		return false;
	}
	
	public void doneMelee() {
		//if(this.gunSwing.isSwinging()) 
		{
			this.gunSwing.endSwing();
		}
	}
	
	/**
	 * @return true if we are currently melee attacking
	 */
	public boolean isMeleeAttacking() {
		return this.gunSwing.isSwinging();
	}
	
	/**
	 * @return true if this weapon is ready to fire/use
	 */
	public boolean isReady() {
		return state == State.READY;
	}
	
	/**
	 * @return true if this weapon is switching
	 */
	public boolean isSwitchingWeapon() {
		return state == State.SWITCHING;
	}
	
	/**
	 * @return true if this weapon is currently firing
	 */
	public boolean isFiring() {
		return state == State.FIRING;
	}
	
	/**
	 * @return true if this weapon is currently reloading
	 */
	public boolean isReloading() {
		return state == State.RELOADING;
	}
	
	/**
	 * @return the bulletsInClip
	 */
	public int getBulletsInClip() {
		return bulletsInClip;
	}
	
	/**
	 * @return the totalAmmo
	 */
	public int getTotalAmmo() {
		return totalAmmo;
	}
	
	/**
	 * @return the clipSize
	 */
	public int getClipSize() {
		return clipSize;
	}
	
	protected void setMeleeAttack() {
		state = State.MELEE_ATTACK;
		weaponTime = 200;
	}
	
	protected void setReadyState() {
		this.state = State.READY;
	}
	
	protected void setReloadingState() {
		state = State.RELOADING;
	}
	
	protected void setWaitingState() {
		state = State.WAITING; 
	}
	
	public void setSwitchingWeaponState() {
		state = State.SWITCHING;
		weaponTime = 900;
		game.emitSound(getOwnerId(), SoundType.WEAPON_SWITCH, getPos());
	}
	
	protected void setFireState() {
		state = State.FIRING;
//		this.fired = false;
	}
	
	protected void setFireEmptyState() {
		if(getState() != Weapon.State.FIRE_EMPTY) {
			game.emitSound(getOwnerId(), SoundType.EMPTY_FIRE, getPos());	
		}
		this.weaponTime = 500;
		state = State.FIRE_EMPTY;						
//		this.fired = false;
	}
	
	/**
	 * @return the state
	 */
	public State getState() {
		return state;
	}
	
	/**
	 * Invoked for when the trigger is held down
	 * @return true if the weapon discharged
	 */
	public boolean beginFire() {
		return false;
	}
	
	/**
	 * Invoked when the trigger is done being pulled.
	 * @return true if the weapon discharged
	 */
	public boolean endFire() { 
		return false;
	}
	
	/**
	 * @return true if this weapon is loaded and ready to fire
	 */
	public boolean canFire() {
		return weaponTime <= 0 && bulletsInClip > 0;
	}
	
	/**
	 * @return true if this weapon is ready for a melee attack at this moment.
	 */
	public boolean canMelee() {
		return this.gunSwing.canSwing();
	}
	
	/**
	 * @return true if any bullets are current in the clip
	 */
	public boolean isLoaded() {
		return bulletsInClip > 0;
	}
	
	/**
	 * Calculates the velocity of the projectile discharged from the weapon.
	 * @param facing
	 * @return the velocity
	 */
	protected abstract Vector2f calculateVelocity(Vector2f facing);
	
	/**
	 * Attempts to reload the weapon
	 * @return true if reloading was activated; false otherwise
	 */
	public boolean reload() {
		if (bulletsInClip < clipSize && weaponTime <= 0) {
			if(totalAmmo > 0) {
				weaponTime = reloadTime;
				bulletsInClip = (totalAmmo < clipSize) ? totalAmmo : clipSize;
				totalAmmo -= bulletsInClip;
				setReloadingState();
				return true;
			}
		}
		
		return false;
	}
	
	public void addAmmo(int amount) {
		totalAmmo += amount;
	}
	
	/**
	 * @param bulletSpawnDistance the bulletSpawnDistance to set
	 */
	public void setBulletSpawnDistance(float bulletSpawnDistance) {
		this.bulletSpawnDistance = bulletSpawnDistance;
	}
	
	/**
	 * @return the distance of the bullet spawn point from the owners origin
	 */
	public float getBulletSpawnDistance() {
		return this.bulletSpawnDistance;
	}
	
	/**
	 * @return the position where the bullet spawns
	 */
	protected Vector2f newBulletPosition() {
		Vector2f ownerDir = owner.getFacing();
		Vector2f ownerPos = owner.getCenterPos();
		Vector2f pos = new Vector2f(ownerPos.x + ownerDir.x * getBulletSpawnDistance()
								  , ownerPos.y + ownerDir.y * getBulletSpawnDistance());
		
		return pos;
	}
	
	/**
	 * @return creates a new {@link Bullet} that is not piercing
	 */
	protected Bullet newBullet() {
		return newBullet(false);
	}
	
	/**
	 * Creates a new {@link Bullet}
	 * @param isPiercing
	 * @return the {@link Bullet}
	 */
	protected Bullet newBullet(boolean isPiercing) {
		Vector2f pos = newBulletPosition();
		Vector2f vel = calculateVelocity(owner.getFacing());
		
		final int speed = 1500 + (random.nextInt(10) * 100);
		
		Bullet bullet = new Bullet(pos, speed, game, owner, vel, damage, isPiercing);
		bullet.setMaxDistance(getBulletRange());				
		
		game.addEntity(bullet);		
		return bullet;
	}
	
	/**
	 * @param rocketSpawnDistance the rocketSpawnDistance to set
	 */
	public void setRocketSpawnDistance(float rocketSpawnDistance) {
		this.rocketSpawnDistance = rocketSpawnDistance;
	}
	
	/**
	 * The distance from the owners location where the rocket is spawned
	 * @return the distance from the owner to where the rocket is spawned
	 */
	public float getRocketSpawnDistance() {
		return this.rocketSpawnDistance;
	}
	
	/**
	 * @return the position where the new Rocket is spawned
	 */
	protected Vector2f newRocketPosition() {
		Vector2f ownerDir = owner.getFacing();
		Vector2f ownerPos = owner.getCenterPos();
		Vector2f pos = new Vector2f(ownerPos.x + ownerDir.x * getRocketSpawnDistance(), ownerPos.y + ownerDir.y * getRocketSpawnDistance());
		return pos;
	}
	
	/**
	 * Spawns a new {@link Rocket}
	 * @return the {@link Rocket}
	 */
	protected Rocket newRocket() {		
		Vector2f pos = newRocketPosition();
		Vector2f vel = calculateVelocity(owner.getFacing());
		
		final int speed = 650;
		final int splashDamage = 80;
		
		Rocket bullet = new Rocket(pos, speed, game, owner, vel, damage, splashDamage);
				
		game.addEntity(bullet);		
		return bullet;
	}
	
	/**
	 * @param grenadeSpawnDistance the grenadeSpawnDistance to set
	 */
	public void setGrenadeSpawnDistance(float grenadeSpawnDistance) {
		this.grenadeSpawnDistance = grenadeSpawnDistance;
	}
	
	/**
	 * @return the distance the grenade spawns from the owners origin
	 */
	public float getGenadeSpawnDistance() {
		return this.grenadeSpawnDistance;
	}
	
	/**
	 * Spawns a new grenade
	 * @param timePinPulled the time the pin was pulled (effects distance)
	 * @return the {@link Grenade}
	 */
	protected Entity newGrenade(int timePinPulled) {
		Vector2f ownerDir = owner.getFacing();
		Vector2f ownerPos = owner.getCenterPos();
		Vector2f pos = new Vector2f(ownerPos.x + ownerDir.x * getGenadeSpawnDistance()
								  , ownerPos.y + ownerDir.y * getGenadeSpawnDistance());
		
		Vector2f vel = calculateVelocity(owner.getFacing());
		final int maxSpeed = 250;
		int speed = Math.min(120 + (timePinPulled*7), maxSpeed);				
		
		Entity bullet = new Grenade(pos, speed, game, owner, vel, damage);
				
		game.addEntity(bullet);		
		return bullet;
	}
	
	/**
	 * Spawns a new grenade
	 * @param timePinPulled the time the pin was pulled (effects distance)
	 * @return the {@link NapalmGrenade}
	 */
	protected Entity newNapalmGrenade(int timePinPulled) {
		Vector2f ownerDir = owner.getFacing();
		Vector2f ownerPos = owner.getCenterPos();
		Vector2f pos = new Vector2f(ownerPos.x + ownerDir.x * getGenadeSpawnDistance()
								  , ownerPos.y + ownerDir.y * getGenadeSpawnDistance());
		
		Vector2f vel = calculateVelocity(owner.getFacing());
		final int maxSpeed = 250;
		int speed = Math.min(120 + (timePinPulled*7), maxSpeed);				
		
		Entity bullet = new NapalmGrenade(pos, speed, game, owner, vel, damage);
				
		game.addEntity(bullet);		
		return bullet;
	}
	
	
	/**
	 * Adds a new {@link Explosion}
	 * @param pos
	 * @param owner
	 * @param splashDamage
	 * @return the {@link Explosion}
	 */
	protected Explosion newExplosion(Vector2f pos, int splashDamage) {
		return game.newExplosion(pos, owner, splashDamage);
	} 
	
	/**
	 * Calculates the accuracy of the shot based on the owner {@link Entity}'s state.
	 * 
	 * <p>
	 * The more still you are, the more accurate you are.
	 * 
	 * @param facing
	 * @param standardAccuracy
	 * @return the altered 'standardAccuracy' value
	 */
	protected int calculateAccuracy(Vector2f facing, int standardAccuracy) {
		
		int newAccuracy = standardAccuracy;
		switch(owner.getCurrentState()) {
			case CROUCHING:
				newAccuracy = standardAccuracy / 2;
				break;			
			case IDLE:
				newAccuracy = standardAccuracy;
				break;
			case RUNNING:
				newAccuracy = standardAccuracy + (standardAccuracy/4);
				break;
			case SPRINTING:
				newAccuracy = standardAccuracy * 3;
				break;
			case WALKING:
				newAccuracy = standardAccuracy + (standardAccuracy/10);
				break;
			default: newAccuracy = standardAccuracy * 10;
		}
		
		if(newAccuracy<0) {
			newAccuracy = 0;
		}
		
		return newAccuracy;
	}
	
	/**
	 * Calculates a random velocity given a facing and a maxSpread value.
	 * @param facing
	 * @param maxSpread
	 * @return a rotated vector of the supplied facing randomized between -maxSpread and maxSpread
	 */
	protected Vector2f spread(Vector2f facing, int maxSpread) {			
		maxSpread = calculateAccuracy(facing, maxSpread);
		
		double rd = random.nextInt(maxSpread) / 100.0;
		int sd = random.nextInt(2);
		return sd>0 ? facing.rotate(rd) : facing.rotate(-rd);
	}
	
	public NetWeapon getNetWeapon() {
		netWeapon.ammoInClip = (byte)bulletsInClip;
		netWeapon.totalAmmo = (short)totalAmmo;
		netWeapon.state = state.netValue();		
		
		return this.netWeapon;
	}
	
}
