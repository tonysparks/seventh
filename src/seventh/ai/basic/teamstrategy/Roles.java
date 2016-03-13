/*
 * see license.txt 
 */
package seventh.ai.basic.teamstrategy;

import seventh.game.PlayerInfo;
import seventh.shared.SeventhConstants;

/**
 * A Roles database; keeps track of which roles are assigned to a player.
 * 
 * @author Tony
 *
 */
public class Roles {

	public enum Role {
		Capturer,	
		CoCapturer,
		Defender,		
		Retriever,
		
		None,
		
		MaxRoles,
		;
		
		private static Role[] values = values();
		public static Role get(int i) {
			return values[i]; 
		}
	}
	
	private PlayerInfo[][] roles;
	
	
	/**
	 * 
	 */
	public Roles() {
		this.roles = new PlayerInfo[Role.MaxRoles.ordinal()][];
		for(int i = 0; i < this.roles.length; i++) {
			this.roles[i] = new PlayerInfo[SeventhConstants.MAX_PLAYERS];
		}
	}
	
	public PlayerInfo getPlayer(Role role) {		
		for(int i = 0; i < this.roles[role.ordinal()].length; i++) {
			PlayerInfo entity = this.roles[role.ordinal()][i];
			if(entity!=null && entity.isAlive()) {
				return entity;
			}
		}
		
		return null;
	}
		
	public PlayerInfo[] getPlayers(Role role) {		
		return this.roles[role.ordinal()];
	}
	
	public boolean hasRoleAssigned(Role role) {
		return getPlayer(role) != null;
	}
	
	public Role getAssignedRole(PlayerInfo entity) {
		for(int i = 0; i < this.roles.length; i++) {
			if(this.roles[i][entity.getId()] != null) {
				return Role.get(i);
			}
		}
		
		return Role.None;
	}
	
	public void assignRole(Role role, PlayerInfo entity) {
		if(entity.isAlive()) {
			
			for(int i = 0; i < this.roles.length; i++) {
				this.roles[i][entity.getId()] = null;
			}
			
			this.roles[role.ordinal()][entity.getId()] = entity;
		}
	}
		
	public void removeDeadEntities() {
		for(int i = 0; i < this.roles.length; i++) {
			for(int j = 0; j < this.roles[i].length; j++) {
				if(this.roles[i][j] != null && !this.roles[i][j].isAlive()) {
					this.roles[i][j] = null;
				}
			}
		}
	}
	
	public void removeDeadPlayer(PlayerInfo entity) {
		for(int i = 0; i < this.roles.length; i++) {
			this.roles[i][entity.getId()] = null;
		}
	}

}
