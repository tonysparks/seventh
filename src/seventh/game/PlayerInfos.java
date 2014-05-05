/*
 * see license.txt 
 */
package seventh.game;


/**
 * Simple structure that holds references to all the Players in the game
 * 
 * @author Tony
 *
 */
public interface PlayerInfos {


	/**
	 * Simple call back mechanism used for iterating through the 
	 * available players.
	 * 
	 * @author Tony
	 *
	 */
	public static interface PlayerInfoIterator {
		public void onPlayerInfo(PlayerInfo player);
	}
	
	/**
	 * @param id the {@link PlayerInfo} id
	 * @return the {@link PlayerInfo} if found, null if no entity exists with
	 * the supplied id
	 */
	public abstract PlayerInfo getPlayerInfo(int id);

	/**
	 * @return the total number of players in the game
	 */
	public abstract int getNumberOfPlayers();

	/**
	 * Iterates through all the available players, invoking the 
	 * {@link PlayerInfoIterator} for each player.
	 * @param it
	 */
	public abstract void forEachPlayerInfo(PlayerInfoIterator it);

	/**
	 * @return the underlying list of players.  For performance reasons
	 * this array may contain empty slots, it is up to the client
	 * to filter these.
	 */
	public abstract PlayerInfo[] getPlayerInfos();

	/**
	 * @return attempts to find a Player that is alive.  Null if none are found
	 */
	public abstract Player getRandomAlivePlayer();

	public abstract Player getPrevAlivePlayerFrom(Player oldPlayer);

	public abstract Player getNextAlivePlayerFrom(Player oldPlayer);

}