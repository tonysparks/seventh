/*
 * see license.txt 
 */
package seventh.game;

import java.util.ArrayList;
import java.util.List;

import seventh.game.entities.Entity;
import seventh.game.entities.PlayerEntity;
import seventh.game.net.NetTeam;
import seventh.math.Vector2f;
import seventh.shared.Debugable;

/**
 * A team is a collection of {@link Player}s. The team contains a score, at the
 * end of a game the team with the highest score wins!
 * 
 * @author Tony
 *
 */
public class Team implements Debugable {

	/**
	 * Team ID's
	 */
	public static final byte ALLIED_TEAM_ID = 2, AXIS_TEAM_ID = 4, SPECTATOR_TEAM_ID = -1;

	/**
	 * Team Names
	 */
	public static final String ALLIED_TEAM_NAME = "Allies";
	public static final String AXIS_TEAM_NAME = "Axis";
	public static final String SPECTATOR_NAME = "Spectator";

	/**
	 * The Spectator team
	 */
	public static final Team SPECTATOR = new Team(SPECTATOR_TEAM_ID) {

		/**
		 * Spectators are not allowed to score points
		 */
		public void score(int points) {
		}
	};

	/**
	 * @return a new Allied team
	 */
	public static Team newAlliedTeam() {
		return new Team(ALLIED_TEAM_ID);
	}

	/**
	 * @return a new Axis team
	 */
	public static Team newAxisTeam() {
		return new Team(AXIS_TEAM_ID);
	}

	/**
	 * Gets the Team name based on the Team ID.
	 * 
	 * @param id
	 *            the Team ID
	 * @return the name representing the ID
	 */
	public static String getName(byte id) {
		return CheckNameType(id);
	}

	public static String CheckNameType(byte id) {
		switch (id) {
		case ALLIED_TEAM_ID: {
			return ALLIED_TEAM_NAME;
		}
		case AXIS_TEAM_ID: {
			return AXIS_TEAM_NAME;
		}
		default: {
			return SPECTATOR_NAME;
		}
		}
	}

	private final byte id;
	private List<Player> players;
	private NetTeam netTeam;

	private int score;
	private boolean isAttacker;
	private boolean isDefender;

	/**
	 * 
	 */
	private Team(byte id) {
		this.id = id;
		netTeam = new NetTeam();
		netTeam.id = id;

		this.players = new ArrayList<Player>();
	}

	/**
	 * @param isAttacker
	 *            the isAttacker to set
	 */
	public void setAttacker(boolean isAttacker) {
		this.isAttacker = isAttacker;
		this.isDefender = false;
	}

	/**
	 * @param isDefender
	 *            the isDefender to set
	 */
	public void setDefender(boolean isDefender) {
		this.isDefender = isDefender;
		this.isAttacker = false;
	}

	/**
	 * @return the isAttacker
	 */
	public boolean isAttacker() {
		return isAttacker;
	}

	/**
	 * @return the isDefender
	 */
	public boolean isDefender() {
		return isDefender;
	}

	/**
	 * @return true if this is either the allied or axis team
	 */
	public boolean isValid() {
		return this.getId() != SPECTATOR.getId();
	}

	/**
	 * Returns the number of players in the supplied list that are on this team.
	 * 
	 * @param players
	 * @return the number of players in the list on this team
	 */

	public int getNumberOfPlayersOnTeam(List<PlayerEntity> players) {
		return CalculateNumberOfPlayerOnTeam(players);
	}

	public int CalculateNumberOfPlayerOnTeam(List<PlayerEntity> players) {
		int sum = 0;

		for (int i = 0; i < players.size(); i++) {
			if (CheckPlayerOnTeam(players, i))
				sum++;
		}
		return sum;
	}

	public boolean CheckPlayerOnTeam(List<PlayerEntity> players, int i) {
		PlayerEntity player = players.get(i);
		Team team = player.getTeam();
		if ((player != null) && (team != null) && (team.getId() == getId()))
			return true;
		return false;
	}

	/**
	 * @return the team name
	 */
	public String getName() {
		return getName(getId());
	}

	/**
	 * Score points
	 * 
	 * @param points
	 */
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
		if (!IsAlreadyOnTeam(p)) {
			p.setTeam(this);
		}
	}

	public boolean IsAlreadyOnTeam(Player p) {
		boolean isAlreadyOnTeam = false;
		for (int i = 0; i < this.players.size(); i++) {
			if ((getPlayer(i) != null) && (getPlayer(i).getId() == p.getId())) {
					isAlreadyOnTeam = true;
					break;
			}
		}
		return isAlreadyOnTeam;
	}
	
	public Player getPlayer(int i){
		return this.players.get(i);
	}

	public void removePlayer(Player p) {
		this.players.remove(p);
		p.setTeam(null);
	}

	/**
	 * Determines if the supplied player is on the team
	 * 
	 * @param p
	 * @return true if the supplied player is on the team
	 */
	public boolean onTeam(Player p) {
		return this.players.contains(p);
	}

	public boolean onTeam(int playerId) {
		int size = this.players.size();
		for (int i = 0; i < size; i++) {
			if (this.players.get(i).getId() == playerId) {
				return true;
			}
		}

		return false;
	}

	/**
	 * @return the number of players on this team
	 */
	public int teamSize() {
		return this.players.size();
	}

	/**
	 * @return the total number of kills this team as accrued
	 */
	public int getTotalKills() {
		int kills = 0;
		for (int i = 0; i < this.players.size(); i++) {
			kills += this.players.get(i).getKills();
		}

		return kills;
	}

	/**
	 * @return true if and only if each team member is dead
	 */
	public boolean isTeamDead() {
		boolean isDead = true;
		for (int i = 0; i < this.players.size(); i++) {
			isDead &= this.players.get(i).isDead();
		}
		return isDead;
	}

	/**
	 * @return true if there is a commander on this team
	 */
	public boolean hasCommander() {
		for (int i = 0; i < this.players.size(); i++) {
			if (this.players.get(i).isCommander()) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @return a semi-random alive player that is on this team, null if no
	 *         player is alive
	 */
	public Player getAlivePlayer() {
		for (int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(i);
			if (player.isAlive()) {
				return player;
			}
		}
		return null;
	}

	/**
	 * @return a semi-random alive player (that is a bot) that is on this team,
	 *         null if no player is alive
	 */
	public Player getAliveBot() {
		for (int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(i);
			if (player.isAlive() && player.isBot()) {
				return player;
			}
		}
		return null;
	}

	/**
	 * @return all the players on this team
	 */
	public List<Player> getPlayers() {
		return this.players;
	}

	/**
	 * Gets the closest alive player to the supplied entity
	 * 
	 * @param other
	 * @return the closest player or null if no alive players
	 */
	public Player getClosestPlayerTo(Entity other) {
		Player closest = null;
		float distance = -1;

		for (int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(i);
			if (player.isAlive()) {
				PlayerEntity ent = player.getEntity();
				float dist = Vector2f.Vector2fDistanceSq(ent.getPos(), other.getPos());
				if (closest != null || dist < distance) {
					closest = player;
					distance = dist;
				}
			}
		}
		return closest;
	}

	/**
	 * Gets the closest alive bot to the supplied entity
	 * 
	 * @param other
	 * @return the closest bot or null if no alive players
	 */
	public Player getClosestBotTo(Entity other) {
		Player closest = null;
		float distance = -1;

		for (int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(i);
			if (player.isAlive() && player.isBot()) {
				PlayerEntity ent = player.getEntity();
				float dist = Vector2f.Vector2fDistanceSq(ent.getPos(), other.getPos());
				if (closest != null || dist < distance) {
					closest = player;
					distance = dist;
				}
			}
		}
		return closest;
	}

	/**
	 * The next available alive player on this team from the 'oldPlayer'
	 * 
	 * @param oldPlayer
	 * @return the next {@link Player} or null if none
	 */
	public Player getNextAlivePlayerFrom(Player oldPlayer) {
		if (oldPlayer == null)
			return getAlivePlayer();

		int nextPlayerIndex = NextPlayerIndex(players.indexOf(oldPlayer));

		for (int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(nextPlayerIndex);
			if (IsPrevAlivePlayer(player, oldPlayer)) {
					return player;
			}

			nextPlayerIndex = (nextPlayerIndex + 1) % players.size();
		}

		return null;
	}
	
	
	
	public int NextPlayerIndex(int oldPlayerIndex){
		if(oldPlayerIndex < 0)
			return 0;
		return oldPlayerIndex;
	}

	/**
	 * The previous available alive player on this team from the 'oldPlayer'
	 * 
	 * @param oldPlayer
	 * @return the previous {@link Player} or null if none
	 */

	
	public Player getPrevAlivePlayerFrom(Player oldPlayer) {
		if (oldPlayer == null)
			return getAlivePlayer();

		int nextPlayerIndex = players.indexOf(oldPlayer);
		nextPlayerIndex = IsUnderIndex(nextPlayerIndex);

		for (int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(nextPlayerIndex);
			if (IsPrevAlivePlayer(player, oldPlayer)) {
					return player;
			}

			nextPlayerIndex = (nextPlayerIndex - 1) % players.size();
			nextPlayerIndex = IsUnderIndex(nextPlayerIndex);
		}
		return null;
	}
	
	public boolean IsPrevAlivePlayer(Player player, Player oldPlayer){
		return player != null && player.isAlive() && player != oldPlayer;
	}
	
	public int IsUnderIndex(int nextPlayerIndex){
		if(nextPlayerIndex <0)
			return Math.max(players.size()-1, 0);
		return nextPlayerIndex;
	}
	
	

	/**
	 * The next alive bot from the supplied Player slot
	 * 
	 * @param old
	 * @return the next alive bot, or null if no bot is alive
	 */
		
	public Player getNextAliveBotFrom(Player old) {
		if (old == null) {
			return getAliveBot();
		}

		boolean found = false;
		boolean firstIteration = true;

		for (int i = 0; i < this.players.size(); i++) {
			Player player = this.players.get(i);
			found = IsFoundId(firstIteration, old, player);
			if(OverPlayersSize(i)){
				i = 0;
				firstIteration = false;
			}
			if(FoundOrAliveAndBot(found, player))
				return player;
		}
		return null;
	}
	
	public boolean FoundOrAliveAndBot(boolean found, Player player){
		return (found && IsAliveAndBot(player)||IsAliveAndBot(player));
		
	}
	
	public boolean OverPlayersSize(int i){
		return i>= this.players.size()-1;
	}
	
	public boolean IsAliveAndBot(Player player){
		return player.isAlive() && player.isBot();
	}
	
	public boolean IsFoundId(boolean firstIteration, Player old, Player player){
		if(firstIteration && (old.getId() == player.getId()))
				return true;
		return false;
	}
	

	/**
	 * @return the total number of deaths accrued on this team
	 */
	public int getTotalDeaths() {
		int deaths = 0;
		for (int i = 0; i < this.players.size(); i++) {
			deaths += this.players.get(i).getDeaths();
		}

		return deaths;
	}

	/**
	 * The number of total alive players on this team
	 * 
	 * @return the number of alive players on this team
	 */
	public int getNumberOfAlivePlayers() {
		int numberOfAlivePlayers = 0;
		for (int i = 0; i < this.players.size(); i++) {
			if (this.players.get(i).isAlive()) {
				numberOfAlivePlayers++;
			}
		}

		return numberOfAlivePlayers;
	}

	/**
	 * @return the number of bots on this team
	 */
	public int getNumberOfBots() {
		int numberOfBots = 0;
		for (int i = 0; i < this.players.size(); i++) {
			if (this.players.get(i).isBot()) {
				numberOfBots++;
			}
		}

		return numberOfBots;
	}

	/**
	 * @return the current team size
	 */
	public int getTeamSize() {
		return this.players.size();
	}

	/**
	 * @return this team on a serializable form
	 */
	public NetTeam getNetTeam() {
		netTeam.playerIds = new int[this.players.size()];
		for (int i = 0; i < this.players.size(); i++) {
			netTeam.playerIds[i] = this.players.get(i).getId();
		}
		netTeam.isDefender = this.isDefender;
		netTeam.isAttacker = this.isAttacker;
		return netTeam;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (obj instanceof Team) {
			return ((Team) obj).getId() == this.getId();
		}

		return false;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation me = new DebugInformation();
		me.add("name", getName()).add("id", getId()).add("score", getScore()).add("total_deaths", getTotalDeaths())
				.add("total_kills", getTotalKills()).add("players", getPlayers());

		return me;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDebugInformation().toString();
	}
}
