/*
 * see license.txt 
 */
package seventh.server;

/**
 * Listens for {@link GameSession} creation/destruction.
 * 
 * @author Tony
 *
 */
public interface GameSessionListener {

	/**
	 * A new {@link GameSession} has started
	 * 
	 * @param session
	 */
	public void onGameSessionCreated(GameSession session);
	
	
	/**
	 * The current {@link GameSession} has been terminated
	 * 
	 * @param session
	 */
	public void onGameSessionDestroyed(GameSession session);
}
