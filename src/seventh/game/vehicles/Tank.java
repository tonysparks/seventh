/**
 * 
 */
package seventh.game.vehicles;

import java.util.List;

import seventh.game.Entity;
import seventh.game.Game;
import seventh.game.PlayerEntity.Keys;
import seventh.game.SoundType;
import seventh.game.net.NetEntity;
import seventh.game.net.NetTank;
import seventh.game.weapons.Bullet;
import seventh.game.weapons.Explosion;
import seventh.game.weapons.Railgun;
import seventh.game.weapons.Rocket;
import seventh.game.weapons.RocketLauncher;
import seventh.game.weapons.Weapon;
import seventh.map.Map;
import seventh.math.OOB;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.DebugDraw;
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * Represents a Tank
 * 
 * @author Tony
 * 
 */
public class Tank extends Vehicle {

	private final NetTank netTank;
	
	private int previousKeys;
	private float previousOrientation;
	
	private long nextMovementSound;
	private long lastTorsoMovementSound;

	private boolean isFiringPrimary;
	private boolean isFiringSecondary;
	private boolean isRetracting;
	private float throttle;
	private float turretOrientation;
	private float desiredTurretOrientation;		
	
//	private float movementTime;
//	private float movementRate;	
	private float desiredOrientation;

	private Weapon primaryWeapon;
	private Weapon secondaryWeapon;

	private Vector2f turretFacing;
	
	private int armor;
	
	private OOB tankBB;
	
	
	/**
	 * @param position
	 * @param speed
	 * @param game
	 * @param type
	 */
	public Tank(Vector2f position, Game game) {
		super(position, WeaponConstants.TANK_MOVEMENT_SPEED, game, Type.TANK);

		this.turretFacing = new Vector2f();
		this.netTank = new NetTank();
		this.netTank.id = getId();
		
		this.primaryWeapon = new RocketLauncher(game, this) {

			@Override
			protected Vector2f newRocketPosition() {
				this.bulletsInClip = 500;

				Vector2f ownerDir = getTurretFacing();
				Vector2f ownerPos = owner.getCenterPos();
				Vector2f pos = new Vector2f();

				float x = ownerDir.y * 5.0f;
				float y = -ownerDir.x * 5.0f;

				Vector2f.Vector2fMA(ownerPos, ownerDir, 105.0f, pos);

				pos.x += x;
				pos.y += y;

				return pos;
			}
			
			/* (non-Javadoc)
			 * @see seventh.game.weapons.Railgun#calculateVelocity(seventh.math.Vector2f)
			 */
			@Override
			protected Vector2f calculateVelocity(Vector2f facing) {
				return super.calculateVelocity(getTurretFacing());
			}
			
			/* (non-Javadoc)
			 * @see seventh.game.weapons.Weapon#newRocket()
			 */
			@Override
			protected Entity newRocket() {
				Entity rocket = super.newRocket();
				rocket.setOrientation(turretOrientation);
				return rocket;
			}
		};
		
		this.secondaryWeapon = new Railgun(game, this) {

			@Override
			protected Vector2f newBulletPosition() {				
				Vector2f ownerDir = getTurretFacing();
				Vector2f ownerPos = owner.getCenterPos();
				Vector2f pos = new Vector2f();

				float x = ownerDir.y * 1.0f;
				float y = -ownerDir.x * 1.0f;

				Vector2f.Vector2fMA(ownerPos, ownerDir, 55.0f, pos);

				pos.x += x;
				pos.y += y;

				return pos;
			}
			
			/* (non-Javadoc)
			 * @see seventh.game.weapons.Railgun#calculateVelocity(seventh.math.Vector2f)
			 */
			@Override
			protected Vector2f calculateVelocity(Vector2f facing) {
				return super.calculateVelocity(getTurretFacing());
			}
			
			
		};

		bounds.width = WeaponConstants.TANK_AABB_WIDTH;
		bounds.height = WeaponConstants.TANK_AABB_HEIGHT;
		operateHitBox.width = bounds.width + WeaponConstants.VEHICLE_HITBOX_THRESHOLD;
		operateHitBox.height = bounds.height + WeaponConstants.VEHICLE_HITBOX_THRESHOLD;

		Vector2f center = new Vector2f(position);
		center.x -= WeaponConstants.TANK_WIDTH/2f;
		center.y -= WeaponConstants.TANK_HEIGHT/2f;
		tankBB = new OOB( getOrientation(), center, WeaponConstants.TANK_WIDTH, WeaponConstants.TANK_HEIGHT);
		
		onTouch = new OnTouchListener() {

			@Override
			public void onTouch(Entity me, Entity other) {
				/* kill the other players */
				if(hasOperator()) {
					if(other != getOperator()) {
						if (other.getType().isPlayer()) {
							other.kill(me);
						}
					}
				}
			}
		};
	}

	/* (non-Javadoc)
	 * @see seventh.game.Entity#collideX(int, int)
	 */
	@Override
	protected boolean collideX(int newX, int oldX) {	
		return false;//game.getMap().hasHeightMask(newX, bounds.y);
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#collideY(int, int)
	 */
	@Override
	protected boolean collideY(int newY, int oldY) {
		return false;//game.getMap().hasHeightMask(bounds.x, newY);
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see seventh.game.PlayerEntity#update(seventh.shared.TimeStep)
	 */
	@Override
	public boolean update(TimeStep timeStep) {

		updateOrientation(timeStep);
		updateTurretOrientation(timeStep);

		updateWeapons(timeStep);
		
		
		this.vel.set(this.facing);
		Vector2f.Vector2fMult(vel, this.throttle, vel);
		Vector2f.Vector2fNormalize(vel, vel);
		
		boolean isBlocked = //super.update(timeStep);
				movementUpdate(timeStep);
		
		updateOperateHitBox();
		
		Vector2f center = new Vector2f(pos);
		center.x += WeaponConstants.TANK_AABB_WIDTH/2f;
		center.y += WeaponConstants.TANK_AABB_HEIGHT/2f;
		this.tankBB.update(orientation, center);
		
		DebugDraw.drawRectRelative(bounds.x, bounds.y, bounds.width, bounds.height, 0xffffff00);
		DebugDraw.drawOOBRelative(tankBB, 0xff00ff00);
		DebugDraw.fillRectRelative((int)pos.x, (int)pos.y, 5, 5, 0xffff0000);
		
		return isBlocked;
	}
	
	/**
	 * @param dt
	 * @return true if blocked
	 */
	private boolean movementUpdate(TimeStep timeStep) {
		boolean isBlocked = false;
		
		if(isAlive() && !this.vel.isZero()) {
			if(currentState != State.WALKING && currentState != State.SPRINTING) {
				currentState = State.RUNNING;
			}
								
			int movementSpeed = calculateMovementSpeed();
						
			
			double dt = timeStep.asFraction();
			int newX = (int)Math.round(pos.x + vel.x * movementSpeed * dt);
			int newY = (int)Math.round(pos.y + vel.y * movementSpeed * dt);					
			
			Map map = game.getMap();
			
			bounds.x = newX;
			if( map.rectCollides(bounds) ) {
				tankBB.setLocation(newX+WeaponConstants.TANK_AABB_WIDTH/2f, tankBB.center.y);
				isBlocked = map.rectCollides(tankBB);
				if(isBlocked) { 
					bounds.x = (int)pos.x;
				}
								
			}
			else if(collidesAgainstVehicle(bounds)) {
				bounds.x = (int)pos.x;				
				isBlocked = true;
			}
			
			
			bounds.y = newY;
			if( map.rectCollides(bounds)) {
				tankBB.setLocation(tankBB.center.x, newY+WeaponConstants.TANK_AABB_HEIGHT/2f);
				isBlocked = map.rectCollides(tankBB);
				if(isBlocked) {
					bounds.y = (int)pos.y;
				}
			}
			else if(collidesAgainstVehicle(bounds)) {				
				bounds.y = (int)pos.y;
				isBlocked = true;
			}
			

			/* some things want to stop dead it their tracks
			 * if a component is blocked
			 */
			if(isBlocked && !continueIfBlock()) {
				bounds.x = (int)pos.x;
				bounds.y = (int)pos.y;
			}
						
			pos.x = bounds.x;
			pos.y = bounds.y;
					
			vel.zeroOut();
			
//			this.walkingTime = WALK_TIME;
		}
		else {						
//			if(this.walkingTime<=0 && currentState!=State.CROUCHING) {
//				currentState = State.IDLE;
//			}
//			
//			this.walkingTime -= timeStep.getDeltaTime();
		
		}
		
		
		
		return isBlocked;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#continueIfBlock()
	 */
	@Override
	protected boolean continueIfBlock() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#collidesAgainstVehicle(seventh.math.Rectangle)
	 */
	@Override
	protected boolean collidesAgainstVehicle(Rectangle bounds) {
		boolean collides = false;
		List<Vehicle> vehicles = game.getVehicles();
		for(int i = 0; i < vehicles.size(); i++) {
			Vehicle vehicle = vehicles.get(i);
			if(vehicle.isAlive() && this != vehicle) {
				
				/* determine if moving to this bounds we would touch
				 * the vehicle
				 */
				collides = bounds.intersects(vehicle.getBounds());
				
				/* if we touched, determine if we would be inside
				 * the vehicle, if so, kill us
				 */
				if(collides) {
					
					// do a more expensive collision detection
					if(vehicle.isTouching(this)) {					
						return true;
					}					
				}
			}
			
			
		}
		
		return collides;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#isTouching(seventh.game.Entity)
	 */
	@Override
	public boolean isTouching(Entity other) {
		
		// first check the cheap AABB
		if(bounds.intersects(other.getBounds())) {
		
			if(other instanceof Tank) {
				Tank otherTank = (Tank)other;
				return this.tankBB.intersects(otherTank.tankBB);
			}
			else {
				return this.tankBB.intersects(other.getBounds());
			}
		}
		
		return false; 
	}
	
	protected void updateOrientation(TimeStep timeStep) {
		
		float deltaMove = 0.25f * (float)timeStep.asFraction();
		if(this.vel.x > 0) {
			this.desiredOrientation += deltaMove;
		}
		else if(this.vel.x < 0) {
			this.desiredOrientation -= deltaMove;
		}
		this.orientation = this.desiredOrientation;

		this.facing.set(1, 0); // make right vector
		Vector2f.Vector2fRotate(this.facing, orientation, this.facing);
		this.tankBB.rotateTo(orientation);
		
	}
	
	protected void updateTurretOrientation(TimeStep timeStep) {
		this.turretOrientation = this.desiredTurretOrientation;

		this.turretFacing.set(1, 0); // make right vector
		Vector2f.Vector2fRotate(this.turretFacing, this.turretOrientation, this.turretFacing);
	}
		
	/**
	 * Update the {@link Weapon}s
	 * @param timeStep
	 */
	protected void updateWeapons(TimeStep timeStep) {
		if(this.isFiringPrimary) {
			this.primaryWeapon.beginFire();
		}
		this.primaryWeapon.update(timeStep);
		
		if(this.isFiringSecondary) {
			this.secondaryWeapon.beginFire();
		}
		this.secondaryWeapon.update(timeStep);
	}

	protected void makeMovementSounds(TimeStep timeStep) {
		if (nextMovementSound <= 0) {
			SoundType snd = SoundType.TANK_MOVE1;
			if (isRetracting) {
				snd = SoundType.TANK_MOVE2;
			}

			isRetracting = !isRetracting;
			game.emitSound(getId(), snd, getCenterPos());
			nextMovementSound = 700;// 1500;
		} else {
			if ((currentState == State.RUNNING || currentState == State.SPRINTING)) {
				nextMovementSound -= timeStep.getDeltaTime();
			} else {
				nextMovementSound = 1;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see seventh.game.PlayerEntity#damage(seventh.game.Entity, int)
	 */
	@Override
	public void damage(Entity damager, int amount) {
		armor -= amount;
		
		if(armor < 0) {		
			if (damager instanceof Bullet) {
				amount = 1;
			} else if (damager instanceof Explosion) {
				amount = 1;
			} 
			else if(damager instanceof Rocket) {
				amount /= 2;
			}
			else {
				amount /= 10;
			}
	
			super.damage(damager, amount);
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see seventh.game.PlayerEntity#setOrientation(float)
	 */
	@Override
	public void setOrientation(float orientation) {
		this.desiredOrientation = orientation;
	}

	/* (non-Javadoc)
	 * @see seventh.game.Controllable#handleUserCommand(seventh.game.UserCommand)
	 */
	@Override
	public void handleUserCommand(int keys, float orientation) {
				
		
		if( Keys.FIRE.isDown(keys) ) {
			isFiringPrimary = true;
		}
		else if(Keys.FIRE.isDown(previousKeys)) {
			endPrimaryFire();
			isFiringPrimary = false;
		}		
		
		
		if(Keys.THROW_GRENADE.isDown(keys)) {
			isFiringSecondary = true;
		}
		else if(Keys.THROW_GRENADE.isDown(previousKeys)) {
			endSecondaryFire();	
			isFiringSecondary = false;
		}
		
		
		/* ============================================
		 * Handles the movement of the tank
		 * ============================================
		 */
					
		if(Keys.LEFT.isDown(keys)) {
			maneuverLeft();
		}
		else if(Keys.RIGHT.isDown(keys)) {
			maneuverRight();
		}
		else {
			stopManeuvering();
		}
		
		if(Keys.MELEE_ATTACK.isDown(keys) || Keys.UP.isDown(keys)) {
			forwardThrottle();
		}
		else if(Keys.DOWN.isDown(keys)) {
			backwardThrottle();
		}
		else {
			stopThrottle();
		}
				
		if(previousOrientation != orientation) {
			setTurretOrientation(orientation);
		}
		
		this.previousKeys = keys;
		this.previousOrientation = orientation;
	}

		
	/**
	 * Begins the primary fire
	 * @return
	 */
	public boolean beginPrimaryFire() {
		return this.primaryWeapon.beginFire();		
	}
	
	
	/**
	 * Ends the primary fire
	 * @return
	 */
	public boolean endPrimaryFire() {
		return this.primaryWeapon.endFire();
	}
	
	
	/**
	 * Begins firing the secondary fire
	 * @return
	 */
	public boolean beginSecondaryFire() {
		return this.secondaryWeapon.beginFire();
	}
	
	/**
	 * Ends the secondary fire
	 * @return
	 */
	public boolean endSecondaryFire() {
		return this.secondaryWeapon.endFire();
	}
	
	
	/**
	 * Adds throttle
	 */
	public void forwardThrottle() {
		this.throttle = 1;
	}
	
	/**
	 * Reverse throttle
	 */
	public void backwardThrottle() {
		this.throttle = -1;
	}
	
	/**
	 * Stops the throttle
	 */
	public void stopThrottle() {
		this.throttle = 0;
	}
	
	/**
	 * Maneuvers the tracks to the left
	 */
	public void maneuverLeft() {
		this.vel.x = -1;
	}
	
	/**
	 * Maneuvers the tracks to the right
	 */
	public void maneuverRight() {
		this.vel.x = 1;
	}
	
	/**
	 * Stops maneuvering the tracks
	 */
	public void stopManeuvering() {
		this.vel.x = 0;
	}
	
	/**
	 * Sets the turret to the desired orientation.
	 * @param desiredOrientation the desired orientation in Radians
	 */
	public void setTurretOrientation(float desiredOrientation) {
		this.desiredTurretOrientation = desiredOrientation;
	}
	
	/**
	 * @return the turretOrientation
	 */
	public float getTurretOrientation() {
		return turretOrientation;
	}
	
	/**
	 * @return the turretFacing
	 */
	public Vector2f getTurretFacing() {
		return turretFacing;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#getNetEntity()
	 */
	@Override
	public NetEntity getNetEntity() {
		super.setNetEntity(netTank);
		netTank.state = getCurrentState().netValue();
		netTank.operatorId = hasOperator() ? this.getOperator().getId() : 0;
		netTank.turretOrientation = (short)Math.toDegrees(turretOrientation);
		netTank.primaryWeaponState = primaryWeapon.getState().netValue();
		netTank.secondaryWeaponState = secondaryWeapon.getState().netValue();
		
		return netTank;
	}
}
