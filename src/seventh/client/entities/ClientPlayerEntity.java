/*
 * see license.txt 
 */
package seventh.client.entities;

import static seventh.shared.SeventhConstants.PLAYER_MIN_SPEED;
import static seventh.shared.SeventhConstants.PLAYER_SPEED;
import static seventh.shared.SeventhConstants.SPRINT_SPEED_FACTOR;
import static seventh.shared.SeventhConstants.WALK_SPEED_FACTOR;

import java.util.Random;

import seventh.client.ClientGame;
import seventh.client.ClientPlayer;
import seventh.client.ClientSeventhConfig;
import seventh.client.ClientTeam;
import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Light;
import seventh.client.gfx.LightSystem;
import seventh.client.gfx.PlayerSprite;
import seventh.client.gfx.RenderFont;
import seventh.client.gfx.effects.AnimationEffect;
import seventh.client.gfx.effects.ClientGameEffects;
import seventh.client.gfx.effects.particle_system.Emitters;
import seventh.client.sfx.Sounds;
import seventh.client.weapon.ClientFlameThrower;
import seventh.client.weapon.ClientKar98;
import seventh.client.weapon.ClientM1Garand;
import seventh.client.weapon.ClientMP40;
import seventh.client.weapon.ClientMP44;
import seventh.client.weapon.ClientPistol;
import seventh.client.weapon.ClientRisker;
import seventh.client.weapon.ClientRocketLauncher;
import seventh.client.weapon.ClientShotgun;
import seventh.client.weapon.ClientSpringfield;
import seventh.client.weapon.ClientThompson;
import seventh.client.weapon.ClientWeapon;
import seventh.game.entities.Entity.State;
import seventh.game.entities.Entity.Type;
import seventh.game.net.NetEntity;
import seventh.game.net.NetPlayer;
import seventh.game.net.NetPlayerPartial;
import seventh.game.net.NetWeapon;
import seventh.game.type.GameType;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.WeaponConstants;
/**
 * @author Tony
 *
 */
public class ClientPlayerEntity extends ClientControllableEntity {
    
    private final ClientWeapon[] WEAPONS = new ClientWeapon[11];
    
    private int health;        
    private int stamina;
        
    private long invinceableTime;
            
    private PlayerSprite sprite;
        
    private int fadeAlphaColor;
    private int teamColor;
    
    private ClientPlayer player;
    private ClientWeapon weapon;
    private byte numberOfGrenades;
    private boolean isSmokeGrenades;
        
    private int weaponWeight;
    
    private Light mussleFlash;
    private ClientGameEffects effects;
        
    
    private boolean isSelected;
    private Rectangle selectionBounds;
    
    // buffer the damage done to this 
    // entity
    private boolean[] damaged;
    private int damageBufferIndex;
    private Vector2f bulletCasingPos;
    
    private ClientSeventhConfig config;
    /**
     * @param game
     * @param player
     * @param pos
     */
    public ClientPlayerEntity(ClientGame game, ClientPlayer player, Vector2f pos) {
        super(game, pos);
        
        this.config = game.getApp().getConfig();
        this.effects = game.getGameEffects();
        
        this.type = Type.PLAYER;
            
        this.currentState = State.IDLE;    
        this.invinceableTime = 0;        
        this.lineOfSight = WeaponConstants.DEFAULT_LINE_OF_SIGHT;
                        
        this.bounds.width = 24;//16;
        this.bounds.height = 24;
        
        this.selectionBounds = new Rectangle(40,40);
        this.bulletCasingPos = new Vector2f();
        
        this.damaged = new boolean[8];
        this.damageBufferIndex = 0;
                
        LightSystem lightSystem = game.getLightSystem();                
        this.mussleFlash = lightSystem.newPointLight();
        this.mussleFlash.setTexture(Art.fireWeaponLight);
        this.mussleFlash.setColor(0.5f,0.5f,0.5f);
        
        ClientPlayer localPlayer = game.getLocalPlayer();
        if(localPlayer != null) {
            setControlledByLocalPlayer(localPlayer.getId() == player.getId());
        }
        
        setPlayer(player);        
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientEntity#destroy()
     */
    @Override
    public void destroy() {
        super.destroy();
        mussleFlash.destroy();
    }
    
    /**
     * The player just spawned, so set them up accordingly
     */
    public void spawned() {
        this.invinceableTime = 2000;
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientEntity#killIfOutdated(long)
     */
    @Override
    public boolean killIfOutdated(long gameClock) {    
        return false;
    }

    
    /**
     * @param player the player to set
     */
    public void setPlayer(ClientPlayer player) {
        this.player = player;
        if(player != null) {            
            WEAPONS[0] = new ClientThompson(this);
            WEAPONS[1] = new ClientShotgun(this);
            WEAPONS[2] = new ClientRocketLauncher(this);
            WEAPONS[3] = new ClientSpringfield(this);
            WEAPONS[4] = new ClientM1Garand(this);
            WEAPONS[5] = new ClientKar98(this);
            WEAPONS[6] = new ClientMP44(this);
            WEAPONS[7] = new ClientMP40(this);
            WEAPONS[8] = new ClientPistol(this);
            WEAPONS[9] = new ClientRisker(this);
            WEAPONS[10] = new ClientFlameThrower(this);
            
            player.setEntity(this);
        }
    }
    
        
    /**
     * @return the player
     */
    public ClientPlayer getPlayer() {
        return player;
    }
    
    /**
     * @return the mussleFlash
     */
    public Light getMussleFlash() {
        return mussleFlash;
    }
    
    
    public void emitBulletCasing() {
        if(!this.isOperatingVehicle() && (this.lastUpdate+200 > this.gameClock)) {
            Vector2f.Vector2fMA(getCenterPos(), getFacing(), 10.0f, this.bulletCasingPos);
            this.effects.spawnBulletCasing(this.bulletCasingPos, getOrientation());
        }
    }
    

    /**
     * @return the selectionBounds
     */
    public Rectangle getSelectionBounds() {
        this.selectionBounds.centerAround(getCenterPos());
        return selectionBounds;
    }
    
    /**
     * If this has been selected
     * 
     * @param isSelected
     */
    public void isSelected(boolean isSelected) {
        this.isSelected = isSelected;
    }
    
    /**
     * @return if this entity is selected
     */
    public boolean isSelected() {
        return this.isSelected;
    }
        
    /**
     * @return the Players ID
     */
    public int getPlayerId() {
        return this.player.getId();
    }
    
    /**
     * @return the team
     */
    public ClientTeam getTeam() {
        return this.player.getTeam();
    }
    
    /**
     * @return the health
     */
    public int getHealth() {
        return health;
    }
    
    
    /**
     * @return the weapon
     */
    public ClientWeapon getWeapon() {
        return weapon;
    }
    
    public byte getNumberOfGrenades() {
        return this.numberOfGrenades;
    }
    
    /**
     * @return the isSmokeGrenades
     */
    public boolean isSmokeGrenades() {
        return isSmokeGrenades;
    }
    
    /**
     * @return the stamina
     */
    public int getStamina() {
        return stamina;
    }
    
    /**
     * @param team
     */
    public void changeTeam(ClientTeam team) {        
        switch(team) {            
            case AXIS: {
                this.sprite = this.player.getAxisSprite();
                break;
            }
            case ALLIES:
            default: {
                this.sprite = this.player.getAlliedSprite();                
            }
        }
        
        this.teamColor = team.getColor();
    }
    
    
    
    /**
     * @return calculates the movement speed based on
     * state + current weapon + stamina
     */
    @Override
    protected int calculateMovementSpeed() {
        int speed = PLAYER_SPEED;
        int mSpeed = speed;
        if(currentState==State.WALKING) {
            mSpeed = (int)( (float)speed * WALK_SPEED_FACTOR);
        }
        else if(currentState == State.SPRINTING) {            
            mSpeed = (int)( (float)speed * SPRINT_SPEED_FACTOR); // 1.35        
        }
                
        mSpeed -= weaponWeight;
                
        if(mSpeed < PLAYER_MIN_SPEED) {
            mSpeed = PLAYER_MIN_SPEED;
        }
        
        return mSpeed;
    }
    
    /* (non-Javadoc)
     * @see palisma.client.ClientEntity#netUpdate(palisma.game.net.NetEntity)
     */
    @Override
    public void updateState(NetEntity state, long time) {
        super.updateState(state, time);        
        
        /*
         * There a full and partial NetPlayer updates.  It
         * would have been nice to have NetPlayer inherit
         * from NetPlayerPartial but due to some bit optimizations
         * it made it a bit awkward, so we live with this
         * redundant code
         * 
         * The Partial updates are for the entities that the
         * local player doesn't control (they don't need to 
         * know their stamina, ammo, etc. so we can save some 
         * bytes by not sending them)
         */
        if(state instanceof NetPlayer) {
            NetPlayer ps = (NetPlayer) state;
            
            this.currentState = State.fromNetValue(ps.state);
            int newHealth = ps.health;
            int newDamageDelta = newHealth - this.health;
            if(newDamageDelta < 0) {
                this.damaged[this.damageBufferIndex] = true;
                this.damageBufferIndex = (this.damageBufferIndex + 1) % this.damaged.length;                
            }
            
            this.health = ps.health;
            this.stamina = ps.stamina;
            
            this.numberOfGrenades = ps.grenades;
            this.isSmokeGrenades = ps.isSmokeGrenades;
            
            if(ps.isOperatingVehicle) {
                if(this.vehicle == null || this.vehicle.getId() != ps.vehicleId) {
                    this.vehicle = game.getVehicleById(ps.vehicleId);
                }
            }
            else {
                this.vehicle = null;
                
                updateWeaponState(ps.weapon, time);
            }
        }
        else {
            NetPlayerPartial ps = (NetPlayerPartial) state;
            this.currentState = State.fromNetValue(ps.state);
            int newHealth = ps.health;
            
            int newDamageDelta = newHealth - this.health;
            if(newDamageDelta < 0) {
                this.damaged[this.damageBufferIndex] = true;
                this.damageBufferIndex = (this.damageBufferIndex + 1) % this.damaged.length;                
            }
            
            this.health = ps.health;
            
            if(ps.isOperatingVehicle) {
                if(this.vehicle == null || this.vehicle.getId() != ps.vehicleId) {
                    this.vehicle = game.getVehicleById(ps.vehicleId);
                }
            }
            else {
                if(this.vehicle != null) {
                    this.vehicle.setOperator(null);
                }
                
                updateWeaponState(ps.weapon, time);
            }
        }
    }

    /**
     * Updates the weapon state
     * 
     * @param netWeapon
     * @param time
     */
    protected void updateWeaponState(NetWeapon netWeapon, long time) {
        if(netWeapon != null){
            lineOfSight = WeaponConstants.DEFAULT_LINE_OF_SIGHT;
            
            Type type = Type.fromNet(netWeapon.type); 
            switch(type) {
                case THOMPSON: {
                    weapon = WEAPONS[0];
                    lineOfSight = WeaponConstants.THOMPSON_LINE_OF_SIGHT;
                    weaponWeight = WeaponConstants.THOMPSON_WEIGHT;
                    break;
                }
                case SHOTGUN: {
                    weapon = WEAPONS[1];
                    lineOfSight = WeaponConstants.SHOTGUN_LINE_OF_SIGHT;
                    weaponWeight = WeaponConstants.SHOTGUN_WEIGHT;
                    break;
                }
                case ROCKET_LAUNCHER: {
                    weapon = WEAPONS[2];
                    lineOfSight = WeaponConstants.RPG_LINE_OF_SIGHT;
                    weaponWeight = WeaponConstants.RPG_WEIGHT;
                    break;
                }
                case SPRINGFIELD: {
                    weapon = WEAPONS[3];
                    lineOfSight = WeaponConstants.SPRINGFIELD_LINE_OF_SIGHT;
                    weaponWeight = WeaponConstants.SPRINGFIELD_WEIGHT;
                    break;
                }    
                case M1_GARAND: {
                    weapon = WEAPONS[4];
                    lineOfSight = WeaponConstants.M1GARAND_LINE_OF_SIGHT;
                    weaponWeight = WeaponConstants.M1GARAND_WEIGHT;
                    break;
                }
                case KAR98: {
                    weapon = WEAPONS[5];
                    lineOfSight = WeaponConstants.KAR98_LINE_OF_SIGHT;
                    weaponWeight = WeaponConstants.KAR98_WEIGHT;
                    break;
                }
                case MP44: {
                    weapon = WEAPONS[6];
                    lineOfSight = WeaponConstants.MP44_LINE_OF_SIGHT;
                    weaponWeight = WeaponConstants.MP44_WEIGHT;
                    break;
                }
                case MP40: {
                    weapon = WEAPONS[7];
                    lineOfSight = WeaponConstants.MP40_LINE_OF_SIGHT;
                    weaponWeight = WeaponConstants.MP40_WEIGHT;
                    break;
                }
                case PISTOL: {
                    weapon = WEAPONS[8];
                    lineOfSight = WeaponConstants.PISTOL_LINE_OF_SIGHT;
                    weaponWeight = WeaponConstants.PISTOL_WEIGHT;
                    break;
                }
                case RISKER: {
                    weapon = WEAPONS[9];
                    lineOfSight = WeaponConstants.RISKER_LINE_OF_SIGHT;
                    weaponWeight = WeaponConstants.RISKER_WEIGHT;
                    break;
                }
                case FLAME_THROWER: {
                    weapon = WEAPONS[10];
                    lineOfSight = WeaponConstants.FLAME_THROWER_LINE_OF_SIGHT;
                    weaponWeight = WeaponConstants.FLAME_THROWER_WEIGHT;
                    break;
                }
                default: {                    
                    weapon = null;
                    weaponWeight = 0;
                }
            }
            
            if(weapon!=null) {
                weapon.updateState(netWeapon, time);                
            }
        }
        else {
            weapon = null;
            weaponWeight = 0;
        }
    }
    

    
    /* (non-Javadoc)
     * @see palisma.client.ClientEntity#update(leola.live.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {        
        if(this.invinceableTime > 0 ) {
            this.invinceableTime -= timeStep.getDeltaTime();
        }
                        
        long clockTime = timeStep.getGameClock();
        // 
        if(weapon != null && (isControlledByLocalPlayer() || (lastUpdate+500) >= clockTime)) {
            weapon.update(timeStep);
        }
        
        this.sprite.update(timeStep);
        
        
        if ((lastUpdate+150) < clockTime && !isControlledByLocalPlayer()) {
            fadeAlphaColor = 255 - ((int)(clockTime-lastUpdate)/3);
            if (fadeAlphaColor < 0) fadeAlphaColor = 0;                        
        }
        else {
            fadeAlphaColor = 255;
        }    
        
        if(currentState.isVehicleState()) {
            if(this.vehicle != null) {
                this.vehicle.setOperator(this);
            }
            
            fadeAlphaColor = 0;
        }
        
        
        Vector2f centerPos = getCenterPos();        
        mussleFlash.setPos(centerPos);
        Vector2f.Vector2fMA(mussleFlash.getPos(), getFacing(), 40.0f, mussleFlash.getPos());
        mussleFlash.setOrientation(getOrientation());
        mussleFlash.setLuminacity((fadeAlphaColor-150)/255.0f);
        mussleFlash.setColor(0.7f,.7f,0.5f);
        
        hearingBounds.centerAround(centerPos);
        visualBounds.centerAround(centerPos);
        
        /* Make sure the player was actually hit before we display any blood 
         * effects.  We do this by making sure our last update was recent, and
         * this wasn't just an entity we are seeing again after a while (which
         * would cause damageDelta to be < 0)
         */                
        if((previousNetUpdate+400) >= clockTime) {
            for(int i = 0; i < this.damaged.length;i++) {
                if(this.damaged[i]) {
                    onDamage();
                    this.damaged[i] = false;
                }
            }
        }    
        else {
            for(int i = 0; i < this.damaged.length;i++) {                
                this.damaged[i] = false;                
            }
        }
        
                
        super.update(timeStep);                        
    }

    /**
     * The player has taken damage
     */
    protected void onDamage() {        
        if(this.config.getBloodEnabled()) {
            this.effects.spawnBloodSplatter(getCenterPos());                
        }
        
        if(isControlledByLocalPlayer()) {
            this.effects.getHurtEffect().reset();
        }
    }
    
    @Override
    public void kill(Type meansOfDeath, Vector2f locationOfDeath) {    
        super.kill(meansOfDeath, locationOfDeath);
        
        if(isControlledByLocalPlayer()) {
            this.effects.getHurtEffect().reset(1500);
        }
        
        switch(meansOfDeath) {
            case EXPLOSION:
            case GRENADE: {                                        
                AnimatedImage anim = null;
                Vector2f pos = new Vector2f(locationOfDeath);
                Vector2f.Vector2fMA(pos, getFacing(), 0, pos);
                
                switch(player.getTeam()) {
                    case ALLIES:
                        anim = Art.newAlliedExplosionDeathAnim();
                        break;
                    case AXIS:
                        anim = Art.newAxisExplosionDeathAnim();
                        break;
                    default:
                        break;
                }
                // Objective game type keeps the dead bodies around
                boolean persist = this.game.getGameType().equals(GameType.Type.OBJ);
                
                if(config.getBloodEnabled()) {
                    this.effects.addBackgroundEffect(Emitters.newBloodEmitter(locationOfDeath, 18, 15200, 50));
                    this.effects.addBackgroundEffect(Emitters.newGibEmitter(locationOfDeath, 3));                        
                    this.effects.addBackgroundEffect(new AnimationEffect(anim, pos, getOrientation(), persist));
                }
                break;
            }
            case ROCKET:
            case ROCKET_LAUNCHER:
                if(config.getBloodEnabled()) {
                    this.effects.addBackgroundEffect(Emitters.newBloodEmitter(locationOfDeath, 18, 15200, 50));
                    this.effects.addBackgroundEffect(Emitters.newGibEmitter(locationOfDeath, 15));
                }
                Sounds.startPlaySound(Sounds.gib, getId(), locationOfDeath.x, locationOfDeath.y);
                
                break;                
            default: {
            
                if(meansOfDeath != Type.FIRE && config.getBloodEnabled()) {                                            
                    this.effects.addBackgroundEffect(Emitters.newBloodEmitter(locationOfDeath, 16, 15200, 30));
                }
                
                Vector2f pos = new Vector2f(locationOfDeath);
                Vector2f.Vector2fMA(pos, getFacing(), 0, pos);                                                                                    
                                    
                Random random = this.game.getRandom();            
                AnimatedImage anim = null;
                
                switch(player.getTeam()) {
                // TODO use pool
                    case ALLIES: {
                        switch(random.nextInt(4)) {
                        case 0:
                            anim = Art.newAlliedBackDeathAnim();
                            break;
                        case 1:
                            anim = Art.newAlliedBackDeath2Anim();
                            break;
                        case 2: 
                            anim = Art.newAlliedFrontDeathAnim();
                            break;
                        default:
                            anim = Art.newAlliedFrontDeath2Anim();
                            break;
                        }
                        break;
                    }
                    case AXIS: {
                        switch(random.nextInt(4)) {
                        case 0:
                            anim = Art.newAxisBackDeathAnim();
                            break;
                        case 1:
                            anim = Art.newAxisBackDeath2Anim();
                            break;
                        case 2: 
                            anim = Art.newAxisFrontDeathAnim();
                            break;
                        default:
                            anim = Art.newAxisFrontDeath2Anim();
                            break;
                        }
                        break;
                    }
                    default: { // nothing                            
                    }
                }
                
                if(anim!=null) {
                    // Objective game type keeps the dead bodies around
                    boolean persist = this.game.getGameType().equals(GameType.Type.OBJ);
                    
                    // spawn the death animation
                    this.effects.addBackgroundEffect(new AnimationEffect(anim, pos, getOrientation(), persist));
                    
                    Sounds.startPlaySound(Sounds.die, getId(), locationOfDeath.x, locationOfDeath.y);
                }
            }
        }    
                        
    
        
    }
    
    
    /* (non-Javadoc)
     * @see palisma.client.ClientEntity#render(leola.live.gfx.Canvas, leola.live.gfx.Camera)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        canvas.setCompositeAlpha(fadeAlphaColor/255.0f);
        canvas.setColor(teamColor, fadeAlphaColor);    
        
        //states[currentState].render(canvas, camera)
        Vector2f c = camera.getRenderPosition(alpha);
        Vector2f pos = getRenderPos(alpha);
        float rx = pos.x - c.x;
        float ry = pos.y - c.y;

        canvas.setFont("Consola", 14);
        canvas.boldFont();
                
        if(fadeAlphaColor > 0) {            
            RenderFont.drawShadedString(canvas, player.getName(), rx - (bounds.width/2f), ry + (bounds.height/2f) + 40f, teamColor );
        
            if (invinceableTime > 0 || isSelected()) {
                canvas.setColor(teamColor, 122);
                canvas.fillCircle(20, rx - (bounds.width/2f), ry - (bounds.height/2f), null);
                canvas.drawCircle(21, rx - (bounds.width/2f)-1f, ry - (bounds.height/2f)-1f, 0xff000000);
            }                
        }
                
        sprite.render(canvas, camera, alpha);
        canvas.setCompositeAlpha(1.0f);

    }

}
