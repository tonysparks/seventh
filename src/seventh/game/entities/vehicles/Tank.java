/**
 * 
 */
package seventh.game.entities.vehicles;

import java.util.List;

import seventh.game.Game;
import seventh.game.SmoothOrientation;
import seventh.game.SoundEmitter;
import seventh.game.entities.Entity;
import seventh.game.entities.PlayerEntity.Keys;
import seventh.game.net.NetEntity;
import seventh.game.net.NetTank;
import seventh.game.weapons.Bullet;
import seventh.game.weapons.Explosion;
import seventh.game.weapons.MG42;
import seventh.game.weapons.Rocket;
import seventh.game.weapons.RocketLauncher;
import seventh.game.weapons.Weapon;
import seventh.map.Map;
import seventh.map.Tile;
import seventh.math.FastMath;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.DebugDraw;
import seventh.shared.EaseInInterpolation;
import seventh.shared.Geom;
import seventh.shared.SeventhConstants;
import seventh.shared.SoundType;
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

    private boolean isFiringPrimary;
    private boolean isFiringSecondary;
    private boolean isRetracting;
    private float throttle;
    private float turretOrientation;
    private float desiredTurretOrientation;        
    private float lastValidOrientation;
    
    private long throttleStartTime;
    private long throttleWarmupTime;
    
    private float desiredOrientation;

    private Weapon primaryWeapon;
    private Weapon secondaryWeapon;

    private Vector2f turretFacing;
    private SmoothOrientation turretSmoother;
    
    private int armor;
    
    private Timer blowupTimer, explosionTimer;
    private boolean isDying;
    private boolean isStopping;
    private boolean isStopped;
    private Entity killer;
    
    private EaseInInterpolation stopEase;
    private Vector2f previousVel;
    
    private SoundEmitter idleEngineSnd,
                         moveSnd,
                         turretRotateSnd, 
                         refDownSnd;
    
    /**
     * @param type
     * @param position
     * @param game
     */
    protected Tank(Type type, Vector2f position, final Game game, long timeToKill) {
        super(position, WeaponConstants.TANK_MOVEMENT_SPEED, game, type, timeToKill);

        this.turretFacing = new Vector2f();
        this.netTank = new NetTank(type);
        this.netTank.id = getId();
        
        this.blowupTimer = new Timer(false, 3_800);
        this.explosionTimer = new Timer(true, 450);
        this.isDying = false;
        
        this.stopEase = new EaseInInterpolation(WeaponConstants.TANK_MOVEMENT_SPEED, 0f, 800);
        this.previousVel = new Vector2f();
        
        this.idleEngineSnd = new SoundEmitter(8_500, true);
        this.moveSnd = new SoundEmitter(5_800, true);
        this.turretRotateSnd = new SoundEmitter(800, true);
        this.refDownSnd = new SoundEmitter(1_500, true);
        
        this.primaryWeapon = new RocketLauncher(game, this) {

            @Override
            protected void emitFireSound() {
                game.emitSound(getOwnerId(), SoundType.TANK_FIRE, getPos());
            }
            
            @Override
            public boolean beginFire() {            
                boolean isFired = super.beginFire();
                if(isFired) {
                    weaponTime = 3_000;
                }
                return isFired;
            }
            
            @Override
            protected Vector2f newRocketPosition() {
                this.bulletsInClip = 500;

                Vector2f ownerDir = getTurretFacing();
                Vector2f ownerPos = owner.getCenterPos();
                Vector2f pos = new Vector2f();

                float x = ownerDir.y * 5.0f;
                float y = -ownerDir.x * 5.0f;

                Vector2f.Vector2fMA(ownerPos, ownerDir, 145.0f, pos);

                pos.x += x;
                pos.y += y;

                return pos;
            }
            
            @Override
            protected Vector2f calculateVelocity(Vector2f facing) {
                return super.calculateVelocity(getTurretFacing());
            }
            
            @Override
            protected Rocket newRocket() {
                Rocket rocket = super.newRocket();
                rocket.setOwnerHeightMask(CROUCHED_HEIGHT_MASK);
                rocket.setOrientation(turretOrientation);
                rocket.setOwner(getOperator());
                
                return rocket;
            }
        };
        
        this.secondaryWeapon = new MG42(game, this) {

            @Override
            protected Vector2f newBulletPosition() {                
                Vector2f ownerDir = getTurretFacing();
                Vector2f ownerPos = owner.getCenterPos();
                Vector2f pos = new Vector2f();

                float x = ownerDir.y * 1.0f;
                float y = -ownerDir.x * 1.0f;

                Vector2f.Vector2fMA(ownerPos, ownerDir, 145.0f, pos);

                pos.x += x;
                pos.y += y;

                return pos;
            }
            
            @Override
            protected Bullet newBullet() {            
                Bullet bullet = super.newBullet();
                bullet.setOwner(getOperator());
                return bullet;
            }
            
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
        
        this.turretSmoother = new SmoothOrientation(Math.toRadians(1.5f));
        
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

        DebugDraw.drawRectRelative(bounds.x, bounds.y, bounds.width, bounds.height, 0xffffff00);
        DebugDraw.drawOOBRelative(vehicleBB, 0xff00ff00);
        DebugDraw.fillRectRelative((int)pos.x, (int)pos.y, 5, 5, 0xffff0000);
        DebugDraw.fillRectRelative((int)vehicleBB.topLeft.x, (int)vehicleBB.topLeft.y, 5, 5, 0xff1f0000);
        DebugDraw.fillRectRelative((int)vehicleBB.topRight.x, (int)vehicleBB.topRight.y, 5, 5, 0xffff0000);
        DebugDraw.fillRectRelative((int)vehicleBB.bottomLeft.x, (int)vehicleBB.bottomLeft.y, 5, 5, 0xff001f00);
        DebugDraw.fillRectRelative((int)vehicleBB.bottomRight.x, (int)vehicleBB.bottomRight.y, 5, 5, 0xff00ff00);
        
        DebugDraw.drawStringRelative("" + vehicleBB.topLeft, bounds.x, bounds.y+240, 0xffff0000);
        DebugDraw.drawStringRelative("" + vehicleBB.bottomLeft, bounds.x, bounds.y+220, 0xffff0000);
        
        DebugDraw.drawStringRelative(String.format(" Tracks: %3.2f : %3.2f", Math.toDegrees(this.orientation), Math.toDegrees(this.desiredOrientation)), 
                (int)getPos().x, (int)getPos().y-20, 0xffff0000);
        
        DebugDraw.drawStringRelative(String.format(" Current: %3.2f : %3.2f", Math.toDegrees(this.turretOrientation), Math.toDegrees(desiredTurretOrientation)), 
                getPos(), 0xffff0000);
        
        if(isDestroyed()) {
            return false;
        }
        
        if( checkIfDying(timeStep) ) {
            return false;
        }
        
        boolean isBlocked = false;
        if(hasOperator()) {
//            this.primaryWeapon.setOwner(getOperator());
//            this.secondaryWeapon.setOwner(getOperator());
            
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
        
        game.doesVehicleTouchPlayers(this);
        
        return isBlocked;
    }
    
    private boolean checkIfDying(TimeStep timeStep) {
        if(this.isDying) {
            this.blowupTimer.update(timeStep);
            this.explosionTimer.update(timeStep);
            
            if(this.explosionTimer.isTime()) {
                game.newBigExplosion(getCenterPos(), this, 20, 80, 100);
            }
            
            if(this.blowupTimer.isTime()) {
                super.kill(killer);
                this.isDying = false;
            }
        }
        
        return this.isDying;
    }
    
    /**
     * @param dt
     * @return true if blocked
     */
    private boolean movementUpdate(TimeStep timeStep) {
        if(isDestroyed()) {
            return false;
        }
        
        boolean isBlocked = false;
        
        boolean hasThrottle = ! this.vel.isZero();
        
        if(!hasThrottle && !this.isStopped) {
            this.isStopping = true;
            //game.emitSound(getId(), SoundType.TANK_REV_DOWN, getCenterPos());
        }
        
        
        if(isAlive() && (hasThrottle || this.isStopping) ) {
            if(currentState != State.WALKING && currentState != State.SPRINTING) {
                currentState = State.RUNNING;
            }
            
            if(this.throttleWarmupTime < 600) {
                this.throttleWarmupTime += timeStep.getDeltaTime();
                
                if(hasOperator() && this.throttleWarmupTime > 590) {
                    game.emitSound(getOperator().getId(), SoundType.TANK_REV_UP, getCenterPos());
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
                //        game.emitSound(getOperator().getId(), SoundType.TANK_MOVE1, getCenterPos());
                    }
                }
                
                float normalSpeed = this.isStopping ? this.stopEase.getValue() : 90f;                                
                final float movementSpeed = 
                            this.throttleStartTime > 0 ? 160f : normalSpeed;
                            
                float dt = (float)timeStep.asFraction();
                float newX = pos.x + vel.x * movementSpeed * dt;
                float newY = pos.y + vel.y * movementSpeed * dt;                    
                
                newX = Math.max(0, newX);
                newY = Math.max(0, newY);
                
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
                
                if(!isBlocked) {
                    vehicleBB.setLocation(newX+WeaponConstants.TANK_AABB_WIDTH/2f, vehicleBB.center.y);
                    if(collidesAgainstVehicle(bounds)) {                
                        bounds.x = (int)pos.x;                
                        isBlocked = true;
                        isXBlocked = true;
                    }
                }
                
                
                
                bounds.y = (int)newY;
                if( map.rectCollides(bounds)) {
                    vehicleBB.setLocation(vehicleBB.center.x, newY+WeaponConstants.TANK_AABB_HEIGHT/2f);
                    isBlocked = map.rectCollides(vehicleBB);
                    if(isBlocked) {
                        bounds.y = (int)pos.y;
                        isYBlocked = true;
                    }
                }
                                
                if(!isBlocked) {
                    vehicleBB.setLocation(vehicleBB.center.x, newY+WeaponConstants.TANK_AABB_HEIGHT/2f);
                    if(collidesAgainstVehicle(bounds)) {                
                        bounds.y = (int)pos.y;                
                        isBlocked = true;
                        isYBlocked = true;
                    }
                }
                
    
                /* some things want to stop dead it their tracks
                 * if a component is blocked
                 */
                if(isBlocked && !continueIfBlock()) {
                    bounds.x = (int)pos.x;
                    bounds.y = (int)pos.y;
                    isXBlocked = isYBlocked = true;
                }
                            
    //            pos.x = bounds.x;
    //            pos.y = bounds.y;
                
                pos.x = isXBlocked ? pos.x : newX;
                pos.y = isYBlocked ? pos.y : newY;
                                    
                vel.zeroOut();
                
    //            this.walkingTime = WALK_TIME;                                
            }
        }
        else {                        
//            if(this.walkingTime<=0 && currentState!=State.CROUCHING) {
//                currentState = State.IDLE;
//            }
//            
//            this.walkingTime -= timeStep.getDeltaTime();

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
        
        return false;
    }
    
    protected void updateOrientation(TimeStep timeStep) {
        
        if(this.vel.x > 0 || this.vel.x < 0) {
            float deltaMove = 0.55f * (float)timeStep.asFraction();
            if(this.vel.x < 0) {
                deltaMove *= -1;
            }
            
            this.desiredOrientation = this.orientation;
            
            this.desiredOrientation += deltaMove;
            if(this.desiredOrientation<0) {
                this.desiredOrientation=FastMath.fullCircle-this.desiredOrientation;
            }
            else if(this.desiredOrientation>FastMath.fullCircle) {
                float remainder = this.desiredOrientation-FastMath.fullCircle; 
                this.desiredOrientation=remainder;
            }
            
            float newOrientation = this.desiredOrientation;
            
            Map map = game.getMap();
            if(map.rectCollides(bounds) || collidesAgainstVehicle(bounds)) {
                
                this.vehicleBB.rotateTo(newOrientation);
                
                /* If we collide, then revert back to a valid orientation */
                if(map.rectCollides(vehicleBB)|| collidesAgainstVehicle(bounds)) {                    
                    this.orientation = this.lastValidOrientation;
                    return;
                }
            }
            
            this.lastValidOrientation = this.orientation;
            this.orientation = newOrientation;
            this.desiredOrientation = newOrientation;
            
            
            this.facing.set(1, 0); // make right vector
            Vector2f.Vector2fRotate(this.facing, orientation, this.facing);
        }
    }
    
    protected void updateTurretOrientation(TimeStep timeStep) {
        this.turretSmoother.setDesiredOrientation(this.desiredTurretOrientation);
        this.turretSmoother.setOrientation(this.turretOrientation);
        this.turretSmoother.update(timeStep);
        if(this.turretSmoother.moved()) {
            this.turretRotateSnd.play(game, getId(), SoundType.TANK_TURRET_MOVE, getPos());
        }
        
        this.turretOrientation = this.turretSmoother.getOrientation();
        this.turretFacing.set(this.turretSmoother.getFacing());
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
//            SoundType snd = SoundType.TANK_MOVE1;
//            if (isRetracting) {
//                snd = SoundType.TANK_START_MOVE;
//            }

            isRetracting = !isRetracting;
//            game.emitSound(getId(), snd, getCenterPos());
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
        
        
        this.idleEngineSnd.update(timeStep);
        this.moveSnd.update(timeStep);
        this.turretRotateSnd.update(timeStep);
        this.refDownSnd.update(timeStep);
        
        if(currentState==State.IDLE) 
        {
            this.idleEngineSnd.play(game, getId(), SoundType.TANK_IDLE, getCenterPos());
            this.moveSnd.reset();
        }
        else {
            this.moveSnd.play(game, getId(), SoundType.TANK_MOVE, getCenterPos());
            this.idleEngineSnd.reset();
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
            //amount /= 2;
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
    
    
    /**
     * Set this tank to destroyed
     */
    public void disable() {
        this.currentState = State.DESTROYED;
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
    
    public void setOrientationNow(float desiredOrientation) {
        final float fullCircle = FastMath.fullCircle;
        if(desiredOrientation < 0) {
            desiredOrientation += fullCircle;
        }
        this.orientation = desiredOrientation;
        this.desiredOrientation = desiredOrientation;
                        
        syncOOB(this.orientation, pos);
    }
    
    public void setTurretOrientationNow(float desiredOrientation) {
        final float fullCircle = FastMath.fullCircle;
        if(desiredOrientation < 0) {
            desiredOrientation += fullCircle;
        }
        this.turretOrientation = desiredOrientation;
        this.desiredTurretOrientation = desiredOrientation;
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

    /* (non-Javadoc)
     * @see seventh.game.Entity#calculateLineOfSight()
     */
    @Override
    public List<Tile> calculateLineOfSight(List<Tile> tiles) {
        Map map = game.getMap();
        Geom.calculateLineOfSight(tiles, getCenterPos(), getTurretFacing(), WeaponConstants.TANK_DEFAULT_LINE_OF_SIGHT, map, getHeightMask(), cache);
        return tiles;
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
        
        if(game.getRandom().nextInt(5)==4)
            this.turretRotateSnd.play(game, getId(), SoundType.TANK_SHIFT, getCenterPos());
        //this.refDownSnd.play(game, getId(), SoundType.TANK_REV_UP, getCenterPos());
    }
    
    /**
     * Maneuvers the tracks to the right
     */
    public void maneuverRight() {
        this.vel.x = 1;
        if(game.getRandom().nextInt(5)==4)
            this.turretRotateSnd.play(game, getId(), SoundType.TANK_SHIFT, getCenterPos());
        //this.refDownSnd.play(game, getId(), SoundType.TANK_REV_UP, getCenterPos());
    }
    
    /**
     * Stops maneuvering the tracks
     */
    public void stopManeuvering() {
        this.vel.x = 0;
    }
    
    @Override
    protected void beginOperating() {
        game.emitSound(getId(), SoundType.TANK_ON, getCenterPos());
    }
    
    @Override
    protected void endOperating() {
        game.emitSound(getId(), SoundType.TANK_OFF, getCenterPos());
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
        netTank.operatorId = hasOperator() ? this.getOperator().getId() : SeventhConstants.INVALID_PLAYER_ID;
        netTank.turretOrientation = (short)Math.toDegrees(turretOrientation);
        netTank.primaryWeaponState = primaryWeapon.getState().netValue();
        netTank.secondaryWeaponState = secondaryWeapon.getState().netValue();
        
        return netTank;
    }
}
