/*
 * see license.txt 
 */
package seventh.game;


/**
 * Allows an {@link Entity} to be responsive to {@link UserCommand}'s
 * 
 * @author Tony
 *
 */
public interface Controllable {

	/**
	 * Handles the users input commands
	 * 
	 * @param keys
	 * @param orientation
	 */
	public void handleUserCommand(int keys, float orientation);			
}
