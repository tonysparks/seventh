/*
 * see license.txt 
 */
package seventh.shared;

/**
 * Shared constants between the server and client
 * 
 * @author Tony
 *
 */
public interface SeventhConstants {

	/**
	 * Default port
	 */
	public static final int DEFAULT_PORT = 9844;
	
	public static final int MAX_TIMERS = 32;
	public static final int MAX_ENTITIES = 256;
	public static final int MAX_PLAYERS = 12;
	public static final int MAX_PERSISTANT_ENTITIES = 64;
	
	public static final int MAX_SOUNDS = 32;
	
	public static final int SPAWN_INVINCEABLILITY_TIME = 2_000;
	
	
	public static final int PLAYER_HEARING_RADIUS = 900;
	public static final int PLAYER_WIDTH = 24;//16;
	public static final int PLAYER_HEIGHT = 24;
	
	public static final int PLAYER_SPEED = 120; 
	public static final int PLAYER_MIN_SPEED = 20;
	public static final int RUN_DELAY_TIME = 300;
	public static final int SPRINT_DELAY_TIME = 200;
	
	public static final float WALK_SPEED_FACTOR = 0.484f;
	public static final float SPRINT_SPEED_FACTOR = 1.60f; // 1.95f
	
	public static final int ENTERING_VEHICLE_TIME = 2500;
	public static final int EXITING_VEHICLE_TIME = 2000;
	
	public static final int RECOVERY_TIME = 2000;
	
	public static final byte MAX_STAMINA = 100;
	public static final float STAMINA_DECAY_RATE = 4; // 2
	public static final float STAMINA_RECOVER_RATE = 0.5f;
}
