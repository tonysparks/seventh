/*
 * see license.txt 
 */
package seventh.ai.basic;

import java.util.ArrayList;
import java.util.List;

import seventh.ai.basic.actions.Actions;
import seventh.game.BombTarget;
import seventh.game.Entity;
import seventh.game.GameInfo;
import seventh.game.Player;
import seventh.game.PlayerEntity;
import seventh.game.SoundEventPool;
import seventh.game.Team;
import seventh.game.type.GameType;
import seventh.game.type.ObjectiveGameType;
import seventh.game.vehicles.Vehicle;
import seventh.map.Map;
import seventh.map.MapGraph;
import seventh.map.Tile;
import seventh.math.OBB;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Randomizer;
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
	private Randomizer random;
	
	private List<Tile> tiles;
	
	private Rectangle tileBounds;
	private GameInfo game;
	private SoundEventPool lastFramesSounds;
	
	private List<AttackDirection> attackDirections;
	private List<BombTarget> activeBombs;
	
	private Zones zones;
	
	private Actions goals;
	
	private AIConfig config;
	
	/**
	 * @param entities
	 * @param map
	 * @param graph
	 */
	public World(AIConfig config, GameInfo game, Zones zones, Actions goals, Randomizer randomizer) {
		super();
				
		this.config = config;
		this.game = game;
		this.zones = zones;
		this.goals = goals;
		this.random = randomizer;
		
		this.entities = game.getEntities();
		this.players = game.getPlayerEntities();
		
		this.map = game.getMap();
		this.graph = game.getGraph();
		
		
		this.tiles = new ArrayList<Tile>();
		this.tileBounds = new Rectangle();
		this.tileBounds.setWidth(map.getTileWidth());
		this.tileBounds.setHeight(map.getTileHeight());
		
		this.lastFramesSounds = new SoundEventPool(SeventhConstants.MAX_SOUNDS);
		this.attackDirections = new ArrayList<AttackDirection>();
		
		this.activeBombs = new ArrayList<BombTarget>();
	}
	
	/**
	 * @return the config
	 */
	public AIConfig getConfig() {
		return config;
	}
	
	/**
	 * @return the goals
	 */
	public Actions getGoals() {
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
	 * @return the vehicles
	 */
	public List<Vehicle> getVehicles() {
		return game.getVehicles();
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
	 * @param entity
	 * @param target
	 * @return determines if the enemy is in line of fire
	 */
	public boolean inLineOfFire(PlayerEntity entity, PlayerEntity target) {
		// TODO: account for vehicles
		return !map.lineCollides(entity.getCenterPos(), target.getCenterPos(), entity.getHeightMask());
	}
	
	/**
	 * @param entity
	 * @param target
	 * @return determines if the enemy is in line of fire
	 */
	public boolean inLineOfFire(PlayerEntity entity, Vector2f target) {
		return !map.lineCollides(entity.getCenterPos(), target, entity.getHeightMask());
	}
	
	/**
	 * 
	 * @param players
	 * @param entity
	 * @return
	 */
	public List<PlayerEntity> getPlayersInLineOfSight(List<PlayerEntity> players, PlayerEntity entity) {		
		tiles = entity.calculateLineOfSight(tiles);
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
		return game.findFreeRandomSpot(entity);
	}
	
	
	/**
	 * @param entity
	 * @param bounds
	 * @return a random position anywhere in the supplied bounds
	 */
	public Vector2f getRandomSpot(Entity entity, Rectangle bounds) {
		return game.findFreeRandomSpot(entity, bounds);
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
		return game.findFreeRandomSpot(entity, x, y, width, height);
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
		return game.findFreeRandomSpotNotIn(entity, x, y, width, height, notIn);
	}
	
	/**
	 * @return the random
	 */
	public Randomizer getRandom() {
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
	
	public Zone[] findAdjacentZones(Zone zone, int minDistance) {
		Rectangle bounds = zone.getBounds();
		int fuzzy = 5;
		
		Zone[] adjacentZones = new Zone[8];
		
		adjacentZones[0] = getNorthZone(bounds, fuzzy, minDistance);
		adjacentZones[1] = getNorthEastZone(bounds, fuzzy, minDistance);
		adjacentZones[2] = getEastZone(bounds, fuzzy, minDistance);
		adjacentZones[3] = getSouthEastZone(bounds, fuzzy, minDistance);
		adjacentZones[4] = getSouthZone(bounds, fuzzy, minDistance);
		adjacentZones[5] = getSouthWestZone(bounds, fuzzy, minDistance);
		adjacentZones[6] = getWestZone(bounds, fuzzy, minDistance);
		adjacentZones[7] = getNorthWestZone(bounds, fuzzy, minDistance);
		
		return adjacentZones;
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
		
		final int numberOfDirections = 8;
		int adjacentIndex = random.nextInt(numberOfDirections);
		for(int i = 0; i < numberOfDirections && adjacentZone==null; i++) {
			switch(adjacentIndex) {
				case 0:
					adjacentZone = getNorthZone(bounds, fuzzy, minDistance);
					break;
				case 1:
					adjacentZone = getNorthEastZone(bounds, fuzzy, minDistance);
					break;
				case 2:
					adjacentZone = getEastZone(bounds, fuzzy, minDistance);
					break;
				case 3:
					adjacentZone = getSouthEastZone(bounds, fuzzy, minDistance);
					break;
				case 4:
					adjacentZone = getSouthZone(bounds, fuzzy, minDistance);
					break;
				case 5: 
					adjacentZone = getSouthWestZone(bounds, fuzzy, minDistance);
					break;
				case 6:
					adjacentZone = getWestZone(bounds, fuzzy, minDistance);
					break;
				case 7:
					adjacentZone = getNorthWestZone(bounds, fuzzy, minDistance);
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
		int adjacentZoneY = bounds.y - (bounds.height/2 + fuzzy + minDistance);
		Zone adjacentZone = getZone(adjacentZoneX, adjacentZoneY);
		return adjacentZone;
	}
	
	private Zone getNorthWestZone(Rectangle bounds, int fuzzy, int minDistance) {
		
		int adjacentZoneX = bounds.x - (bounds.width/2 + fuzzy + minDistance);
		int adjacentZoneY = bounds.y - (bounds.height/2 + fuzzy + minDistance);
		Zone adjacentZone = getZone(adjacentZoneX, adjacentZoneY);
		return adjacentZone;
	}
	
	private Zone getNorthEastZone(Rectangle bounds, int fuzzy, int minDistance) {
		
		int adjacentZoneX = bounds.x + (bounds.width+(bounds.width/2) + fuzzy + minDistance);
		int adjacentZoneY = bounds.y - (bounds.height/2 + fuzzy + minDistance);
		Zone adjacentZone = getZone(adjacentZoneX, adjacentZoneY);
		return adjacentZone;
	}
	
	private Zone getEastZone(Rectangle bounds, int fuzzy, int minDistance) {
		
		int adjacentZoneX = bounds.x + (bounds.width+(bounds.width/2) + fuzzy + minDistance);
		int adjacentZoneY = bounds.y;
		Zone adjacentZone = getZone(adjacentZoneX, adjacentZoneY);		
		return adjacentZone;
	}
	
	private Zone getSouthZone(Rectangle bounds, int fuzzy, int minDistance) {
		
		int adjacentZoneX = bounds.x;
		int adjacentZoneY = bounds.y + (bounds.height+(bounds.height/2) + fuzzy + minDistance);
		Zone adjacentZone = getZone(adjacentZoneX, adjacentZoneY);
		return adjacentZone;
	}
	
	private Zone getSouthWestZone(Rectangle bounds, int fuzzy, int minDistance) {
		
		int adjacentZoneX = bounds.x - (bounds.width/2 + fuzzy + minDistance);
		int adjacentZoneY = bounds.y + (bounds.height+(bounds.height/2) + fuzzy + minDistance);
		Zone adjacentZone = getZone(adjacentZoneX, adjacentZoneY);
		return adjacentZone;
	}
	
	private Zone getSouthEastZone(Rectangle bounds, int fuzzy, int minDistance) {
		
		int adjacentZoneX = bounds.x + (bounds.width+(bounds.width/2) + fuzzy + minDistance);
		int adjacentZoneY = bounds.y + (bounds.height+(bounds.height/2) + fuzzy + minDistance);
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
//		int bestCost = -1;
		float distToClosest = 0f;
		Vector2f bestHidingSpot = new Vector2f();
		Vector2f nextHidingSpot = new Vector2f();
		Vector2f tilePos = new Vector2f();
		
		for(int i = 0; i < obstacles.size(); i++) {
			
			Tile tile = obstacles.get(i);
			tilePos.set(tile.getX()+tile.getWidth()/2, tile.getY()+tile.getHeight()/2);
			nextHidingSpot = getHidingPosition(tilePos, tile.getWidth(), targetPos, nextHidingSpot);
			
			/* skip if this is an invalid spot */
//			if(map.pointCollides((int)nextHidingSpot.x, (int)nextHidingSpot.y)) {
//				continue;
//			}
			Tile collidableTile = map.getWorldCollidableTile((int)nextHidingSpot.x, (int)nextHidingSpot.y);
			if(collidableTile != null) {
				continue;
			}
			
			Tile wTile = map.getWorldTile(0, (int)nextHidingSpot.x, (int)nextHidingSpot.y);
			if(wTile == null) {
				continue;
			}
			
			nextHidingSpot.x = wTile.getX() + wTile.getWidth()/2;
			nextHidingSpot.y = wTile.getY() + wTile.getHeight()/2;
						
			
//			int cost = this.graph.pathCost(myPos, nextHidingSpot);
//			if(cost > bestCost) {
//				bestCost = cost;
//				bestHidingSpot.set(nextHidingSpot);
//			}
			
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
		Vector2f bestHidingSpot = getClosestCoverPosition(entity, attackDir);
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
		return getAttackDirections(entity.getCenterPos(), 300f, 10);
	}
	
	/**
     * Calculates the possible {@link AttackDirection}'s from the supplied position.
     * 
     * @param pos
     * @return the list of possible {@link AttackDirection}s
     */
    public List<AttackDirection> getAttackDirections(Vector2f pos, float distanceToCheck, int numberOfDirectionsToCheck) {
        this.attackDirections.clear();
        
        Vector2f attackDir = new Vector2f();
        
        /* check each direction and see if a wall is providing us some
         * cover
         */
        float currentAngle = 0;
        for(int i = 0; i < numberOfDirectionsToCheck; i++) {
            attackDir.set(1,0);
            
            Vector2f.Vector2fRotate(attackDir, Math.toRadians(currentAngle), attackDir);
            Vector2f.Vector2fMA(pos, attackDir, distanceToCheck, attackDir);
            
            if(!map.lineCollides(pos, attackDir)) {
                this.attackDirections.add(new AttackDirection(attackDir.createClone()));
            }
            
            currentAngle += 360f/(float)numberOfDirectionsToCheck;
        }                       
        
        return this.attackDirections;
    }

    public void tilesTouchingEntity(Entity entOnTile, List<Tile> tilesToAvoid) {
    	tilesToAvoid.clear();
    	
    	getMap().getTilesInRect(entOnTile.getBounds(), tilesToAvoid);
    	
    	if(entOnTile.getType().isVehicle()) {
			Vehicle vehicle = (Vehicle) entOnTile;
			OBB oob = vehicle.getOBB();
			for(int i = 0; i < tilesToAvoid.size(); ) {
				Tile t = tilesToAvoid.get(i);
				if(!oob.intersects(t.getBounds())) {
					tilesToAvoid.remove(i);
				}
				else {
					i++;
				}
			}
		}
    }

}
