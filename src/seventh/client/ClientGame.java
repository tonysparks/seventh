/*
 * see license.txt 
 */
package seventh.client;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

import seventh.client.ClientEntity.OnRemove;
import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Camera2d;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.FrameBufferRenderable;
import seventh.client.gfx.ImageBasedLightSystem;
import seventh.client.gfx.LightSystem;
import seventh.client.gfx.particle.AnimationEffect;
import seventh.client.gfx.particle.BloodEmitter;
import seventh.client.gfx.particle.Effect;
import seventh.client.gfx.particle.Effects;
import seventh.client.gfx.particle.Emitter;
import seventh.client.gfx.particle.GibEmitter;
import seventh.client.gfx.particle.RocketTrailEmitter;
import seventh.client.sfx.Sounds;
import seventh.client.weapon.ClientBomb;
import seventh.game.Entity;
import seventh.game.Entity.Type;
import seventh.game.Game;
import seventh.game.PlayerEntity.Keys;
import seventh.game.net.NetEntity;
import seventh.game.net.NetGamePartialStats;
import seventh.game.net.NetGameState;
import seventh.game.net.NetGameStats;
import seventh.game.net.NetGameTypeInfo;
import seventh.game.net.NetGameUpdate;
import seventh.game.net.NetPlayerPartialStat;
import seventh.game.net.NetPlayerStat;
import seventh.game.net.NetSound;
import seventh.game.type.GameType;
import seventh.map.Map;
import seventh.map.Tile;
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
import seventh.network.messages.PlayerSwitchTeamMessage;
import seventh.network.messages.RoundEndedMessage;
import seventh.network.messages.RoundStartedMessage;
import seventh.network.messages.TeamTextMessage;
import seventh.network.messages.TextMessage;
import seventh.shared.Cons;
import seventh.shared.DebugDraw;
import seventh.shared.Geom;
import seventh.shared.TimeStep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;

/**
 * The {@link ClientGame} is responsible for rendering the client's view of the game world.  The view
 * is generated from messages from the server.
 * 
 * @author Tony
 *
 */
public class ClientGame {	
	
	
	private SeventhGame app;	
	private Map map;
	
	private ClientPlayer localPlayer;
	
	private java.util.Map<Integer, ClientPlayer> players;
	private java.util.Map<Integer, ClientEntity> entities;	
	private List<ClientEntity> entityList;
	private List<ClientEntity> deadEntities;
	private List<ClientBombTarget> bombTargets;
	private List<ClientVehicle> vehicles;
//	private List<ClientPlayerEntity> playerEntities;
	
	private List<FrameBufferRenderable> frameBufferRenderables;
	
	private long nextFOWUpdate;
	private List<Tile> fowTiles;
	
	private long gameClock;
				
	private Camera camera;
	private Vector2f cameraCenterAround;
	
	private Scoreboard scoreboard;
	private Effects backgroundEffects, foregroundEffects;
	
	
	private boolean gameEnded, roundEnded;
	private Hud hud;
	
	private Rectangle cameraShakeBounds;
	private Rectangle cacheRect;
	private Random random;
	
	private GameType.Type gameType;
		
	private long rconToken;
	
	private Vector2f playerVelocity;
	private LightSystem lightSystem;
	
	private ClientEntityListener entityListener;
	
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
	 * @throws Exception 
	 */
	public ClientGame(SeventhGame app, java.util.Map<Integer, ClientPlayer> players, Map map, int localPlayerId) throws Exception {
		this.app = app;		
		this.players = players;
		this.map = map;
								
		this.localPlayer = players.get(localPlayerId);
		
		this.scoreboard = new Scoreboard(this);
		this.entities = new HashMap<Integer, ClientEntity>();
		this.entityList = new ArrayList<ClientEntity>();
		this.deadEntities = new ArrayList<ClientEntity>();
//		this.playerEntities = new ArrayList<ClientPlayerEntity>();
		this.bombTargets = new ArrayList<ClientBombTarget>();
		this.vehicles = new ArrayList<ClientVehicle>();
		
		this.frameBufferRenderables = new ArrayList<>();
		
		this.fowTiles = new ArrayList<Tile>();		
		this.cameraCenterAround = new Vector2f();
		this.backgroundEffects = new Effects();
		this.foregroundEffects = new Effects();
				
		this.camera = newCamera(map.getMapWidth(), map.getMapHeight());
		this.cameraShakeBounds = new Rectangle(600, 600);
		
		this.playerVelocity = new Vector2f();
		
		this.hud = new Hud(this);
		this.random = new Random();
		this.gameType = GameType.Type.TDM;

		this.cacheRect = new Rectangle();
	
		this.lightSystem = new ImageBasedLightSystem();		
		this.frameBufferRenderables.add(lightSystem);
		
		this.entityListener = lightSystem.getClientEntityListener();		
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
		this.backgroundEffects.addEffect(effect);
	}
	
	/**
	 * Adds a foreground effect
	 * 
	 * @param effect
	 */
	public void addForegroundEffect(Effect effect) {
		this.foregroundEffects.addEffect(effect);
	}
	
	/**
	 * Updates the special effects, etc.
	 * 
	 * @param timeStep
	 */
	public void update(TimeStep timeStep) {
		this.lightSystem.update(timeStep);
		
		long gameClock = timeStep.getGameClock();
		this.deadEntities.clear();
		
		int size = this.entityList.size();
		for(int i = 0; i < size; i++) {
			ClientEntity ent = this.entityList.get(i);
			ent.update(timeStep);
			
			if(ent.killIfOutdated(gameClock)) {
				this.deadEntities.add(ent);
			}
		}

		size = this.deadEntities.size();
		for(int i = 0; i < size; i++) {
			ClientEntity ent = deadEntities.get(i);
			removeEntity(ent.getId());
		}
		
		backgroundEffects.update(timeStep);
		foregroundEffects.update(timeStep);
		
		size = frameBufferRenderables.size();
		for(int i = 0; i < size; i++) {
			FrameBufferRenderable r = this.frameBufferRenderables.get(i);
			r.update(timeStep);
		}
		
		updateFow(timeStep);
		
		map.update(timeStep);		
		camera.update(timeStep);
		hud.update(timeStep);			
	}
	
	
	/**
	 * Handles calculating the local players Fog Of War
	 * @param timeStep
	 */
	private void updateFow(TimeStep timeStep) {
		if(this.localPlayer.isAlive()||this.localPlayer.isSpectating()) {						
			ClientControllableEntity entity = null;
			if(this.localPlayer.isAlive()) {				
				entity = this.localPlayer.getEntity();				
			}
			else if(this.players.containsKey(this.localPlayer.getSpectatingPlayerId())) { 
				entity = this.players.get(this.localPlayer.getSpectatingPlayerId()).getEntity();
			}
			
																								
			if(entity!=null) {
				if( entity.isOperatingVehicle() ) {
					entity = entity.getVehicle();
				}
				
				entity.movementPrediction(map, timeStep, playerVelocity);
				
				cameraCenterAround.set(entity.getPos());
				Vector2f.Vector2fRound(cameraCenterAround, cameraCenterAround);;
				camera.centerAround(cameraCenterAround);
		
				Sounds.setPosition(cameraCenterAround);
				
				/* Calculates the Fog Of War
				 */
				nextFOWUpdate -= timeStep.getDeltaTime();
				if(nextFOWUpdate <= 0) {
					fowTiles = Geom.calculateLineOfSight(fowTiles, entity.getCenterPos(), entity.getFacing(), entity.getLineOfSight(), map, entity.getHeightMask());
					Geom.addFadeEffect(map, fowTiles);
					
					/* only calculate every 100 ms */
					nextFOWUpdate = 100;					
				}			
				
				
			}
		}
	}
	
	/**
	 * Renders to the frame buffer
	 * @param canvas
	 */
	public void renderFrameBuffer(Canvas canvas) {
		int size = this.frameBufferRenderables.size();
		if(size>0) 
		{
			canvas.fboBegin();
			canvas.setDefaultTransforms();
			canvas.setShader(null);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			canvas.begin();
			for(int i = 0; i < size; i++) {
				FrameBufferRenderable r = this.frameBufferRenderables.get(i);
				r.frameBufferRender(canvas, camera);
			}
			canvas.end();
			canvas.fboEnd();
			
		
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
			canvas.setDefaultTransforms();
			canvas.setShader(null);
			
			for(int i = 0; i < size; i++) {
				FrameBufferRenderable r = this.frameBufferRenderables.get(i);
				r.render(canvas, camera, 0);
			}
		}
	}
		
	/**
	 * Renders the game
	 * 
	 * @param canvas
	 */
	public void render(Canvas canvas) {				
		renderFrameBuffer(canvas);
						
		canvas.begin();		
		map.render(canvas, camera, 0);
		canvas.end();
		
		backgroundEffects.render(canvas, camera, 0);
		
		int size = this.entityList.size();		
		for(int i = 0; i < size; i++) {
			ClientEntity entity = this.entityList.get(i);
			entity.render(canvas, camera, 0);

			boolean debug = false;
			if(debug) {
				debugRenderEntity(canvas, entity);								
			}
		}						
		
		//this.lightSystem.render(canvas, camera, 0);
		
		foregroundEffects.render(canvas, camera, 0);
		map.renderForeground(canvas, camera, 0);
		
		canvas.setColor(0, 45);
		map.renderSolid(canvas, camera, 0);
		
		lightSystem.render(canvas, camera, 0);
		
		DebugDraw.enable(false);
		DebugDraw.render(canvas, camera);
		
		canvas.setShader(null);
		hud.render(canvas, camera, 0);
		
	}
	
	/**
	 * Renders debug information for an entity
	 * 
	 * @param canvas
	 * @param entity
	 */
	private void debugRenderEntity(Canvas canvas, ClientEntity entity) {
		Vector2f cameraPos = camera.getPosition();		
		int debugColor = 0xa300aa00;
		canvas.drawRect( (int)(entity.bounds.x-cameraPos.x), (int)(entity.bounds.y-cameraPos.y)
				   ,entity.bounds.width, entity.bounds.height, debugColor);
		
		Vector2f center = entity.getCenterPos();				
		Tile tile = map.getWorldTile(0, (int)center.x, (int)center.y);
		if(tile != null) {
			canvas.fillRect(tile.getX()-(int)cameraPos.x, tile.getY()-(int)cameraPos.y, tile.getWidth(), tile.getHeight(), debugColor);
			canvas.fillRect(tile.getX()-(int)cameraPos.x, tile.getY()-(int)cameraPos.y, 2, 2, 0xff00ff00);
			canvas.drawString(center + " : (" + tile.getX() + "," + tile.getY() + ")"
					, (int)center.x-(int)cameraPos.x-20, (int)center.y-(int)cameraPos.y+50, debugColor);
		}
		
		canvas.fillRect((int)center.x-(int)cameraPos.x, (int)center.y-(int)cameraPos.y, 2, 2, 0xff00ff00);
	}
	
	
	/**
	 * @return the rconToken
	 */
	public long getRconToken() {
		return rconToken;
	}
	
	/**
	 * @param rconToken the rconToken to set
	 */
	public void setRconToken(long rconToken) {
		this.rconToken = rconToken;
	}
	
	/**
	 * @return the clients ping
	 */
	public int getPing() {
		return this.localPlayer.getPing();
	}
	
	/**
	 * @return the lightSystem
	 */
	public LightSystem getLightSystem() {
		return lightSystem;
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
	public java.util.Map<Integer, ClientPlayer> getPlayers() {
		return players;
	}
	
	/**
	 * @return the localPlayer
	 */
	public ClientPlayer getLocalPlayer() {
		return localPlayer;
	}
	
	/**
	 * @return the app
	 */
	public SeventhGame getApp() {
		return app;
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
	 * Applies the players input.  This is used for
	 * client side prediction.
	 * 
	 * @param keys
	 */
	public void applyPlayerInput(int keys) {
		if(Keys.UP.isDown(keys)) {
			playerVelocity.y = -1;			 
		}
		else if(Keys.DOWN.isDown(keys)) {
			playerVelocity.y = 1;
		}
		else {
			playerVelocity.y = 0;
		}
		
		if(Keys.LEFT.isDown(keys)) {
			playerVelocity.x = -1;
		}
		else if (Keys.RIGHT.isDown(keys)) {
			playerVelocity.x = 1;
		}
		else {
			playerVelocity.x = 0;
		}
				
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
		if(this.entities.containsKey(ent.id)) {
			return;
		}
		
		ClientEntity entity = null;
		
		Type type = Type.fromNet(ent.type);
		Vector2f pos = new Vector2f(ent.posX, ent.posY);
		switch(type) {
			case PLAYER_PARTIAL: {
				ClientPlayer player = this.players.get(ent.id);
				if(player!=null) {					
					entity = new ClientPlayerEntity(this, player, pos);					
				}
				break;
			}
			case PLAYER: {
				ClientPlayer player = this.players.get(ent.id);
				if(player!=null) {					
					entity = new ClientPlayerEntity(this, player, pos);				
				}
				break;
			}
			case BULLET: {
				entity = new ClientBullet(this, pos);
				break;
			}
			case ROCKET: {
				entity = new ClientRocket(this, pos);
				
				Emitter rocketTrail = new RocketTrailEmitter(pos, 4000, 0);				
				rocketTrail.attachTo(entity);
				rocketTrail.start();
				
				foregroundEffects.addEffect(rocketTrail);
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
				}
				else {
					entity = new ClientFire(this, pos);	
				}
				
				// if an explosion happens,
				// shake the camera
				if(this.localPlayer.isAlive()) {
					Vector2f centerPos = this.localPlayer.getEntity().getCenterPos();
					cameraShakeBounds.centerAround(centerPos);
					if(cameraShakeBounds.contains(pos)) {
						float force = Vector2f.Vector2fDistanceSq(centerPos, pos);
						camera.shake(300, Math.max(900 - force/100, 100));						
					}
				}
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
			default: {
				Cons.println("Unknown type of entity: " + type.name());
			}
		}
		
		if(entity != null) {
			entity.updateState(ent, gameClock);
			entities.put(ent.id, entity);
			
			entityListener.onEntityCreated(entity);
			
			/* Hack to render players,etc.
			 * above dropped weapons and bombs
			 */
			if(entity.isBackgroundObject()) {
				entityList.add(0, entity);
			}
			else {
				entityList.add(entity);
			}
		}
	}
	

	public void applyFullGameState(NetGameState gs) {
		
		for(ClientEntity ent : this.entityList) {
			if(ent!=null) {
				OnRemove remove = ent.getOnRemove();
				if(remove!=null) {
					remove.onRemove(ent, this);
				}
			}
		}
		
		this.entities.clear();
		this.entityList.clear();
		this.bombTargets.clear();
		this.vehicles.clear();
		this.lightSystem.removeAllLights();
				
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
					if(entities.containsKey(netEnt.id)) {
						ClientEntity ent = entities.get(netEnt.id);
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
					
					if( i < Game.MAX_PERSISTANT_ENTITIES) {
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
			int size = netUpdate.sounds.length;
			for(int i = 0; i < size; i++) {
				NetSound snd = netUpdate.sounds[i];
				if(snd!=null) {
					Sounds.playSound(snd, snd.posX, snd.posY);
				}
			}
		}
		
		if(netUpdate.spectatingPlayerId > -1) {
			int previousSpec = localPlayer.getSpectatingPlayerId();
			localPlayer.setSpectatingPlayerId(netUpdate.spectatingPlayerId);
			if(previousSpec != netUpdate.spectatingPlayerId) {
				ClientEntity ent = this.entities.get(netUpdate.spectatingPlayerId);
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
				
				if(!players.containsKey(playerId)) {
					ClientPlayer player = new ClientPlayer(stat.name, playerId);
					players.put(playerId, player);									
				}
				
				ClientPlayer player = players.get(playerId);
				player.updateStats(stat);
				
				ClientEntity entity = entities.get(playerId); 
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
				
				if(players.containsKey(playerId)) {																						
					ClientPlayer player = players.get(playerId);
					player.updatePartialStats(stat);
					
					ClientEntity entity = entities.get(playerId); 
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
		ClientPlayer player = players.get(msg.playerId);
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
			
			
			entities.put(msg.playerId, entity);
			entityList.add(entity);
//			playerEntities.add(entity);
			entityListener.onEntityCreated(entity);
			
			Sounds.startPlaySound(Sounds.respawnSnd, msg.playerId, spawnLocation.x, spawnLocation.y);
		}
		
	}
	
	public void playerKilled(PlayerKilledMessage msg) {
		ClientPlayer player = players.get(msg.playerId);
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
					backgroundEffects.addEffect(new BloodEmitter(locationOfDeath, 4, 15200, 14000));
				}
				
				switch(meansOfDeath) {
				case EXPLOSION:
				case GRENADE: 
				case ROCKET:
				case ROCKET_LAUNCHER:					
//					backgroundEffects.addEffect(new BloodEmitter(locationOfDeath, 7, 5200, 4000));
					backgroundEffects.addEffect(new GibEmitter(locationOfDeath));
					Sounds.startPlaySound(Sounds.gib, msg.playerId, locationOfDeath.x, locationOfDeath.y);
					break;
				default:
					Vector2f pos = new Vector2f(locationOfDeath);
										
					AnimatedImage anim = null;
					switch(player.getTeam()) {
						case ALLIES: {
							if(random.nextBoolean()) {
								anim = Art.newAlliedFrontDeathAnim();
								Vector2f.Vector2fMA(pos, entity.getFacing(), 25, pos);
							}
							else {
								anim = Art.newAlliedBackDeathAnim();
								Vector2f.Vector2fMA(pos, entity.getFacing(), -35, pos);
							}														
							break;
						}
						case AXIS: {
							if(random.nextBoolean()) {
								anim = Art.newAxisFrontDeathAnim();
								Vector2f.Vector2fMA(pos, entity.getFacing(), 25, pos);
							}
							else {
								anim = Art.newAxisBackDeathAnim();
								Vector2f.Vector2fMA(pos, entity.getFacing(), -35, pos);
							}
							break;
						}
						default: { // nothing							
						}
					}
					
					if(anim!=null) {
						backgroundEffects.addEffect(new AnimationEffect(anim, pos, entity.getOrientation(), gameType.equals(GameType.Type.OBJ)));					
						Sounds.startPlaySound(Sounds.die, msg.playerId, locationOfDeath.x, locationOfDeath.y);
					}
				}	
								
			}
			
			hud.postDeathMessage(player, players.get(msg.killedById), meansOfDeath);
			removeEntity(msg.playerId);
		}
	}
	
	public void roundEnded(RoundEndedMessage msg) {
		this.roundEnded = true;
		
		applyGameStats(msg.stats);
		
		scoreboard.setWinner(ClientTeam.fromId(msg.winnerTeamId));
		showScoreBoard(true);				
	}
	
	public void roundStarted(RoundStartedMessage msg) {
		backgroundEffects.clearEffects();
		foregroundEffects.clearEffects();
						
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
	}

	public void teamTextMessage(TeamTextMessage msg) {
		ClientPlayer player = players.get(msg.playerId);
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
		ClientPlayer player = players.get(msg.playerId);
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
		this.players.put(msg.playerId, new ClientPlayer(msg.name, msg.playerId));
		hud.postMessage(msg.name + " has joined the game.");
	}

	public void playerDisconnected(PlayerDisconnectedMessage msg) {
		ClientPlayer player = this.players.remove(msg.playerId);
		if(player != null) {			
			removeEntity(player.getId());
			hud.postMessage(player.getName() + " has left the game.");
		}
	}
	
	private boolean removeEntity(int id) {
		ClientEntity ent = entities.remove(id);
		if(ent != null) {	
			
			ent.setAlive(false);			
			entityList.remove(ent);
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
	 * Cleans up resources
	 */
	public void destroy() {
		this.entities.clear();
		this.entityList.clear();
		this.bombTargets.clear();
		this.vehicles.clear();
//		this.playerEntities.clear();
		
		this.lightSystem.destroy();
		
		this.backgroundEffects.clearEffects();
		this.foregroundEffects.clearEffects();
	}

	/**
	 * @param msg
	 */
	public void playerSwitchedTeam(PlayerSwitchTeamMessage msg) {
		ClientPlayer player = this.players.get(msg.playerId);
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
}
