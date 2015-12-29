/*
 * see license.txt 
 */
package seventh.client;

import java.util.ArrayList;
import java.util.List;

import seventh.client.gfx.Camera;
import seventh.client.sfx.Sounds;
import seventh.game.PlayerEntity.Keys;
import seventh.map.Map;
import seventh.map.Tile;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Geom;
import seventh.shared.TimeStep;
import seventh.shared.Updatable;

/**
 * During spectating, the spectator is free to control the camera in a freeform fashion.
 * 
 * @author Tony
 *
 */
public class CameraController implements Updatable {

    private static final Vector2f GAME_SPEED = new Vector2f(130, 130);
    private static final Vector2f FREEFORM_SPEED = new Vector2f(330, 330);
    
    private static final int freeformSpeed=220, fastFreeformSpeed=280;
    
	private Map map;
	private Camera camera;
	private Vector2f cameraCenterAround;
	private Vector2f cameraDest;
	
	private ClientPlayer localPlayer;
	
	private Vector2f playerVelocity;
	
	private long nextFOWUpdate;
	private List<Tile> fowTiles;
	
	
	private Rectangle cameraShakeBounds;
	
	private boolean isCameraRoaming, isFastCamera;
	private int previousKeys;
	
	private ClientGame game;
	
	/**
	 * @param game
	 */
	public CameraController(ClientGame game) {
	    this.game = game;
		this.camera = game.getCamera();
		this.map = game.getMap();
		this.localPlayer = game.getLocalPlayer();

		this.fowTiles = new ArrayList<Tile>();
		
		this.cameraCenterAround = new Vector2f();
		this.cameraDest = new Vector2f();
		this.cameraShakeBounds = new Rectangle(600, 600);
		
		this.playerVelocity = new Vector2f();

		setGameCameraSpeed();
	}
	
	/**
	 * Sets the camera speed to game mode
	 */
	private void setGameCameraSpeed() {
		camera.setMovementSpeed(GAME_SPEED);
	}
	
	/**
	 * Sets the camera speed to free form mode
	 */
	private void setFreeformCameraSpeed() {
		camera.setMovementSpeed(FREEFORM_SPEED);
	}

	
	/**
	 * Shake the camera relative to the force of 'sourcePosition'.
	 * @param sourcePosition the position of the source of the screen shake.  This would be the position
	 * of a bomb going off. This impacts the shake amount (the closer, the more force applied to
	 * the camera).
	 */
	public void shakeCamera(Vector2f sourcePosition) {
		if(this.localPlayer.isAlive()) {
			Vector2f centerPos = this.localPlayer.getEntity().getCenterPos();
			cameraShakeBounds.centerAround(centerPos);
			if(cameraShakeBounds.contains(sourcePosition)) {
				float force = Vector2f.Vector2fDistanceSq(centerPos, sourcePosition);
				camera.shake(300, Math.max(900 - force/100, 100));						
			}
		}
	}
	
	/**
	 * Video graphics have been reloaded
	 * 
	 * @param camera
	 */
	public void onVideoReload(Camera camera) {
		this.camera = camera;
	}
	
	/**
	 * @return the isCameraRoaming
	 */
	public boolean isCameraRoaming() {
		return isCameraRoaming && this.localPlayer.isPureSpectator();
	}
	
	/* (non-Javadoc)
	 * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		updateCameraPosition(timeStep);
	}
	
	
	/**
	 * Applies the player input
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

		if(Keys.SPRINT.isDown(keys)) {
		    if(isCameraRoaming()) {
//		        setFastFreeformCameraSpeed();
		    	isFastCamera = true;
		    }
		}
		else {
		    if(isCameraRoaming()) {
		        isFastCamera = false;
		    }
		}
		
		if(this.localPlayer.isPureSpectator()) {
			if(Keys.FIRE.isDown(previousKeys) && !Keys.FIRE.isDown(keys)) {
				this.isCameraRoaming = !this.isCameraRoaming;
				if(this.isCameraRoaming) {
					enterFreeformCameraMode();
				}
				else {
					enterFollowPlayerCameraMode();
				}
			}			
		}
		
		this.previousKeys = keys;
	}
	
	private void enterFreeformCameraMode() {
		setFreeformCameraSpeed();
		
		this.localPlayer.stopSpectatingPlayer();
		
		// center the camera position
		cameraDest.set(camera.getPosition());
		cameraDest.x += camera.getViewPort().width/2;
		cameraDest.y += camera.getViewPort().height/2;
		
		
		// remove Fog of War
		Geom.clearMask(fowTiles, map);
	}
	
	private void enterFollowPlayerCameraMode() {
		setGameCameraSpeed();
	}
	
	/**
	 * Updates the camera roaming position
	 * 
	 * @param timeStep
	 */
	private void updateCameraForRoamingMovements(TimeStep timeStep) {
		final int movementSpeed = isFastCamera ? fastFreeformSpeed : freeformSpeed;
		if(playerVelocity.lengthSquared() > 0) {
			Vector2f pos = cameraDest;		
			Rectangle bounds = new Rectangle(camera.getViewPort());
						
			bounds.setLocation(pos);
			
			double dt = timeStep.asFraction();
			int newX = (int)Math.round(pos.x + playerVelocity.x * movementSpeed * dt);
			int newY = (int)Math.round(pos.y + playerVelocity.y * movementSpeed * dt);					
			
			
			bounds.x = newX;
			if( map.checkBounds(bounds.x, bounds.y) || 
				((bounds.x < bounds.width/2) || (bounds.y < bounds.height/2)) ||
				map.checkBounds(bounds.x + bounds.width/2, bounds.y + bounds.height/2) ) {
				bounds.x = (int)pos.x;
			}
					
			
			bounds.y = newY;
			if( map.checkBounds(bounds.x, bounds.y) ||
				((bounds.x < bounds.width/2) || (bounds.y < bounds.height/2)) ||
				map.checkBounds(bounds.x + bounds.width/2, bounds.y + bounds.height/2) ) {
				bounds.y = (int)pos.y;
			}
						
			pos.x = bounds.x;
			pos.y = bounds.y;
		
			cameraCenterAround.set(pos);
			
			Vector2f.Vector2fRound(cameraCenterAround, cameraCenterAround);;
			camera.centerAround(cameraCenterAround);
			Sounds.setPosition(cameraCenterAround);
		}
	}
	
	
	/**
	 * Updates the camera relative to the players movements
	 * 
	 * @param timeStep
	 */
	private void updateCameraForPlayerMovements(TimeStep timeStep) {
		if(this.localPlayer.isAlive()||this.localPlayer.isSpectating()) {
			ClientControllableEntity entity = game.getLocalPlayerFollowingEntity();
																											
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
					Geom.calculateLineOfSight(fowTiles, entity.getCenterPos(), entity.getFacing(), entity.getLineOfSight(), map, entity.getHeightMask());
					Geom.addFadeEffect(map, fowTiles);
					
					/* only calculate every 100 ms */
					nextFOWUpdate = 100;					
				}			
			}
		}
	}
	
	/**
	 * Updates the camera positioning
	 * 
	 * @param timeStep
	 */
	private void updateCameraPosition(TimeStep timeStep) {
		
		if(isCameraRoaming()) {
			updateCameraForRoamingMovements(timeStep);
		}
		else {						
			updateCameraForPlayerMovements(timeStep);
		}
	}
}
