/*
 * see license.txt 
 */
package seventh.client;


import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import seventh.client.ClientEntity.OnRemove;
import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Camera2d;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.ExplosionEffect;
import seventh.client.gfx.ExplosionEffectShader;
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
import seventh.game.PlayerEntity.Keys;
import seventh.game.net.NetEntity;
import seventh.game.net.NetExplosion;
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
import seventh.shared.SeventhConstants;
import seventh.shared.TimeStep;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.glutils.ShaderProgram;

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
	
	private final ClientEntities entities;
	private final ClientPlayers players;
	
	private final ClientBulletPool bulletPool;
	
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
	
	
	private final Effects backgroundEffects, foregroundEffects;
	private final LightSystem lightSystem;
	private final ExplosionEffect explosions;

	private final List<FrameBufferRenderable> frameBufferRenderables;
	private final Sprite frameBufferSprite;
	
	private final Rectangle cacheRect;
	private final Random random;
	

	private long rconToken;	
	
	
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
	public ClientGame(SeventhGame app, ClientPlayers players, Map map, int localPlayerId) throws Exception {
		this.app = app;		
		this.players = players;
		this.map = map;
														
		this.scoreboard = new Scoreboard(this);
		
		this.localPlayer = players.getPlayer(localPlayerId);		
		this.entities = new ClientEntities(SeventhConstants.MAX_ENTITIES);	
		this.renderingOrderEntities = new ClientEntity[SeventhConstants.MAX_ENTITIES];		
		
		this.bombTargets = new ArrayList<ClientBombTarget>();
		this.vehicles = new ArrayList<ClientVehicle>();
		
		this.frameBufferRenderables = new ArrayList<>();
		
		this.backgroundEffects = new Effects();
		this.foregroundEffects = new Effects();
				
		this.camera = newCamera(map.getMapWidth(), map.getMapHeight());
		this.cameraController = new CameraController(this);
		
		
		this.hud = new Hud(this);
		this.random = new Random();
		this.gameType = GameType.Type.TDM;

		this.cacheRect = new Rectangle();
	
		this.lightSystem = new ImageBasedLightSystem();		
		this.frameBufferRenderables.add(lightSystem);
		
		this.entityListener = lightSystem.getClientEntityListener();		
		
		this.bulletPool = new ClientBulletPool(this, SeventhConstants.MAX_ENTITIES);
		
		
		this.explosions = new ExplosionEffect(15, 800, 0.6f);
		this.frameBufferSprite = new Sprite();
//		fullScreenQuad = new Sprite(TextureUtil.createImage(getApp().getScreenWidth(), getApp().getScreenHeight()));
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

		
		backgroundEffects.update(timeStep);
		foregroundEffects.update(timeStep);
		explosions.update(timeStep);
		
		size = frameBufferRenderables.size();
		for(int i = 0; i < size; i++) {
			FrameBufferRenderable r = this.frameBufferRenderables.get(i);
			r.update(timeStep);
		}
		
		cameraController.update(timeStep);
		
		map.update(timeStep);		
		camera.update(timeStep);
		hud.update(timeStep);			
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
			
			for(int i = 0; i < this.frameBufferRenderables.size(); ) {
				FrameBufferRenderable r = this.frameBufferRenderables.get(i);
				if(r.isExpired()) {
					this.frameBufferRenderables.remove(i);
				}
				else {
					r.render(canvas, camera, 0);
					i++;
				}
			}
		}
	}
		
	public void render(Canvas canvas) {
		
		canvas.fboBegin();
		{
			canvas.setDefaultTransforms();
			canvas.setShader(null);
			Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		
			canvas.begin();
			int size = this.frameBufferRenderables.size();
			for(int i = 0; i < size; i++) {
				FrameBufferRenderable r = this.frameBufferRenderables.get(i);
				r.frameBufferRender(canvas, camera);
			}
			canvas.end();
			
			for(int i = 0; i < this.frameBufferRenderables.size(); ) {
				FrameBufferRenderable r = this.frameBufferRenderables.get(i);
				if(r.isExpired()) {
					this.frameBufferRenderables.remove(i);
				}
				else {
					r.render(canvas, camera, 0);
					i++;
				}
			}
			
			canvas.setShader(null);
			renderWorld(canvas);
		}
		canvas.fboEnd();
		
		
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);
		canvas.setDefaultTransforms();
		//canvas.setShader(null);

		frameBufferSprite.setRegion(canvas.getFrameBuffer());
		
		canvas.begin();
		{
			canvas.getFrameBuffer().bind();
			{
				ShaderProgram shader = ExplosionEffectShader.getInstance().getShader();
				
				canvas.setShader(shader);
				canvas.drawImage(frameBufferSprite, 0, 0, 0x0);
			}
		}
		canvas.end();

		canvas.setShader(null);
		DebugDraw.enable(false);
		DebugDraw.render(canvas, camera);

		
		hud.render(canvas, camera, 0);
	}
	
	private void renderWorld(Canvas canvas) {
		canvas.begin();		
		map.render(canvas, camera, 0);
		canvas.end();
		
		backgroundEffects.render(canvas, camera, 0);
		
				
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
				
		foregroundEffects.render(canvas, camera, 0);
		map.renderForeground(canvas, camera, 0);
		
		canvas.setColor(0, 45);
		map.renderSolid(canvas, camera, 0);
		
		lightSystem.render(canvas, camera, 0);		
	}
	
	/**
	 * Renders the game
	 * 
	 * @param canvas
	 */
	public void render2(Canvas canvas) {				
		renderFrameBuffer(canvas);
						
		canvas.begin();		
		map.render(canvas, camera, 0);
		canvas.end();
		
		backgroundEffects.render(canvas, camera, 0);
		
				
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
		
		//this.lightSystem.render(canvas, camera, 0);
		
		foregroundEffects.render(canvas, camera, 0);
		map.renderForeground(canvas, camera, 0);
		
		canvas.setColor(0, 45);
		map.renderSolid(canvas, camera, 0);
		
		lightSystem.render(canvas, camera, 0);
		
		DebugDraw.enable(true);
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
//	private void debugRenderEntity(Canvas canvas, ClientEntity entity) {
//		Vector2f cameraPos = camera.getPosition();		
//		int debugColor = 0xa300aa00;
//		canvas.drawRect( (int)(entity.bounds.x-cameraPos.x), (int)(entity.bounds.y-cameraPos.y)
//				   ,entity.bounds.width, entity.bounds.height, debugColor);
//		
//		Vector2f center = entity.getCenterPos();				
//		Tile tile = map.getWorldTile(0, (int)center.x, (int)center.y);
//		if(tile != null) {
//			canvas.fillRect(tile.getX()-(int)cameraPos.x, tile.getY()-(int)cameraPos.y, tile.getWidth(), tile.getHeight(), debugColor);
//			canvas.fillRect(tile.getX()-(int)cameraPos.x, tile.getY()-(int)cameraPos.y, 2, 2, 0xff00ff00);
//			canvas.drawString(center + " : (" + tile.getX() + "," + tile.getY() + ")"
//					, (int)center.x-(int)cameraPos.x-20, (int)center.y-(int)cameraPos.y+50, debugColor);
//		}
//		
//		canvas.fillRect((int)center.x-(int)cameraPos.x, (int)center.y-(int)cameraPos.y, 2, 2, 0xff00ff00);
//	}
	
	
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
				entity = this.bulletPool.alloc(ent.id, pos);
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
					
					/* create the explosion effect */
					NetExplosion explosion = (NetExplosion)ent;
					
					/* convert to UV coordinates */
					Vector2f uvPos = new Vector2f(pos);
					uvPos.x = (pos.x - camera.getPosition().x) / app.getScreenWidth();
					uvPos.y = 1f - (pos.y - camera.getPosition().y) / app.getScreenHeight();
					
					/* limit the explosion per player */
					this.explosions.activate(explosion.ownerId, uvPos);
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
						backgroundEffects.addEffect(new AnimationEffect(anim, pos, entity.getOrientation(), gameType.equals(GameType.Type.OBJ)));					
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
//			System.out.println("Removing entity: " + id); 
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
		this.bulletPool.clear();
		this.entities.clear();		
		this.bombTargets.clear();
		this.vehicles.clear();
		
		this.lightSystem.destroy();
		
		this.backgroundEffects.clearEffects();
		this.foregroundEffects.clearEffects();
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
