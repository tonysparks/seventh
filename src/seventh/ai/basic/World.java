/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import seventh.game.BombTarget;
import seventh.game.Entity;
import seventh.game.GameInfo;
import seventh.game.PlayerEntity;
import seventh.game.Team;
import seventh.game.events.SoundEmittedEvent;
import seventh.game.type.GameType;
import seventh.game.type.ObjectiveGameType;
import seventh.map.Map;
import seventh.map.MapGraph;
import seventh.map.Tile;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Geom;

/**
 * Just a collection of data so that the {@link Brain}s 
 * can make sense of the world.
 * 
 * @author Tony
 *
 */
public class World {

	private Entity[] entities;
	private PlayerEntity[] players;
	private Map map;
	private MapGraph<?> graph;
	private Random random;
	
	private List<Tile> tiles;
	
	private Rectangle tileBounds;
	private GameInfo game;
	private List<SoundEmittedEvent> lastFramesSounds;
	
	private List<AttackDirection> attackDirections;
	
	private Zones zones;
	
	/**
	 * @param entities
	 * @param map
	 * @param graph
	 */
	public World(GameInfo game, Zones zones) {
		super();
				
		this.game = game;
		this.zones = zones;
		
		this.entities = game.getEntities();
		this.players = game.getPlayerEntities();
		
		this.map = game.getMap();
		this.graph = game.getGraph();
		
		this.random = game.getRandom();
		this.tiles = new ArrayList<Tile>();
		this.tileBounds = new Rectangle();
		this.tileBounds.setWidth(map.getTileWidth());
		this.tileBounds.setHeight(map.getTileHeight());
		
		this.lastFramesSounds = new ArrayList<SoundEmittedEvent>();
		this.attackDirections = new ArrayList<AttackDirection>();
	}
	
	/**
	 * @return the zones
	 */
	public Zones getZones() {
		return zones;
	}
	
	/**
	 * @return the soundEvents
	 */
	public List<SoundEmittedEvent> getSoundEvents() {		
		this.lastFramesSounds.clear();
		this.lastFramesSounds.addAll(this.game.getLastFramesSoundEvents());
		this.lastFramesSounds.addAll(this.game.getSoundEvents());
		
		return this.lastFramesSounds;
	}
	
	/**
	 * @return the entities
	 */
	public Entity[] getEntities() {
		return entities;
	}
	
	/**
	 * Gets a player by id
	 * @param id
	 * @return the player
	 */
	public PlayerEntity getPlayerById(long id) {
		int size = this.players.length;
		for(int i = 0; i < size; i++) {
			PlayerEntity ent = this.players[i];
			if(ent!=null) {
				if(ent.getId() == id) {
					return ent;
				}
			}
		}
		return null;
	}
	
	/**
	 * @return the map
	 */
	public Map getMap() {
		return map;
	}
	/**
	 * @return the graph
	 */
	public MapGraph<?> getGraph() {
		return graph;
	}
	
	/**
	 * @return the bomb targets
	 */
	public List<BombTarget> getBombTargets() {
		return this.game.getBombTargets();
	}
	
	/**
	 * @return true if there are bomb targets
	 */
	public boolean hasBombTargets() {
		return !this.game.getBombTargets().isEmpty();
	}
	
	/**
	 * @param team
	 * @return true if the supplied team is on offense
	 */
	public boolean isOnOffense(Team team) {
		GameType gt = game.getGameType();
		if(gt instanceof ObjectiveGameType) {
			ObjectiveGameType objectiveGameType = (ObjectiveGameType)gt;
			Team attacker = objectiveGameType.getAttacker();
			if(team != null && attacker != null) {
				return attacker.getId() == team.getId();
			}
		}
		return false;
	}
	
	/**
	 * 
	 * @param players
	 * @param entity
	 * @return
	 */
	public List<PlayerEntity> getPlayersInLineOfSight(List<PlayerEntity> players, PlayerEntity entity) {
		Vector2f pos = entity.getPos();
		Vector2f facing = entity.getFacing();
		int radius = entity.getLineOfSight();
						
		tiles = Geom.calculateLineOfSight(tiles, pos, facing, radius, map, entity.getHeightMask());
		int size = tiles.size();
		for(int i = 0; i < size;i++) {
			Tile tile = tiles.get(i);
			if(tile!=null && tile.getMask() > 0) {
				this.tileBounds.setLocation(tile.getX(), tile.getY());
				playersIn(players, tileBounds);
			}
		}
		
		map.setMask(tiles, 0);

		if(!players.isEmpty()) {
			players.remove(entity);
		}
		
		return players;
	}
	
	/**	 
	 * @param bounds
	 * @return the players found in the supplied bounds
	 */
	public List<PlayerEntity> playersIn(List<PlayerEntity> result, Rectangle bounds) {
		
		/*
		 * Currently uses brute force
		 */
		for(int i = 0; i < this.players.length; i++) {
			PlayerEntity entity = this.players[i];
			if(entity!=null) {
				if(bounds.contains(entity.getCenterPos())) {
					result.add(entity);
				}			
			}
		}
		
		return result;
	}
	
	/**
	 * @param entity
	 * @return a random position anywhere in the game world
	 */
	public Vector2f getRandomSpot(Entity entity) {
		return getRandomSpot(entity, 0, 0, map.getMapWidth()-20, map.getMapHeight()-20);
	}
	
	
	/**
	 * @param entity
	 * @param bounds
	 * @return a random position anywhere in the supplied bounds
	 */
	public Vector2f getRandomSpot(Entity entity, Rectangle bounds) {
		return getRandomSpot(entity, bounds.x, bounds.y, bounds.width, bounds.height);
	}
	
	/**
	 * 
	 * @param entity
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @return a random position anywhere in the supplied bounds
	 */
	public Vector2f getRandomSpot(Entity entity, int x, int y, int width, int height) {
		Vector2f pos = new Vector2f(x+random.nextInt(width), y+random.nextInt(height));
		Rectangle temp = new Rectangle(entity.getBounds());
		temp.setLocation(pos);
		
		while (map.rectCollides(temp)) {
			pos.x = x + random.nextInt(width);
			pos.y = y + random.nextInt(height);
			temp.setLocation(pos);
		}
		
		return pos;
	}
	
	/**
	 * 
	 * @param entity
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param notIn
	 * @return a random position anywhere in the supplied bounds and not in the supplied {@link Rectangle}
	 */
	public Vector2f getRandomSpotNotIn(Entity entity, int x, int y, int width, int height, Rectangle notIn) {
		Vector2f pos = new Vector2f(x+random.nextInt(width), y+random.nextInt(height));
		Rectangle temp = new Rectangle(entity.getBounds());
		temp.setLocation(pos);
		
		while (map.rectCollides(temp) || notIn.contains(temp)) {
			pos.x = x + random.nextInt(width);
			pos.y = y + random.nextInt(height);
			temp.setLocation(pos);
		}
		
		return pos;
	}
	
	/**
	 * @return the random
	 */
	public Random getRandom() {
		return random;
	}
	
	
	/**
	 * @param pos
	 * @return the Zone at the supplied position
	 */
	public Zone getZone(Vector2f pos) {
		return this.zones.getZone(pos);
	}
	
	/**
	 * The hiding position
	 * 
	 * @param obstaclePos
	 * @param obstacleRadius
	 * @param targetPos
	 * @param result the result
	 * @return
	 */
	public Vector2f getHidingPosition(Vector2f obstaclePos, float obstacleRadius, Vector2f targetPos, Vector2f result) {
		final float distanceFromBoundary = 5f;
		float distAway = obstacleRadius + distanceFromBoundary;
		
		Vector2f.Vector2fSubtract(obstaclePos, targetPos, result);
		Vector2f.Vector2fNormalize(result, result);
		Vector2f.Vector2fMA(obstaclePos, result, distAway, result);
		
		return result;
	}
	
	
	/**
	 * Locates the best hiding position
	 * 
	 * @param obstacles list of obstacles to hide by
	 * @param myPos the Agents current position
	 * @param targetPos the position which you want to hide from
	 * @return the best hiding position or the ZERO vector if non could be found
	 */
	public Vector2f findBestHidingPosition(List<Tile> obstacles, Vector2f myPos, Vector2f targetPos) {
		float distToClosest=0f;
		Vector2f bestHidingSpot = new Vector2f();
		Vector2f nextHidingSpot = new Vector2f();
		Vector2f tilePos = new Vector2f();
		
		for(int i = 0; i < obstacles.size(); i++) {
			
			Tile tile = obstacles.get(i);
			tilePos.set(tile.getX()+tile.getWidth()/2, tile.getY()+tile.getHeight()/2);
			nextHidingSpot = getHidingPosition(tilePos, tile.getWidth(), targetPos, nextHidingSpot);
			
			/* skip if this is an invalid spot */
			if(map.pointCollides((int)nextHidingSpot.x, (int)nextHidingSpot.y)) {
				continue;
			}
			
			
			/* if this hiding spot is closer to the agent, use it */
			float dist = Vector2f.Vector2fDistanceSq(nextHidingSpot, myPos);
			if(dist < distToClosest || bestHidingSpot.isZero()) {
				bestHidingSpot.set(nextHidingSpot);
				distToClosest = dist;
			}
		}
		
		return bestHidingSpot;
	}
	
	/**
	 * Attempts to find {@link Cover} between the agent and an attack direction
	 * 
	 * @param entity
	 * @param attackDir
	 * @return a place to take {@link Cover}
	 */
	public Cover getCover(Entity entity, Vector2f attackDir) {
		Vector2f pos = entity.getCenterPos();
		map.getTilesInCircle( (int)pos.x, (int)pos.y, 250, tiles);
		List<Tile> collidableTiles = map.getCollisionTilesAt(tiles, new ArrayList<Tile>());
		
		Vector2f bestHidingSpot = findBestHidingPosition(collidableTiles, pos, attackDir);
//		DebugDraw.fillRectRelative( (int)bestHidingSpot.x, (int)bestHidingSpot.y, 10, 10, 0xff00ff00);
//		
//		for(Tile tile : collidableTiles) {
//			DebugDraw.fillRectRelative(tile.getX(), tile.getY(), tile.getWidth(), tile.getHeight(), 0x3fff0000);
//		}
		
		return new Cover(bestHidingSpot, attackDir);
	}
	
	/**
	 * Calculates the possible {@link AttackDirection}'s from the supplied {@link Entity}s position.
	 * 
	 * @param entity
	 * @return the list of possible {@link AttackDirection}s
	 */
	public List<AttackDirection> getAttackDirections(Entity entity) {
		this.attackDirections.clear();
		
		Vector2f pos = entity.getCenterPos();
		final float distanceToCheck = 300f;
		final float maxDirectionsToCheck = 10f;
		Vector2f attackDir = new Vector2f();
		
		/* check each direction and see if a wall is providing us some
		 * cover
		 */
		float currentAngle = 0;
		for(int i = 0; i < maxDirectionsToCheck; i++) {
			attackDir.set(1,0);
			
			Vector2f.Vector2fRotate(attackDir, Math.toRadians(currentAngle), attackDir);
			Vector2f.Vector2fMA(pos, attackDir, distanceToCheck, attackDir);
			
			if(!map.lineCollides(pos, attackDir)) {
				this.attackDirections.add(new AttackDirection(attackDir.createClone()));
			}
			
			currentAngle += 360f/maxDirectionsToCheck;
		}						
		
		return this.attackDirections;
	}
}
