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
import seventh.math.FastMath;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.DebugDraw;
import seventh.shared.EaseInInterpolation;
import seventh.shared.TimeStep;
import seventh.shared.Timer;
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
	
	private long throttleStartTime;
	private long throttleWarmupTime;
	
//	private float movementTime;
//	private float movementRate;	
	private float desiredOrientation;

	private Weapon primaryWeapon;
	private Weapon secondaryWeapon;

	private Vector2f turretFacing;
	
	private int armor;
	
	private Timer blowupTimer, explosionTimer;
	private boolean isDying;
	private boolean isStopping;
	private boolean isStopped;
	private Entity killer;
	
	private EaseInInterpolation stopEase;
	private Vector2f previousVel;
	
	/**
	 * @param position
	 * @param speed
	 * @param game
	 * @param type
	 */
	public Tank(Vector2f position, final Game game) {
		super(position, WeaponConstants.TANK_MOVEMENT_SPEED, game, Type.TANK);

		this.turretFacing = new Vector2f();
		this.netTank = new NetTank();
		this.netTank.id = getId();
		
		this.blowupTimer = new Timer(false, 3_000);
		this.explosionTimer = new Timer(true, 500);
		this.isDying = false;
		
		this.stopEase = new EaseInInterpolation(WeaponConstants.TANK_MOVEMENT_SPEED, 0f, 800);
		this.previousVel = new Vector2f();
		
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
			
			
		};

		bounds.width = WeaponConstants.TANK_AABB_WIDTH;
		bounds.height = WeaponConstants.TANK_AABB_HEIGHT;
		
		aabbWidth = bounds.width;
		aabbHeight = bounds.height;

		this.orientation = 0;
		this.desiredOrientation = this.orientation;
		
		this.armor = 200;
		
		vehicleBB.setBounds(WeaponConstants.TANK_WIDTH, WeaponConstants.TANK_HEIGHT);		
		syncOOB(getOrientation(), position);
		
		operateHitBox.width = bounds.width + WeaponConstants.VEHICLE_HITBOX_THRESHOLD;
		operateHitBox.height = bounds.height + WeaponConstants.VEHICLE_HITBOX_THRESHOLD;
		
		onTouch = new OnTouchListener() {

			@Override
			public void onTouch(Entity me, Entity other) {
				/* kill the other players */
				if(hasOperator()) {
					if(other != getOperator()) {
						if (other.getType().isPlayer()) {
							other.kill(getOperator());
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
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#collideY(int, int)
	 */
	@Override
	protected boolean collideY(int newY, int oldY) {
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see seventh.game.PlayerEntity#update(seventh.shared.TimeStep)
	 */
	@Override
	public boolean update(TimeStep timeStep) {

		if( checkIfDying(timeStep) ) {
			return false;
		}
		
		boolean isBlocked = false;
		if(hasOperator()) {
//			this.primaryWeapon.setOwner(getOperator());
//			this.secondaryWeapon.setOwner(getOperator());
			
			makeMovementSounds(timeStep);
		}
		
		{
			updateOrientation(timeStep);
			updateTurretOrientation(timeStep);

			updateWeapons(timeStep);
		
		
			this.vel.set(this.facing);
			Vector2f.Vector2fMult(vel, this.throttle, vel);
			Vector2f.Vector2fNormalize(vel, vel);
			
			isBlocked = movementUpdate(timeStep);
		}			
		updateOperateHitBox();
	
		syncOOB(orientation, pos);

		DebugDraw.drawRectRelative(bounds.x, bounds.y, bounds.width, bounds.height, 0xffffff00);
		DebugDraw.drawOOBRelative(vehicleBB, 0xff00ff00);
		DebugDraw.fillRectRelative((int)pos.x, (int)pos.y, 5, 5, 0xffff0000);
		DebugDraw.fillRectRelative((int)vehicleBB.topLeft.x, (int)vehicleBB.topLeft.y, 5, 5, 0xff1f0000);
		DebugDraw.fillRectRelative((int)vehicleBB.topRight.x, (int)vehicleBB.topRight.y, 5, 5, 0xffff0000);
		DebugDraw.fillRectRelative((int)vehicleBB.bottomLeft.x, (int)vehicleBB.bottomLeft.y, 5, 5, 0xff001f00);
		DebugDraw.fillRectRelative((int)vehicleBB.bottomRight.x, (int)vehicleBB.bottomRight.y, 5, 5, 0xff00ff00);
		
		DebugDraw.drawStringRelative("" + vehicleBB.topLeft, bounds.x, bounds.y+240, 0xffff0000);
		DebugDraw.drawStringRelative("" + vehicleBB.bottomLeft, bounds.x, bounds.y+220, 0xffff0000);
		
		return isBlocked;
	}
	
	private boolean checkIfDying(TimeStep timeStep) {
		if(this.isDying) {
			this.blowupTimer.update(timeStep);
			this.explosionTimer.update(timeStep);
			
			if(this.explosionTimer.isTime()) {
				game.newBigExplosion(getCenterPos(), this, 20, 50, 100);
			}
			
			if(this.blowupTimer.isTime()) {
				super.kill(killer);
			}
		}
		
		return this.isDying;
	}
	
	/**
	 * @param dt
	 * @return true if blocked
	 */
	private boolean movementUpdate(TimeStep timeStep) {
		boolean isBlocked = false;
		
		boolean hasThrottle = ! this.vel.isZero();
		
		if(!hasThrottle && !this.isStopped) {
			this.isStopping = true;
		}
		
		if(isAlive() && (hasThrottle || this.isStopping) ) {
			if(currentState != State.WALKING && currentState != State.SPRINTING) {
				currentState = State.RUNNING;
			}
			
			if(this.throttleWarmupTime < 600) {
				this.throttleWarmupTime += timeStep.getDeltaTime();
				
				if(hasOperator() && this.throttleWarmupTime > 590) {
					game.emitSound(getOperator().getId(), SoundType.TANK_START_MOVE, getCenterPos());
				}
			}
			else {
				
				this.throttleStartTime -= timeStep.getDeltaTime();
				
				if(this.isStopping) {
					this.vel.set(this.previousVel);
					this.stopEase.update(timeStep);
					if(this.stopEase.isExpired()) {
						this.isStopping = false;
						this.isStopped = true;
						return isBlocked;
					}
				}
				else {
					this.stopEase.reset(90f, 0f, 800);
					this.isStopped = false;
					this.previousVel.set(this.vel);
					
					if(hasOperator()) {
				//		game.emitSound(getOperator().getId(), SoundType.TANK_MOVE1, getCenterPos());
					}
				}
				
				float normalSpeed = this.isStopping ? this.stopEase.getValue() : 90f;								
				final float movementSpeed = 
							this.throttleStartTime > 0 ? 160f : normalSpeed;
							
				float dt = (float)timeStep.asFraction();
				float newX = pos.x + vel.x * movementSpeed * dt;
				float newY = pos.y + vel.y * movementSpeed * dt;					
				
				
				Map map = game.getMap();
				
				boolean isXBlocked = false;
				boolean isYBlocked = false;
							
				bounds.x = (int)newX;
				if( map.rectCollides(bounds) ) {
					vehicleBB.setLocation(newX+WeaponConstants.TANK_AABB_WIDTH/2f, vehicleBB.center.y);
					isBlocked = map.rectCollides(vehicleBB);
					if(isBlocked) { 
						bounds.x = (int)pos.x;
						isXBlocked = true;
					}
									
				}
	//			else if(collidesAgainstVehicle(bounds)) {
	//				bounds.x = (int)pos.x;				
	//				isBlocked = true;
	//			}
				
				
				bounds.y = (int)newY;
				if( map.rectCollides(bounds)) {
					vehicleBB.setLocation(vehicleBB.center.x, newY+WeaponConstants.TANK_AABB_HEIGHT/2f);
					isBlocked = map.rectCollides(vehicleBB);
					if(isBlocked) {
						bounds.y = (int)pos.y;
						isYBlocked = true;
					}
				}
	//			else if(collidesAgainstVehicle(bounds)) {				
	//				bounds.y = (int)pos.y;
	//				isBlocked = true;
	//			}
				
	
				/* some things want to stop dead it their tracks
				 * if a component is blocked
				 */
				if(isBlocked && !continueIfBlock()) {
					bounds.x = (int)pos.x;
					bounds.y = (int)pos.y;
					isXBlocked = isYBlocked = true;
				}
							
	//			pos.x = bounds.x;
	//			pos.y = bounds.y;
				
				pos.x = isXBlocked ? pos.x : newX;
				pos.y = isYBlocked ? pos.y : newY;
									
				vel.zeroOut();
				
	//			this.walkingTime = WALK_TIME;
				
				game.doesVehicleTouchPlayers(this);
			}
		}
		else {						
//			if(this.walkingTime<=0 && currentState!=State.CROUCHING) {
//				currentState = State.IDLE;
//			}
//			
//			this.walkingTime -= timeStep.getDeltaTime();

			this.currentState = State.IDLE;
			
			this.throttleWarmupTime = 0;
			this.throttleStartTime = 200;	
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
	
	protected void updateOrientation(TimeStep timeStep) {
		
		if(this.vel.x > 0 || this.vel.x < 0) {
			float deltaMove = 0.25f * (float)timeStep.asFraction();
			if(this.vel.x < 0) {
				deltaMove *= -1;
			}
			
			this.desiredOrientation += deltaMove;
			if(this.desiredOrientation<0) {
				this.desiredOrientation=FastMath.fullCircle;
			}
			else if(this.desiredOrientation>FastMath.fullCircle) {
				this.desiredOrientation=0f;
			}
			
			float newOrientation = this.desiredOrientation;
			
			Map map = game.getMap();
			if(map.rectCollides(bounds)) {
				
				this.vehicleBB.rotateTo(newOrientation);
				
				if(map.rectCollides(vehicleBB)) {
					newOrientation = orientation;
					
					desiredOrientation = orientation;
					float adjustAmount = 0.001f * ((this.vel.x < 0) ? 1f : -1f);
					float totalAmountAdjusted = 0f;
					do {
						newOrientation += adjustAmount;
						totalAmountAdjusted += adjustAmount;
						this.vehicleBB.rotateTo(newOrientation);						
					}
					while(map.rectCollides(vehicleBB) && (Math.abs(totalAmountAdjusted) < Math.abs(deltaMove)));
				}
			}
			
			this.orientation = newOrientation;
	
			this.facing.set(1, 0); // make right vector
			Vector2f.Vector2fRotate(this.facing, orientation, this.facing);
		}
	}
	
	protected void updateTurretOrientation(TimeStep timeStep) {
		final float fullCircle = FastMath.fullCircle;
		float deltaOrientation = (this.desiredTurretOrientation-this.turretOrientation);
		float deltaOrientationAbs = Math.abs(deltaOrientation);
		
		if(deltaOrientationAbs > 0.001f) {
			final double movementSpeed = Math.toRadians(1.5f);
			
			if(deltaOrientationAbs > (fullCircle/2) ) {
				deltaOrientation *= -1;
			}
			
			if(deltaOrientation != 0) {
				float direction = deltaOrientation / deltaOrientationAbs;
				
				this.turretOrientation += (direction * Math.min(movementSpeed, deltaOrientationAbs));
				if(this.turretOrientation < 0) {
					this.turretOrientation = fullCircle + this.turretOrientation;
				}
				this.turretOrientation %= fullCircle;
			}
		
			this.turretFacing.set(1, 0); // make right vector
			Vector2f.Vector2fRotate(this.turretFacing, this.turretOrientation, this.turretFacing);
		}
		
		
		DebugDraw.drawStringRelative(String.format(" Tracks: %3.2f : %3.2f", Math.toDegrees(this.orientation), Math.toDegrees(this.desiredOrientation)), 
				(int)getPos().x, (int)getPos().y-20, 0xffff0000);
		
		DebugDraw.drawStringRelative(String.format(" Current: %3.2f : %3.2f", Math.toDegrees(this.turretOrientation), Math.toDegrees(desiredTurretOrientation)), 
				getPos(), 0xffff0000);
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
				snd = SoundType.TANK_START_MOVE;
			}

			isRetracting = !isRetracting;
			game.emitSound(getId(), snd, getCenterPos());
			nextMovementSound = 700;// 1500;
		} else {
			nextMovementSound -= timeStep.getDeltaTime();
			/*
			if ((currentState == State.RUNNING || currentState == State.SPRINTING)) {
				nextMovementSound -= timeStep.getDeltaTime();
			} else {
				nextMovementSound = 1;
			}*/
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see seventh.game.PlayerEntity#damage(seventh.game.Entity, int)
	 */
	@Override
	public void damage(Entity damager, int amount) {
		
		if (damager instanceof Explosion) {
			amount = 1;
		} 
		else if(damager instanceof Rocket) {
			amount /= 2;
		} else if (damager instanceof Bullet) {
			amount = 0;
		}
		else {
			amount /= 10;
		}
		
		armor -= amount;
		
		if(armor < 0) {				
			super.damage(damager, amount);
		}
		
	}

	
	/* (non-Javadoc)
	 * @see seventh.game.Entity#kill(seventh.game.Entity)
	 */
	@Override
	public void kill(Entity killer) {
		this.killer = killer;
		this.isDying = true;
		this.explosionTimer.start();
		this.blowupTimer.start();
		
		if(hasOperator()) {
			getOperator().kill(killer);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * 
	 * @see seventh.game.PlayerEntity#setOrientation(float)
	 */
	@Override
	public void setOrientation(float orientation) {
		final float fullCircle = FastMath.fullCircle;
		if(desiredOrientation < 0) {
			desiredOrientation += fullCircle;
		}
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
		final float fullCircle = FastMath.fullCircle;
		if(desiredOrientation < 0) {
			desiredOrientation += fullCircle;
		}
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
