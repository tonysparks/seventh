/*
 * see license.txt 
 */
package seventh.game.entities;

import static seventh.shared.SeventhConstants.ENTERING_VEHICLE_TIME;
import static seventh.shared.SeventhConstants.EXITING_VEHICLE_TIME;
import static seventh.shared.SeventhConstants.MAX_PRIMARY_WEAPONS;
import static seventh.shared.SeventhConstants.MAX_STAMINA;
import static seventh.shared.SeventhConstants.PLAYER_HEARING_RADIUS;
import static seventh.shared.SeventhConstants.PLAYER_HEIGHT;
import static seventh.shared.SeventhConstants.PLAYER_MIN_SPEED;
import static seventh.shared.SeventhConstants.PLAYER_SPEED;
import static seventh.shared.SeventhConstants.PLAYER_WIDTH;
import static seventh.shared.SeventhConstants.RECOVERY_TIME;
import static seventh.shared.SeventhConstants.RUN_DELAY_TIME;
import static seventh.shared.SeventhConstants.SPRINT_DELAY_TIME;
import static seventh.shared.SeventhConstants.SPRINT_SPEED_FACTOR;
import static seventh.shared.SeventhConstants.STAMINA_DECAY_RATE;
import static seventh.shared.SeventhConstants.STAMINA_RECOVER_RATE;
import static seventh.shared.SeventhConstants.WALK_SPEED_FACTOR;

import java.util.List;

import seventh.game.Controllable;
import seventh.game.Game;
import seventh.game.Inventory;
import seventh.game.Player;
import seventh.game.PlayerClass;
import seventh.game.PlayerClass.WeaponEntry;
import seventh.game.SoundEventPool;
import seventh.game.SurfaceTypeToSoundType;
import seventh.game.Team;
import seventh.game.entities.vehicles.Vehicle;
import seventh.game.events.SoundEmittedEvent;
import seventh.game.net.NetEntity;
import seventh.game.net.NetPlayer;
import seventh.game.net.NetPlayerPartial;
import seventh.game.weapons.Bullet;
import seventh.game.weapons.FlameThrower;
import seventh.game.weapons.GrenadeBelt;
import seventh.game.weapons.Hammer;
import seventh.game.weapons.Kar98;
import seventh.game.weapons.M1Garand;
import seventh.game.weapons.MP40;
import seventh.game.weapons.MP44;
import seventh.game.weapons.Pistol;
import seventh.game.weapons.Risker;
import seventh.game.weapons.RocketLauncher;
import seventh.game.weapons.Shotgun;
import seventh.game.weapons.Smoke;
import seventh.game.weapons.Springfield;
import seventh.game.weapons.Thompson;
import seventh.game.weapons.Weapon;
import seventh.map.Map;
import seventh.map.Tile;
import seventh.map.Tile.SurfaceType;
import seventh.math.Line;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Geom;
import seventh.shared.SoundType;
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
        UP                  (1<<0),
        DOWN                (1<<1),
        LEFT                (1<<2),
        RIGHT               (1<<3),
        WALK                (1<<4),
        FIRE                (1<<5),
        RELOAD              (1<<6),
        WEAPON_SWITCH_UP    (1<<7),
        WEAPON_SWITCH_DOWN  (1<<8),
        
        THROW_GRENADE       (1<<9),
        
        SPRINT              (1<<10),
        CROUCH              (1<<11),
        
        USE                 (1<<12),
        DROP_WEAPON         (1<<13),
        MELEE_ATTACK        (1<<14),
        IRON_SIGHTS         (1<<15),
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
    
    private Rectangle headshot, limbshot;
    private Vector2f bulletDir;
    
    
    private int speedMultiplier, damageMultiplier;
    
    /**
     * @param position
     * @param speed
     * @param game
     */
    public PlayerEntity(int id, PlayerClass playerClass, Vector2f position, Game game) {
        super(id, position, PLAYER_SPEED, game, Type.PLAYER);
                        
        this.player = new NetPlayer();
        this.player.id = id;
        
        this.partialPlayer = new NetPlayerPartial();
        this.partialPlayer.id = id;
                
        this.bounds.set(position, PLAYER_WIDTH, PLAYER_HEIGHT);
        this.inputVel = new Vector2f();
        this.enemyDir = new Vector2f();
        
        this.headshot = new Rectangle(4, 4);
        this.limbshot = new Rectangle(10, 10);
        this.bulletDir = new Vector2f();
                
        this.inventory = new Inventory(MAX_PRIMARY_WEAPONS);                        
        this.hearingBounds = new Rectangle(PLAYER_HEARING_RADIUS, PLAYER_HEARING_RADIUS);
        
        this.stamina = MAX_STAMINA;
        
        this.visualBounds = new Rectangle(2000, 2000);
        
        this.speedMultiplier  = playerClass.getSpeedMultiplier();
        this.damageMultiplier = playerClass.getDamageMultiplier();
        
        setLineOfSight(WeaponConstants.DEFAULT_LINE_OF_SIGHT);
        setHearingRadius(PLAYER_HEARING_RADIUS);
        
        setMaxHealth(getMaxHealth() + playerClass.getHealthMultiplier());
        setHealth(getMaxHealth());
    }
    
    
    /**
     * Apply the player class
     * 
     * @param playerClass
     * @param activeWeapon
     * @return true if success, false otherwise
     */
    public void setPlayerClass(PlayerClass playerClass, Type activeWeapon) {
        if(!playerClass.isAvailableWeapon(activeWeapon)) {
            activeWeapon = playerClass.getDefaultPrimaryWeapon().getTeamWeapon(getTeam());
        }
        
        this.inventory.clear();        

        WeaponEntry weaponEntry = playerClass.getAvailableWeaponEntry(activeWeapon);
        Weapon currentWeapon = createWeapon(weaponEntry);
        if(currentWeapon != null) {
            this.inventory.addItem(currentWeapon);
        }
        
        List<WeaponEntry> weapons = playerClass.getDefaultWeapons();
        for(int i = 0; i < weapons.size(); i++) {
            WeaponEntry entry = weapons.get(i);
            Weapon weapon = createWeapon(entry);
            
            if(weapon != null) {
                this.inventory.addItem(weapon);
            }
        }
        
        
        checkLineOfSightChange();        
    }
    
    private Weapon createWeapon(WeaponEntry entry) {
        Weapon weapon = null;

        switch(entry.type.getTeamWeapon(getTeam())) {        
            case SPRINGFIELD:
            case KAR98:                
                weapon = isAlliedPlayer() ? new Springfield(game, this) : new Kar98(game, this);                
                break;
                
            case THOMPSON:
            case MP40:                
                weapon = isAlliedPlayer() ? new Thompson(game, this) : new MP40(game, this);
                break;
                
            case M1_GARAND:
            case MP44:
                weapon = isAlliedPlayer() ? new M1Garand(game, this) : new MP44(game, this);                
                break;
                
            case PISTOL:
                weapon = new Pistol(game, this);
                break;
            case HAMMER:
                weapon = new Hammer(game, this);
                break;
            case SHOTGUN:
                weapon = new Shotgun(game, this);
                break;
            case RISKER: 
                weapon = new Risker(game, this);
                break;
            case ROCKET_LAUNCHER:
                weapon = new RocketLauncher(game, this);                
                break;
            case FLAME_THROWER:
                weapon = new FlameThrower(game, this);
                break;
            case GRENADE:
                weapon = GrenadeBelt.newFrag(game, this, entry.ammoBag);
                break;
            case SMOKE_GRENADE:
                weapon = GrenadeBelt.newSmoke(game, this, entry.ammoBag);
                break;            
            default:                
        }
        
        if(weapon != null && entry.ammoBag > -1) {
            weapon.setTotalAmmo(entry.ammoBag);
        }
        
        return weapon;
    }
    
    
    /* (non-Javadoc)
     * @see seventh.game.Entity#kill(seventh.game.Entity)
     */
    @Override
    public void kill(Entity killer) {    
        unuse();
        super.kill(killer);
    }
    
    /**
     * Drops the flag if carrying one
     */
    public void dropFlag() {
        this.inventory.dropFlag();
    }
    
    /**
     * Drops the currently held item
     * @param makeSound
     */
    public void dropItem(boolean makeSound) {
        if(this.inventory.isCarryingFlag()) {
            dropFlag();
        }
        else {        
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
    }
    
    /**
     * Picks up an item
     * 
     * @param item
     * @return true if the item was picked up
     */
    public boolean pickupItem(Weapon weapon) {        
        Type type = weapon.getType();
        if(inventory.hasItem(type)) {            
            Weapon myWeapon = inventory.getItem(type);
            myWeapon.addAmmo(weapon.getTotalAmmo());
            game.emitSound(getId(), SoundType.AMMO_PICKUP, getPos());
            return true;
        }
        else {                
            if(inventory.addItem(weapon)) {
                weapon.setOwner(this);
                game.emitSound(getId(), SoundType.WEAPON_PICKUP, getPos());
                return true;
            }
        }
        
        return false;
    }
    
    /**
     * Attempts to pick up the {@link DroppedItem}
     * 
     * @param item
     * @return true if the player picked up the {@link DroppedItem}
     */
    public boolean pickupDroppedItem(DroppedItem item) {
        return item.pickup(this);
    }
    
    /**
     * Pickup a flag
     * 
     * @param flag
     */
    public void pickupFlag(Flag flag) {
        this.inventory.pickupFlag(flag);
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
     * @return the damageMultiplier
     */
    public int getDamageMultiplier() {
        return damageMultiplier;
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
    
    @Override
    public boolean isPlayer() {     
        return true;
    }
    
    public boolean isAlliedPlayer() {
        return getTeam().isAlliedTeam();
    }
    
    public boolean isAxisPlayer() {
        return getTeam().isAxisTeam();
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
        boolean isInviceable = this.invinceableTime > 0;
                    
        /* if the damager is a bullet, we should
         * determine the entrance wound, as it will
         * impact the amount of damage done.
         */
        if(damager instanceof Bullet) {
            Bullet bullet = (Bullet) damager;
            
            PlayerEntity damageOwner = null;
            
            Entity owner = bullet.getOwner();
            if(owner instanceof PlayerEntity) {
                damageOwner = (PlayerEntity)owner;
            }
            
            Vector2f center = getCenterPos();
            Vector2f bulletCenter = bullet.getCenterPos();
            
            this.headshot.centerAround(center);
            
            // determine which body part the bullet hit
            Vector2f.Vector2fMA(bulletCenter, bullet.getTargetVel(), 35, this.bulletDir);
            if(Line.lineIntersectsRectangle(bulletCenter, this.bulletDir, this.headshot)) {
                // headshots deal out a lot of damage
                amount *= 3;    
                
                if(damageOwner != null) {
                    game.emitSoundFor(damageOwner.getId(), SoundType.HEADSHOT, bullet.getPos(), damageOwner.getId());
                }
            }
            else {
                // limb shots are not as lethal
                
                // right side
                Vector2f.Vector2fPerpendicular(getFacing(), cache);
                Vector2f.Vector2fMA(center, cache, 10, cache);
                this.limbshot.centerAround(cache);
                if(Line.lineIntersectsRectangle(bulletCenter, this.bulletDir, this.limbshot)) {
                    amount /= 2;
                }
                             
                // left side
                Vector2f.Vector2fPerpendicular(getFacing(), cache);
                Vector2f.Vector2fMS(center, cache, 10, cache);
                this.limbshot.centerAround(cache);
                if(Line.lineIntersectsRectangle(bulletCenter, this.bulletDir, this.limbshot)) {
                    amount /= 2;
                }               
                
                if(damageOwner != null) {
                    game.emitSoundFor(damageOwner.getId(), SoundType.HIT_INDICATOR, bullet.getPos(), damageOwner.getId());
                }
            }
            
            //DebugDraw.drawLineRelative(bullet.getCenterPos(), this.bulletDir, 0xff00ffff);
            
            
            game.emitSound(bullet.getId(), SoundType.IMPACT_FLESH, bullet.getPos());
            
            if(damageOwner != null) {                    
                game.emitSoundFor(damageOwner.getId(), SoundType.IMPACT_FLESH, damageOwner.getPos(), damageOwner.getId());
            }
                            
            if(!bullet.isPiercing() || isInviceable) {                    
                bullet.kill(this);    
            }  
        }
            
        if(!isInviceable) {
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
            this.operating.operate(this);
            moveTo(this.operating.getCenterPos());
        }
        
        /*{   
            Vector2f center = getCenterPos();
            headshot.centerAround(center);
            
            Vector2f.Vector2fPerpendicular(getFacing(), cache);
            Vector2f.Vector2fMA(center, cache, 10, cache);
            this.limbshot.centerAround(cache);
            DebugDraw.drawRectRelative(limbshot, 0xff0000ff);
            
            Vector2f.Vector2fPerpendicular(getFacing(), cache);
            Vector2f.Vector2fMS(center, cache, 10, cache);
            this.limbshot.centerAround(cache);
            DebugDraw.drawRectRelative(limbshot, 0xff0000ff);
        
            DebugDraw.drawRectRelative(bounds, 0xff00ffff);
            DebugDraw.drawRectRelative(headshot, 0xff00ffff);
        }*/
        
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
            else if(currentState == State.EXITING_VEHICLE) {
                
                Vehicle vehicle = getVehicle();
                Rectangle area = new Rectangle(300, 300);
                area.centerAround(vehicle.getCenterPos());
                Vector2f newPos = game.findFreeRandomSpotNotIn(this, area, vehicle.getOBB());
                
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
            else {
                if(isOperatingVehicle()) {
                    currentState = State.OPERATING_VEHICLE;
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
        
        
        int mSpeed = this.speed + this.speedMultiplier;
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
            Player player = team.getPlayerById(this.id);
            setPlayerClass(player.getPlayerClass(), player.getWeaponClass());
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
                if(currentState==State.OPERATING_VEHICLE&&!this.operating.isMoving()) {
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
            else {
                unuse();
                
                if(Keys.USE.isDown(previousKeys)) {
                    useSingle();
                }
            }
            
            if(Keys.MELEE_ATTACK.isDown(keys)) {
                meleeAttack();
            }
            else if(Keys.MELEE_ATTACK.isDown(previousKeys)) {
                doneMeleeAttack();
            }
            
            if(Keys.DROP_WEAPON.isDown(previousKeys) && !Keys.DROP_WEAPON.isDown(keys)) {            
                dropWeapon();
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
                noMoveY();
            }
            
            if(Keys.LEFT.isDown(keys)) {
                moveLeft();
            }
            else if (Keys.RIGHT.isDown(keys)) {
                moveRight();
            }
            else {
                noMoveX();
            }
                        
            if(Keys.SPRINT.isDown(keys)) {
                if(!inputVel.isZero() && currentState != State.WALKING) {                                        
                    sprint();
                }
            }
            else {
                stopSprinting();        
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
        if(isSprinting()) {
            return;
        }
        
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
            Weapon weapon = this.inventory.currentItem();
            if(weapon==null||!weapon.isHeavyWeapon()) {            
                game.emitSound(getId(), SoundType.RUFFLE, getCenterPos());                                
                this.currentState = State.CROUCHING;
            }
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
         * 6) you are not reloading
         */
        
        if(currentState!=State.DEAD &&                
           stamina > 0 &&
           !firing &&
           !wasSprinting &&           
           recoveryTime <= 0) {        
        
            Weapon weapon = this.inventory.currentItem();
            boolean isReady = weapon != null ? weapon.isReady() : true;
            if(weapon == null || !weapon.isHeavyWeapon()  && isReady) {            
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
     * @return true if we are entering a vehicle
     */
    public boolean isEnteringVehicle() {
        return this.currentState == State.ENTERING_VEHICLE;
    }
    
    /**
     * @return true if we are exiting the vehicle
     */
    public boolean isExitingVehicle() {
        return this.currentState == State.EXITING_VEHICLE;
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
        if(weapon != null && !weapon.isHeavyWeapon()) {
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
            if(weapon.isHeavyWeapon()) {
                standup();
            }
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
            if(weapon.isHeavyWeapon()) {
                standup();
            }
            checkLineOfSightChange();
        }
        
    }
    
    public void armWeapon(Weapon weapon) {
        if(weapon != null) {
            if(inventory.hasItem(weapon.getType())) {
                inventory.equipItem(weapon.getType());
                
                weapon.setSwitchingWeaponState();
                if(weapon.isHeavyWeapon()) {
                    standup();
                }
                checkLineOfSightChange();    
            }
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
     * Handles a {@link BombTarget}, meaning this will either plant or disarm
     * the {@link BombTarget}.
     * 
     * @param target
     */
    protected void handleBombTarget(BombTarget target) {
        if(target!=null) {
            
            if(target.getOwner().equals(getTeam())) {
                if(target.bombActive()) {
                    Bomb bomb = target.getBomb();
                    bomb.disarm(this);                        
                                            
                    game.emitSound(getId(), SoundType.BOMB_DISARM, getPos());                        
                }    
            }
            else {
                if(!target.isBombAttached()) {                            
                    Bomb bomb = game.newBomb(target); 
                    bomb.plant(this, target);
                    target.attachBomb(bomb);
                    game.emitSound(getId(), SoundType.BOMB_PLANT, getPos());
                }
            } 
        }
    }
    
    protected void handleDoor(Door door) {
        door.handleDoor(this);
    }
    
    /**
     * Drop the players current weapon, and if there is a weapon
     * near, and we have a full inventory
     */
    public void dropWeapon() {
        // if we have a full inventory, and there is a weapon
        // near this entity, replace the dropped item with the near weapon
        DroppedItem itemNearMe = game.getArmsReachDroppedWeapon(this);
        if(itemNearMe != null) {
            if(this.inventory.isPrimaryFull()) {
                dropItem(true);
            }
            
            pickupDroppedItem(itemNearMe);            
            armWeapon(itemNearMe.getDroppedItem());
        }   
        else {
            dropItem(true);
        }
    }
    
    /**
     * For actions that rely on a button press.  This differs
     * from use() because use() relies on a button being held down,
     * as this relies on the press (the button is pushed and once
     *  released this method is activated).
     */
    public void useSingle() {
        Door door = game.getArmsReachDoor(this);
        if(door != null) {
            handleDoor(door);
        }
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
                this.bombTarget = game.getArmsReachBombTarget(this);
                if(this.bombTarget != null) {
                    handleBombTarget(this.bombTarget);
                }
            }
            
            if(this.bombTarget == null) {
                Vehicle vehicle = game.getArmsReachOperableVehicle(this);
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
     * If this {@link PlayerEntity} is currently holding a specific
     * weapon type.
     * 
     * @param weaponType
     * @return true if the current active weapon is of the supplied type
     */
    public boolean isYielding(Type weaponType) {
        Weapon weapon = this.inventory.currentItem();
        return (weapon != null && weapon.getType().equals(weaponType));
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
        this.hearingBounds.centerAround(getCenterPos());        
        
        int size = soundEvents.numberOfSounds();        
        for(int i = 0; i < size; i++) {
            SoundEmittedEvent event = soundEvents.getSound(i);
            if((this.hearingBounds.contains(event.getPos()) && event.getForEntityId() < 0) || 
                this.id == event.getForEntityId()) {
                
                soundsHeard.add(event);                
            } 
        }
                
        return soundsHeard;
    }
    
    @Override
    public List<Tile> calculateLineOfSight(List<Tile> tiles) {
        Map map = game.getMap();
        Geom.calculateLineOfSight(tiles, getCenterPos(), getFacing(), getLineOfSight(), map, getHeightMask(), cache);
        
        int tileSize = tiles.size();
        List<Door> doors = game.getDoors();
        int doorSize = doors.size();
        
        Vector2f centerPos = getCenterPos();
        
        for(int j = 0; j < doorSize; j++ ) {
            Door door = doors.get(j);
            if(this.visualBounds.intersects(door.getBounds())) {        
                for(int i = 0; i < tileSize; i++) {
                    Tile tile = tiles.get(i);
                    if(Line.lineIntersectLine(centerPos, tile.getCenterPos(), 
                                              door.getPos(), door.getHandle())) {
                        tile.setMask(Tile.TILE_INVISIBLE);
                    }
                }
            }
        }
        
        /*
        List<Smoke> smoke = game.getSmokeEntities();
        int smokeSize = smoke.size();
        
        if(smokeSize > 0) {
            for(int j = 0; j < smokeSize; j++) {
                Smoke s = smoke.get(j);
                if(this.visualBounds.intersects(s.getBounds())) {
                    for(int i = 0; i < tileSize; i++) {
                        Tile tile = tiles.get(i);
                        if(tile.getMask() > 0) {                                                    
                            if(Line.lineIntersectsRectangle(centerPos, tile.getCenterPos(), s.getBounds())) {
                                tile.setMask(Tile.TILE_INVISIBLE);
                            }
                        }
                    }                    
                }
             
            }
        }*/
        
        return tiles;
    }
    
    /**
     * Hides players that are behind smoke
     */
    protected void pruneEntitiesBehindSmoke(List<Entity> entitiesInView) {
        int entitySize = entitiesInView.size();
        List<Smoke> smoke = game.getSmokeEntities();
        int smokeSize = smoke.size();
        
        Vector2f centerPos = getCenterPos();
        
        if(entitySize > 0 && smokeSize > 0) {
            for(int j = 0; j < smokeSize; j++) {
                Smoke s = smoke.get(j);
                if(this.visualBounds.intersects(s.getBounds())) {
                    for(int i = 0; i < entitySize;) {
                        Entity ent = entitiesInView.get(i);
                        if(ent.getType()==Type.PLAYER && Line.lineIntersectsRectangle(ent.getCenterPos(), centerPos, s.getBounds())) {
                            entitiesInView.remove(i);
                            entitySize--;
                        }
                        else {
                            i++;
                        }
                    }
                }
             
            }
        }
        
    }
    
    /**
     * Given the game state, retrieve the {@link Entity}'s in the current entities view.
     * @param game
     * @return a list of {@link Entity}s that are in this players view
     */
    public List<Entity> getEntitiesInView(Game game, List<Entity> entitiesInView) {
        /*
         * Calculate all the visuals this player can see
         */
        Map map = game.getMap();
        Entity[] entities = game.getEntities();
        
        Vector2f centerPos = getCenterPos();
        this.visualBounds.centerAround(centerPos);
        
        if(isOperatingVehicle()) {
            getVehicle().calculateLineOfSight(game.tilesInLineOfSight);
        }
        else {
            calculateLineOfSight(game.tilesInLineOfSight);                        
        }
        
        
        for(int i = 0; i < entities.length; i++) {
            Entity ent = entities[i];
            if(ent==null /*|| !ent.isAlive()*/) {
                continue;
            }
            
            Type entType = ent.getType();
            boolean isCalculatedEntity = entType==Type.PLAYER;
            
            if(isCalculatedEntity && game.isEnableFOW()) {                
                if(ent.getId() != id) {
                    Vector2f pos = ent.getCenterPos();
                    
                    Vector2f.Vector2fSubtract(pos, centerPos, this.enemyDir);
                    Vector2f.Vector2fNormalize(this.enemyDir, this.enemyDir);
                    
                    if(!game.isEntityReachable(ent, centerPos, this.enemyDir)) {
                        continue;
                    }
                    
//                    if(map.lineCollides(pos, centerPos)) {
//                        continue;
//                    }                                
                    
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
            else {        
                /* We don't always send every entity over the wire */
                Type type = ent.getType();
                switch(type) {
                    case BOMB: 
                        Bomb bomb = (Bomb)ent;
                        if(bomb.isPlanted()) {
                            entitiesInView.add(ent);
                        }
                        break;
                    /*case ALLIED_FLAG:
                    case AXIS_FLAG:
                        entitiesInView.add(ent);
                        break;*/
                    case LIGHT_BULB:
                    case BOMB_TARGET:
                        /* don't add */
                        //break; we must add this, scripts
                        // may add these items well after the initial
                        // NetGameState message was sent
                    default: {
                        if(visualBounds.intersects(ent.getBounds())) {
                            entitiesInView.add(ent); 
                        }
                    }
                }
            }                    
        }
        
        pruneEntitiesBehindSmoke(entitiesInView);
        
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

        player.state = currentState;
        
        if(inventory.hasGrenades()) {
            GrenadeBelt belt = inventory.getGrenades();
            player.grenades = (byte)belt.getNumberOfGrenades();
            player.isSmokeGrenades = belt.getType() == Type.SMOKE_GRENADE;
        }
        else {
            player.grenades = 0;
        }
        
        player.health = (byte)getHealth(); 
        
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
        
        partialPlayer.state = currentState;                                
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
