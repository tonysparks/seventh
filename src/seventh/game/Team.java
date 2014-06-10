/*
 * see license.txt 
 */
package seventh.game;

import java.util.ArrayList;
import java.util.List;

import seventh.game.net.NetTeam;
import seventh.math.Vector2f;

/**
 * A team is a collection of {@link Player}s.  The team contains a score, at the
 * end of a game the team with the highest score wins!
 * 
 * @author Tony
 *
 */
public class Team {

	public static final byte ALLIED_TEAM=2, AXIS_TEAM=4, SPECTATOR_TEAM=-1;
	public static final String ALLIED_TEAM_NAME = "Allies";
	public static final String AXIS_TEAM_NAME = "Axis";
	public static final String SPECTATOR_NAME = "Spectator";
	
	/**
	 * Gets the Team name based on the Team ID.
	 * 
	 * @param id the Team ID
	 * @return the name representing the ID
	 */
	public static String getName(byte id) {
		switch(id) {
			case ALLIED_TEAM: {
				return ALLIED_TEAM_NAME;
			}
			case AXIS_TEAM: {
				return AXIS_TEAM_NAME;
			}
			default: {
				return SPECTATOR_NAME;
			}
		}
	}
	public final byte id;
	private List<Player> players;
	private NetTeam netTeam;
	
	private int score;
	
	public static final Team SPECTATOR = new Team(SPECTATOR_TEAM) {
		public void score(int points) {}
	};
	
	/**
	 * 
	 */
	public Team(byte id) {
		this.id = id;
		netTeam = new NetTeam();
		netTeam.id = id;		
		
		this.players = new ArrayList<Player>();
	}
	
	/**
	 * Returns the number of players in the supplied list that are on this 
	 * team.
	 * @param players
	 * @return the number of players in the list on this team
	 */
	public int getNumberOfPlayersOnTeam(List<PlayerEntity> players) {
		int sum = 0;
		for(int i = 0; i < players.size(); i++) {
			PlayerEntity player = players.get(i);
			if(player != null) {
				Team team = player.getTeam();
				
				if(team != null) {
					if(team.getId() == getId()) {
						sum++;
					}
				}
			}
		}
		
		return sum;
	}
	
	public String getName() {
		return getName(getId());
	}
	
	public void score(int points) {
		this.score += points;
	}
	
	/**
	 * @return the score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * @return the id
	 */
	public byte getId() {
		return id;
	}
	
	public void addPlayer(Player p) {
		this.players.add(p);
		p.setTeam(this);
	}
	public void removePlayer(Player p) {
		this.players.remove(p);
		p.setTeam(null);
	}
	
	public boolean onTeam(Player p) {
		return this.players.contains(p);
	}
	
	public int teamSize() {
		return this.players.size();
	}
	
	public int getTotalKills() {
		int kills = 0;
		for(int i = 0; i < this.players.size(); i++) {
			kills += this.players.get(i).getKills();
		}
		
		return kills;
	}
	
	public boolean isTeamDead() {
		boolean isDead = true;
		for(int i = 0; i < this.players.size(); i++) {
			isDead &= this.players.get(i).isDead();
		}
		return isDead;
	}
	
	public Player getAlivePlayer() {
		for(int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(i);
			if(player.isAlive()) {
				return player;
			}
		}
		return null;
	}
	
	public Player getAliveBot() {
		for(int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(i);
			if(player.isAlive() && player.isBot()) {
				return player;
			}
		}
		return null;
	}
	
	public List<Player> getPlayers() {
		return this.players;
	}
	
	/**
	 * Gets the closest alive player to the supplied entity
	 * @param other
	 * @return the closest player or null if no alive players
	 */
	public Player getClosestPlayerTo(Entity other) {
		Player closest = null;
		float distance = -1;
		
		for(int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(i);
			if(player.isAlive()) {
				PlayerEntity ent = player.getEntity();
				float dist = Vector2f.Vector2fDistanceSq(ent.getPos(), other.getPos());
				if(closest != null || dist < distance) {
					closest = player;
					distance = dist;
				}
			}
		}
		return closest;
	}
	
	/**
	 * Gets the closest alive bot to the supplied entity
	 * @param other
	 * @return the closest bot or null if no alive players
	 */
	public Player getClosestBotTo(Entity other) {
		Player closest = null;
		float distance = -1;
		
		for(int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(i);
			if(player.isAlive() && player.isBot()) {
				PlayerEntity ent = player.getEntity();
				float dist = Vector2f.Vector2fDistanceSq(ent.getPos(), other.getPos());
				if(closest != null || dist < distance) {
					closest = player;
					distance = dist;
				}
			}
		}
		return closest;
	}
	
	public Player getNextAlivePlayerFrom(Player old) {
		
		boolean found = false;
		boolean firstIteration = true;
		
		for(int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(i);
			if(firstIteration) {
				if(old.getId() == player.getId()) {
					found = true;
				}
				else if(found) {
				
					if(player.isAlive()) {
						return player;
					}
				}
				
				if(i>=this.players.size()-1) {
					i = 0;
					firstIteration = false;
				}
			}
			else {
				if(player.isAlive()) {
					return player;
				}
			}
		}
		return null;
	}
	
	public Player getNextAliveBotFrom(Player old) {
		if(old==null) {
			return getAliveBot();
		}
		
		boolean found = false;
		boolean firstIteration = true;
		
		for(int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(i);
			if(firstIteration) {
				if(old.getId() == player.getId()) {
					found = true;
				}
				else if(found) {
				
					if(player.isAlive() && player.isBot()) {
						return player;
					}
				}
				
				if(i>=this.players.size()-1) {
					i = 0;
					firstIteration = false;
				}
			}
			else {
				if(player.isAlive() && player.isBot()) {
					return player;
				}
			}
		}
		return null;
	}
	
	public int getTotalDeaths() {
		int deaths = 0;
		for(int i = 0; i < this.players.size(); i++) {
			deaths += this.players.get(i).getDeaths();
		}
		
		return deaths;
	}
	
	/**
	 * The number of total alive players on this team
	 * @return the number of alive players on this team
	 */
	public int getNumberOfAlivePlayers() {
		int numberOfAlivePlayers = 0;
		for(int i = 0; i < this.players.size(); i++) {
			if(this.players.get(i).isAlive()) {
				numberOfAlivePlayers++;
			}
		}
		
		return numberOfAlivePlayers;
	}
	
	public NetTeam getNetTeam() {
		netTeam.playerIds = new int[this.players.size()];
		for(int i = 0; i < this.players.size(); i++) {
			netTeam.playerIds[i] = this.players.get(i).getId();
		}
		return netTeam;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Team) {
			return ((Team)obj).getId() == this.getId();
		}
		
		return false;
	}
}
