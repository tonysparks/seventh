/*
 * see license.txt 
 */
package seventh.client;

import java.util.ArrayList;
import java.util.List;

/**
 * Storage for {@link ClientPlayer}s
 * 
 * @author Tony
 *
 */
public class ClientPlayers {

	private ClientPlayer[] players;
	
	/**
	 * @param maxEntities
	 */
	public ClientPlayers(int maxEntities) {
		this.players = new ClientPlayer[maxEntities];
	}
	
	/**
	 * The number of player slots, not the actual number
	 * of players in the game.
	 * 
	 * @return the maximum number of players
	 */
	public int getMaxNumberOfPlayers() {
		return players.length;
	}
	
	/**
	 * @param id
	 * @return the {@link ClientPlayer} at the supplied position or null if not available
	 */
	public ClientPlayer getPlayer(int id) {
		if(id > -1 && id < this.players.length) {
			return this.players[id];
		}
		return null;
	}
	
	/**
	 * @param id
	 * @return true if the entity exists in this list
	 */
	public boolean containsPlayer(int id) {
		if(id > -1 && id < this.players.length) {
			return this.players[id] != null;
		}
		return false; 
	}
	
	/**
	 * Adds the entity
	 * @param e
	 */
	public void addPlayer(ClientPlayer e) {
		int id = e.getId();
		if(id > -1 && id < this.players.length) {
			this.players[id] = e;
		}
				
	}
	
	/**
	 * Removes {@link ClientPlayer}
	 * @param id
	 */
	public ClientPlayer removePlayer(int id) {	
		ClientPlayer result = null;
		if(id > -1 && id < this.players.length) {
			result = this.players[id];
			this.players[id] = null;
		}
		return result;
	}
	
	/**
	 * Removes a {@link ClientPlayer}
	 * @param e
	 */
	public ClientPlayer removePlayer(ClientPlayer e) {
		return removePlayer(e.getId());
	}
	
	/**
	 * Clears out the entities
	 */
	public void clear() {
		for(int i = 0; i < this.players.length; i++) {			
			this.players[i] = null;
		}
	}
	
	
	/**
	 * @return as a new {@link List}
	 */
	public List<ClientPlayer> asList() {
		List<ClientPlayer> result = new ArrayList<ClientPlayer>();
		for(int i = 0; i < this.players.length; i++) {			
			if( this.players[i] != null) {
				result.add(this.players[i]);
			}
		}
		return result;
	}
	
}
