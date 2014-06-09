/*
 * see license.txt 
 */
package seventh.game;

import java.util.List;

import seventh.game.net.NetEntity;
import seventh.game.vehicles.Vehicle;
import seventh.map.Map;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Base class for anything that is able to be interacted with in the game world.
 * 
 * 
 * @author Tony
 *
 */
public abstract class Entity {
	
	/**
	 * Invalid entity ID
	 */
	public static final int INVALID_ENTITY_ID = Integer.MIN_VALUE;

	/**
	 * The entities state
	 * 
	 * @author Tony
	 *
	 */
	public static enum State {
		IDLE,
		CROUCHING,
		WALKING,
		RUNNING,
		SPRINTING,
		
		ENTERING_VEHICLE,
		OPERATING_VEHICLE,
		EXITING_VEHICLE,
		
		DEAD,
		
		;
		
		public byte netValue() {
			return (byte)ordinal();
		}
		
		public static State fromNetValue(byte b) {
			return values()[b];
		}
		
		/**
		 * @return true if we are in the vehicle operation states
		 */
		public boolean isVehicleState() {
			return this==OPERATING_VEHICLE||
					this==ENTERING_VEHICLE||
					this==EXITING_VEHICLE;
		}
	}
	
	public static enum Events {
		DAMAGE(1<<0),
		KILLED(1<<1),
		;
		
		private Events(int e) {
			this.netValue = e;
		}
		
		private int netValue;
		
		public int netValue() {
			return netValue;
		}
		
	}
	
	/**
	 * Entity Type
	 * 
	 * @author Tony
	 *
	 */
	public static enum Type {
		PLAYER_PARTIAL, 
		PLAYER,
		BULLET,
		EXPLOSION,
		FIRE,
		AMMO,
		ROCKET,
		
		// Alies Weapons		
		THOMPSON,				
		SPRINGFIELD,		
		M1_GARAND,
		
		// Axis Weapons
		KAR98,
		MP44,
		MP40,
				
		// Shared Weapons
		RISKER,
		SHOTGUN,
		ROCKET_LAUNCHER,
		GRENADE,
		NAPALM_GRENADE,
		PISTOL,
		
		RAILGUN,
		
		BOMB,
		BOMB_TARGET,
		
		LIGHT_BULB,		
		DROPPED_ITEM,
								
		/* Vehicles */
		TANK,
		
		UNKNOWN,
		
		;
		
		/**
		 * @return the byte representation of this Type
		 */
		public byte netValue() {
			return (byte)this.ordinal();
		}
		
		
		/**
		 * @param value the network value
		 * @return the Type
		 */
		public static Type fromNet(byte value) {
			if(value < 0 || value >= values().length) {
				return UNKNOWN;
			}
			
			return values()[value];
		}
		
		/**
		 * @return true if this is a vehicle
		 */
		public boolean isVehicle() {
			return this == TANK;
		}
		
		/**
		 * @return true if this is a player
		 */
		public boolean isPlayer() {
			return this == PLAYER || this == PLAYER_PARTIAL;
		}
		
		/**
		 * @return true if this is entity type can take damage
		 */
		public boolean isDamagable() {
			return isPlayer() || isVehicle();
		}
	}

	public static float getAngleBetween(Vector2f a, Vector2f b) {
		return (float)Math.atan2(a.y-b.y,a.x-b.x);
	}
	
	/**
	 * Listens for when an Entity is killed
	 * @author Tony
	 *
	 */
	public static interface KilledListener {
		void onKill(Entity entity, Entity killer);
	}
	
	/**
	 * Listens for when an Entity is damaged
	 * @author Tony
	 *
	 */
	public static interface OnDamageListener {
		void onDamage(Entity damager, int amount);
	}
	
	
	/**
	 * Listens for when an Entity is touched
	 * 
	 * @author Tony
	 *
	 */
	public static interface OnTouchListener {
		void onTouch(Entity me, Entity other);
	}
	
	public KilledListener onKill;
	public OnDamageListener onDamage;
	public OnTouchListener onTouch;
	
	protected Vector2f pos;
	protected Vector2f vel;
	private Vector2f centerPos;
	private Vector2f movementDir;
	
	protected Rectangle bounds;
	
	protected float orientation;
	protected Vector2f facing;
	
	protected State currentState;
	protected int speed;
	
	private boolean canTakeDamage;
	private boolean isAlive;
	private int health;
	
	protected Game game;
	
	private Type type;		
	protected int id;
	
	private int events;
	
	/* if states become unweidly, convert to EntityStateMachine */
	private long walkingTime;
	private static final long WALK_TIME = 150;
	
//	private long deadTime;
//	private static final long DEAD_TIME = 150;
				
	public static final int STANDING_HEIGHT_MASK = 0;
	public static final int CROUCHED_HEIGHT_MASK = 1;
	
	public int deadFrame;
	
	/**
	 * @param position
	 * @param speed
	 * @param game
	 * @param type
	 */
	public Entity(Vector2f position, int speed, Game game, Type type) {
		this(game.getNextEntityId(), position, speed, game, type);
	}
	/**
	 * @param position
	 * @param speed
	 * @param game
	 * @param type
	 */
	public Entity(int id, Vector2f position, int speed, Game game, Type type) {	
		this.id = id;
		this.type = type;
		this.pos = position;
		this.speed = speed;
		this.game = game;
		
		this.currentState = State.IDLE;
		this.facing = new Vector2f(1,0);
		this.setOrientation(0);
		
		this.vel = new Vector2f();
		this.isAlive = true;
		this.canTakeDamage = true;
		this.health = 100;
		
		this.movementDir = new Vector2f();
		
		this.bounds = new Rectangle();	
		this.bounds.setLocation(position);
		this.centerPos = new Vector2f();
	}
	
	/**
	 * @return true if this entity can take damage
	 */
	public boolean canTakeDamage() {
		return this.canTakeDamage;
	}
	
	/**
	 * @param canTakeDamage the canTakeDamage to set
	 */
	public void setCanTakeDamage(boolean canTakeDamage) {
		this.canTakeDamage = canTakeDamage;
	}

	/**
	 * @return the height mask for if the entity is crouching or standing
	 */
	public int getHeightMask() {
		if( currentState == State.CROUCHING ) {
			return CROUCHED_HEIGHT_MASK;
		}
		return STANDING_HEIGHT_MASK;
	}
	
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}
	
	/**
	 * @param events the events to set
	 */
	public void setEvents(int events) {
		this.events = events;
	}
	
	public void addEvent(Events event) {
		this.events |= event.netValue();
	}
	
	public void removeEvent(Events event) {
		this.events &= ~event.netValue();
	}
	
	/**
	 * @return the events
	 */
	public int getEvents() {
		return events;
	}
	
	
	/**
	 * @param type the type to set
	 */
	protected void setType(Type type) {
		this.type = type;
	}
	
	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * @return the bounds
	 */
	public Rectangle getBounds() {
		return bounds;
	}
	
	/**
	 * @return the orientation
	 */
	public float getOrientation() {
		return orientation;
	}
	
	/**
	 * @return the network friendly version of the orientation
	 */
	public short getNetOrientation() {
		return (short) Math.toDegrees(getOrientation());
	}
	
	/**
	 * @return the centerPos
	 */
	public Vector2f getCenterPos() {
		centerPos.set(pos.x + bounds.width/2, pos.y + bounds.height/2);
		return centerPos;
	}
	
	/**
	 * @param orientation the orientation to set
	 */
	public void setOrientation(float orientation) {		
		this.orientation = orientation;
		this.facing.set(1, 0); // make right vector
		Vector2f.Vector2fRotate(this.facing, /*Math.toRadians(d)*/ orientation, this.facing);
	}
	
	/**
	 * @return the facing
	 */
	public Vector2f getFacing() {
		return facing;
	}
	
	/**
	 * @return the health
	 */
	public int getHealth() {
		return health;
	}
	
	/**
	 * @param health the health to set
	 */
	public void setHealth(int health) {
		this.health = health;
	}
	
	public void moveTo(Vector2f pos) {
		this.pos.set(pos);
		this.bounds.setLocation(pos);
	}

	/**
	 * @return the movement direction
	 */
	public Vector2f getMovementDir() {
		return movementDir;
	}
	
	/**
	 * @return the pos
	 */
	public Vector2f getPos() {
		return pos;
	}
	
	/**
	 * @return the speed
	 */
	public int getSpeed() {
		return speed;
	}
	
	/**
	 * @return the vel
	 */
	public Vector2f getVel() {
		return vel;
	}
	
	/**
	 * @return the currentState
	 */
	public State getCurrentState() {
		return currentState;
	}
	
	/**
	 * @return the isAlive
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	protected int calculateMovementSpeed() {
		return speed;
	}
	
	/**
	 * Invoked when the x component collides with a map element
	 * @param newX
	 * @param oldX
	 */
	protected boolean collideX(int newX, int oldX) {
		return true;
	}
	
	/**
	 * Invoked when the y component collides with a map element
	 * @param newY
	 * @param oldY
	 */
	protected boolean collideY(int newY, int oldY) {
		return true;
	}
	
	/**
	 * Continue to check Y coordinate if X was blocked
	 * @return true if we should continue collision checks
	 */
	protected boolean continueIfBlock() {
		return true;
	}
	
	/**
	 * @param dt
	 * @return true if blocked
	 */
	public boolean update(TimeStep timeStep) {
		boolean isBlocked = false;
		
//		this.movementDir.set(vel);
		this.movementDir.zeroOut();
		if(this.isAlive && !this.vel.isZero()) {
			if(currentState != State.WALKING && currentState != State.SPRINTING) {
				currentState = State.RUNNING;
			}
								
			int movementSpeed = calculateMovementSpeed();
						
			
			double dt = timeStep.asFraction();
			int newX = (int)Math.round(pos.x + vel.x * movementSpeed * dt);
			int newY = (int)Math.round(pos.y + vel.y * movementSpeed * dt);					
			
//			this.movementDir.zeroOut();
			if( Math.abs(pos.x - newX) > 2.5) {
				this.movementDir.x = vel.x;
			}
			
			if( Math.abs(pos.y - newY) > 2.5) {
				this.movementDir.y = vel.y;
			}
			
			
			Map map = game.getMap();
			
			bounds.x = newX;
			if( map.rectCollides(bounds) ) {
				isBlocked = collideX(newX, bounds.x);
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
				isBlocked = collideY(newY, bounds.y);
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
			
			this.walkingTime = WALK_TIME;
		}
		else {						
			if(this.walkingTime<=0 && currentState!=State.CROUCHING) {
				currentState = State.IDLE;
			}
			
			this.walkingTime -= timeStep.getDeltaTime();
		
		}
		
		
		
		return isBlocked;
	}
	
	/**
	 * Determines if the bounds touches any vehicle
	 * @param bounds
	 * @return true if we collide with a vehicle
	 */
	protected boolean collidesAgainstVehicle(Rectangle bounds) {
		boolean collides = false;
		List<Vehicle> vehicles = game.getVehicles();
		for(int i = 0; i < vehicles.size(); i++) {
			Vehicle vehicle = vehicles.get(i);
			if(vehicle.isAlive() && this != vehicle && this != vehicle.getOperator()) {
				
				/* determine if moving to this bounds we would touch
				 * the vehicle
				 */
				collides = bounds.intersects(vehicle.getBounds());
				
				/* if we touched, determine if we would be inside
				 * the vehicle, if so, kill us
				 */
				if(collides) {
					
					if(getType().isPlayer() && vehicle.isMoving() && vehicle.getBounds().contains(bounds)) {
						kill(vehicle);
					}
					
					break;
				}
			}
			
			
		}
		
		return collides;
	}
	
	/**
	 * @param other
	 * @return true if this entity is touching the other one
	 */
	public boolean isTouching(Entity other) {
		if(other != null) {
			return this.bounds.intersects(other.bounds);
		}
		return false;
	}
	
	/**
	 * Does not broadcast that this entity
	 * is dead. 
	 */
	public void softKill() {
		this.isAlive = false;
	}
	
	/**
	 * Kills this entity
	 */
	public void kill(Entity killer) {
		if (isAlive) {
			currentState = State.DEAD;
			
			health = 0;
			isAlive = false;
			
			if(onKill != null) {
				onKill.onKill(this, killer);
			}
		
			addEvent(Events.KILLED);
		}
	}
	
	/**
	 * Damages this entity
	 * 
	 * @param damager
	 * @param amount
	 */
	public void damage(Entity damager, int amount) {
		health -= amount;
		
		if(health <= 0) {
			kill(damager);
		}
		else {
			if (onDamage != null) {
				onDamage.onDamage(damager, amount);
			}
			addEvent(Events.DAMAGE);
		}
	}
	
	/**
	 * @return the networked object representation of this {@link Entity}
	 */
	public abstract NetEntity getNetEntity();
	
	/**
	 * Sets the base {@link NetEntity} information
	 * @param netEntity
	 */
	protected void setNetEntity(NetEntity netEntity) {
		netEntity.id = this.id;
		
		netEntity.posX = (short)this.pos.x;
		netEntity.posY = (short)this.pos.y;
//		netEntity.width = (byte)this.bounds.width;
//		netEntity.height = (byte)this.bounds.height;
		
		netEntity.orientation = getNetOrientation();		
	}
	
	/**
	 * Picks the closest entity from the list to this one
	 * @param entities
	 * @return the closest entity to this one, null if the list is empty
	 */
	public Entity getClosest(List<? extends Entity> entities) {
		if(entities==null || entities.isEmpty()) {
			return null;
		}
		
		if(entities.size() < 2) {
			return entities.get(0);
		}
		
		Vector2f myPos = getPos();
		Entity closest = null;
		float closestDist = 0.0f;
		
		for(int i = 0; i < entities.size(); i++) {
			Entity other = entities.get(i);
			
			float dist = Vector2f.Vector2fDistanceSq(myPos, other.getPos());
			
			if( (closest==null) || dist < closestDist ) {				
				closestDist = dist;
				closest = other;
			}
			
		}
		
		return closest;
	}
}

