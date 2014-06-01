/*
 * see license.txt 
 */
package seventh.game;

import seventh.game.Entity.Type;
import seventh.math.Vector2f;

/**
 * Gather information about a Players state
 * 
 * @author Tony
 *
 */
public interface PlayerInfo {

	/**
	 * @return the killedAt the position this player was last killed at
	 */
	public abstract Vector2f getKilledAt();

	/**
	 * @return the weaponClass
	 */
	public abstract Type getWeaponClass();

	/**
	 * @return true if we are ready to spawn
	 */
	public abstract boolean readyToSpawn();

	/**
	 * @return determines if enough time has passed if the
	 * player can stop viewing their death
	 */
	public abstract boolean readyToLookAwayFromDeath();

	/**
	 * @return the number of times this player has died.
	 */
	public abstract int getDeaths();

	/**
	 * @return the number of kills this player has accrued.
	 */
	public abstract int getKills();

	/**
	 * @return the name
	 */
	public abstract String getName();

	/**
	 * @return true if this player is a bot
	 */
	public abstract boolean isBot();

	/**
	 * @return true if this player is a bot and has a 'dummy' brain
	 */
	public abstract boolean isDummyBot();
	
	/**
	 * @return the time this player joined the game.  In Epoch time.
	 */
	public abstract long getJoinTime();

	/**
	 * @return the ping of the Player. If this represents a remote player,
	 * this is be the round trip time from their client to the server in milliseconds.
	 */
	public abstract int getPing();
	
	/**
	 * @return the Players Id
	 */
	public abstract int getId();

	/**
	 * @return true if this player is controlling a game world {@link Entity}
	 */
	public abstract boolean hasEntity();

	/**
	 * @return true if this Player is not controlling an {@link Entity} or the
	 * controlled {@link Entity} is dead.
	 */
	public abstract boolean isDead();

	/**
	 * @return true if this Player is controlling an {@link Entity} and said {@link Entity}
	 * is alive.
	 * @see PlayerInfo#isDead()
	 */
	public abstract boolean isAlive();

	/**
	 * @return true if this player is spectating
	 */
	public abstract boolean isSpectating();

	/**
	 * @return the player is not in the game, but is a purely spectating
	 */
	public abstract boolean isPureSpectator();

	/**
	 * @return the {@link Entity} which this player is spectating
	 */
	public abstract PlayerEntity getSpectatingEntity();

	/**
	 * @return the {@link Entity}'s id which this player is spectating.
	 */
	public abstract int getSpectatingPlayerId();

	/**
	 * @return the {@link Player} which this player is spectating
	 */
	public abstract Player getSpectating();

	/**
	 * @return the {@link Entity} this player is controlling in the game world.
	 */
	public abstract PlayerEntity getEntity();

	/**
	 * @return the team which this player belongs to
	 */
	public abstract Team getTeam();

	/**
	 * @return the teamId (if the stats are reset, this will keep the teamId)
	 */
	public abstract byte getTeamId();

}