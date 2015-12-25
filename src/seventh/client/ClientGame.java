/*
 * see license.txt 
 */
package seventh.client;


import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import leola.vm.Leola;
import leola.vm.types.LeoObject;
import seventh.client.ClientEntity.OnRemove;
import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.AnimationPool;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Camera2d;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.LightSystem;
import seventh.client.gfx.particle.AnimationEffect;
import seventh.client.gfx.particle.BloodEmitter;
import seventh.client.gfx.particle.Effect;
import seventh.client.gfx.particle.Emitter;
import seventh.client.gfx.particle.GibEmitter;
import seventh.client.gfx.particle.RocketTrailEmitter;
import seventh.client.sfx.Sound;
import seventh.client.sfx.Sounds;
import seventh.client.weapon.ClientBomb;
import seventh.game.Entity;
import seventh.game.Entity.Type;
import seventh.game.PlayerEntity.Keys;
import seventh.game.SoundType;
import seventh.game.Timers;
import seventh.game.net.NetEntity;
import seventh.game.net.NetExplosion;
import seventh.game.net.NetGamePartialStats;
import seventh.game.net.NetGameState;
import seventh.game.net.NetGameStats;
import seventh.game.net.NetGameTypeInfo;
import seventh.game.net.NetGameUpdate;
import seventh.game.net.NetMapDestructables;
import seventh.game.net.NetPlayerPartialStat;
import seventh.game.net.NetPlayerStat;
import seventh.game.net.NetSound;
import seventh.game.type.GameType;
import seventh.map.Map;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.network.messages.BombDisarmedMessage;
import seventh.network.messages.BombExplodedMessage;
import seventh.network.messages.BombPlantedMessage;
import seventh.network.messages.GameEndedMessage;
import seventh.network.messages.GameReadyMessage;
import seventh.network.messages.GameUpdateMessage;
import seventh.network.messages.PlayerConnectedMessage;
import seventh.network.messages.PlayerDisconnectedMessage;
import seventh.network.messages.PlayerKilledMessage;
import seventh.network.messages.PlayerSpawnedMessage;
import seventh.network.messages.PlayerSpeechMessage;
import seventh.network.messages.PlayerSwitchTeamMessage;
import seventh.network.messages.RoundEndedMessage;
import seventh.network.messages.RoundStartedMessage;
import seventh.network.messages.TeamTextMessage;
import seventh.network.messages.TextMessage;
import seventh.network.messages.TileRemovedMessage;
import seventh.network.messages.TilesRemovedMessage;
import seventh.server.SeventhScriptingCommonLibrary;
import seventh.shared.Cons;
import seventh.shared.DebugDraw;
import seventh.shared.Scripting;
import seventh.shared.SeventhConstants;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * The {@link ClientGame} is responsible for rendering the client's view of the game world.  The view
 * is generated from messages from the server.
 * 
 * @author Tony
 *
 */
public class ClientGame {	
	
	private final SeventhGame app;	
	private final Map map;
	
	private final ClientPlayer localPlayer;
	private final LocalSession localSession;
	
	private final ClientEntities entities;
	private final ClientPlayers players;
	
	private final Pools pools;
	
	private final ClientEntity[] renderingOrderEntities;
	
	private final List<ClientBombTarget> bombTargets;
	private final List<ClientVehicle> vehicles;
	
	private final ClientEntityListener entityListener;
	
	private GameType.Type gameType;
	private ClientTeam attackingTeam, defendingTeam;

	
	private long gameClock;
	private boolean gameEnded, roundEnded;
	
	

	private       Camera camera;
	private final CameraController cameraController;
	
	private final Scoreboard scoreboard;
	private Hud hud;
		
	private final ClientGameEffects gameEffects;
	private final Zings zings;
	
	private final Rectangle cacheRect;
	private final Random random;
	
	private final Timers gameTimers;
	
	/**
	 * Listens for {@link ClientEntity} life cycle
	 * 
	 * @author Tony
	 *
	 */
	public static interface ClientEntityListener {
		
		/**
		 * A {@link ClientEntity} was created
		 * @param ent
		 */
		public void onEntityCreated(ClientEntity ent);
		
		/**
		 * A {@link ClientEntity} was destroyed
		 * @param ent
		 */
		public void onEntityDestroyed(ClientEntity ent);
	}
	
	/**
	 * @param app
	 * @param players
	 * @param map
	 * @param session
	 * @throws Exception
	 */
	public ClientGame(SeventhGame app, ClientPlayers players, Map map, LocalSession session) throws Exception {
		this.app = app;		
		this.players = players;
		this.map = map;
		this.localSession = session;
														
		this.scoreboard = new Scoreboard(this);
		
		this.localPlayer = players.getPlayer(session.getSessionPlayerId());		
		this.entities = new ClientEntities(SeventhConstants.MAX_ENTITIES);	
		this.renderingOrderEntities = new ClientEntity[SeventhConstants.MAX_ENTITIES];		
		
		this.bombTargets = new ArrayList<ClientBombTarget>();
		this.vehicles = new ArrayList<ClientVehicle>();
		
				
		this.camera = newCamera(map.getMapWidth(), map.getMapHeight());
		this.cameraController = new CameraController(this);
		
		this.gameTimers = new Timers(SeventhConstants.MAX_TIMERS);
		
		this.hud = new Hud(this);
		this.random = new Random();
		this.gameType = GameType.Type.TDM;

		this.cacheRect = new Rectangle();

		this.gameEffects = new ClientGameEffects();
		this.entityListener = this.gameEffects.getLightSystem().getClientEntityListener();		
		
		this.pools = new Pools(this);
		this.zings = new Zings(this);
	}	
	
	/**
	 * @return the attackingTeam
	 */
	public ClientTeam getAttackingTeam() {
		return attackingTeam;
	}
	
	/**
	 * @return the defendingTeam
	 */
	public ClientTeam getDefendingTeam() {
		return defendingTeam;
	}
		
	/**
	 * @return the map
	 */
	public Map getMap() {
		return map;
	}

	/**
	 * @return the random
	 */
	public Random getRandom() {
		return random;
	}
	
	/**
	 * Reloads the HUD graphics 
	 */
	public void debugReloadGfx() {
		this.hud = new Hud(this);		
	}
	
	/**
	 * Reload the video, readjusts the screen
	 */
	public void onReloadVideo() {
		this.camera = newCamera(map.getMapWidth(), map.getMapHeight());
		this.cameraController.onVideoReload(camera);
	}
	
	/**
	 * @param x - screen x position
	 * @param y - screen y position
	 * @return the x and y converted to world coordinates
	 */
	public Vector2f screenToWorldCoordinates(int x, int y) {
		Vector2f pos = camera.getPosition();
		Vector2f wpos = new Vector2f(x + pos.x, y + pos.y);
		return wpos;
	}
	
	/**
	 * Determines if the position collides with something on the map
	 * @param pos
	 * @param width
	 * @param height
	 * @return true if the rectangle with width and height centered around pos collides
	 * with a map tile 
	 */
	public boolean doesCollide(Vector2f pos, int width, int height) {
		cacheRect.setSize(width, height);
		cacheRect.centerAround(pos);
						
		return map.rectCollides(cacheRect);
	}
	
	/**
	 * Adds a background effect
	 * 
	 * @param effect
	 */
	public void addBackgroundEffect(Effect effect) {
		this.gameEffects.addBackgroundEffect(effect);
	}
	
	/**
	 * Adds a foreground effect
	 * 
	 * @param effect
	 */
	public void addForegroundEffect(Effect effect) {
		this.gameEffects.addForegroundEffect(effect);
	}
	
	
	/**
	 * Post a message to the client
	 * @param message
	 */
	public void postMessage(String message) {
		this.hud.postMessage(message);
	}
	
	/**
	 * Updates the special effects, etc.
	 * 
	 * @param timeStep
	 */
	public void update(TimeStep timeStep) {
		this.gameEffects.update(timeStep);
		
		long gameClock = timeStep.getGameClock();
				
		ClientEntity[] entityList = entities.getEntities();
		int size = entityList.length;
		for(int i = 0; i < size; i++) {
			ClientEntity ent = entityList[i];
			if(ent != null) {
				ent.update(timeStep);
				
				if(ent.killIfOutdated(gameClock)) {
					removeEntity(ent.getId());	
				}
			}
		}
		
		zings.checkForBulletZings(timeStep);
		
		cameraController.update(timeStep);
		
		gameTimers.update(timeStep);
		
		map.update(timeStep);		
		camera.update(timeStep);
		hud.update(timeStep);			
	}
	
	
	/**
	 * Renders the game world
	 * 
	 * @param canvas
	 */
	public void render(Canvas canvas) {
		boolean renderMethod1 = false;
		
		// TODO The lighting system is now broke :(
		if(renderMethod1) {
			canvas.fboBegin();
			{
				gameEffects.preRenderFrameBuffer(canvas, camera);
				gameEffects.postRenderFrameBuffer(canvas, camera);
	
				canvas.setShader(null);
				renderWorld(canvas, camera);
			}
			canvas.fboEnd();
			
			canvas.setShader(null);
			gameEffects.renderFrameBuffer(canvas, camera);
			
			canvas.setShader(null);
			DebugDraw.enable(false);
			DebugDraw.render(canvas, camera);
	
			
			hud.render(canvas, camera, 0);
		}
		else {
	
			canvas.fboBegin();
			{
				gameEffects.preRenderFrameBuffer(canvas, camera);
	
			}
			canvas.fboEnd();
	
			gameEffects.postRenderFrameBuffer(canvas, camera);
			
			renderWorld(canvas, camera);
			
			canvas.setShader(null);
			DebugDraw.enable(false);
			DebugDraw.render(canvas, camera);
	
			
			hud.render(canvas, camera, 0);
		}
		
	}
	
	private void renderWorld(Canvas canvas, Camera camera) {				
		canvas.begin();		
		map.render(canvas, camera, 0);
		canvas.end();
		
		gameEffects.renderBackground(canvas, camera);
		
				
		ClientEntity[] entityList = entities.getEntities();
		int size = entityList.length;
		
		
		/* first render the background entities */
		for(int i = 0; i < size; i++) {
			/* clear out the foreground entities */
			renderingOrderEntities[i] = null;
			
			ClientEntity entity = entityList[i];			
			if(entity != null) {
				
				if(entity.isBackgroundObject()) {
					entity.render(canvas, camera, 0);
				}
				else {
					renderingOrderEntities[i] = entity;
				}				
			}
		}						
		
		/* now render the foreground entities */
		for(int i = 0; i < size; i++) {			
			ClientEntity entity = renderingOrderEntities[i];			
			if(entity != null) {								
				entity.render(canvas, camera, 0);				
			}
			
			renderingOrderEntities[i] = null;
		}						
				
		gameEffects.renderForeground(canvas, camera);
		map.renderForeground(canvas, camera, 0);
		
		canvas.setColor(0, 45);
		map.renderSolid(canvas, camera, 0);

		gameEffects.renderLightSystem(canvas, camera);
	}
	
	/**
     * @return the localSession
     */
    public LocalSession getLocalSession() {
        return localSession;
    }
		
	/**
	 * @return the lightSystem
	 */
	public LightSystem getLightSystem() {
		return gameEffects.getLightSystem();
	}
	
	/**
	 * @return the scoreboard
	 */
	public Scoreboard getScoreboard() {
		return scoreboard;
	}
	
	/**
	 * @return the bombTargets
	 */
	public List<ClientBombTarget> getBombTargets() {
		return bombTargets;
	}
	
	/**
	 * @return the vehicles
	 */
	public List<ClientVehicle> getVehicles() {
		return vehicles;
	}
	
	/**
     * @return the entities
     */
    public ClientEntities getEntities() {
        return entities;
    }
	
    /**
     * Adds a Timer to the client game world.
     * 
     * @param timer
     * @return true if it was added
     */
    public boolean addGameTimer(Timer timer) {
    	return this.gameTimers.addTimer(timer);
    }
    
    /**
     * Adds a {@link Timer} which will execute the supplied {@link LeoObject}.
     * 
     * @param loop
     * @param endTime
     * @param function
     * @return true if it was added
     */
    public boolean addGameTimer(boolean loop, long endTime, final LeoObject function) {
    	return addGameTimer(new Timer(loop, endTime) {
    		
    		@Override
    		public void onFinish(Timer timer) {
    			function.xcall();
    		}
    	});
    }
    
    
    /**
     * API for playing a sound
     * 
     * @param soundType
     * @param x
     * @param y
     * @return the {@link Sound}
     */
    public Sound playSound(SoundType soundType, float x, float y) {
    	return Sounds.playSound(soundType, x, y);
    }
    
    /**
     * API for loading a sound
     * 
     * @param path
     * @return the {@link Sound} 
     */
    public Sound loadSound(String path) {
    	return Sounds.loadSound(path);
    }
    
	/**
	 * @param id
	 * @return the {@link ClientVehicle} that has the supplied ID, or null
	 * if not found
	 */
	public ClientVehicle getVehicleById(int id) {
		for(int i = 0; i < this.vehicles.size(); i++) {
			ClientVehicle vehicle = this.vehicles.get(i);
			if(vehicle.getId() == id) {
				return vehicle;
			}
		}
		return null;
	}
	
	
	/**
	 * @return the players
	 */
	public ClientPlayers getPlayers() {
		return players;
	}
	
	/**
	 * @return the localPlayer
	 */
	public ClientPlayer getLocalPlayer() {
		return localPlayer;
	}
	
	/**
	 * Get the entity the local player is controlling.  This can either be the player's entity
	 * or a spectating entity.
	 * 
	 * @return the entity the local player is controlling
	 */
	public ClientControllableEntity getLocalPlayerFollowingEntity() {
	    ClientControllableEntity entity = null;
        if(this.localPlayer.isAlive()) {                
            entity = this.localPlayer.getEntity();              
        }
        else if(players.containsPlayer(this.localPlayer.getSpectatingPlayerId())) { 
            entity = players.getPlayer(this.localPlayer.getSpectatingPlayerId()).getEntity();
        }
        
        return entity;
	}
	
	/**
	 * @return the app
	 */
	public SeventhGame getApp() {
		return app;
	}
	
	/**
	 * @return the gameEffects
	 */
	public ClientGameEffects getGameEffects() {
		return gameEffects;
	}
	
	/**
	 * @return the pools
	 */
	public Pools getPools() {
		return pools;
	}
	
	/**
	 * @return the gameClock
	 */
	public long getGameClock() {
		return gameClock;
	}
	
	/**
	 * @return the camera
	 */
	public Camera getCamera() {
		return camera;
	}
	
	
	public void showScoreBoard(boolean showScoreboard) {
		if(!gameEnded && !roundEnded) {			
			this.scoreboard.showScoreBoard(showScoreboard);
		}
		else { 
			this.scoreboard.setGameEnded(true);
			this.scoreboard.showScoreBoard(true); 
		}
	}
	
	/**
	 * @return true if the camera is roaming
	 */
	public boolean isFreeformCamera() {
		return this.cameraController.isCameraRoaming();
	}
	
	/**
	 * Applies the players input.  This is used for
	 * client side prediction.
	 * 
	 * @param keys
	 */
	public void applyPlayerInput(int keys) {
		this.cameraController.applyPlayerInput(keys);
		
		/* Check to see if the user is planting or disarming
		 * a bomb
		 */
		hud.setAtBomb(false);
		if(Keys.USE.isDown(keys)) {
			if(this.localPlayer != null && this.localPlayer.isAlive()) {
				Rectangle bounds = this.localPlayer.getEntity().getBounds(); 
				for(int i = 0; i < bombTargets.size(); i++) {
					ClientBombTarget target = bombTargets.get(i);					
					if(target.isAlive()) {
						if(bounds.intersects(target.getBounds())) {
							
							/* if we are disarming it takes longer
							 * than planting
							 */
							if(target.isBombPlanted()) {
								hud.setBombCompletionTime(5_000);
							}
							else {
								hud.setBombCompletionTime(3_000);
							}
							
							hud.setAtBomb(true);
							break;
						}
					}
				}
			}
		}		
	}
	
	/**
	 * Determines if the player is hovering over a bomb target and if they can take
	 * a valid action with it.  That is, if they are an attacker and the bomb has not 
	 * been planted, or if they are a defender and the bomb is planted.
	 * 
	 * @return true if hovering over a bomb target
	 */
	public boolean isHoveringOverBomb() {
		
		
		if(this.localPlayer != null && this.localPlayer.isAlive()) {
			boolean isAttacker = this.localPlayer.getTeam().equals(getAttackingTeam());
			
			Rectangle bounds = this.localPlayer.getEntity().getBounds(); 
			for(int i = 0; i < bombTargets.size(); i++) {
				ClientBombTarget target = bombTargets.get(i);					
				if(target.isAlive()) {
					if(bounds.intersects(target.getBounds())) {
						if(target.isBombPlanted()) {																			
							return !isAttacker;
						}
						else {
							return isAttacker;
						}												
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Calculates the local players orientation relative to the supplied point
	 * @param mx
	 * @param my
	 * @return the orientation in radians
	 */
	public float calcPlayerOrientation(float mx, float my) {
		if(this.localPlayer != null && this.localPlayer.isAlive()) {
			ClientPlayerEntity entity = localPlayer.getEntity();
			
			Vector2f pos = entity.isOperatingVehicle() ? entity.getVehicle().getCenterPos() : entity.getCenterPos();
			Vector2f cameraPos = camera.getPosition();
			
			double orientation = Math.atan2((my+cameraPos.y)-pos.y, (mx+cameraPos.x)-pos.x);
			return (float)orientation;
		}
		return 0f;
	}
	
	
	/**
	 * Creates a new {@link Camera}
	 * @param map
	 * @return
	 */
	private Camera newCamera(int mapWidth, int mapHeight) {
		Camera camera = new Camera2d();		
		camera.setWorldBounds(new Vector2f(mapWidth, mapHeight));		
		camera.setViewPort(new Rectangle(this.app.getScreenWidth(), this.app.getScreenHeight()));
//		camera.setMovementSpeed(new Vector2f(4000, 4000));
		camera.setMovementSpeed(new Vector2f(130, 130));
				
		return camera;
	}
	
	/**
	 * Changes the active screen 
	 * 
	 * @see GameApplication#setScreen(Screen)
	 * @param screen
	 */
	public void changeScreen(Screen screen) {
		this.app.setScreen(screen);
	}
	
	private void createEntity(NetEntity ent) {				
		if(this.entities.containsEntity(ent.id)) {
			return;
		}				
		
		ClientEntity entity = null;
		
		Type type = Type.fromNet(ent.type);
		
//		System.out.println("Creating: " + type.name() + " id " + ent.id);
		 
		final Vector2f pos = new Vector2f(ent.posX, ent.posY);
		switch(type) {
			case PLAYER_PARTIAL: {
				ClientPlayer player = players.getPlayer(ent.id);
				if(player!=null) {					
					entity = new ClientPlayerEntity(this, player, pos);					
				}
				break;
			}
			case PLAYER: {
				ClientPlayer player = players.getPlayer(ent.id);
				if(player!=null) {					
					entity = new ClientPlayerEntity(this, player, pos);				
				}
				break;
			}
			case BULLET: {
				entity = this.pools.getBulletPool().alloc(ent.id, pos);
				break;
			}
			case ROCKET: {
				entity = new ClientRocket(this, pos);
				
				Emitter rocketTrail = new RocketTrailEmitter(pos, 4000, 0);				
				rocketTrail.attachTo(entity);
				rocketTrail.start();
				
				gameEffects.addForegroundEffect(rocketTrail);
				break;
			}
			case NAPALM_GRENADE:
			case GRENADE: {
				entity = new ClientGrenade(this, pos);
				break;
			}
			case DROPPED_ITEM: {
				entity = new ClientDroppedItem(this, pos);
				break;
			}
			
			case FIRE: 			
			case EXPLOSION: {
				if(type==Type.EXPLOSION) {
					entity = new ClientExplosion(this, pos);
					
					/* create the explosion effect */
					NetExplosion explosion = (NetExplosion)ent;
					
					/* convert to UV coordinates */
					Vector2f uvPos = new Vector2f(pos);
					uvPos.x = (pos.x - camera.getPosition().x) / app.getScreenWidth();
					uvPos.y = 1f - (pos.y - camera.getPosition().y) / app.getScreenHeight();
					
					/* limit the explosion per player */
//					gameEffects.addExplosion(explosion.ownerId, uvPos);
					gameEffects.addExplosion(this, explosion.ownerId, pos);
				}
				else {
					entity = new ClientFire(this, pos);	
				}
				
				// if an explosion happens,
				// shake the camera
				cameraController.shakeCamera(pos);
				
				break;
			}
			case TANK: {
				entity = new ClientTank(this, pos);
				vehicles.add( (ClientVehicle)entity );
				break;
			}
			case BOMB_TARGET: {
				entity = new ClientBombTarget(this, pos);
				bombTargets.add( (ClientBombTarget)entity);
				break;
			}
			case BOMB: {
				entity = new ClientBomb(this, pos);
				
				break;
			}
			case LIGHT_BULB: {
				entity = new ClientLightBulb(this, pos);
				break;
			}
			case HEALTH_PACK: {
				entity = new ClientHealthPack(this, pos);
				break;
			}
			default: {
				Cons.println("Unknown type of entity: " + type.name());
			}
		}
		
		if(entity != null) {
			entity.updateState(ent, gameClock);
			entities.addEntity(ent.id, entity);
			renderingOrderEntities[ent.id] = entity;
			
			entityListener.onEntityCreated(entity);
		}
	}
	
	/**
	 * Prepares the game for play
	 * 
	 * @param mapFile
	 * @param gameState
	 */
	public void prepareGame(String mapFile, NetGameState gameState) {
	    applyFullGameState(gameState);
	    loadMapProperties(mapFile);
	}
	
	/**
     * Load the client maps properties file
     * 
     * @param mapFile
     * @param game
     */
    private void loadMapProperties(String mapFile) {      
        File propertiesFile = new File(mapFile + ".client.props.leola");
        if(propertiesFile.exists()) {
            try {
                Leola runtime = Scripting.newSandboxedRuntime();
                
                runtime.loadStatics(SeventhScriptingCommonLibrary.class);
                runtime.put("game", this);
                runtime.eval(propertiesFile);
            }
            catch(Exception e) {
                Cons.println("*** ERROR -> Loading " + propertiesFile.getName() + ":" + e);
            }
        }
    }
	

	public void applyFullGameState(NetGameState gs) {
		
		ClientEntity[] entityList = this.entities.getEntities();
		for(int i = 0; i < entityList.length; i++) {
			ClientEntity ent = entityList[i];
			if(ent!=null) {
				OnRemove remove = ent.getOnRemove();
				if(remove!=null) {
					remove.onRemove(ent, this);
				}
			}
		}
				
		this.entities.clear();		
		this.bombTargets.clear();
		this.vehicles.clear();
		
		this.gameEffects.removeAllLights();
		this.gameEffects.clearEffects();
				
		applyGameStats(gs.stats);
		
		if(gs.entities != null) {
			int size = gs.entities.length;
			for(int i = 0; i < size; i++) {
				NetEntity netEnt = gs.entities[i];
				if(netEnt != null) {
					createEntity(netEnt);
				}
			}
		}
		
		NetGameTypeInfo gameType = gs.gameType;
		if(gameType!=null) {
			this.gameType = GameType.Type.fromNet(gameType.type);
			
			for(int i = 0; i < gameType.teams.length; i++) {
//				this.scoreboard.setScore(ClientTeam.fromId(gameType.teams[i].id), .score);
				if(gameType.teams[i].isAttacker) {
					this.attackingTeam = ClientTeam.fromId(gameType.teams[i].id);
				}
				
				if(gameType.teams[i].isDefender) {
					this.defendingTeam = ClientTeam.fromId(gameType.teams[i].id);
				}
			}
		}
		
		NetMapDestructables destructables = gs.mapDestructables;
		if(destructables != null) {
	        this.map.removeDestructableTilesAt(destructables.tiles);
		}
	}
	
	public void applyGameUpdate(GameUpdateMessage msg) {
		NetGameUpdate netUpdate = msg.netUpdate;

		gameClock = netUpdate.time;
		
		if(netUpdate.entities != null) {
			int size = netUpdate.entities.length;
			for(int i = 0; i < size; i++) {
				NetEntity netEnt = netUpdate.entities[i];
				if(netEnt != null) {
					if(entities.containsEntity(netEnt.id)) {
						ClientEntity ent = entities.getEntity(netEnt.id);
						if(Type.fromNet(netEnt.type) == ent.getType()) {						
							ent.updateState(netEnt, gameClock);
						}
						else {
							removeEntity(i);
							createEntity(netEnt);
						}
					}
					else {
						createEntity(netEnt);
					}
				}
				else {
					
					if( i < SeventhConstants.MAX_PERSISTANT_ENTITIES) {
						/* if a persistant entity has been removed, lets
						 * remove it on the client side
						 */
						if(netUpdate.deadPersistantEntities.getBit(i)) {					
							removeEntity(i);
						}
					}
					else {
						removeEntity(i);
					}
				}
			}
		}
		
		if(netUpdate.sounds != null) {
			int size = netUpdate.numberOfSounds;
			for(int i = 0; i < size; i++) {
				NetSound snd = netUpdate.sounds[i];
				if(snd!=null) {
					Sounds.playSound(snd, snd.posX, snd.posY);
				}
			}
		}
		
		if(netUpdate.spectatingPlayerId > -1 && !cameraController.isCameraRoaming()) {
			int previousSpec = localPlayer.getSpectatingPlayerId();
			localPlayer.setSpectatingPlayerId(netUpdate.spectatingPlayerId);
			if(previousSpec != netUpdate.spectatingPlayerId) {
				ClientEntity ent = this.entities.getEntity(netUpdate.spectatingPlayerId);
				if(ent!=null) {
					camera.centerAroundNow(ent.getCenterPos());
				}
			}
		}
		else {
			localPlayer.setSpectatingPlayerId(Entity.INVALID_ENTITY_ID);
		}
	}
	
	public void applyGameStats(NetGameStats stats) {
		if(stats.playerStats != null) {
			for(NetPlayerStat stat : stats.playerStats) {
				int playerId = stat.playerId;
				
				if(!players.containsPlayer(playerId)) {
					ClientPlayer player = new ClientPlayer(stat.name, playerId);
					players.addPlayer(player);									
				}
				
				ClientPlayer player = players.getPlayer(playerId);
				player.updateStats(stat);
				
				ClientEntity entity = entities.getEntity(playerId); 
				if( entity instanceof ClientPlayerEntity ) {
					player.setEntity( (ClientPlayerEntity) entity );
				}
			}
		}
		
		if(stats.teamStats!=null) {
			for(int i = 0; i < stats.teamStats.length; i++) {
				this.scoreboard.setScore(ClientTeam.fromId(stats.teamStats[i].id), stats.teamStats[i].score);
			}
		}
	}
	
	public void applyGamePartialStats(NetGamePartialStats stats) {
		if(stats.playerStats != null) {
			for(NetPlayerPartialStat stat : stats.playerStats) {
				int playerId = stat.playerId;
				
				if(players.containsPlayer(playerId)) {																						
					ClientPlayer player = players.getPlayer(playerId);
					player.updatePartialStats(stat);
					
					ClientEntity entity = entities.getEntity(playerId); 
					if( entity instanceof ClientPlayerEntity ) {
						player.setEntity( (ClientPlayerEntity) entity );
					}
				}				
			}
		}
		
		if(stats.teamStats!=null) {
			for(int i = 0; i < stats.teamStats.length; i++) {
				this.scoreboard.setScore(ClientTeam.fromId(stats.teamStats[i].id), stats.teamStats[i].score);
			}
		}
	}

	public void playerSpawned(PlayerSpawnedMessage msg) {
		ClientPlayer player = players.getPlayer(msg.playerId);
		if(player != null) {
			Vector2f spawnLocation = new Vector2f(msg.posX, msg.posY);	
			
			removeEntity(msg.playerId);
			
			ClientPlayerEntity entity = new ClientPlayerEntity(this, player, spawnLocation);			
			entity.spawned();
			
			if(localPlayer != null ) {
				if( player == localPlayer) {			
					entity.setControlledByLocalPlayer(true);
					camera.centerAroundNow(spawnLocation);
				}			
				else if ( localPlayer.getSpectatingPlayerId() == player.getId()) {
					camera.centerAroundNow(spawnLocation);
				}
			}
			
			
			entities.addEntity(msg.playerId, entity);
			entityListener.onEntityCreated(entity);
			
			Sounds.startPlaySound(Sounds.respawnSnd, msg.playerId, spawnLocation.x, spawnLocation.y);
		}
		
	}
	
	public void playerKilled(PlayerKilledMessage msg) {
		ClientPlayer player = players.getPlayer(msg.playerId);
		if(player != null) {
			
			Type meansOfDeath = Type.fromNet(msg.deathType);
			Vector2f locationOfDeath = new Vector2f(msg.posX, msg.posY);
			
			ClientPlayerEntity entity = player.getEntity();
			if(entity != null) {
				entity.setAlive(false);
				
//				BloodEmitter emitter = entity.getBloodEmitter();
//				emitter.resetTimeToLive();
//				backgroundEffects.addEffect(emitter);
				if(meansOfDeath != Type.FIRE) {
					gameEffects.addBackgroundEffect(new BloodEmitter(locationOfDeath, 4, 15200, 14000));
				}
				
				switch(meansOfDeath) {
				case EXPLOSION:
				case GRENADE: 
				case ROCKET:
				case ROCKET_LAUNCHER:					
//					backgroundEffects.addEffect(new BloodEmitter(locationOfDeath, 7, 5200, 4000));
					gameEffects.addBackgroundEffect(new GibEmitter(locationOfDeath));
					Sounds.startPlaySound(Sounds.gib, msg.playerId, locationOfDeath.x, locationOfDeath.y);
					break;
				default:
					Vector2f pos = new Vector2f(locationOfDeath);
										
					AnimatedImage anim = null;
					AnimationPool pool = null;
					switch(player.getTeam()) {
					// TODO use pool
						case ALLIES: {
							if(random.nextBoolean()) {
								anim = Art.newAlliedFrontDeathAnim();
								Vector2f.Vector2fMA(pos, entity.getFacing(), 0, pos);
							}
							else {
								anim = Art.newAlliedBackDeathAnim();								
								Vector2f.Vector2fMA(pos, entity.getFacing(), 0, pos);
							}														
							break;
						}
						case AXIS: {
							if(random.nextBoolean()) {
								anim = Art.newAxisFrontDeathAnim();
								Vector2f.Vector2fMA(pos, entity.getFacing(), 0, pos);
							}
							else {
								anim = Art.newAxisBackDeathAnim();								
								Vector2f.Vector2fMA(pos, entity.getFacing(), 0, pos);
							}
							break;
						}
						default: { // nothing							
						}
					}
					
					if(anim!=null) {
						// Objective game type keeps the dead bodies around
						boolean persist = gameType.equals(GameType.Type.OBJ);
						
						// spawn the death animation
						gameEffects.addBackgroundEffect(new AnimationEffect(anim, pos, entity.getOrientation(), persist));
						
						Sounds.startPlaySound(Sounds.die, msg.playerId, locationOfDeath.x, locationOfDeath.y);
					}
				}	
								
			}
			
			hud.postDeathMessage(player, players.getPlayer(msg.killedById), meansOfDeath);
			removeEntity(msg.playerId);
		}
	}
	
	public void roundEnded(RoundEndedMessage msg) {
		this.roundEnded = true;
		
		applyGameStats(msg.stats);
		
		scoreboard.setWinner(ClientTeam.fromId(msg.winnerTeamId));
		showScoreBoard(true);
		
		gameTimers.removeTimers();
	}
	
	public void roundStarted(RoundStartedMessage msg) {		
		gameEffects.clearEffects();
						
		this.roundEnded = false;
		
		scoreboard.setGameEnded(false);
		scoreboard.setWinner(null);
		
		this.hud.getMessageLog().clearLogs();
		
		showScoreBoard(false);
		applyFullGameState(msg.gameState);
	}
	
	public void gameEnded(GameEndedMessage msg) {
		applyGameStats(msg.stats);
		
		this.gameEnded = true;
		showScoreBoard(true);
	}
	
	public void gameReady(GameReadyMessage msg) {
		this.gameEnded = false;
		this.gameType = GameType.Type.fromNet(msg.gameState.gameType.type);
		
		hud.getObjectiveLog().log("Objective: ");
		switch(this.gameType) {
        case OBJ:
            if(this.localPlayer==null || this.localPlayer.isPureSpectator()) {
                hud.getObjectiveLog().log(this.defendingTeam.getName() + " must defend the objectives.");
            }
            else {
                if(this.localPlayer.getTeam().equals(this.defendingTeam)) {
                    hud.getObjectiveLog().log("You must defend the bomb targets from the " + this.attackingTeam.getName());
                }
                else {
                    hud.getObjectiveLog().log("You must plant bombs on the " + this.defendingTeam.getName() + " protected targets.");
                }
            }
            break;
        case TDM:
            if(this.localPlayer==null || this.localPlayer.isPureSpectator()) {
                hud.getObjectiveLog().log("The team with the most kills, wins.");
            }
            else {
                hud.getObjectiveLog().log("You must kill as many " + localPlayer.getTeam().opposingTeam().getName() + " as possible.");
            }
            break;
        default:
            break;
		
		}
	}

	public void teamTextMessage(TeamTextMessage msg) {
		ClientPlayer player = players.getPlayer(msg.playerId);
		if(player != null) {
			if(player.isAlive()) {
				hud.postMessage("(Team) " + player.getName() + ": " + msg.message);
			}
			else {
				hud.postMessage("(Team) (Dead)" + player.getName() + ": " + msg.message);
			}
		}
		else {
			hud.postMessage("(Team) : " + msg.message);
		}
	}
	
	public void textMessage(TextMessage msg) {
		ClientPlayer player = players.getPlayer(msg.playerId);
		if(player != null) {
			if(player.isAlive()) {
				hud.postMessage(player.getName() + ": " + msg.message);
			}
			else {
				hud.postMessage("(Dead) " + player.getName() + ": " + msg.message);
			}
		}
		else {
			hud.postMessage(msg.message);
		}
	}
	
	public void playerConnected(PlayerConnectedMessage msg) {
		this.players.addPlayer(new ClientPlayer(msg.name, msg.playerId));
		hud.postMessage(msg.name + " has joined the game.");
	}

	public void playerDisconnected(PlayerDisconnectedMessage msg) {
		ClientPlayer player = this.players.removePlayer(msg.playerId);
		if(player != null) {			
			removeEntity(player.getId());
			hud.postMessage(player.getName() + " has left the game.");
		}
	}
	
	private boolean removeEntity(int id) {				
		ClientEntity ent = entities.removeEntity(id);
		if(ent != null) {	
			ent.setAlive(false);		
			ent.destroy();
			
			bombTargets.remove(ent);
			vehicles.remove(ent);
			
			OnRemove onRemove = ent.getOnRemove();
			if(onRemove != null) {
				onRemove.onRemove(ent, this);
			}		
			
			entityListener.onEntityDestroyed(ent);
			
			return true;
		}
		return false;
	}
	
	
	/**
	 * Determines if the supplied entity touches any other entities.
	 * 
	 * NOTE: This isn't entirely accurate measurement because entities positions
	 * are only updated if they are within the clients visible view port.
	 * It is very possible for this function to return a false positive.
	 * 
	 * @param entity
	 * @return true if the supplied entity touches another entity
	 */
	public boolean doesEntityTouchOther(ClientEntity entity) {
		ClientEntity[] entityList = entities.getEntities();
		for(int i = 0; i < entityList.length; i++) {
			ClientEntity other = entityList[i];
			if(other != null && other != entity) {
				if(entity.isAlive()) {
					if(entity.touches(other)) {
						return true;
					}
				}
			}
		}
		
		return false;
	}
	
	/**
	 * Cleans up resources
	 */
	public void destroy() {
		this.pools.destroy();
		this.entities.clear();		
		this.bombTargets.clear();
		this.vehicles.clear();
		
		this.gameEffects.destroy();
	}

	/**
	 * @param msg
	 */
	public void playerSwitchedTeam(PlayerSwitchTeamMessage msg) {
		ClientPlayer player = this.players.getPlayer(msg.playerId);
		if(player != null) {
			ClientTeam newTeam = ClientTeam.fromId(msg.teamId);
			player.changeTeam(newTeam);
			if(newTeam == ClientTeam.NONE) {
				hud.postMessage(player.getName() + " is now spectating.");
			}
			else {
				hud.postMessage(player.getName() + " switched to the " + newTeam.getName());
			}
		}
	}

	/**
	 * @param msg
	 */
	public void playerSpeech(PlayerSpeechMessage msg) {
		ClientPlayer player = this.players.getPlayer(msg.playerId);
		if(player != null) {
			Sounds.playSpeechSound(player.getTeam().getId(), msg.speechCommand, msg.posX, msg.posY);
		}
	}
	
	/**
	 * The bomb has been planted
	 * 
	 * @param msg
	 */
	public void bombPlanted(BombPlantedMessage msg) {
		hud.postMessage("Bomb has been planted!");
		for(int i = 0; i < bombTargets.size(); i++) {
			ClientBombTarget target = bombTargets.get(i);
			if(target.getId() == msg.bombTargetId) {
				target.setBombPlanted(true);
			}
		}
	}
	
	/**
	 * The bomb has been disarmed!
	 * @param msg
	 */
	public void bombDisarmed(BombDisarmedMessage msg) {
		hud.postMessage("Bomb has been disarmed!");
		
		for(int i = 0; i < bombTargets.size(); i++) {
			ClientBombTarget target = bombTargets.get(i);
			if(target.getId() == msg.bombTargetId) {
				target.setBombPlanted(false);
			}
		}
	}

	/**
	 * @param msg
	 */
	public void bombExploded(BombExplodedMessage msg) {		
		hud.postMessage("A bomb has been destroyed!");
	}
	
	public void removeTile(TileRemovedMessage msg) {
	    map.removeDestructableTileAt(msg.x, msg.y);
	}
	
	public void removeTiles(TilesRemovedMessage msg) {
	    map.removeDestructableTilesAt(msg.tiles);
	}
}
 