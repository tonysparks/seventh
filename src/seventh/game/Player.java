/*
 * see license.txt 
 */
package seventh.game;

import java.util.Date;

import seventh.game.Entity.Type;
import seventh.game.PlayerEntity.Keys;
import seventh.game.net.NetPlayerPartialStat;
import seventh.game.net.NetPlayerStat;
import seventh.math.Vector2f;
import seventh.shared.Debugable;
import seventh.shared.TimeStep;

/**
 * Represents a player in the game
 * 
 * @author Tony
 *
 */
public class Player implements PlayerInfo, Debugable {

	/**
	 * Delay to next spawn
	 */
	public static final long SPAWN_DELAY = 3000;
	public static final long LOOK_AT_DEATH_DELAY = 2000;
	
	private int id;
	private String name;
	
	private int kills;
	private int deaths;
	private int ping;
	
	private long joinTime;
	
	private PlayerEntity entity;
	private Player spectating;
	private Team team;
	private byte teamId;
	private boolean isBot;	
	private boolean isDummy;
	private boolean isCommander;
	
	private long spawnTime;
	private long lookAtDeathTime;
	private boolean isLooking;
	
	private NetPlayerStat stats;
	private NetPlayerPartialStat partialStats;
	
	private Vector2f killedAt;
	
	private Type weaponClass;
	
	private int previousKeys;
	
	/**
	 * @param id
	 */
	public Player(int id) {
		this(id, false, false, "");
	}
	
	/**
	 * @param id
	 * @param isBot
	 * @param isDummy
	 * @param name
	 */
	public Player(int id, boolean isBot, boolean isDummy, String name) {
		this.id = id;		
		this.isBot = isBot;
		this.isDummy = isDummy;
		this.name = name;
		this.joinTime = System.currentTimeMillis();
		this.stats = new NetPlayerStat();
		this.stats.playerId = id;
		
		this.partialStats = new NetPlayerPartialStat();
		this.partialStats.playerId = id;
		
		this.weaponClass = Type.UNKNOWN;
		
		this.killedAt = new Vector2f();
				
		setTeam(Team.SPECTATOR);
	}
	
	/**
	 * Resets the players statistics
	 */
	public void resetStats() {
		this.deaths = 0;
		this.kills = 0;
		this.joinTime = System.currentTimeMillis();
		this.team = Team.SPECTATOR;
		// DO NOT RESET teamId, this is used for map_restarts to keep
		// people on the same team
		// this.teamId = this.team.getId();
		this.entity = null;
		
		this.killedAt.zeroOut();
		
		this.isLooking = false;
		this.lookAtDeathTime = 0;
	}
	
	/**
	 * Updates the counter so that we are ready to spawn this player
	 * @param timeStep
	 */
	public void updateSpawnTime(TimeStep timeStep) {
		if(spawnTime > 0) {
			spawnTime -= timeStep.getDeltaTime();
		}		
	}
	
	/**
	 * Updates the counter for looking at own death
	 * @param timeStep
	 */
	public void updateLookAtDeathTime(TimeStep timeStep) {		
		if(lookAtDeathTime > 0) {
			lookAtDeathTime -= timeStep.getDeltaTime();
		}
	}
	
	/**
	 * Marks the spot where this player was killed
	 */
	public void setKilledAt() {
		if(this.entity != null) {
			this.killedAt.set(this.entity.getPos());
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#getKilledAt()
	 */
	@Override
	public Vector2f getKilledAt() {
		return killedAt;
	}
	
	/**
	 * @param weaponClass the weaponClass to set
	 */
	public void setWeaponClass(Type weaponClass) {
		this.weaponClass = weaponClass;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#getWeaponClass()
	 */
	@Override
	public Type getWeaponClass() {
		return weaponClass;
	}
	
	/**
	 * The player has died, delay their time to spawn
	 */
	public void applySpawnDelay() {
		applySpawnDelay(SPAWN_DELAY);
	}
	
	/**
	 * The player has died, delay their time to spawn
	 * 
	 * @param time
	 */
	public void applySpawnDelay(long time) {
		this.spawnTime = time;
	}
	
	/**
	 * Let the player soak in that they have died.
	 */
	public void applyLookAtDeathDelay() {
		if(!this.isLooking) {
			this.lookAtDeathTime = LOOK_AT_DEATH_DELAY;
			this.isLooking = true;
		}
	}
	
	/**
	 * @return true if the spawn delay has been applied
	 */
    public boolean spawnDelayApplied() {
    	return spawnTime > 0;
    }
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#readyToSpawn()
	 */
	@Override
	public boolean readyToSpawn() {
		return (isDead() && spawnTime <= 0);
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#readyToLookAwayFromDeath()
	 */
	@Override
	public boolean readyToLookAwayFromDeath() {
		return this.isDead() && this.isLooking && this.lookAtDeathTime <= 0;
	}
	
	/**
	 * @param deaths the deaths to set
	 */
	public void setDeaths(int deaths) {
		this.deaths = deaths;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#getDeaths()
	 */
	@Override
	public int getDeaths() {
		return deaths;
	}
	
	public int incrementDeaths() {
		return ++this.deaths;
	}
	
	public int incrementKills() {
		return ++this.kills;
	}
	public int loseKill() {
		return --this.kills;
	}
			
	/**
	 * @param kills the kills to set
	 */
	public void setKills(int kills) {
		this.kills = kills;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#getKills()
	 */
	@Override
	public int getKills() {
		return kills;
	}
	
	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#getName()
	 */
	@Override
	public String getName() {
		return name;
	}
	
	/**
	 * @param ping the ping to set
	 */
	public void setPing(int ping) {
		this.ping = ping;
	}
	
	/**
	 * @return the ping
	 */
	@Override
	public int getPing() {
		return ping;
	}
	
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#isBot()
	 */
	@Override
	public boolean isBot() {
		return isBot;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#isDummyBot()
	 */
	@Override
	public boolean isDummyBot() {	
		return this.isDummy;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#getJoinTime()
	 */
	@Override
	public long getJoinTime() {
		return joinTime;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#getId()
	 */
	@Override
	public int getId() {
		return id;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#isTeammateWith(int)
	 */
	@Override
	public boolean isTeammateWith(int playerId) {	
		return getTeam().onTeam(spectating);
	}
	
	/**
	 * Kills themselves
	 */
	public void commitSuicide() {
		if(hasEntity()) {
			getEntity().kill(getEntity());
		}
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#hasEntity()
	 */
	@Override
	public boolean hasEntity() {
		return this.entity != null;
	}
	
	
	@Override
	public boolean canSpawn() {
		return isDead() && !isPureSpectator() && !isCommander();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#isDead()
	 */
	@Override
	public boolean isDead() {
		return this.entity == null || !this.entity.isAlive();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#isAlive()
	 */
	@Override
	public boolean isAlive() {
		return !isDead();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#isSpectating()
	 */
	@Override
	public boolean isSpectating() {
		return getSpectatingEntity() != null || team==Team.SPECTATOR;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#isPureSpectator()
	 */
	@Override
	public boolean isPureSpectator() {
		return team==Team.SPECTATOR;
	}
	
	@Override
	public boolean isCommander() {
		return this.isCommander;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#getSpectatingEntity()
	 */
	@Override
	public PlayerEntity getSpectatingEntity() {
		return spectating != null && spectating.isAlive() ? spectating.getEntity() : null;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#getSpectatingPlayerId()
	 */
	@Override
	public int getSpectatingPlayerId() {
		PlayerEntity ent = getSpectatingEntity();
		if(ent==null) {
			return Entity.INVALID_ENTITY_ID;
		}
		return ent.getId();
	}

	/**
	 * Handle input from the player
	 * 
	 * @param game
	 * @param keys
	 */
	public void handleInput(Game game, int keys) {
		if (isSpectating()) {
			if(Keys.LEFT.isDown(this.previousKeys) && !Keys.LEFT.isDown(keys)) {
				Player spectateMe = game.getGameType().getPrevPlayerToSpectate(game.getPlayers(), this);
				setSpectating(spectateMe);
			}
			else if(Keys.RIGHT.isDown(this.previousKeys) && !Keys.RIGHT.isDown(keys)) {
				Player spectateMe = game.getGameType().getNextPlayerToSpectate(game.getPlayers(), this);
				setSpectating(spectateMe);
			}
			this.previousKeys = keys;
		}
	}
	
	/**
	 * Make this player a commander
	 */
	public void setCommander(boolean commander) {
		this.isCommander = commander;
	}
	
	/**
	 * @param spectatingEntity the spectatingEntity to set
	 */
	public void setSpectating(Player spectating) {
		this.spectating = spectating;
	}
	
	/**
	 * Stops spectating a player
	 */
	public void stopSpectating() {
		this.spectating = null;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#getSpectating()
	 */
	@Override
	public Player getSpectating() {
		return this.spectating;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#getEntity()
	 */
	@Override
	public PlayerEntity getEntity() {
		return entity;
	}
	
	/**
	 * @param entity the entity to set
	 */
	public void setEntity(PlayerEntity entity) {
		this.entity = entity;
		if(hasEntity()) {
			this.entity.setTeam(team);
			this.spawnTime = SPAWN_DELAY;
			this.spectating = null;
			
			this.isLooking = false;
			this.lookAtDeathTime = 0;
		}
	}
	
	/**
	 * @param team the team to set
	 */
	public void setTeam(Team team) {
		this.team = team;
		if(this.team==null) {
			this.team = Team.SPECTATOR;			
		}
		else if(hasEntity()) {
			this.entity.setTeam(team);
		}
		
		this.teamId = this.team.getId();
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#getTeam()
	 */
	@Override
	public Team getTeam() {
		return team;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.PlayerInfo#getTeamId()
	 */
	@Override
	public byte getTeamId() {
		return teamId;
	}
	
	/**
	 * @return the network player statistics
	 */
	public NetPlayerStat getNetPlayerStat() {
		this.stats.isBot = this.isBot;
		this.stats.joinTime = (int)this.joinTime;
		this.stats.kills = (short)this.kills;
		this.stats.deaths = (short)this.deaths;
		this.stats.ping = (short)this.ping; 
		this.stats.name = this.name;
		this.stats.teamId = (team!=null) ? team.getId() : Team.SPECTATOR_TEAM_ID;
		return this.stats;
	}

	/**
	 * @return the partial statistics of this player
	 */
	public NetPlayerPartialStat getNetPlayerPartialStat() {
		this.partialStats.kills = (short)this.kills;
		this.partialStats.deaths = (short)this.deaths;		
		return this.partialStats;
	}
	

	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation me = new DebugInformation();
		me.add("id", getId())
		  .add("name", getName())
		  .add("deaths", getDeaths())
		  .add("kills", getKills())
		  .add("ping", getPing())
		  .add("time_joined", new Date(getJoinTime()).toString())		  
		  .add("weapon_class", getWeaponClass().name())
		  .add("isAlive", isAlive())
		  .add("entity_id", isAlive() ? getEntity().getId() : null);
		;
		return me;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return getDebugInformation().toString();
	}
}
