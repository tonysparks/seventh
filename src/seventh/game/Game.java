/*
 * see license.txt 
 */
package seventh.game;

import static seventh.shared.SeventhConstants.MAX_ENTITIES;
import static seventh.shared.SeventhConstants.MAX_PERSISTANT_ENTITIES;
import static seventh.shared.SeventhConstants.MAX_PLAYERS;
import static seventh.shared.SeventhConstants.MAX_TIMERS;
import static seventh.shared.SeventhConstants.SPAWN_INVINCEABLILITY_TIME;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Random;

import leola.frontend.listener.EventDispatcher;
import leola.frontend.listener.EventMethod;
import leola.vm.Leola;
import leola.vm.types.LeoObject;
import seventh.ai.AISystem;
import seventh.ai.basic.AILeolaLibrary;
import seventh.ai.basic.DefaultAISystem;
import seventh.game.Entity.KilledListener;
import seventh.game.Entity.Type;
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
import seventh.game.events.TileRemovedEvent;
import seventh.game.net.NetEntity;
import seventh.game.net.NetGamePartialStats;
import seventh.game.net.NetGameState;
import seventh.game.net.NetGameStats;
import seventh.game.net.NetGameUpdate;
import seventh.game.net.NetMapDestructables;
import seventh.game.net.NetPlayerPartialStat;
import seventh.game.net.NetPlayerStat;
import seventh.game.net.NetSound;
import seventh.game.net.NetSoundByEntity;
import seventh.game.type.GameType;
import seventh.game.vehicles.PanzerTank;
import seventh.game.vehicles.ShermanTank;
import seventh.game.vehicles.Tank;
import seventh.game.vehicles.Vehicle;
import seventh.game.weapons.Explosion;
import seventh.game.weapons.Fire;
import seventh.game.weapons.Weapon;
import seventh.graph.GraphNode;
import seventh.map.GraphNodeFactory;
import seventh.map.Layer;
import seventh.map.Layer.LayerTileIterator;
import seventh.map.Map;
import seventh.map.MapGraph;
import seventh.map.Tile;
import seventh.math.OBB;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.network.messages.AICommandMessage;
import seventh.network.messages.PlayerInputMessage;
import seventh.server.GameServerLeolaLibrary;
import seventh.server.SeventhScriptingCommonLibrary;
import seventh.shared.Cons;
import seventh.shared.Debugable;
import seventh.shared.Scripting;
import seventh.shared.SeventhConfig;
import seventh.shared.SeventhConstants;
import seventh.shared.SoundType;
import seventh.shared.TimeStep;
import seventh.shared.Timer;
import seventh.shared.Updatable;

/**
 * Represents the current Game Session
 * 
 * @author Tony
 *
 */
public class Game implements GameInfo, Debugable, Updatable {
	
	
	/**
	 * Null Node Data.
	 * 
	 * @author Tony
	 *
	 */
	private static class NodeData implements GraphNodeFactory<Void> {		
		@Override
		public Void createEdgeData(Map map, GraphNode<Tile, Void> left,
				GraphNode<Tile, Void> right) {		
			return null;
		}				
	}
		
	
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
	private List<Flag> flags;
	
	private Players players;
				
	private EventDispatcher dispatcher;
			
	private long time;
	
	private Random random;
	
	private SoundEventPool soundEvents
					     , lastFramesSoundEvents;		
		
	private boolean enableFOW;
	
	// data members that are strictly here for performance
	// reasons
	List<Tile> aTiles = new ArrayList<Tile>();
	List<SoundEmittedEvent> aSoundsHeard = new ArrayList<SoundEmittedEvent>();
	List<Entity> aEntitiesInView = new ArrayList<Entity>();
		
	private NetGameUpdate[] playerUpdates;
	private NetGameState gameState;
	private NetGameStats gameStats;
	private NetGamePartialStats gamePartialStats;
	
	private Timers gameTimers;
	private Triggers gameTriggers;
	
	private final float DISTANCE_CHECK;
	private final int TILE_WIDTH, TILE_HEIGHT;
	
	private SeventhConfig config;

	
	private int lastValidId;
	
	private AISystem aiSystem;
	
	/**
	 * @param config
	 * @param players
	 * @param gameType
	 * @param gameMap
	 * @param dispatcher
	 */
	public Game(SeventhConfig config, 
			final Players players, 
			final GameType gameType, 
			GameMap gameMap, 
			EventDispatcher dispatcher) {
		
		this.config = config;
		this.gameType = gameType;		
		
		this.dispatcher = dispatcher;
		
		this.gameMap = gameMap;
		this.map = gameMap.getMap();
		
		this.graph = map.createMapGraph(new NodeData());
		this.gameTimers = new Timers(MAX_TIMERS);
		this.gameTriggers = new Triggers(this);
		
		this.entities = new Entity[MAX_ENTITIES];
		this.playerEntities = new PlayerEntity[MAX_PLAYERS];
		
		this.deadFrames = new int[MAX_ENTITIES];
		
		this.playerUpdates = new NetGameUpdate[MAX_ENTITIES];
		for(int i = 0; i < this.playerUpdates.length; i++) {
			this.playerUpdates[i] = new NetGameUpdate();
		}
		
		this.bombTargets = new ArrayList<BombTarget>();
		this.vehicles = new ArrayList<Vehicle>();
		this.flags = new ArrayList<Flag>();
		
		this.soundEvents = new SoundEventPool(SeventhConstants.MAX_SOUNDS);
		this.lastFramesSoundEvents = new SoundEventPool(SeventhConstants.MAX_SOUNDS);
		
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
//				soundEvents.add(event);
				soundEvents.emitSound(event);
			}
		});				
		
		this.dispatcher.addEventListener(RoundStartedEvent.class, new RoundStartedListener() {
			
			@Override
			public void onRoundStarted(RoundStartedEvent event) {
				map.restoreDestroyedTiles();
			    loadMapScripts();
				aiSystem.startOfRound(Game.this);				
			}
		});
		
		this.dispatcher.addEventListener(RoundEndedEvent.class, new RoundEndedListener() {

			@Override
			public void onRoundEnded(RoundEndedEvent event) {
				gameTimers.removeTimers();
				gameTriggers.removeTriggers();
				aiSystem.endOfRound(Game.this);
			}
		});
		
		this.aiSystem.init(this);		
		this.gameType.registerListeners(this, dispatcher);
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
	 * @param id
	 * @param sound
	 */
	public void emitSound(int id, SoundType sound) {
		soundEvents.emitSound(id, sound, id);
	}
	
	/**
	 * Emits a sound for the client to hear
	 * 
	 * @param sound
	 * @param pos
	 */
	public void emitSound(int id, SoundType sound, Vector2f pos) {
		soundEvents.emitSound(id, sound, pos);
	}
	
	
	/**
	 * Starts the game
	 */
	public void startGame() {
		this.gameType.start(this);
	}
	
	/**
	 * Attempts to add a bot to the world
	 * @param name
	 * @return the ID slot which the bot occupies.  
	 */
	public int addBot(String name) {
		for(int i = 0; i < this.players.maxNumberOfPlayers(); i++) {
			if(!this.players.hasPlayer(i)) {
				return addBot(i, name);
			}
		}
		return -1;
	}
	
	/**
	 * Adds a bot
	 * 
	 * @param id the bot id
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
	public SeventhConfig getConfig() {
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
	
	/**
	 * @return the gameTimers
	 */
	@Override
	public Timers getGameTimers() {
		return gameTimers;
	}
	
	/**
	 * Adds a trigger to the game world
	 * 
	 * @param trigger
	 */
	public void addTrigger(Trigger trigger) {
		this.gameTriggers.addTrigger(trigger);
	}
	
	/**
	 * Adds a trigger to the game world.
	 * 
	 * @param function
	 */
	public void addTrigger(final LeoObject function) {
		final LeoObject gameClass = LeoObject.valueOf(this);
		final LeoObject cond = function.getObject("checkCondition");
		final LeoObject exe = function.getObject("execute");
		
		addTrigger(new Trigger() {			
			@Override
			public boolean checkCondition(Game game) {			
				return LeoObject.isTrue(cond.call(gameClass));
			}			
			@Override
			public void execute(Game game) {
				exe.call(gameClass);
			}
		});
	}
	
	/**
	 * Adds a {@link Timer}
	 * 
	 * @param timer
	 * @return true if the timer was added;false otherwise
	 */
	public boolean addGameTimer(Timer timer) {
		return this.gameTimers.addTimer(timer);
	}
	
	/**
	 * Adds the {@link LeoObject} callback as the timer function
	 * @param loop
	 * @param endTime
	 * @param function
	 * @return true if the timer was added;false otherwise
	 */
	public boolean addGameTimer(boolean loop, long endTime, final LeoObject function) {
		return addGameTimer(new Timer(loop, endTime) {			
			@Override
			public void onFinish(Timer timer) {
				LeoObject result = function.call();
				if(result.isError()) {
					Cons.println("*** ERROR: Script error in GameTimer: " + result);
				}
			}
		});
	}
	
	/**
	 * Adds the {@link LeoObject} callback as a the timer function, which will also randomize the start/end time.
	 * 
	 * @param loop
	 * @param minStartTime
	 * @param maxEndTime
	 * @param function
	 * @return true if the timer was added;false otherwise
	 */
	public boolean addRandomGameTimer(boolean loop, final long minStartTime, final long maxEndTime, final LeoObject function) {
		return addGameTimer(new Timer(loop, minStartTime) {			
			@Override
			public void onFinish(Timer timer) {
				LeoObject result = function.call();
				if(result.isError()) {
					Cons.println("*** ERROR: Script error in GameTimer: " + result);
				}
				
				long delta = maxEndTime - minStartTime;
				int millis = (int)delta / 100;
				timer.setEndTime(minStartTime + random.nextInt(millis) * 100);
			}
		});
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getLastFramesSoundEvents()
	 */
	@Override
	public SoundEventPool getLastFramesSoundEvents() {
		return lastFramesSoundEvents;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getSoundEvents()
	 */
	@Override
	public SoundEventPool getSoundEvents() {
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
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getBombTargets()
	 */
	@Override
	public List<BombTarget> getBombTargets() {
		return bombTargets;
	}
	
	/**
	 * @return the vehicles
	 */
	@Override
	public List<Vehicle> getVehicles() {
		return vehicles;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getFlags()
	 */
	@Override
	public List<Flag> getFlags() {	
		return this.flags;
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
	@Override
	public void update(TimeStep timeStep) {		
		for(int i = 0; i < entities.length; i++) {
			Entity ent = entities[i];
			if(ent!=null) {
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
		this.gameTimers.update(timeStep);
		this.gameTriggers.update(timeStep);
		
		this.gameType.update(this, timeStep);
		this.time = this.gameType.getRemainingTime();								
	}
	
	/**
	 * Invoked after an update, a hack to work
	 * around processing event queue
	 */
	public void postUpdate() {		
		lastFramesSoundEvents.clear();
		lastFramesSoundEvents.set(soundEvents);
		soundEvents.clear();
		
		lastValidId = 0;
		
	}
	
	/**
	 * Loads any map scripts and/or special entities associated with the game map.  This should
	 * be invoked every time a new Round begins  
	 */
	private void loadMapScripts() {
	    File propertiesFile = new File(gameMap.getMapFileName() + ".props.leola");
        if(propertiesFile.exists()) {
            try {   
                // TODO:
                // Clean this up, I don't think we actually need a 'properties'
                // loading, this can all be done thru the gameType scripts.
                // The weird thing about this is the lighting entities,
                // this should probably be moved to a post load
                // of the Map.
                Leola runtime = Scripting.newSandboxedRuntime();                   
                runtime.loadStatics(SeventhScriptingCommonLibrary.class);
                
                GameServerLeolaLibrary gLib = new GameServerLeolaLibrary(this);             
                runtime.loadLibrary(gLib, "game2");
                
                AILeolaLibrary aiLib = new AILeolaLibrary(this.aiSystem);
                runtime.loadLibrary(aiLib, "ai");
                
                runtime.put("game", this);
                runtime.eval(propertiesFile);
                
                final Map map = getMap();
                
                /* Load any layers that have predefined entities
                 * on them, as or right now this only includes
                 * lights
                 */
                Layer[] layers = map.getBackgroundLayers();
                for(int i = 0; i < layers.length; i++) {
                    Layer layer = layers[i];
                    if(layer != null && layer.isLightLayer()) {                        
                        layer.foreach(new LayerTileIterator() {                               
                            @Override
                            public void onTile(Tile tile, int x, int y) {
                                if(tile != null) {
                                    LightBulb light = newLight(map.tileToWorld(x, y));
                                    light.setColor(0.9f, 0.85f, 0.85f);
                                    light.setLuminacity(0.95f);
                                }
                            }
                        });                                                    
                    }
                }
            }
            catch(Exception e) {
                Cons.println("*** ERROR -> Loading map properties file: " + propertiesFile.getName() + " -> ");
                Cons.println(e);
            }
        }
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
	
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#getGraph()
	 */
	@Override
	public MapGraph<Void> getGraph() {
		return graph;
	}
	
	/**
	 * Finds a free location on the map, one in which the supplied {@link PlayerEntity} will not collide with.
	 * 
	 * @param player
	 * @return the {@link Vector2f} that is suitable for the {@link PlayerEntity}
	 */
	public Vector2f findFreeSpot(PlayerEntity player) {				
		Vector2f freeSpot = player.getPos();
		
		int safety = 100000;
		while((map.rectCollides(player.getBounds()) || 
			   map.hasWorldCollidableTile((int)player.getCenterPos().x, (int)player.getCenterPos().y)) && 
			   safety>0) {
			
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
			
			safety--;
		} 
		
		return freeSpot;
	}
	
	
	/**
	 * @param entity
	 * @return a random position anywhere in the game world
	 */
	public Vector2f findFreeRandomSpot(Entity entity) {
		return findFreeRandomSpot(entity, 0, 0, map.getMapWidth()-20, map.getMapHeight()-20);
	}
	
	
	/**
	 * @param entity
	 * @param bounds
	 * @return a random position anywhere in the supplied bounds
	 */
	public Vector2f findFreeRandomSpot(Entity entity, Rectangle bounds) {
		return findFreeRandomSpot(entity, bounds.x, bounds.y, bounds.width, bounds.height);
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
	public Vector2f findFreeRandomSpot(Entity entity, int x, int y, int width, int height) {
		Vector2f pos = new Vector2f(x+random.nextInt(width), y+random.nextInt(height));
		Rectangle temp = new Rectangle(entity.getBounds());
		temp.setLocation(pos);
		
		int loopChecker = 0;
		
		while (map.rectCollides(temp) && !map.hasWorldCollidableTile(temp.x, temp.y) ) {
			pos.x = x + random.nextInt(width);
			pos.y = y + random.nextInt(height);
			temp.setLocation(pos);
			
			// this bounds doesn't have a free spot
			if(loopChecker++ > 500_000) {
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
	public Vector2f findFreeRandomSpotNotIn(Entity entity, int x, int y, int width, int height, Rectangle notIn) {
		Vector2f pos = new Vector2f(x+random.nextInt(width), y+random.nextInt(height));
		Rectangle temp = new Rectangle(entity.getBounds());
		temp.setLocation(pos);
		
		while ((map.rectCollides(temp) && !map.hasWorldCollidableTile(temp.x, temp.y)) || notIn.intersects(temp)) {
			pos.x = x + random.nextInt(width);
			pos.y = y + random.nextInt(height);
			temp.setLocation(pos);
		}
		
		return pos;
	}
	
	public Vector2f findFreeRandomSpotNotIn(Entity entity, Rectangle bounds, OBB notIn) {
		Vector2f pos = new Vector2f(bounds.x+random.nextInt(bounds.width), bounds.y+random.nextInt(bounds.height));
		Rectangle temp = new Rectangle(entity.getBounds());
		temp.setLocation(pos);
		
		int numberOfAttempts = 0;
		
		while ((map.rectCollides(temp) && !map.hasWorldCollidableTile(temp.x, temp.y)) || 
				(notIn.expensiveIntersects(temp))) {
			
			pos.x = bounds.x + random.nextInt(bounds.width);
			pos.y = bounds.y + random.nextInt(bounds.height);
			temp.setLocation(pos);
			
			if(numberOfAttempts++ > 100_00) {
				return null;
			}
		}
		
		return pos;
	}
	
	/**
	 * A {@link Player} joined the game.
	 * 
	 * @param player
	 */
	public void playerJoined(Player player) {
		Cons.println("Player " + player.getName() + " has joined the game @ " + new Date());
		
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
			Cons.println("Player " + player.getName() + " has left the game @ " + new Date());
			
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
		
		this.gameTimers.removeTimers();
		
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
		if( player.getTeamId() == Team.ALLIED_TEAM_ID ) {
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
			entities[id] = ent;
		}
	}
	
	public boolean playerSwitchedTeam(int playerId, byte teamId) {
		boolean playerSwitched = false;
		Player player = this.players.getPlayer(playerId);
		if(player!=null) {
			playerSwitched = gameType.switchTeam(player, teamId);
			if(playerSwitched) {
			
				// always kill the player				
				if(player.hasEntity()) {
					player.commitSuicide();
				}
	
				if(Team.SPECTATOR_TEAM_ID != teamId) {
					player.stopSpectating();
				}
				
				/* make sure the player has the teams weaponry */
				switch(player.getWeaponClass()) {
					case THOMPSON:
						player.setWeaponClass(Type.MP40);
						break;
					case MP40: 
						player.setWeaponClass(Type.THOMPSON);
						break;
						
					case KAR98:
						player.setWeaponClass(Type.SPRINGFIELD);
						break;
					case SPRINGFIELD:
						player.setWeaponClass(Type.KAR98);
						break;
						
					case MP44:
						player.setWeaponClass(Type.M1_GARAND);
						break;
					case M1_GARAND:
						player.setWeaponClass(Type.MP44);
					
					case SHOTGUN:
					case ROCKET_LAUNCHER:
					case RISKER:
						break;
						
					/* make the player use the default weapon */
					default: {
						switch(teamId) {
							case Team.ALLIED_TEAM_ID:
								player.setWeaponClass(Type.THOMPSON);
								break;
							case Team.AXIS_TEAM_ID:
								player.setWeaponClass(Type.MP40);
								break;
						}
					}
				}
			}
		}
					
		return playerSwitched;
	}
	
	/**
	 * A player has requested to switch its weapon class
	 * 
	 * @param playerId
     * @param weaponType
     */
    public void playerSwitchWeaponClass(int playerId, Type weaponType) {
        Player player = players.getPlayer(playerId);
        if(player!=null) {            
            Team team = player.getTeam();
            if(team!=null) {
                boolean allowed = false;
                
                switch(team.getId()) {
                    case Team.ALLIED_TEAM_ID:
                        switch(weaponType) {
                            case THOMPSON:
                            case M1_GARAND:
                            case SPRINGFIELD:
                            case RISKER:
                            case SHOTGUN:
                            case ROCKET_LAUNCHER:
                                allowed = true;
                                break;
                            default: allowed = false;
                        }
                        break;
                    case Team.AXIS_TEAM_ID:
                        switch(weaponType) {
                            case MP40:
                            case MP44:
                            case KAR98:
                            case RISKER:
                            case SHOTGUN:
                            case ROCKET_LAUNCHER:
                                allowed = true;
                                break;
                            default: allowed = false;
                        }
                        break;
                    default:
                            break;
                }
                
                if(allowed) {
                    player.setWeaponClass(weaponType);
                }
            }
        }
    }
    
    
    /**
     * Attempts to change the player's commander status
     * @param player
     * @param isCommander
     * @return true if successfully changed the commander status of the player
     */
    public boolean playerCommander(Player player, boolean isCommander) {
    	if(player!=null) {
    		if(isCommander) {
    			Team team = player.getTeam();
    			if(team.isValid() && !team.hasCommander()) {
    				if(player.isAlive()) {
    					Entity ent = player.getEntity();
    					ent.softKill();
    						
    					removePlayer(ent);
    				}
    				
    				player.setCommander(isCommander);
    				return true;
    			}
    		}
    		else {
    			player.setCommander(isCommander);
    			return true;
    		}
    	}
    	return false;
    }
    
    /**
     * Receives an AICommand from a player.
     * 
     * @param fromPlayerId
     * @param msg
     */
    public void receiveAICommand(int fromPlayerId, AICommandMessage msg) {
        Player player = players.getPlayer(fromPlayerId);
        if(player != null) {
            PlayerInfo botPlayer = getPlayerById(msg.botId);
            if(botPlayer.isBot()) {
                if(player.getTeamId() == botPlayer.getTeamId()) {               
                    aiSystem.receiveAICommand(botPlayer, msg.command);
                }
            }
        }
    }
	
	/**
	 * Applies the remote {@link PlayerInputMessage} to the {@link PlayerEntity}
	 * 
	 * @param playerId
	 * @param msg
	 */
	public void applyPlayerInput(int playerId, PlayerInputMessage msg) {
		Player player = this.players.getPlayer(playerId);
		if(player != null) {			
			if(player.isAlive()) {
				PlayerEntity entity = player.getEntity();				
				entity.handleUserCommand(msg.keys, msg.orientation);								
			}
			else {				
				player.handleInput(this, msg.keys);
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
	 * Removes a destructable tile at the supplied world coordinate
	 * 
	 * @param x
	 * @param y
	 */
	public void removeTileAtWorld(int x, int y) {
	    if(map.removeDestructableTileAtWorld(x, y)) {
	        int tileX = map.worldToTileX(x);
	        int tileY = map.worldToTileY(y);
	        this.dispatcher.queueEvent(new TileRemovedEvent(this, tileX, tileY));
	    }
	}
	
	/**
	 * Spawns a new {@link PanzerTank}
	 * 
	 * @param x
	 * @param y
	 * @return the {@link PanzerTank}
	 */
	public Tank newPanzerTank(float x, float y, Long timeToKill) {
		return newPanzerTank(new Vector2f(x, y), timeToKill);
	}
	
	/**
	 * Spawns a new {@link PanzerTank}
	 * 
	 * @param pos
	 * @param timeToKill the time it takes for this once it is destroyed to be
	 * set to the killed state
	 * @return the {@link PanzerTank}
	 */
	public Tank newPanzerTank(Vector2f pos, Long timeToKill) {
		if(timeToKill==null) {
			timeToKill = -1L;
		}
		return registerTank(new PanzerTank(pos, this, timeToKill));				
	}
	
	/**
	 * Spawns a new {@link ShermanTank}
	 * 
	 * @param x
	 * @param y
	 * @param timeToKill the time it takes for this once it is destroyed to be
	 * set to the killed state
	 * @return the {@link ShermanTank}
	 */
	public Tank newShermanTank(float x, float y, Long timeToKill) {
		return newShermanTank(new Vector2f(x, y), timeToKill);
	}
	
	/**
	 * Spawns a new {@link ShermanTank}
	 * 
	 * @param pos
	 * @param timeToKill the time it takes for this once it is destroyed to be
	 * set to the killed state
	 * @return the {@link ShermanTank}
	 */
	public Tank newShermanTank(Vector2f pos, Long timeToKill) {
		if(timeToKill==null) {
			timeToKill = -1L;
		}
		return registerTank(new ShermanTank(pos, this, timeToKill));				
	}
	
	/**
	 * Register a {@link Tank} with the game
	 * 
	 * @param tank
	 * @return the supplied tank
	 */
	private Tank registerTank(final Tank tank) {
		tank.onKill = new KilledListener() {
			
			@Override
			public void onKill(Entity entity, Entity killer) {
				vehicles.remove(tank);				
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
	
	
	/**
	 * Creates a new {@link HealthPack}
	 * 
	 * @param x
	 * @param y
	 * @return the {@link HealthPack}
	 */
	public HealthPack newHealthPack(float x, float y) {
	    return newHealthPack(new Vector2f(x,y));
	}
	
	
	/**
	 * Creates a new {@link HealthPack}
	 * 
	 * @param pos
	 * @return the {@link HealthPack}
	 */
	public HealthPack newHealthPack(Vector2f pos) {
	    HealthPack pack = new HealthPack(pos, this);
	    addEntity(pack);
	    return pack;
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
	
	/**
	 * @param position
	 * @return a new Allied Flag
	 */
	public Flag newAlliedFlag(Vector2f position) {
		Flag flag = new Flag(this, position, Type.ALLIED_FLAG);
		addEntity(flag);
		this.flags.add(flag);
		
		return flag;
	}
	
	/**
	 * @param position
	 * @return a new Axis flag
	 */
	public Flag newAxisFlag(Vector2f position) {
		Flag flag = new Flag(this, position, Type.AXIS_FLAG);
		addEntity(flag);
		this.flags.add(flag);
		
		return flag;
	}
	
	/*
	 * (non-Javadoc)
	 * @see seventh.game.GameInfo#getArmsReachBombTarget(seventh.game.PlayerEntity)
	 */
	@Override
	public BombTarget getArmsReachBombTarget(PlayerEntity entity) {
		BombTarget handleMe = null;
		
		int size = this.bombTargets.size();
		for(int i = 0; i < size; i++) {
			BombTarget target = this.bombTargets.get(i);
			if(target.canHandle(entity)) {											
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
	public Vehicle getArmsReachOperableVehicle(Entity operator) {
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
	
	
	/**
	 * @param vehicle
	 * @return true if the supplied {@link Vehicle} touches a {@link PlayerEntity}
	 */
	@Override
	public boolean doesVehicleTouchPlayers(Vehicle vehicle) {
		if(vehicle.hasOperator()) {
			PlayerEntity operator = vehicle.getOperator();
			for(int i = 0; i < this.playerEntities.length; i++) {
				Entity other = this.playerEntities[i];
				if(other != null) {
					if(other != operator && vehicle.isTouching(other)) {
						if(vehicle.onTouch != null) {
							vehicle.onTouch.onTouch(vehicle, other);
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	/* (non-Javadoc)
	 * @see seventh.game.GameInfo#doesTouchOthers(seventh.game.Entity)
	 */
	@Override
	public boolean doesTouchOthers(Entity ent) {
		for(int i = 0; i < this.entities.length; i++) {
			Entity other = this.entities[i];
			if(other != null) {
				if(other != ent && /*other.bounds.intersects(ent.bounds)*/ ent.isTouching(other)) {
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
				if(other != ent && /*other.bounds.intersects(ent.bounds)*/ ent.isTouching(other)) {
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
				if(other != ent && other.isTouching(ent)) {
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
					if(other != ent && other.canTakeDamage() && /*other.bounds.intersects(ent.bounds)*/ ent.isTouching(other)) {								
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
	
	public void foreachEntity(final LeoObject func) {
		for(int i = 0; i < this.entities.length; i++) {
			Entity ent = this.entities[i];
			if(ent!=null) {
				if (LeoObject.isTrue(func.call(LeoObject.valueOf(ent)))) {
					break;
				}
			}
		}
	}
	
	public void foreachPlayer(final LeoObject func) {
		for(int i = 0; i < this.playerEntities.length; i++) {
			Entity ent = this.playerEntities[i];
			if(ent!=null) {
				if (LeoObject.isTrue(func.call(LeoObject.valueOf(ent)))) {
					break;
				}
			}
		}
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
		
		List<Tile> removedTiles = this.map.getRemovedTiles();
		if(!removedTiles.isEmpty()) {
		    int[] tiles = new int[removedTiles.size() * 2];
		    if(gameState.mapDestructables==null) {
		        gameState.mapDestructables = new NetMapDestructables();
		    }
		    
		    int tileIndex = 0;
		    for(int i = 0; i < removedTiles.size(); i++, tileIndex += 2) {
		        Tile tile = removedTiles.get(i);
		        tiles[tileIndex + 0] = tile.getXIndex();
		        tiles[tileIndex + 1] = tile.getYIndex();
		    }
		    
		    gameState.mapDestructables.length = tiles.length;
		    gameState.mapDestructables.tiles = tiles;
		}
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
	 * In order to properly play the location of a sound, some sounds are attached
	 * to an entity.  But, not all players are updated every frame, so this can cause
	 * the sound to play in the wrong position; to fix this, if the Player is not included
	 * in this update, we enable the positional information of the NetSound and by pass
	 * the client's positional information.
	 * 
	 * @param snds
	 * @param entities
	 */
	private void adjustNetSoundsPosition(NetSound[] snds, NetEntity[] entities) {
		if(snds!=null) {
			for(int sndIndex = 0; sndIndex < snds.length; sndIndex++) {
				NetSound snd = snds[sndIndex];
				if(snd != null) {
					switch(snd.getSoundType().getSourceType()) {
						case REFERENCED:
						case REFERENCED_ATTACHED:
							/* If the attached entity is not included in this
							 * packet, then include the positional information of the
							 * sound
							 */
							NetSoundByEntity sndByEntity = (NetSoundByEntity) snd;
							if(entities[sndByEntity.entityId] == null) {
								sndByEntity.enablePosition();
							}
							
							break;
						default: /* do nothing */
					}
				}
			}
		}
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
								
		// TODO: Figure out a way to cache the NetGameUpdate object
		NetGameUpdate netUpdate =  new NetGameUpdate();//
					//this.playerUpdates[playerId];				
		netUpdate.clear();				
		
		if (player.isPureSpectator()) {
			NetEntity.toNetEntities(entities, netUpdate.entities);
			netUpdate.setNetSounds(NetSound.toNetSounds(soundEvents));
						
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
		else if(player.isCommander()) {
			Team team = player.getTeam();
			List<Player> players = team.getPlayers();
			
			aEntitiesInView.clear();
			aSoundsHeard.clear();
			
			
			for(int i = 0; i < players.size(); i++) {
				Player p = players.get(i);
				if(p.isAlive()) {
					PlayerEntity playerEntity = p.getEntity();
					
					aSoundsHeard = playerEntity.getHeardSounds(soundEvents, aSoundsHeard);			
					aEntitiesInView = playerEntity.getEntitiesInView(this, aEntitiesInView);	
					aEntitiesInView.add(playerEntity);
				}
			}
			
			netUpdate.setNetSounds(NetSound.consolidateToNetSounds(aSoundsHeard)); 			
			NetEntity.toNetEntities(aEntitiesInView, netUpdate.entities);
			
			adjustNetSoundsPosition(netUpdate.sounds, netUpdate.entities);
		}
		else {
			PlayerEntity playerEntity = player.isSpectating() ? player.getSpectatingEntity() : player.getEntity();
			
			if(playerEntity != null) {
				/*
				 * Calculate all the sounds this player can hear
				 */			
				aSoundsHeard.clear();
				aSoundsHeard = playerEntity.getHeardSounds(soundEvents, aSoundsHeard);			
				netUpdate.setNetSounds(NetSound.toNetSounds(aSoundsHeard)); 
										
				
				/*
				 * Calculate all the visuals this player can see
				 */
				aEntitiesInView.clear();
				aEntitiesInView = playerEntity.getEntitiesInView(this, aEntitiesInView);
				NetEntity.toNetEntities(aEntitiesInView, netUpdate.entities);
				
				/* now add the players full entity state */
				if(playerEntity.isAlive()) {
					netUpdate.entities[playerEntity.getId()] = playerEntity.getNetPlayer();
				}
				
				adjustNetSoundsPosition(netUpdate.sounds, netUpdate.entities);
			}
		}
		
		for(int i = 0; i < MAX_PERSISTANT_ENTITIES; i++) {
			if(deadFrames[i] > 0) {
				netUpdate.deadPersistantEntities.setBit(i);
			}
		}
		
		netUpdate.time = (int)time;		
		netUpdate.spectatingPlayerId = player.getSpectatingPlayerId();
		return netUpdate;
	}
		
	/* (non-Javadoc)
	 * @see seventh.shared.Debugable#getDebugInformation()
	 */
	@Override
	public DebugInformation getDebugInformation() {
		DebugInformation me = new DebugInformation();
		me.add("entities", this.entities)
		  .add("bombTargets", this.bombTargets)
		  .add("map", this.map)
		  .add("game_type", this.gameType)
		  .add("ai", this.aiSystem)
		  ;
		  	
		return me;
	}
}
