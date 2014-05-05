/*
 * see license.txt 
 */
package seventh.game;

import seventh.game.net.NetPlayer;
import seventh.game.net.NetWeapon;
import seventh.game.weapons.Bullet;
import seventh.game.weapons.Explosion;
import seventh.game.weapons.GrenadeBelt;
import seventh.game.weapons.MechRailgun;
import seventh.game.weapons.RocketLauncher;
import seventh.game.weapons.Weapon;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * The big Mech that can be controlled by a player
 * 
 * @author Tony
 *
 */
public class MechPlayerEntity extends PlayerEntity {

	private long nextMovementSound;
	private long lastTorsoMovementSound;
	
	private boolean isRetracting;
	private float movementTime;
	private float movementRate;
	private float desiredOrientation;
	
	private MechRailgun railgun;
	private RocketLauncher rocketLauncher;
		
	/**
	 * @param id
	 * @param position
	 * @param game
	 */
	public MechPlayerEntity(int id, Vector2f position, Game game) {
		super(id, position, game);
		setType(Type.MECH);
		
		this.getInventory().addItem(new GrenadeBelt(game, this));
				
		this.railgun = new MechRailgun(game, this) {
									
			@Override
			protected Vector2f newBulletPosition() {
//				Vector2f ownerDir = owner.getFacing();
				Vector2f ownerPos = owner.getCenterPos();				
				Vector2f pos = new Vector2f();
				
				Vector2f ownerDir = new Vector2f();
				ownerDir.set(1, 0); // make right vector
				Vector2f.Vector2fRotate(ownerDir, owner.getOrientation() - 0.5f, ownerDir);
				
				float x =  -ownerDir.y * 70.0f;
				float y =  ownerDir.x * 70.0f;
				
				Vector2f.Vector2fMA(ownerPos, ownerDir, 25.0f, pos);
				pos.x += x;
				pos.y += y;
				
				return pos;
			}
		};
		
		this.rocketLauncher = new RocketLauncher(game, this){	
			
			@Override
			protected Vector2f newRocketPosition() {
				this.bulletsInClip = 500;

				Vector2f ownerDir = owner.getFacing();
				Vector2f ownerPos = owner.getCenterPos();				
				Vector2f pos = new Vector2f();
				
				float x =  ownerDir.y * 50.0f;
				float y = -ownerDir.x * 50.0f;
				
				Vector2f.Vector2fMA(ownerPos, ownerDir, 105.0f, pos);

				pos.x += x;
				pos.y += y;
							
				return pos;
			}
		};
				
		bounds.width = WeaponConstants.MECH_WIDTH;
		bounds.height = WeaponConstants.MECH_HEIGHT;
		
		setLineOfSight(WeaponConstants.MECH_DEFAULT_LINE_OF_SIGHT);		
		

		NetPlayer player = getNetPlayer();
		player.isMech = true;
		player.weapon = new NetWeapon();
		
		onTouch = new OnTouchListener() {
			
			@Override
			public void onTouch(Entity me, Entity other) {
				/* kill the other players */
				if( other.getType() == Type.PLAYER ) {
					other.kill(me);
				}
			}
		};
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#update(seventh.shared.TimeStep)
	 */
	@Override
	public boolean update(TimeStep timeStep) {	
		if((currentState==State.RUNNING||currentState==State.SPRINTING)) {
			movementTime += timeStep.asFraction();
			movementRate = Math.abs((float)Math.cos(movementTime)) + 0.0f;
		}
		else {
			movementTime = 0;
		}
		
		final float PI2 = (float)(Math.PI*2.0f);
		float deltaOrientation = this.desiredOrientation - this.orientation;

		if( deltaOrientation > Math.PI) {			
			deltaOrientation -= PI2;
			//System.out.println("Over: " + deltaOrientation);
		}
		else if (deltaOrientation < -Math.PI) {
			deltaOrientation += PI2;
		}

		final float threshold = 0.07f;
		if(deltaOrientation > threshold) {
			deltaOrientation = 1.0f;
			
		}
		else if(deltaOrientation < -threshold) {
			deltaOrientation = -1.0f;
		}
		else {
			deltaOrientation = 0.0f;
		}
		
		if(deltaOrientation != 0.0f) {
			lastTorsoMovementSound -= timeStep.getDeltaTime();
			
			if(lastTorsoMovementSound <= 0) {
				game.emitSound(getId(), SoundType.MECH_TORSO_MOVE, getCenterPos());
				lastTorsoMovementSound = 10001800;
			}
			
		}
		else {
			lastTorsoMovementSound = 300;
		}
		
		this.orientation += deltaOrientation * 0.8f * timeStep.asFraction(); 
		if(this.orientation > PI2) {
			this.orientation -= PI2;			
		}
		else if(this.orientation < -PI2) {
			this.orientation += PI2;
			
		}
				
//		System.out.println(orientation + " vs. " + desiredOrientation);
		
		this.facing.set(1, 0); // make right vector
		Vector2f.Vector2fRotate(this.facing, orientation, this.facing);
		
		this.railgun.update(timeStep);
		this.rocketLauncher.update(timeStep);
		
		boolean isBlocked = super.update(timeStep);
		game.doesTouchPlayers(this);
		
		return isBlocked;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#makeMovementSounds(seventh.shared.TimeStep)
	 */
	@Override
	protected void makeMovementSounds(TimeStep timeStep) {
		if(nextMovementSound <= 0) {
			SoundType snd = SoundType.MECH_STEP1;
			if(isRetracting) {
				snd = SoundType.MECH_STEP2;								
			}
			
			isRetracting = !isRetracting;
			game.emitSound(getId(), snd, getCenterPos());
			nextMovementSound = 700;//1500;
		}
		else {
			if((currentState==State.RUNNING||currentState==State.SPRINTING)) {
				nextMovementSound -= timeStep.getDeltaTime();
			}
			else {
				nextMovementSound = 1;
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#damage(seventh.game.Entity, int)
	 */
	@Override
	public void damage(Entity damager, int amount) {
		if(damager instanceof Bullet) {
			amount /= 20;
		}
		else if(damager instanceof Explosion) {
			amount = 1;
		}
		else {
			amount /= 10;
		}
		
		super.damage(damager, amount);
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#setOrientation(float)
	 */
	@Override
	public void setOrientation(float orientation) {	
		this.desiredOrientation = orientation;		
	}
		
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#meleeAttack()
	 */
	@Override
	public boolean meleeAttack() {
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#standup()
	 */
	@Override
	public void standup() {
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#crouch()
	 */
	@Override
	public void crouch() {
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#sprint()
	 */
	@Override
	public void sprint() {
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#reload()
	 */
	@Override
	public void reload() {
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#nextWeapon()
	 */
	@Override
	public void nextWeapon() {
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#prevWeapon()
	 */
	@Override
	public void prevWeapon() {
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#beginFire()
	 */
	@Override
	public boolean beginFire() {
		return railgun.beginFire();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#endFire()
	 */
	@Override
	public boolean endFire() {	
		return railgun.endFire();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#pullGrenadePin()
	 */
	@Override
	public boolean pullGrenadePin() {
		return rocketLauncher.beginFire();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#throwGrenade()
	 */
	@Override
	public boolean throwGrenade() {
		return rocketLauncher.endFire();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#dropItem(boolean)
	 */
	@Override
	public void dropItem(boolean makeSound) {
		/* mechs can't drop their weapons */
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#pickupItem(seventh.game.weapons.Weapon)
	 */
	@Override
	public void pickupItem(Weapon weapon) {
		/* mechs can't pick up weapons */
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#calculateMovementSpeed()
	 */
	@Override
	protected int calculateMovementSpeed() {	
//		return (int)(WeaponConstants.MECH_MOVEMENT_SPEED*movementRate);
		return WeaponConstants.MECH_MOVEMENT_SPEED;
	}

	/* (non-Javadoc)
	 * @see seventh.game.PlayerEntity#getNetPlayer()
	 */
	@Override
	public NetPlayer getNetPlayer() {
		NetPlayer player = super.getNetPlayer();	
		if(player.weapon != null) {
			player.weapon.state = railgun.getState().netValue();
			player.weapon.type = rocketLauncher.getState().netValue();
		}
		return player;
	}
}
