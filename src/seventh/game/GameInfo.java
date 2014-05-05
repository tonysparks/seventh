/*
 * see license.txt 
 */
package seventh.game;

import java.util.List;
import java.util.Random;

import leola.frontend.listener.EventDispatcher;
import seventh.ai.AISystem;
import seventh.game.events.SoundEmittedEvent;
import seventh.game.type.GameType;
import seventh.map.Map;
import seventh.map.MapGraph;
import seventh.math.Vector2f;
import seventh.shared.Config;

/**
 * Used for acquiring information about the current game state
 * 
 * @author Tony
 *
 */
public interface GameInfo {

	/**
	 * @return the aiSystem
	 */
	public abstract AISystem getAISystem();

	/**
	 * @return the config
	 */
	public abstract Config getConfig();

	/**
	 * @return the random
	 */
	public abstract Random getRandom();

	/**
	 * @return the dispatcher
	 */
	public abstract EventDispatcher getDispatcher();

	/**
	 * @return the lastFramesSoundEvents
	 */
	public abstract List<SoundEmittedEvent> getLastFramesSoundEvents();

	/**
	 * @return the soundEvents
	 */
	public abstract List<SoundEmittedEvent> getSoundEvents();

	/**
	 * @param playerId
	 * @return the player, or null if not found
	 */
	public abstract PlayerInfo getPlayerById(int playerId);

	/**
	 * @return the players
	 */
	public abstract PlayerInfos getPlayerInfos();
	
	/**
	 * @return the gameType
	 */
	public abstract GameType getGameType();

	/**
	 * @return the map
	 */
	public abstract Map getMap();

	/**
	 * @return the entities
	 */
	public abstract Entity[] getEntities();

	/**
	 * @return the playerEntities
	 */
	public abstract PlayerEntity[] getPlayerEntities();

	/**
	 * @return the graph
	 */
	public abstract MapGraph<Void> getGraph();

	/**
	 * @return the bomb targets
	 */
	public abstract List<BombTarget> getBombTargets();

	/**
	 * Determines if the supplied entity touches another
	 * entity.  If the {@link Entity#onTouch} listener
	 * is implemented, it will invoke it.
	 * 
	 * @param ent
	 * @return true if it does.
	 */
	public abstract boolean doesTouchOthers(Entity ent);

	/**
	 * Determines if the supplied entity touches another
	 * entity.  If the {@link Entity#onTouch} listener
	 * is implemented, it will invoke it.
	 * 
	 * @param ent
	 * @return true if it does.
	 */
	public abstract boolean doesTouchPlayers(Entity ent);

	/**
	 * Determines if the supplied entity touches another
	 * entity.  If the {@link Entity#onTouch} listener
	 * is implemented, it will invoke it.
	 * 
	 * @param ent
	 * @return true if it does.
	 */
	public abstract boolean doesTouchPlayers(Entity ent, Vector2f origin, Vector2f dir);

	/**
	 * Determines if the supplied entity is reachable given the origin and direction.
	 * 
	 * @param other
	 * @param origin
	 * @param dir
	 * @return true if reachable (i.e., in sight or projectile can pierce)
	 */
	public abstract boolean isEntityReachable(Entity other, Vector2f origin, Vector2f dir);

}