/*
 * see license.txt 
 */
package seventh.game.entities;

import java.util.List;

import leola.vm.types.LeoObject;
import seventh.game.Game;
import seventh.game.entities.vehicles.Vehicle;
import seventh.game.net.NetEntity;
import seventh.map.Map;
import seventh.map.Tile;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Debugable;
import seventh.shared.Geom;
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;

/**
 * Base class for anything that is able to be interacted with in the game world.
 * 
 * 
 * @author Tony
 *
 */
public abstract class Entity implements Debugable {
    
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
        
        /**
         * Entity is destroyed, and shouldn't be
         * removed (only DEAD will cause a removal)
         */
        DESTROYED,
        
        /**
         * Entity is dead and should be removed
         */
        DEAD,
        
        ;
        
        public byte netValue() {
            return (byte)ordinal();
        }
        
        private static State[] values = values();
        
        public static State fromNetValue(byte b) {
            return values[b];
        }
        
        /**
         * @return true if we are in the vehicle operation states
         */
        public boolean isVehicleState() {
            return this==OPERATING_VEHICLE||
                    this==ENTERING_VEHICLE||
                    this==EXITING_VEHICLE;
        }
        
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {
            return "\"" + name() + "\"";
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
        SMOKE,
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
        SMOKE_GRENADE,
        NAPALM_GRENADE,
        PISTOL,
        
        MG42,
        
        BOMB,
        BOMB_TARGET,
        
        LIGHT_BULB,        
        DROPPED_ITEM,
        HEALTH_PACK,         
        DOOR,
        
        /* Vehicles */
        SHERMAN_TANK,
        PANZER_TANK,
        
        ALLIED_FLAG,
        AXIS_FLAG,
        
        
        UNKNOWN,
        
        ;
        
        /**
         * @return the byte representation of this Type
         */
        public byte netValue() {
            return (byte)this.ordinal();
        }
        
        private static Type[] values = values();
        
        /**
         * @param value the network value
         * @return the Type
         */
        public static Type fromNet(byte value) {
            if(value < 0 || value >= values.length) {
                return UNKNOWN;
            }
            
            return values[value];
        }
        
        /**
         * @return true if this is a vehicle
         */
        public boolean isVehicle() {
            return this == SHERMAN_TANK ||
                   this == PANZER_TANK;
        }
        
        /**
         * @return true if this is a player
         */
        public boolean isPlayer() {
            return this == PLAYER || this == PLAYER_PARTIAL;
        }
        
        public boolean isDoor() {
            return this == DOOR;
        }
        
        /**
         * @return true if this is entity type can take damage
         */
        public boolean isDamagable() {
            return isPlayer() || isVehicle() || isDoor();
        }
        
        /* (non-Javadoc)
         * @see java.lang.Enum#toString()
         */
        @Override
        public String toString() {        
            return "\"" + name() + "\"";
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
    protected Vector2f cache;
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
    private int maxHealth;
    
    protected Game game;
    
    private Type type;        
    protected int id;
    
    private int events;

    private LeoObject scriptObj;
    
    /* if states become unweidly, convert to EntityStateMachine */
    private long walkingTime;
    private static final long WALK_TIME = 150;
    
//    private long deadTime;
//    private static final long DEAD_TIME = 150;
                
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
        this.maxHealth = 100;
        this.health = this.maxHealth;
        
        this.movementDir = new Vector2f();
        this.cache = new Vector2f();
        
        this.bounds = new Rectangle();    
        this.bounds.setLocation(position);
        this.centerPos = new Vector2f();
        
        this.scriptObj = LeoObject.valueOf(this);
    }
    
    /**
     * @return this {@link Entity} as a {@link LeoObject}
     */
    public LeoObject asScriptObject() {
        return this.scriptObj;
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
     * @param type
     * @return true if the supplied type name is of the game {@link Type}.  This
     * is used in Leola scripts to type checking
     */
    public boolean isType(String type) {
        return getType().name().equalsIgnoreCase(type);
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
     * @return the maxHealth
     */
    public int getMaxHealth() {
        return maxHealth;
    }
    
    /**
     * @param maxHealth the maxHealth to set
     */
    public void setMaxHealth(int maxHealth) {
        this.maxHealth = maxHealth;
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
        
        this.movementDir.zeroOut();
        if(this.isAlive && !this.vel.isZero()) {
            if(currentState != State.WALKING && currentState != State.SPRINTING) {
                currentState = State.RUNNING;
            }
                                
            int movementSpeed = calculateMovementSpeed();
            
            float dt = (float)timeStep.asFraction();            
            float deltaX = (vel.x * movementSpeed * dt);
            float deltaY = (vel.y * movementSpeed * dt);
            
            float newX = pos.x + deltaX;
            float newY = pos.y + deltaY;
                        
            if( Math.abs(pos.x - newX) > 2.5) {
                this.movementDir.x = vel.x;
            }
            
            if( Math.abs(pos.y - newY) > 2.5) {
                this.movementDir.y = vel.y;
            }
            
            
            Map map = game.getMap();
            
            bounds.x = (int)newX;
            if( map.rectCollides(bounds) ) {
                isBlocked = collideX((int)newX, bounds.x);
                if(isBlocked) { 
                    bounds.x = (int)pos.x;
                    newX = pos.x;    
                }
                                
            }
            else if(collidesAgainstEntity(bounds)) {
                bounds.x = (int)pos.x;
                newX = pos.x;
                isBlocked = true;
            }
            
            
            bounds.y = (int)newY;
            if( map.rectCollides(bounds)) {
                isBlocked = collideY((int)newY, bounds.y);
                if(isBlocked) {
                    bounds.y = (int)pos.y;
                    newY = pos.y;
                }
            }
            else if(collidesAgainstEntity(bounds)) {                
                bounds.y = (int)pos.y;
                newY = pos.y;
                isBlocked = true;
            }
            

            /* some things want to stop dead it their tracks
             * if a component is blocked
             */
            if(isBlocked && !continueIfBlock()) {
                bounds.setLocation(pos);                
                
                newX = pos.x;
                newY = pos.y;
            }
                        
//            pos.x = bounds.x;
//            pos.y = bounds.y;
        
            pos.x = newX;
            pos.y = newY;
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
    
    protected boolean collidesAgainstEntity(Rectangle bounds) {
        return collidesAgainstVehicle(bounds) || collidesAgainstDoor(bounds);
    }
    
    protected boolean collidesAgainstDoor(Rectangle bounds) {
        List<Door> doors = game.getDoors();
        for(int i = 0; i < doors.size(); i++) {
            Door door = doors.get(i);
            if(door.isTouching(bounds)) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Determines if the bounds touches any vehicle
     * @param bounds
     * @return true if we collide with a vehicle
     */
    protected boolean collidesAgainstVehicle(Rectangle bounds) {
        if(!getType().isPlayer()) {
            return false;
        }
        
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
                    
                    // do a more expensive collision detection
                    if(vehicle.isTouching(this)) {
                    
                        if(vehicle.isMoving() /*&& vehicle.getBounds().contains(bounds)*/) {
                            kill(vehicle);
                        }
                        
                        break;
                    }    
                    else {
                        collides = false;
                        break;
                    }
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
     * @param other
     * @return the distance from this entity to the other (squared)
     */
    public float distanceFromSq(Entity other) {
        return Vector2f.Vector2fDistanceSq(getCenterPos(), other.getCenterPos());
    }
    
    /**
     * @param position
     * @return the distance from this entity to the position (squared)
     */
    public float distanceFromSq(Vector2f position) {
        return Vector2f.Vector2fDistanceSq(getCenterPos(), position);
    }
    
    /**
     * 
     * @param target
     * @return true if this {@link Entity} is facing at the target {@link Vector2f}
     */
    public boolean isFacing(Vector2f target) {
        double angle = Vector2f.Vector2fAngle(getFacing(), target);
        return Math.abs(angle) < Math.PI/4d;
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
//        netEntity.width = (byte)this.bounds.width;
//        netEntity.height = (byte)this.bounds.height;
        
        netEntity.orientation = getNetOrientation();        
    }
    
    /**
     * Picks the closest entity from the list to this one
     * @param entities
     * @return the closest entity to this one, null if the list is empty
     */
    public <T extends Entity> T getClosest(List<T> entities) {
        if(entities==null || entities.isEmpty()) {
            return null;
        }
        
        if(entities.size() < 2) {
            return entities.get(0);
        }
        
        Vector2f myPos = getPos();
        T closest = null;
        float closestDist = 0.0f;
        
        for(int i = 0; i < entities.size(); i++) {
            T other = entities.get(i);
            
            float dist = Vector2f.Vector2fDistanceSq(myPos, other.getPos());
            
            if( (closest==null) || dist < closestDist ) {                
                closestDist = dist;
                closest = other;
            }
            
        }
        
        return closest;
    }
    
    /**
     * @param tiles an empty list of tiles that is used as the returned list
     * @return calculates the line of sight, which returns a {@link List} of {@link Tile} this entity
     * is able to see
     */
    public List<Tile> calculateLineOfSight(List<Tile> tiles) {
        Map map = game.getMap();
        Geom.calculateLineOfSight(tiles, centerPos, getFacing(), WeaponConstants.DEFAULT_LINE_OF_SIGHT, map, getHeightMask(), cache);
        return tiles;
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Debugable#getDebugInformation(seventh.shared.Debugable.DebugEntryChain)
     */
    @Override
    public DebugInformation getDebugInformation() {
        DebugInformation me = new DebugInformation();
        me.add("id", getId())
          .add("pos", getPos())
          .add("vel", getVel())
          .add("movementDir", getMovementDir())
          .add("centerPos", getCenterPos())
          .add("facing", getFacing())
          .add("bounds", getBounds())
          .add("orientation", getOrientation())
          .add("state", getCurrentState())
          .add("speed", getSpeed())
          .add("isAlive", isAlive())
          .add("health", getHealth())
          .add("type", getType());                
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

