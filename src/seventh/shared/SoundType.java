/*
 * see license.txt 
 */
package seventh.shared;

/**
 * All available sounds in the game
 * 
 * @author Tony
 *
 */
public enum SoundType {
    SURFACE_NORMAL,
    SURFACE_DIRT,
    SURFACE_SAND,
    SURFACE_WATER,
    SURFACE_GRASS,
    SURFACE_METAL,
    SURFACE_WOOD,
    
    EMPTY_FIRE,
    
    // Alies Weapon Sounds
    M1_GARAND_FIRE,
    M1_GARAND_LAST_FIRE(SoundSourceType.REFERENCED_ATTACHED),
    M1_GARAND_RELOAD(SoundSourceType.REFERENCED_ATTACHED),
    
    THOMPSON_FIRE,
    THOMPSON_RELOAD(SoundSourceType.REFERENCED_ATTACHED),
    
        
    SPRINGFIELD_FIRE,
    SPRINGFIELD_RECHAMBER(SoundSourceType.REFERENCED_ATTACHED),
    SPRINGFIELD_RELOAD(SoundSourceType.REFERENCED_ATTACHED),
    
    KAR98_FIRE,
    KAR98_RECHAMBER(SoundSourceType.REFERENCED_ATTACHED),
    KAR98_RELOAD(SoundSourceType.REFERENCED_ATTACHED),
    
    MP44_FIRE,
    MP44_RELOAD(SoundSourceType.REFERENCED_ATTACHED),
    
    MP40_FIRE,
    MP40_RELOAD(SoundSourceType.REFERENCED_ATTACHED),
    
    SHOTGUN_FIRE,
    SHOTGUN_RELOAD(SoundSourceType.REFERENCED_ATTACHED),
    SHOTGUN_PUMP(SoundSourceType.REFERENCED_ATTACHED),
    
    PISTOL_FIRE,
    PISTOL_RELOAD(SoundSourceType.REFERENCED_ATTACHED),
    
    RISKER_FIRE,
    RISKER_RECHAMBER(SoundSourceType.REFERENCED_ATTACHED),
    RISKER_RELOAD(SoundSourceType.REFERENCED_ATTACHED),
    
    RPG_FIRE,
    
    FLAMETHROWER_SHOOT(SoundSourceType.REFERENCED_ATTACHED),
    
    GRENADE_PINPULLED,
    GRENADE_THROW,
    SMOKE_GRENADE,
    
    MELEE_SWING,
    MELEE_HIT,

    BREATH_LITE(SoundSourceType.REFERENCED_ATTACHED),
    BREATH_HEAVY(SoundSourceType.REFERENCED_ATTACHED),
    
    FIRE(SoundSourceType.REFERENCED_ATTACHED),
    EXPLOSION(SoundSourceType.POSITIONAL),
    WEAPON_SWITCH(SoundSourceType.REFERENCED_ATTACHED),
    WEAPON_PICKUP(SoundSourceType.POSITIONAL),
    WEAPON_DROPPED,
    AMMO_PICKUP(SoundSourceType.POSITIONAL),
    HEALTH_PACK_PICKUP,
    RUFFLE(SoundSourceType.REFERENCED_ATTACHED),
    
    BOMB_TICK(SoundSourceType.GLOBAL),
    BOMB_PLANT,
    BOMB_DISARM,
    
    UI_ELEMENT_HOVER(SoundSourceType.GLOBAL),
    UI_ELEMENT_SELECT(SoundSourceType.GLOBAL),
    
    UI_NAVIGATE(SoundSourceType.GLOBAL),
    UI_KEY_TYPE(SoundSourceType.GLOBAL),
    
    IMPACT_METAL(SoundSourceType.POSITIONAL),
    IMPACT_WOOD(SoundSourceType.POSITIONAL),
    IMPACT_FOLIAGE(SoundSourceType.POSITIONAL),
    IMPACT_DEFAULT(SoundSourceType.POSITIONAL),
    IMPACT_FLESH(SoundSourceType.POSITIONAL),
    
    TANK_REV_DOWN(SoundSourceType.REFERENCED_ATTACHED),
    TANK_REV_UP(SoundSourceType.REFERENCED_ATTACHED),
    TANK_ON(SoundSourceType.REFERENCED_ATTACHED),
    TANK_OFF(SoundSourceType.REFERENCED_ATTACHED),
    TANK_IDLE(SoundSourceType.REFERENCED_ATTACHED),
    TANK_SHIFT(SoundSourceType.REFERENCED_ATTACHED),
    TANK_TURRET_MOVE(SoundSourceType.REFERENCED_ATTACHED),
    TANK_MOVE(SoundSourceType.REFERENCED_ATTACHED),
    TANK_FIRE(SoundSourceType.REFERENCED_ATTACHED),
    
    FLAG_CAPTURED(SoundSourceType.GLOBAL),
    FLAG_STOLEN(SoundSourceType.GLOBAL),
    FLAG_RETURNED(SoundSourceType.GLOBAL),
    ENEMY_FLAG_CAPTURED(SoundSourceType.GLOBAL),
    ENEMY_FLAG_STOLEN(SoundSourceType.GLOBAL),
    
    MG42_FIRE,
    
    RADIO_STATIC(SoundSourceType.REFERENCED),
    
    ALLIED_VICTORY(SoundSourceType.GLOBAL),
    AXIS_VICTORY(SoundSourceType.GLOBAL),
    
    DOOR_OPEN(SoundSourceType.POSITIONAL),
    DOOR_CLOSE(SoundSourceType.POSITIONAL),
    DOOR_CLOSE_BLOCKED(SoundSourceType.POSITIONAL),
    DOOR_OPEN_BLOCKED(SoundSourceType.POSITIONAL),
    
    MUTE,
    ;
    
    public static enum SoundSourceType {
        /**
         * Played at an X,Y coordinate
         */
        POSITIONAL,
        
        /**
         * Played at the position of the referenced ID
         * of an Entity
         */
        REFERENCED,
        
        /**
         * Attached to an entity by reference ID
         * (i.e., when the entity moves, so does the
         * sound)
         */
        REFERENCED_ATTACHED,
        
        /**
         * Plays in a global channel, that is right
         * at the camera location so it is always heard
         */
        GLOBAL,
    }
    
    private SoundSourceType sourceType;
    
    private SoundType() {
        this(SoundSourceType.REFERENCED);
    }
    
    /**
     * @param sourceType
     */
    private SoundType(SoundSourceType sourceType) {
        this.sourceType = sourceType;
    }
    
    private static final SoundType[] values = values();
    
    public byte netValue() {
        return (byte)this.ordinal();
    }
    
    /**
     * @return the sourceType
     */
    public SoundSourceType getSourceType() {
        return sourceType;
    }
    
    public static SoundType fromNet(byte value) {
        if(value >= values.length) {
            return MUTE;
        }
        
        if(value < 0) {
            return MUTE;
        }
        
        return values[value];
        
    }
}
