/*
 * see license.txt 
 */
package seventh.game;

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
	M1_GARAND_LAST_FIRE,
	M1_GARAND_RELOAD,
	
	THOMPSON_FIRE,
	THOMPSON_RELOAD,
	
		
	SPRINGFIELD_FIRE,
	SPRINGFIELD_RECHAMBER,
	SPRINGFIELD_RELOAD,
	
	KAR98_FIRE,
	KAR98_RECHAMBER,
	KAR98_RELOAD,
	
	MP44_FIRE,
	MP44_RELOAD,
	
	MP40_FIRE,
	MP40_RELOAD,
	
	SHOTGUN_FIRE,
	SHOTGUN_RELOAD,
	SHOTGUN_PUMP,
	
	PISTOL_FIRE,
	PISTOL_RELOAD,
	
	RISKER_FIRE,
	RISKER_RECHAMBER,
	RISKER_RELOAD,
	
	RPG_FIRE,
	
	GRENADE_PINPULLED,
	GRENADE_THROW,
	
	MELEE_SWING,
	MELEE_HIT,

	BREATH_LITE,
	BREATH_HEAVY,
	
	EXPLOSION,
	WEAPON_SWITCH,
	WEAPON_PICKUP,
	WEAPON_DROPPED,
	AMMO_PICKUP,
	HEALTH_PACK_PICKUP,
	RUFFLE,
	
	BOMB_TICK,
	BOMB_PLANT,
	BOMB_DISARM,
	
	UI_ELEMENT_HOVER,
	UI_ELEMENT_SELECT,
	
	UI_NAVIGATE,
	UI_KEY_TYPE,
	
	IMPACT_METAL,
	IMPACT_WOOD,
	IMPACT_FOLIAGE,
	IMPACT_DEFAULT,
	
	TANK_START_MOVE,
	TANK_MOVE1,
	TANK_MOVE2,
	TANK_TURRET_MOVE, 
	
	MUTE,
	;
	
	private static final SoundType[] values = values();
	
	public byte netValue() {
		return (byte)this.ordinal();
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
