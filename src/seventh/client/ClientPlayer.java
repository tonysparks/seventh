/*
 * see license.txt 
 */
package seventh.client;

import seventh.game.Entity;
import seventh.game.net.NetPlayerPartialStat;
import seventh.game.net.NetPlayerStat;

/**
 * @author Tony
 *
 */
public class ClientPlayer {

	private ClientPlayerEntity entity;
	private ClientTeam team;
	private NetPlayerStat stats;
	
	private int id;
	private String name;
	private int spectatingPlayerId;
	
	/**
	 * 
	 */
	public ClientPlayer(String name, int playerId) {
		this.name = name;
		this.id = playerId;
		this.stats = new NetPlayerStat();
		this.team = ClientTeam.NONE;
		this.spectatingPlayerId = Entity.INVALID_ENTITY_ID;
	}

	public void updateStats(NetPlayerStat state) {
		this.stats= state;
		this.name = state.name;
				
		if(this.team.getId() != this.stats.teamId) {
			changeTeam(ClientTeam.fromId(this.stats.teamId));			
		}
	}
	
	public void updatePartialStats(NetPlayerPartialStat state) {
		this.stats.deaths = state.deaths;
		this.stats.kills = state.kills;		
	}
	
	public int getId() {
		return this.id;
	}
	
	/**
	 * @return this players name
	 */
	public String getName() {
		return name;
	}
	
	public int getKills() {
		return this.stats.kills;
	}
	
	public int getDeaths() {
		return this.stats.deaths;
	}
	
	public int getPing() {
		return this.stats.ping;
	}
	
	
	
	/**
	 * @return the team
	 */
	public ClientTeam getTeam() {
		return team;
	}
	
	public void changeTeam(ClientTeam team) {
		this.team = team;
		if(isAlive()) {
			this.entity.changeTeam(team);
		}
	}
	
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(ClientPlayerEntity entity) {
		if(this.entity!=entity) {
			this.entity = entity;
			if(this.entity != null && team!= null) {
				this.entity.changeTeam(getTeam());
			}
		}
	}
	
	/**
	 * @return the entity
	 */
	public ClientPlayerEntity getEntity() {
		return entity;
	}
	
	/**
	 * @return if this player is Alive
	 */
	public boolean isAlive() {
		return entity != null && entity.isAlive();
	}
	
	/**
	 * @return true if this player is spectating
	 */
	public boolean isSpectating() {
		return this.team == null || this.team == ClientTeam.NONE || this.spectatingPlayerId != Entity.INVALID_ENTITY_ID;
	}
	
	/**
	 * @param spectatingPlayerId the spectatingPlayerId to set
	 */
	public void setSpectatingPlayerId(int spectatingPlayerId) {
		this.spectatingPlayerId = spectatingPlayerId;
	}
	
	/**
	 * @return the spectatingPlayerId
	 */
	public int getSpectatingPlayerId() {
		return spectatingPlayerId;
	}
}
