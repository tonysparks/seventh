/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import seventh.ai.basic.actions.Goals;
import seventh.game.BombTarget;
import seventh.game.Entity;
import seventh.game.GameInfo;
import seventh.game.Player;
import seventh.game.PlayerEntity;
import seventh.game.Team;
import seventh.game.events.SoundEventPool;
import seventh.game.type.GameType;
import seventh.game.type.ObjectiveGameType;
import seventh.map.Map;
import seventh.map.MapGraph;
import seventh.map.Tile;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Geom;
import seventh.shared.SeventhConstants;

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
	private SoundEventPool lastFramesSounds;
	
	private List<AttackDirection> attackDirections;
	private List<BombTarget> activeBombs;
	
	private Zones zones;
	
	private Goals goals;
	
	/**
	 * @param entities
	 * @param map
	 * @param graph
	 */
	public World(GameInfo game, Zones zones, Goals goals) {
		super();
				
		this.game = game;
		this.zones = zones;
		this.goals = goals;
		
		this.entities = game.getEntities();
		this.players = game.getPlayerEntities();
		
		this.map = game.getMap();
		this.graph = game.getGraph();
		
		this.random = game.getRandom();
		this.tiles = new ArrayList<Tile>();
		this.tileBounds = new Rectangle();
		this.tileBounds.setWidth(map.getTileWidth());
		this.tileBounds.setHeight(map.getTileHeight());
		
		this.lastFramesSounds = new SoundEventPool(SeventhConstants.MAX_SOUNDS);
		this.attackDirections = new ArrayList<AttackDirection>();
		
		this.activeBombs = new ArrayList<BombTarget>();
	}
	
	
	/**
	 * @return the goals
	 */
	public Goals getGoals() {
		return goals;
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
	public SoundEventPool getSoundEvents() {		
		this.lastFramesSounds.clear();
		this.lastFramesSounds.set(this.game.getLastFramesSoundEvents());
		this.lastFramesSounds.set(this.game.getSoundEvents());
		
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
	 * @param playerId
	 * @return the brain of the player
	 */
	public Brain getBrain(int playerId) {
		DefaultAISystem aiSystem = (DefaultAISystem) game.getAISystem();
		return aiSystem.getBrain(playerId);
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
	 * @return {@link BombTarget}'s that have an active bomb on it
	 */
	public List<BombTarget> getBombTargetsWithActiveBombs() {
		this.activeBombs.clear();
		
		List<BombTarget> targets = getBombTargets();
		for(int i = 0; i < targets.size(); i++) {
			BombTarget target = targets.get(i);
			if(target.isAlive() && target.bombActive()) {
				activeBombs.add(target);
			}
		}
		
		return this.activeBombs;
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
	 * @param brain
	 * @return the teammates of the supplied bot
	 */
	public List<Player> getTeammates(Brain brain) {
		return brain.getPlayer().getTeam().getPlayers();
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
		
		int loopChecker = 0;
		
		while (map.rectCollides(temp) && !map.hasWorldCollidableTile(temp.x, temp.y) ) {
			pos.x = x + random.nextInt(width);
			pos.y = y + random.nextInt(height);
			temp.setLocation(pos);
			
			// this bounds doesn't have a free spot
			if(loopChecker++ > 100000) {
				return null;
			}
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
		
		while ((map.rectCollides(temp) && !map.hasWorldCollidableTile(temp.x, temp.y)) || notIn.contains(temp)) {
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
	
	public Zone getZone(int x, int y) {
		return this.zones.getZone(x, y);
	}
	
	
	/**
	 * Attempts to find an adjacent {@link Zone} given the minimum distance
	 * @param zone
	 * @param minDistance
	 * @return an adjacent zone or null if none are found
	 */
	public Zone findAdjacentZone(Zone zone, int minDistance) {
		Rectangle bounds = zone.getBounds();
		int fuzzy = 5;
		
		Zone adjacentZone = null;
		
		final int numberOfDirections = 4;
		int adjacentIndex = random.nextInt(numberOfDirections);
		for(int i = 0; i < numberOfDirections && adjacentZone==null; i++) {
			switch(adjacentIndex) {
				case 0:
					adjacentZone = getNorthZone(bounds, fuzzy, minDistance);
					break;
				case 1:
					adjacentZone = getEastZone(bounds, fuzzy, minDistance);
					break;
				case 2:
					adjacentZone = getSouthZone(bounds, fuzzy, minDistance);
					break;
				case 3:
					adjacentZone = getWestZone(bounds, fuzzy, minDistance);
					break;
				default:
					return null;
			}
			
			adjacentIndex = (adjacentIndex+1) %  numberOfDirections;
		}
		
		return adjacentZone;
	}
	
	private Zone getNorthZone(Rectangle bounds, int fuzzy, int minDistance) {
		
		int adjacentZoneX = bounds.x;
		int adjacentZoneY = bounds.y - (bounds.height/2 + fuzzy + minDistance);;
		Zone adjacentZone = getZone(adjacentZoneX, adjacentZoneY);
		return adjacentZone;
	}
	
	private Zone getEastZone(Rectangle bounds, int fuzzy, int minDistance) {
		
		int adjacentZoneX = bounds.x + bounds.width + fuzzy + minDistance;
		int adjacentZoneY = bounds.y + minDistance;
		Zone adjacentZone = getZone(adjacentZoneX, adjacentZoneY);
		return adjacentZone;
	}
	
	private Zone getSouthZone(Rectangle bounds, int fuzzy, int minDistance) {
		
		int adjacentZoneX = bounds.x;
		int adjacentZoneY = bounds.y + (bounds.height + fuzzy + minDistance);;
		Zone adjacentZone = getZone(adjacentZoneX, adjacentZoneY);
		return adjacentZone;
	}
	
	private Zone getWestZone(Rectangle bounds, int fuzzy, int minDistance) {
		
		int adjacentZoneX = bounds.x - (bounds.width/2 + fuzzy + minDistance);
		int adjacentZoneY = bounds.y;
		Zone adjacentZone = getZone(adjacentZoneX, adjacentZoneY);
		return adjacentZone;
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
	 * Attempts to find the closest position which will serve as 'cover' between the agent and an attack direction
	 * 
	 * @param entity
	 * @param attackDir
	 * @return the position to take cover
	 */
	public Vector2f getClosestCoverPosition(Entity entity, Vector2f attackDir) {
		Vector2f pos = entity.getCenterPos();
		map.getTilesInCircle( (int)pos.x, (int)pos.y, 250, tiles);
		List<Tile> collidableTiles = map.getCollisionTilesAt(tiles, new ArrayList<Tile>());
		
		Vector2f bestHidingSpot = findBestHidingPosition(collidableTiles, pos, attackDir);	
		return bestHidingSpot;
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
