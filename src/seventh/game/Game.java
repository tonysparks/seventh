/*
 * see license.txt 
 */
package seventh.game;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import leola.frontend.listener.EventDispatcher;
import leola.frontend.listener.EventMethod;
import seventh.ai.AISystem;
import seventh.ai.basic.DefaultAISystem;
import seventh.game.Entity.KilledListener;
import seventh.game.Entity.Type;
import seventh.game.PlayerEntity.Keys;
import seventh.game.events.GameEndEvent;
import seventh.game.events.PlayerKilledEvent;
import seventh.game.events.PlayerKilledListener;
import seventh.game.events.PlayerSpawnedEvent;
import seventh.game.events.PlayerSpawnedListener;
import seventh.game.events.RoundEndedEvent;
import seventh.game.events.RoundEndedListener;
import seventh.game.events.RoundStartedEvent;
import seventh.game.events.RoundStartedListener;
import seventh.game.events.SoundEmittedEvent;
import seventh.game.events.SoundEmitterListener;
import seventh.game.net.NetEntity;
import seventh.game.net.NetGamePartialStats;
import seventh.game.net.NetGameState;
import seventh.game.net.NetGameStats;
import seventh.game.net.NetGameUpdate;
import seventh.game.net.NetPlayerPartialStat;
import seventh.game.net.NetPlayerStat;
import seventh.game.net.NetSound;
import seventh.game.type.GameType;
import seventh.game.type.GameType.GameState;
import seventh.game.vehicles.Tank;
import seventh.game.vehicles.Vehicle;
import seventh.game.weapons.Explosion;
import seventh.game.weapons.Fire;
import seventh.game.weapons.Weapon;
import seventh.graph.GraphNode;
import seventh.map.GraphNodeFactory;
import seventh.map.Map;
import seventh.map.MapGraph;
import seventh.map.Tile;
import seventh.math.Vector2f;
import seventh.network.messages.UserInputMessage;
import seventh.shared.Config;
import seventh.shared.Cons;
import seventh.shared.TimeStep;

/**
 * Represents the current Game Session
 * 
 * @author Tony
 *
 */
public class Game implements GameInfo {
	private static class NodeData implements GraphNodeFactory<Void> {		
		@Override
		public Void createEdgeData(Map map, GraphNode<Tile, Void> left,
				GraphNode<Tile, Void> right) {		
			return null;
		}				
	}
	
	public static final int MAX_ENTITIES = 256;
	public static final int MAX_PLAYERS = 12;
	public static final int MAX_PERSISTANT_ENTITIES = 64;
	
	public static final int SPAWN_INVINCEABLILITY_TIME = 2_000;
	
	public static final Type[] alliedWeapons = {
		Type.THOMPSON,
		Type.SHOTGUN,
		
		Type.M1_GARAND,
		Type.SPRINGFIELD,				
		
		Type.RISKER,
		Type.ROCKET_LAUNCHER,
	};
	
	public static final Type[] axisWeapons = {
		Type.MP40,
		Type.SHOTGUN,
		
		Type.MP44,
		Type.KAR98,
		
		Type.RISKER,
		Type.ROCKET_LAUNCHER,
	};
	
	private Entity[] entities;	
	private PlayerEntity[] playerEntities;
	private int[] deadFrames;	
	
	private Map map;	
	private MapGraph<Void> graph;
	private GameMap gameMap;
	private GameType gameType;
	
	private List<BombTarget> bombTargets;
	private List<Vehicle> vehicles;
	
	private Players players;
				
	private EventDispatcher dispatcher;
			
	private long time;
	
	private Random random;
	
	private List<SoundEmittedEvent> soundEvents
								  , lastFramesSoundEvents;	
	private boolean gameEnded;	
	private boolean enableFOW;
	private int previousKeys;
	
	// data members that are strictly here for performance
	// reasons
	List<Tile> aTiles = new ArrayList<Tile>();
	List<SoundEmittedEvent> aSoundsHeard = new ArrayList<SoundEmittedEvent>();
	List<Entity> aEntitiesInView = new ArrayList<Entity>();
		
	private NetGameState gameState;
	private NetGameStats gameStats;
	private NetGamePartialStats gamePartialStats;
	
	private final float DISTANCE_CHECK;
	private final int TILE_WIDTH, TILE_HEIGHT;
	
	private Config config;

	
	private int lastValidId;
	
	private AISystem aiSystem;
	
	/**
	 * @param players
	 * @param gameType
	 * @param gameMap
	 * @param dispatcher
	 */
	public Game(Config config, final Players players, final GameType gameType, GameMap gameMap, EventDispatcher dispatcher) {
		this.config = config;
		this.gameType = gameType;		
		this.gameType.registerListeners(this, dispatcher);
		
		this.dispatcher = dispatcher;
		
		this.gameMap = gameMap;
		this.map = gameMap.getMap();
		
		this.graph = map.createMapGraph(new NodeData());
		
		this.entities = new Entity[MAX_ENTITIES];
		this.playerEntities = new PlayerEntity[MAX_PLAYERS];
		
		this.deadFrames = new int[MAX_ENTITIES];
		
		this.bombTargets = new ArrayList<BombTarget>();
		this.vehicles = new ArrayList<Vehicle>();
		
		this.soundEvents = new ArrayList<SoundEmittedEvent>();
		this.lastFramesSoundEvents = new ArrayList<SoundEmittedEvent>();
		
		this.aiSystem = new DefaultAISystem();
		
		this.random = new Random();
		
		this.players = players;				
		
		this.gameState = new NetGameState();
		this.gamePartialStats = new NetGamePartialStats();
		this.gameStats = new NetGameStats();
		
		this.enableFOW = true;
		this.time = gameType.getMatchTime();
		
		this.TILE_WIDTH = map.getTileWidth();
		this.TILE_HEIGHT = map.getTileHeight();
		
		this.DISTANCE_CHECK = TILE_HEIGHT * TILE_WIDTH * 2;
				
		this.dispatcher.addEventListener(PlayerKilledEvent.class, new PlayerKilledListener() {

			@Override
			@EventMethod
			public void onPlayerKilled(PlayerKilledEvent event) {
				if (gameType.isInProgress()) {
					Player killed = event.getPlayer();
					killed.incrementDeaths();

					aiSystem.playerKilled(killed);
					
					Player killer = players.getPlayer(event.getKillerId());
					if (killer != null) {

						// lose a point for team kills
						if (killed.getTeam().getId() == killer.getTeam().getId()) {
							killer.loseKill();
						}
						// lose a point for suicide
						else if (killed.getId() == killer.getId()) {
							killer.loseKill();
						} else {
							killer.incrementKills();
						}
					}
				}
			}
		});
			
		this.dispatcher.addEventListener(PlayerSpawnedEvent.class, new PlayerSpawnedListener() {
			
			@Override
			@EventMethod
			public void onPlayerSpawned(PlayerSpawnedEvent event) {
//				aiSystem.playerSpawned(event.getPlayer());
			
			}
		});
		
		this.dispatcher.addEventListener(SoundEmittedEvent.class, new SoundEmitterListener() {
			
			@Override
			@EventMethod
			public void onSoundEmitted(SoundEmittedEvent event) {
				soundEvents.add(event);
			}
		});				
		
		this.dispatcher.addEventListener(RoundStartedEvent.class, new RoundStartedListener() {
			
			@Override
			public void onRoundStarted(RoundStartedEvent event) {
				aiSystem.startOfRound(Game.this);
				
			}
		});
		
		this.dispatcher.addEventListener(RoundEndedEvent.class, new RoundEndedListener() {

			@Override
			public void onRoundEnded(RoundEndedEvent event) {
				aiSystem.endOfRound(Game.this);
			}
		});
		
		aiSystem.init(this);		
	}
		
	
	/**
	 * @return the next available slot for an entity
	 */
	public int getNextPersistantId() {
		for(int i = MAX_PLAYERS; i < MAX_PERSISTANT_ENTITIES; i++) {
			if(entities[i] == null) {
				return i;
			}
		}
		
		return -1;
	}
	
	/**
	 * Calculates the next valid ID.  If there are no entity slots available, 
	 * this will create room by destroying a volatile object.
	 *  
	 * @return a valid id.
	 */
	public int getNextEntityId() {
		lastValidId = 0;
		
		for(int i = MAX_PLAYERS + MAX_PERSISTANT_ENTITIES + lastValidId; i < MAX_ENTITIES; i++) {
			int index = i;// + (lastValidId % (MAX_ENTITIES-MAX_PLAYERS));
			if(entities[index] == null && deadFrames[i] > 10) {
				lastValidId++;				
				return i;
			}
		}				
		
		/* if we ran out of id's, lets find a
		 * volatile object so we can respawn this
		 * latest object.
		 */
		for(int i = MAX_PLAYERS; i < MAX_ENTITIES; i++) {
			Entity ent = entities[i];
			if(ent!=null) {
				Type type = ent.getType();
				if(type == Type.DROPPED_ITEM || type == Type.BULLET) {
					ent.softKill();				
					return i;
				}
			}
		}
		
		return MAX_PLAYERS;
	}
	
	/**
	 * Emits a sound for the client to hear
	 * 
	 * @param sound
	 * @param pos
	 */
	public void emitSound(int id, SoundType sound, Vector2f pos) {
		soundEvents.add(new SoundEmittedEvent(this, id, sound, pos));
	}
	
	/**
	 * Starts the game
	 */
	public void startGame() {
		this.gameType.start(this);
	}
	
	/**
	 * Adds a bot
	 * @param name
	 * @return the id of the added bot
	 */
	public int addBot(int id, String name) {
		if(id >= 0) {			
			playerJoined(new Player( id, true, false, name));
		}
		return id;
	}
	
	/**
	 * Adds a Dummy bot. This is used for testing purposes.
	 * @param id
	 * @return the id of the added bot
	 */
	public int addDummyBot(int id) {
		if(id >= 0) {			
			playerJoined(new Player( id, true, true, "Dummy"));
		}
		return id;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getAISystem()
	 */
	@Override
	public AISystem getAISystem() {
		return aiSystem;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getConfig()
	 */
	@Override
	public Config getConfig() {
		return config;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getRandom()
	 */
	@Override
	public Random getRandom() {
		return random;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getDispatcher()
	 */
	@Override
	public EventDispatcher getDispatcher() {
		return dispatcher;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getLastFramesSoundEvents()
	 */
	@Override
	public List<SoundEmittedEvent> getLastFramesSoundEvents() {
		return lastFramesSoundEvents;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getSoundEvents()
	 */
	@Override
	public List<SoundEmittedEvent> getSoundEvents() {
		return soundEvents;
	}
	
	/**
	 * Kick a player
	 * @param id
	 */
	public void kickPlayer(int id) {
		playerLeft(id);
	}
		

	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getPlayerById(int)
	 */
	@Override
	public PlayerInfo getPlayerById(int playerId) {
		return this.players.getPlayer(playerId);
	}
	
	/**
	 * @return the mutable list of {@link Players}
	 */
	public Players getPlayers() {
		return players;
	}
	
	/**
	 * @return the vehicles
	 */
	@Override
	public List<Vehicle> getVehicles() {
		return vehicles;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getPlayerInfos()
	 */
	@Override
	public PlayerInfos getPlayerInfos() {	
		return players;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getGameType()
	 */
	@Override
	public GameType getGameType() {
		return gameType;
	}
	
	/**
	 * Updates the game
	 * @param timeStep
	 */
	public void update(TimeStep timeStep) {		
//		lastValidId = 0;
		
//		int numberOfActiveEntities = 0;
		for(int i = 0; i < entities.length; i++) {
			Entity ent = entities[i];
			if(ent!=null) {
//				numberOfActiveEntities++;
				if(ent.isAlive()) {
					deadFrames[i] = 0;
					ent.update(timeStep);
				}
				else {
					deadFrames[i]++;
					if(deadFrames[i] > 1) {
						entities[i] = null;
					}
				}
			}	
			else {				
				deadFrames[i]++;
			}
		}			
		
		this.aiSystem.update(timeStep);
								
		GameState gameState = this.gameType.update(this, timeStep);
		this.time = this.gameType.getRemainingTime();
		
		if(gameState!=GameState.IN_PROGRESS) {
			if(!gameEnded) {
				this.dispatcher.queueEvent(new GameEndEvent(this, this.getNetGameStats()));			
				this.gameEnded = true;
			}
		}
//		else {			
//			this.time -= timeStep.getDeltaTime();						
//		}
				
	}
	
	/**
	 * Invoked after an update, a hack to work
	 * around processing event queue
	 */
	public void postUpdate() {
		lastFramesSoundEvents.clear();
		lastFramesSoundEvents.addAll(soundEvents);
		soundEvents.clear();
		
		lastValidId = 0;
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getMap()
	 */
	@Override
	public Map getMap() {
		return map;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getEntities()
	 */
	@Override
	public Entity[] getEntities() {
		return entities;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getPlayerEntities()
	 */
	@Override
	public PlayerEntity[] getPlayerEntities() {
		return playerEntities;
	}
	
//	public void 
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getGraph()
	 */
	@Override
	public MapGraph<Void> getGraph() {
		return graph;
	}
	
	/**
	 * Finds a free location on the map
	 * @param player
	 * @return
	 */
	public Vector2f findFreeSpot(PlayerEntity player) {				
		Vector2f freeSpot = player.getPos();
		
		while(map.rectCollides(player.getBounds())) {
			int w = (player.getBounds().width + 5);
			int h = (player.getBounds().height + 5);
			
			int x = random.nextInt(map.getMapWidth()-w);
			int y = random.nextInt(map.getMapHeight()-h);
			
			if(x <= w) {
				x = w;
			}
			
			if(y <= h) {
				y = h;
			}
			
			freeSpot.set(x, y);
			player.moveTo(freeSpot);
		} 
		
		return freeSpot;
	}
	
	/**
	 * A {@link Player} joined the game.
	 * 
	 * @param player
	 */
	public void playerJoined(Player player) {
		Cons.println("Player " + player.getName() + " has joined the game.");
		
		this.players.addPlayer(player);
		this.gameType.playerJoin(player);
		
		this.aiSystem.playerJoined(player);
	}
	
	/**
	 * A {@link Player} left the game.
	 * 
	 * @param player
	 */
	public void playerLeft(Player player) {
		playerLeft(player.getId());
	}
	
	/**
	 * A {@link Player} left the game.
	 * 
	 * @param playerId
	 */
	public void playerLeft(int playerId) {
		Player player = this.players.removePlayer(playerId);
		if(player!=null) {
			Cons.println("Player " + player.getName() + " has left the game.");
			
			this.aiSystem.playerLeft(player);
			this.gameType.playerLeft(player);			
			player.commitSuicide();
		}				
	}	
	
	/**
	 * destroys the game, cleans up resources
	 */
	public void destroy() {		
		
		for(int i = 0; i < this.entities.length;i++) {
			this.entities[i] = null;
			this.deadFrames[i] = 0;
		}
		
		for(int i = 0; i < this.playerEntities.length;i++) {
			this.playerEntities[i] = null;
		}
		
		this.bombTargets.clear();
		this.vehicles.clear();
		
		this.players.resetStats();
		this.aiSystem.destroy();
		
		this.dispatcher.removeAllEventListeners();
	}
	
	private void removePlayer(Entity entity) {
		for(int i = 0; i < playerEntities.length;i++) {
			if(playerEntities[i] == entity) {
				playerEntities[i] = null;
				break;
			}
		}	
	}
	
	private void addPlayer(PlayerEntity player) {
		int id = player.getId();
		if(id >= 0 && id < MAX_PLAYERS) {
			playerEntities[id] = player;
		}
	}
		
	
	/**
	 * Spawns a {@link PlayerEntity}
	 * 
	 * @param id - the {@link Player#id} 
	 * @return the {@link PlayerEntity}
	 */
	public PlayerEntity spawnPlayerEntity(final int id) {
		final Player player = this.players.getPlayer(id);
		if(player == null ) {
			Cons.println("No player found with id: " + id);
			return null;
		}
		
	
		Vector2f spawnPosition = new Vector2f(-1,-1);
		if( player.getTeamId() == Team.ALLIED_TEAM ) {
			List<Vector2f> spawnPoints = gameType.getAlliedSpawnPoints();
			if(!spawnPoints.isEmpty()) {			
				spawnPosition.set(spawnPoints.get(random.nextInt(spawnPoints.size())));
			}
		}
		else {
			List<Vector2f> spawnPoints = gameType.getAxisSpawnPoints();
			if(!spawnPoints.isEmpty()) {
				spawnPosition.set(spawnPoints.get(random.nextInt(spawnPoints.size())));
			}
		}
					
		// Spawn a bot (or dummy bot if need-be) or a remote controlled entity
		final PlayerEntity playerEntity = new PlayerEntity(id, spawnPosition, this);	
		
			
		// give them two seconds of invinceability		
		playerEntity.setInvinceableTime(SPAWN_INVINCEABLILITY_TIME);
		playerEntity.onKill = new KilledListener() {
			
			@Override
			public void onKill(Entity entity, Entity killer) {
				dispatcher.queueEvent(new PlayerKilledEvent(this, player, killer, entity.getCenterPos()));
				
				removePlayer(entity);				
				//entities.remove(entity); // we want them to be picked up via deadEntities
				
				
				player.applySpawnDelay();
				player.setKilledAt();
			}
		};
	
		// this doesn't remove points
		if(player.isAlive()) {
			
//			player.commitSuicide();
			
			Entity ent = player.getEntity();
			ent.softKill();
				
			removePlayer(ent);
		}
		
		// safe guard against faulty spawn points
		spawnPosition = findFreeSpot(playerEntity);
		
		player.setEntity(playerEntity);
				
		playerEntity.setWeaponClass(player.getWeaponClass());
		
		addEntity(playerEntity);
		addPlayer(playerEntity);
		
		dispatcher.queueEvent(new PlayerSpawnedEvent(this, player, spawnPosition));		
		aiSystem.playerSpawned(player);
		
		return playerEntity;
	}
		
	/**
	 * Adds an entity to the game world
	 * 
	 * @param ent
	 */
	public void addEntity(Entity ent) {
		int id = ent.getId();
		
		if(id >= 0 && id < MAX_ENTITIES) {
//			if(entities[id] != null) {
//				entities[id].
//			}
			
			entities[id] = ent;
		}
	}
	
	public boolean playerSwitchedTeam(int playerId, byte teamId) {
		boolean playerSwitched = false;
		Player player = this.players.getPlayer(playerId);
		if(player!=null) {
			playerSwitched = gameType.switchTeam(player, teamId);
			
			// always kill the player
			if(playerSwitched && player.hasEntity()) {
				player.commitSuicide();
			}

			if(Team.SPECTATOR_TEAM != teamId && playerSwitched) {
				player.stopSpectating();
			}
		}
					
		return playerSwitched;
	}
	
	/**
	 * Applies the remote {@link UserInputMessage} to the {@link PlayerEntity}
	 * 
	 * @param playerId
	 * @param msg
	 */
	public void applyPlayerInput(int playerId, UserInputMessage msg) {
		Player player = this.players.getPlayer(playerId);
		if(player != null) {			
			if(player.isAlive()) {
				PlayerEntity entity = player.getEntity();				
				entity.handleUserCommand(new UserCommand(msg.keys, msg.orientation));								
			}
			else if (player.isSpectating()) {
				if(Keys.LEFT.isDown(this.previousKeys) && !Keys.LEFT.isDown(msg.keys)) {
					Player spectateMe = gameType.getNextPlayerToSpectate(getPlayers(), player);
					player.setSpectating(spectateMe);
				}
				else if(Keys.RIGHT.isDown(this.previousKeys) && !Keys.RIGHT.isDown(msg.keys)) {
					Player spectateMe = gameType.getNextPlayerToSpectate(getPlayers(), player);
					player.setSpectating(spectateMe);
				}
				this.previousKeys = msg.keys;
			}
		}
	}
	
	/**
	 * Kill all entities, invokes a softKill on
	 * all active game entities
	 */
	public void killAll() {
		for(int i = 0; i < entities.length; i++) {
			Entity ent = entities[i];
			if(ent!=null) {
				ent.softKill();
			}
		}	
		
		for(int i = 0; i < playerEntities.length; i++) {
			playerEntities[i] = null;
		}	
		
		this.bombTargets.clear();
		this.vehicles.clear();
	}
	
	
	/**
	 * Spawns a new {@link Tank}
	 * @param x
	 * @param y
	 * @return the {@link Tank}
	 */
	public Tank newTank(float x, float y) {
		return newTank(new Vector2f(x, y));
	}
	
	/**
	 * Spawns a new {@link Tank}
	 * @param pos
	 * @return the {@link Tank}
	 */
	public Tank newTank(Vector2f pos) {
		final Tank tank = new Tank(pos, this);		
		tank.onKill = new KilledListener() {
			
			@Override
			public void onKill(Entity entity, Entity killer) {
				vehicles.remove(tank);
				if(tank.hasOperator()) {
					tank.getOperator().kill(killer);
				}
			}
		};
		
		vehicles.add(tank);
		addEntity(tank);
		
		return tank;
	}
	
	/**
	 * Spawns a new {@link DroppedItem}
	 * 
	 * @param pos
	 * @param item
	 * @return the item
	 */
	public DroppedItem newDroppedItem(Vector2f pos, Weapon item) {
		DroppedItem droppedItem = new DroppedItem(pos, this, item);
		addEntity(droppedItem);
		return droppedItem;
	}
	
	
	/**
	 * Spawns a new {@link Bomb}
	 * @param target
	 * @return the bomb
	 */
	public Bomb newBomb(BombTarget target) {
		Bomb bomb = new Bomb(target.getCenterPos(), this);		
		addEntity(bomb);
		return bomb;
	}
	
	/**
	 * Spawns a new {@link BombTarget}
	 * @param position
	 * @return the bomb target
	 */
	public BombTarget newBombTarget(Vector2f position) {
		final BombTarget target = new BombTarget(position, this);
		target.onKill = new KilledListener() {
			
			@Override
			public void onKill(Entity entity, Entity killer) {
				bombTargets.remove(target);
			}
		};
		this.bombTargets.add(target);
		addEntity(target);
		return target;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getBombTargets()
	 */
	@Override
	public List<BombTarget> getBombTargets() {
		return bombTargets;
	}
	
	/**
	 * Adds a new {@link LightBulb} to the game
	 * @param pos
	 * @return a new light
	 */
	public LightBulb newLight(Vector2f pos) {
		LightBulb light = new LightBulb(pos, this);
		this.addEntity(light);
		return light;
	}
	
	/**
	 * Adds a new {@link LightBulb} to the game
	 * @param x
	 * @param y
	 * @return a new light
	 */
	public LightBulb newLight(float x, float y) {
		return newLight(new Vector2f(x,y));
	}
	
	/**
	 * Adds a new {@link Explosion}
	 * @param pos
	 * @param owner
	 * @param splashDamage
	 * @return the {@link Explosion}
	 */
	public Explosion newExplosion(Vector2f pos, Entity owner, int splashDamage) {
		Explosion explosion = new Explosion(pos, 0, this, owner, splashDamage);
		this.addEntity(explosion);
		return explosion;
	} 
	
	/**
	 * Adds a new {@link Fire}
	 * @param pos
	 * @param owner
	 * @param damage
	 * @return the {@link Fire}
	 */
	public Fire newFire(Vector2f pos, int speed, Vector2f vel, Entity owner, int damage) {
		Fire fire = new Fire(pos, speed, this, owner, vel, damage);		
		this.addEntity(fire);
		return fire;
	}
	
	public void newBigExplosion(Vector2f position, Entity owner, int splashWidth, int maxSpread, int splashDamage) {				
		Vector2f tl = new Vector2f(position.x  - (splashWidth + random.nextInt(maxSpread)),
								   position.y  - (splashWidth + random.nextInt(maxSpread)));
		newExplosion(tl, owner, splashDamage);
		
		Vector2f tr = new Vector2f(position.x  + (splashWidth + random.nextInt(maxSpread)),
				  				   position.y  - (splashWidth + random.nextInt(maxSpread)));
		newExplosion(tr, owner, splashDamage);
		
		Vector2f bl = new Vector2f(position.x  - (splashWidth + random.nextInt(maxSpread)),
				  				   position.y  + (splashWidth + random.nextInt(maxSpread)));
		newExplosion(bl, owner, splashDamage);
		
		Vector2f br = new Vector2f(position.x  + (splashWidth + random.nextInt(maxSpread)),
				  				   position.y  + (splashWidth + random.nextInt(maxSpread)));
		newExplosion(br, owner, splashDamage);
		
		Vector2f center = new Vector2f(position.x,position.y);
		newExplosion(center, owner, splashDamage);
	}
	
	public void newBigFire(Vector2f position, Entity owner, int damage) {				
		Random random = getRandom();
		final int maxSpread = 360;
		
		for(int i = 0; i < 25; i++) {			
			Vector2f vel = new Vector2f(1.0f, 0.0f);
			double rd = Math.toRadians(random.nextInt(maxSpread));						
			Vector2f.Vector2fRotate(vel, rd, vel);
						
			int speed = 80 + random.nextInt(150);
			
			newFire(position.createClone(), speed, vel, owner, damage);
		}
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.GameInfo#getCloseBombTarget(seventh.game.PlayerEntity)
	 */
	@Override
	public BombTarget getCloseBombTarget(PlayerEntity entity) {
		BombTarget handleMe = null;
		
		int size = this.bombTargets.size();
		for(int i = 0; i < size; i++) {
			BombTarget target = this.bombTargets.get(i);
			if(target.canHandle(entity)) {
									
				if(target.bombActive()) {
					Bomb bomb = target.getBomb();
					bomb.disarm(entity);						
											
					emitSound(entity.getId(), SoundType.BOMB_DISARM, entity.getPos());						
				}
				else {
					if(!target.isBombAttached()) {							
						Bomb bomb = newBomb(target); 
						bomb.plant(entity, target);
						target.attachBomb(bomb);
						emitSound(entity.getId(), SoundType.BOMB_PLANT, entity.getPos());
					}												
				}								
				handleMe = target;
				break;
			}
		}
		
		return handleMe;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.GameInfo#getCloseOperableVehicle(seventh.game.Entity)
	 */
	@Override
	public Vehicle getCloseOperableVehicle(Entity operator) {
		Vehicle rideMe = null;
		for(int i = 0; i < this.vehicles.size(); i++) {
			Vehicle vehicle = this.vehicles.get(i);
			if(vehicle.isAlive()) {
				if(vehicle.canOperate(operator)) {
					rideMe = vehicle;
					break;
				}
			}
		}
		
		return rideMe;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#doesTouchOthers(seventh.game.Entity)
	 */
	@Override
	public boolean doesTouchOthers(Entity ent) {
		for(int i = 0; i < this.entities.length; i++) {
			Entity other = this.entities[i];
			if(other != null) {
				if(other != ent && other.bounds.intersects(ent.bounds)) {
					if(ent.onTouch != null) {
						ent.onTouch.onTouch(ent, other);
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#doesTouchPlayers(seventh.game.Entity)
	 */
	@Override
	public boolean doesTouchPlayers(Entity ent) {
		for(int i = 0; i < this.playerEntities.length; i++) {
			Entity other = this.playerEntities[i];
			if(other != null) {
				if(other != ent && other.bounds.intersects(ent.bounds)) {
					if(ent.onTouch != null) {
						ent.onTouch.onTouch(ent, other);
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.GameInfo#doesTouchVehicles(seventh.game.Entity)
	 */
	@Override
	public boolean doesTouchVehicles(Entity ent) {
		for(int i = 0; i < this.vehicles.size(); i++) {
			Entity other = this.vehicles.get(i);
			if(other != null) {
				if(other != ent && other.bounds.intersects(ent.bounds)) {
					if(ent.onTouch != null) {
						ent.onTouch.onTouch(ent, other);
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#doesTouchPlayers(seventh.game.Entity, seventh.math.Vector2f, seventh.math.Vector2f)
	 */
	@Override
	public boolean doesTouchPlayers(Entity ent, Vector2f origin, Vector2f dir) {		
		if(ent.onTouch != null) {
			for(int i = 0; i < this.playerEntities.length; i++) {
				Entity other = this.playerEntities[i];
				if(other != null) {
					if(other != ent && other.canTakeDamage() && other.bounds.intersects(ent.bounds)) {								
						if(isEntityReachable(other, origin, dir)) {
							ent.onTouch.onTouch(ent, other);
							return true;										
						}
					}
				}
			}
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#isEntityReachable(seventh.game.Entity, seventh.math.Vector2f, seventh.math.Vector2f)
	 */
	@Override
	public boolean isEntityReachable(Entity other, Vector2f origin, Vector2f dir) {
		// we only have to do weird checks if the target entity is
		// ducking
		if(other.getHeightMask() != Entity.STANDING_HEIGHT_MASK) {
			
			Vector2f otherPos = other.getCenterPos();
			// if the bullet came from another tile, we must
			// check and see if this entity is being sheltered by
			// a heightMask tile						
			float traveledSq = Vector2f.Vector2fDistanceSq(otherPos, origin);
			if(traveledSq > DISTANCE_CHECK) {
			
				Tile tile = map.getWorldTile(0, (int)otherPos.x, (int)otherPos.y);
				if(tile != null) {						
					
					int adjacentX = (int)(tile.getX() + (TILE_WIDTH/2) + (TILE_WIDTH * -dir.x));
					int adjacentY = (int)(tile.getY() + (TILE_HEIGHT/2) + (TILE_HEIGHT * -dir.y));
					
					// if the target entity is safely crouching behind 
					// a heightMask tile, the bullet can't touch him
					if( map.hasHeightMask( adjacentX, adjacentY) ) {
						return false;
					}
				}
			}
		}
		
		return true;
	}
	
	/**
	 * @return the enableFOW
	 */
	public boolean isEnableFOW() {
		return enableFOW;
	}
	
	/**
	 * @param enable
	 */
	public void enableFOW(boolean enable) {
		this.enableFOW =enable;
	}
	
	/**
	 * @return the full networked game state
	 */
	public NetGameState getNetGameState() {							
		for(int i = 0; i < this.entities.length; i++) {
			Entity other = this.entities[i];
			if(other != null) {
				gameState.entities[i] = other.getNetEntity();
			}
			else {
				gameState.entities[i] = null;
			}
		}

		gameState.gameType = this.gameType.getNetGameTypeInfo();
		gameState.map = this.gameMap.getNetMap();
						
		gameState.stats = getNetGameStats();						
		return gameState;
	}
	
	/**
	 * @return just returns the networked game statistics
	 */
	public NetGameStats getNetGameStats() {		
		gameStats.playerStats = new NetPlayerStat[this.players.getNumberOfPlayers()];
		Player[] players = this.players.getPlayers();
		
		int j = 0;
		for(int i = 0; i < players.length; i++) {
			Player player = players[i];
			if(player != null) {
				gameStats.playerStats[j++] = player.getNetPlayerStat();
			}
		}
		
		gameStats.teamStats = this.gameType.getNetTeamStats();		
		return gameStats;
	}
	
	/**
	 * @return returns the networked game (partial) statistics
	 */
	public NetGamePartialStats getNetGamePartialStats() {
		gamePartialStats.playerStats = new NetPlayerPartialStat[this.players.getNumberOfPlayers()];
		Player[] players = this.players.getPlayers();
		
		int j = 0;
		for(int i = 0; i < players.length; i++) {
			Player player = players[i];
			if(player != null) {
				gamePartialStats.playerStats[j++] = player.getNetPlayerPartialStat();
			}
		}
		
		gamePartialStats.teamStats = this.gameType.getNetTeamStats();		
		return gamePartialStats;
	}
	
	/**
	 * @param playerId
	 * @return returns only the entities within the viewport of the supplied player
	 */	
	public NetGameUpdate getNetGameUpdateFor(int playerId) {
		Player player = this.players.getPlayer(playerId);
		if(player == null) {
			return null;
		}
								
		NetGameUpdate netUpdate = new NetGameUpdate();
		NetSound[] sounds = null;
		
		
		if (player.isPureSpectator()) {
			NetEntity.toNetEntities(entities, netUpdate.entities);
			sounds = NetSound.toNetSounds(soundEvents);
			/*
			 * If the current player you are watching is dead,
			 * follow another player
			 */						
			if( player.getSpectating()==null || player.getSpectating().isDead()) {
				Player otherPlayer = this.players.getRandomAlivePlayer();
				if(otherPlayer!=null) {					
					player.setSpectating(otherPlayer);					
				}
			}
			
		}		
		else {
			PlayerEntity playerEntity = player.isSpectating() ? player.getSpectatingEntity() : player.getEntity();
			
			if(playerEntity != null) {
				/*
				 * Calculate all the sounds this player can hear
				 */			
				aSoundsHeard.clear();
				aSoundsHeard = playerEntity.getHeardSounds(soundEvents, aSoundsHeard);			
				sounds = NetSound.toNetSounds(aSoundsHeard);
							
				/*
				 * Calculate all the visuals this player can see
				 */
				aEntitiesInView.clear();
				aEntitiesInView = playerEntity.getEntitiesInView(this);
				NetEntity.toNetEntities(aEntitiesInView, netUpdate.entities);
				
				/* now add the players full entity state */
				if(playerEntity.isAlive()) {
					netUpdate.entities[playerEntity.getId()] = playerEntity.getNetPlayer();
				}
			}
		}
		
		for(int i = 0; i < MAX_PERSISTANT_ENTITIES; i++) {
			if(deadFrames[i] > 0) {
				netUpdate.deadPersistantEntities.setBit(i);
			}
		}
		
		netUpdate.time = (int)time;
		netUpdate.sounds = sounds;
		netUpdate.spectatingPlayerId = player.getSpectatingPlayerId();
		return netUpdate;
	}
}
