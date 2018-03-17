/*
 * see license.txt 
 */
package seventh.client.inputs;

import java.util.ArrayList;
import java.util.List;

import seventh.client.ClientGame;
import seventh.client.ClientPlayer;
import seventh.client.ClientSeventhConfig;
import seventh.client.entities.ClientControllableEntity;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Cursor;
import seventh.client.sfx.Sounds;
import seventh.game.entities.PlayerEntity.Keys;
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
    
    private static final int freeformSpeed=750, fastFreeformSpeed=800;
    
    private Map map;
    private Camera camera;
    private Vector2f cameraCenterAround;
    private Vector2f cameraDest;
    private Vector2f previousCameraPos;
    private ClientPlayer localPlayer;
    
    private Vector2f playerVelocity;
    
    private long nextFOWUpdate;
    private List<Tile> fowTiles;
    
    
    private Rectangle cameraShakeBounds;
    private Rectangle bounds;
    
    private int viewportWidth, viewportHeight;
    
    private boolean isCameraRoaming, isFastCamera;
    private boolean isCameraActive;    
    private boolean isIronSights;
    
    
    private int previousKeys;
    private Cursor cursor;
    
    private ClientGame game;
    private ClientSeventhConfig config;
    
    /**
     * @param game
     */
    public CameraController(ClientGame game) {
        this.game = game;
        this.config = game.getConfig();
        this.camera = game.getCamera();
        this.map = game.getMap();
        this.localPlayer = game.getLocalPlayer();
        this.cursor = game.getApp().getUiManager().getCursor();

        this.fowTiles = new ArrayList<Tile>();
        
        this.previousCameraPos = new Vector2f();
        this.cameraCenterAround = new Vector2f();
        this.cameraDest = new Vector2f();        
        this.cameraShakeBounds = new Rectangle(600, 600);
        
        this.playerVelocity = new Vector2f();
                        
        this.bounds = new Rectangle();
        this.viewportWidth = this.camera.getViewPort().width;
        this.viewportHeight = this.camera.getViewPort().height;
        
        this.isCameraActive = true;
        
        setGameCameraSpeed();
    }
    
    /**
     * @return the isCameraActive
     */
    public boolean isCameraActive() {
        return isCameraActive;
    }
    
    /**
     * @param isCameraActive the isCameraActive to set
     */
    public void setCameraActive(boolean isCameraActive) {
        this.isCameraActive = isCameraActive;
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
                float force = Vector2f.Vector2fDistance(centerPos, sourcePosition);                
                force = Math.max(130 - force/10, 10);                
                camera.addShake(300, force);
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
        return isCameraRoaming && (this.localPlayer.isPureSpectator()||this.localPlayer.isCommander());
    }
    
    /* (non-Javadoc)
     * @see seventh.shared.Updatable#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        updateCameraPosition(timeStep);
    }
    
    /**
     * Applies the mouse/joystick inputs
     * 
     * @param mx
     * @param my
     */
    private void applyPlayerMouseInput(float mx, float my) {
        if(isCameraRoaming()) {
            final float threshold = 25.0f;
            if(mx < threshold) {
                this.playerVelocity.x = -1;
            }
            else if(mx > this.viewportWidth-threshold) {
                this.playerVelocity.x = 1;
            }
            
            if(my < threshold) {
                this.playerVelocity.y = -1;
            }
            else if(my > this.viewportHeight-threshold) {
                this.playerVelocity.y = 1;
            }
        }
    }
    
    
    /**
     * Applies the player input
     * 
     * @param keys
     */
    public void applyPlayerInput(float mx, float my, int keys) {
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

        
        applyPlayerMouseInput(mx, my);
        
        if(Keys.SPRINT.isDown(keys)) {
            if(isCameraRoaming()) {
//                setFastFreeformCameraSpeed();
                isFastCamera = true;
            }
        }
        else {
            if(isCameraRoaming()) {
                isFastCamera = false;
            }
        }
        
        if(Keys.IRON_SIGHTS.isDown(keys)) {
            this.isIronSights = true;
        }
        else {
            this.isIronSights = false;
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
    
    public void enterCommanderCameraMode() {
        setFreeformCameraSpeed();
        
        this.isCameraRoaming = true;
        
        // center the camera position
        cameraDest.set(camera.getPosition());
        cameraDest.x += camera.getViewPort().width/2;
        cameraDest.y += camera.getViewPort().height/2;
    }
    
    public void leaveCommanderCameraMode() {
        setGameCameraSpeed();
        this.isCameraRoaming = false;
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
        if(playerVelocity.lengthSquared() > 0 && isCameraActive) {
            Vector2f pos = cameraDest;        
            
            bounds.set(camera.getViewPort());                    
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
                
                cursor.setAccuracy(entity.getAimingAccuracy());
                
                boolean adjustCameraView = !this.localPlayer.isSpectating() && this.isCameraActive && 
                                              (this.config.getFollowReticleEnabled() || this.isIronSights);
                
                
                if(adjustCameraView) {
                    Vector2f.Vector2fMA(entity.getCenterPos(), entity.getFacing(), config.getFollowReticleOffset(), cameraCenterAround);
                                                                                                    
                    // smooth out the camera
                    previousCameraPos.set(cameraCenterAround);
                    Vector2f.Vector2fLerp(cameraCenterAround, previousCameraPos, 0.15f, cameraCenterAround);
 
                }
                else {                    
                    cameraCenterAround.set(entity.getCenterPos());
                }
                
                Vector2f.Vector2fRound(cameraCenterAround, cameraCenterAround);;
                camera.centerAround(cameraCenterAround);
        
                Sounds.setPosition(entity.getCenterPos());
                                
                
                /* Calculates the Fog Of War
                 */
                nextFOWUpdate -= timeStep.getDeltaTime();
                if(nextFOWUpdate <= 0) {
                    entity.calculateLineOfSight(fowTiles);                    
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
