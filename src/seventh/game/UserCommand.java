/*
 * see license.txt 
 */
package seventh.game;


/**
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
