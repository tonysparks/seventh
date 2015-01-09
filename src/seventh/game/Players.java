/*
 * see license.txt 
 */
package seventh.game;

import java.util.Random;

import seventh.shared.SeventhConstants;

/**
 * Simple structure that holds the Players, this structure allows for
 * mutable operations on the list of Players.
 * 
 * @author Tony
 *
 */
public class Players implements PlayerInfos {
	
	public static interface PlayerIterator {
		public void onPlayer(Player player);
	}
	
	private Player[] players;
	private Random random;
	
	/**
	 * 
	 */
	public Players() {
		this.players = new Player[SeventhConstants.MAX_PLAYERS];
		this.random = new Random();
	}
	
	/**
	 * @param id
	 * @return true if the supplied ID is valid
	 */
	private boolean isValidId(int id) {
		return (id >= 0  & id < this.players.length);
	}

	/**
	 * @param id
	 * @return the {@link Player}
	 */
	public Player getPlayer(int id) {
		if(isValidId(id)) {		
			return this.players[id];
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfos#getPlayerInfo(int)
	 */
	@Override
	public PlayerInfo getPlayerInfo(int id) {
		return getPlayer(id);
	}
	
	/**
	 * Adds a {@link Player}
	 * @param player
	 */
	public void addPlayer(Player player) {
		if(isValidId(player.getId())) {
			this.players[player.getId()] = player;
		}		
	}
	
	/**
	 * Removes a {@link Player}
	 * @param id
	 * @return the {@link Player} if found
	 */
	public Player removePlayer(int id) {
		Player player = null;
		if(isValidId(id)) {
			player = this.players[id];
			this.players[id] = null;
		}
		return player;
	}
	
	/**
	 * Removes a {@link Player}
	 * @param player
	 * @return the {@link Player} if found
	 */
	public Player removePlayer(Player player) {
		return removePlayer(player.getId());
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfos#getNumberOfPlayers()
	 */
	@Override
	public int getNumberOfPlayers() {
		int sum = 0;
		for(int i = 0; i < this.players.length; i++) {
			if(this.players[i] != null) {
				sum++;
			}
		}
		return sum;
	}
	
	/**
	 * Iterates through all the available players, invoking the 
	 * {@link PlayerIterator} for each player.
	 * @param it
	 */
	public void forEachPlayer(PlayerIterator it) {
		for(int i = 0; i < this.players.length; i++) {
			Player player = this.players[i];
			if(player != null) {
				it.onPlayer(player);
			}
		}
	}
	

	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfos#forEachPlayerInfo(seventh.game.Players.PlayerInfoIterator)
	 */
	@Override
	public void forEachPlayerInfo(PlayerInfoIterator it) {
		for(int i = 0; i < this.players.length; i++) {
			Player player = this.players[i];
			if(player != null) {
				it.onPlayerInfo(player);
			}
		}
	}
	
	/**
	 * @return the underlying list of players.  For performance reasons
	 * this array may contain empty slots, it is up to the client
	 * to filter these.
	 */
	public Player[] getPlayers() {
		return this.players;
	}
	

	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfos#getPlayerInfos()
	 */
	@Override
	public PlayerInfo[] getPlayerInfos() {
		return this.players;
	}
	
	
	/**
	 * Resets all of the players statistics
	 */
	public void resetStats() {
		for(int i = 0; i < this.players.length; i++) {
			Player player = this.players[i];
			if(player != null) {
				player.resetStats();
			}
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfos#getRandomAlivePlayer()
	 */
	@Override
	public Player getRandomAlivePlayer() {		
		int size = players.length;
		int startingIndex = random.nextInt(size);
		
		for(int i = 0; i < size; i++) {
			Player player = players[(startingIndex + i) % size];
			if(player != null && player.isAlive()) {
				return player;
			}
		}
		return null;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfos#getPrevAlivePlayerFrom(seventh.game.Player)
	 */
	@Override
	public Player getPrevAlivePlayerFrom(Player oldPlayer) {
		// TODO
		return getNextAlivePlayerFrom(oldPlayer);
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfos#getNextAlivePlayerFrom(seventh.game.Player)
	 */
	@Override
	public Player getNextAlivePlayerFrom(Player oldPlayer) {
		if(oldPlayer == null ) return getRandomAlivePlayer();
		
		boolean found = false;
		for(int i = 0; i < this.players.length; i++) {
			Player player = this.players[i];
			if(player == null) {
				continue;
			}
			
			if(oldPlayer.getId() == player.getId()) {
				found = true;
			}
			else if(found) {
				if(player.isAlive()) {
					return player;
				}
			}
		}
		
		if(!found) {
			return getRandomAlivePlayer();
		}
		
		return null;
	}
	
}
