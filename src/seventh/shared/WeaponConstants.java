/*
 * The Seventh
 * see license.txt 
 */
package seventh.shared;

/**
 * Placed in constants so both the server and client can reference
 * 
 * @author Tony
 *
 */
public interface WeaponConstants {

	public static final int DEFAULT_LINE_OF_SIGHT = 150;
	
	public static final int THOMPSON_LINE_OF_SIGHT = 200;
	public static final int THOMPSON_WEIGHT = 5;
	
	public static final int SPRINGFIELD_LINE_OF_SIGHT = 400;
	public static final int SPRINGFIELD_WEIGHT = 40;
	
	public static final int M1GARAND_LINE_OF_SIGHT = 300;
	public static final int M1GARAND_WEIGHT = 13;	
	
	public static final int MP44_LINE_OF_SIGHT = 250;
	public static final int MP44_WEIGHT = 38;
	
	public static final int MP40_LINE_OF_SIGHT = 200;
	public static final int MP40_WEIGHT = 5;
	
	public static final int KAR98_LINE_OF_SIGHT = 400;
	public static final int KAR98_WEIGHT = 15;
	
	public static final int PISTOL_LINE_OF_SIGHT = 150;
	public static final int PISTOL_WEIGHT = 2;

	public static final int RPG_LINE_OF_SIGHT = 200;
	public static final int RPG_WEIGHT = 60;
	
	public static final int SHOTGUN_LINE_OF_SIGHT = 180;
	public static final int SHOTGUN_WEIGHT = 12;
	
	public static final int RISKER_LINE_OF_SIGHT = 300;
	public static final int RISKER_WEIGHT = 40;
	

	/*
	 * Tank properties
	 */
	public static final int TANK_DEFAULT_LINE_OF_SIGHT = 300;
	public static final int TANK_MOVEMENT_SPEED = 90; // 50
	public static final int TANK_WIDTH = 145;
	public static final int TANK_HEIGHT = 110;
	public static final int TANK_AABB_WIDTH = 200;
	public static final int TANK_AABB_HEIGHT = 200;
	
	/**
	 * Number of pixels the player must be away in order
	 * to be able to operate a vehicle by pressing the USE
	 * key
	 */
	public static final int VEHICLE_HITBOX_THRESHOLD = 15;
	
}
