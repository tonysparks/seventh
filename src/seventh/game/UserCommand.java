/*
 * see license.txt 
 */
package seventh.game;


/**
 * A simple structure holding the information a player has issued
 * as input.  This structure is sent to the server for processing (moving,
 * shooting, etc. for the PlayerEntity).
 * 
 * @author Tony
 *
 */
public class UserCommand {

	private int keys;
	private float orientation;
	
	/**
	 * @param keys
	 */
	public UserCommand(int keys, float orientation) {		
		this.keys = keys;
		this.orientation = orientation;
	}
	/**
	 * @return the keys
	 */
	public int getKeys() {
		return keys;
	}
	
	/**
	 * @return the orientation
	 */
	public float getOrientation() {
		return orientation;
	}
}
