/*
 * see license.txt 
 */
package seventh.client.entities;

import java.util.List;

import seventh.client.ClientGame;
import seventh.client.entities.vehicles.ClientVehicle;
import seventh.game.entities.Entity;
import seventh.game.entities.Entity.State;
import seventh.map.Map;
import seventh.map.MapObject;
import seventh.map.Tile;
import seventh.math.Line;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.Geom;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public abstract class ClientControllableEntity extends ClientEntity {
    
    protected State currentState;
    protected int lineOfSight;
    
    protected Vector2f predictedPos;
    protected Vector2f renderPos;
    protected Vector2f cache;
    
    protected ClientVehicle vehicle;
    
    protected long lastMoveTime;    
    private boolean isControlledByLocalPlayer;
    
    protected Rectangle hearingBounds;
    protected Rectangle visualBounds;
    
    private Vector2f xCollisionTilePos, yCollisionTilePos;
    
    /**
     * @param game
     * @param pos
     */
    public ClientControllableEntity(ClientGame game, Vector2f pos) {
        super(game, pos);    
        
        this.predictedPos = new Vector2f();                
        this.renderPos = new Vector2f(pos);
        this.cache = new Vector2f();
        
        this.xCollisionTilePos = new Vector2f();
        this.yCollisionTilePos = new Vector2f();
        
        this.isControlledByLocalPlayer = false;
        

        this.hearingBounds = new Rectangle(200, 200);
        this.hearingBounds.centerAround(pos);
        
        this.visualBounds = new Rectangle(5000,5000);
        this.visualBounds.centerAround(pos);
    }

    /**
     * @return true if we are operating a vehicle
     */
    public boolean isOperatingVehicle() {
        return this.vehicle != null;
    }
    
    
    /**
     * @return the vehicle
     */
    public ClientVehicle getVehicle() {
        return vehicle;
    }
    
    /**
     * @param isControlledByLocalPlayer the isControlledByLocalPlayer to set
     */
    public void setControlledByLocalPlayer(boolean isControlledByLocalPlayer) {
        this.isControlledByLocalPlayer = isControlledByLocalPlayer;
    }
    
    /**
     * @return the isControlledByLocalPlayer
     */
    public boolean isControlledByLocalPlayer() {
        return isControlledByLocalPlayer;
    }
    
    /**
     * @return the lineOfSight
     */
    public int getLineOfSight() {
        return lineOfSight;
    }
        
    
    /**
     * @return the currentState
     */
    public State getCurrentState() {
        return currentState;
    }
    
    /**
     * @return the client side predicted position
     */
    public Vector2f getPredictedPos() {
        return predictedPos;
    }
        
    /**
     * @return the renderPos
     */
    public Vector2f getRenderPos(float alpha) {
        if(isControlledByLocalPlayer()) {
            //Vector2f.Vector2fLerp(predictedPos, pos, alpha, renderPos);
            
//            renderPos.x = predictedPos.x * 0.6f + pos.x * 0.4f;
//            renderPos.y = predictedPos.y * 0.6f + pos.y * 0.4f;
            renderPos.x = renderPos.x * 0.8f + predictedPos.x * 0.2f; // was pos
            renderPos.y = renderPos.y * 0.8f + predictedPos.y * 0.2f;

        }
        else {            
            Vector2f.Vector2fLerp(previousPos, pos, alpha, renderPos);
        }
        
        return renderPos;
    }
        
    /**
     * @return the height mask for if the entity is crouching or standing
     */
    public int getHeightMask() {
        if( currentState == State.CROUCHING ) {
            return Entity.CROUCHED_HEIGHT_MASK;
        }
        return Entity.STANDING_HEIGHT_MASK;
    }
    
    /**
     * Determines if this entity would be able to hear the other {@link ClientEntity}
     * 
     * @param ent
     * @return true if in ear shot distance of the other {@link ClientEntity}
     */
    public boolean inEarShot(ClientEntity ent) {
        return hearingBounds.intersects(ent.getBounds());
    }
    
    
    /**
     * Adjusts the y movement if the player is at the edge of a collidable tile and there
     * is a free space
     * 
     * @param collisionTilePos
     * @param deltaX
     * @param currentX
     * @param currentY
     * @return the adjusted y to move
     */
    private int adjustY(Vector2f collisionTilePos, float deltaX, int currentX, int currentY) {
        Map map = game.getMap();
        Tile collisionTile = map.getWorldCollidableTile((int)collisionTilePos.x, (int)collisionTilePos.y);
        if(collisionTile != null) {
            //DebugDraw.drawRectRelative(collisionTile.getBounds(), 0xff00ff00);
            
            int xIndex = collisionTile.getXIndex();
            int yIndex = collisionTile.getYIndex();
            
            int offset = 32;
            
            if(!map.checkTileBounds(xIndex, yIndex - 1) && !map.hasCollidableTile(xIndex, yIndex - 1)) {
                if(currentY < (collisionTile.getY() - (bounds.height - offset))) {
                    //DebugDraw.drawRectRelative(map.getTile(0, xIndex, yIndex-1).getBounds(), 0xafff0000);
                    return currentY - 1;
                }
            }
            
            if(!map.checkTileBounds(xIndex, yIndex + 1) && !map.hasCollidableTile(xIndex, yIndex + 1)) {
                if(currentY > (collisionTile.getY() + (collisionTile.getHeight() - offset))) {
                    //DebugDraw.drawRectRelative(map.getTile(0, xIndex, yIndex+1).getBounds(), 0xaf0000ff);
                    return currentY + 1;
                }
            }            
        }
        
        return currentY;
    }
    
    
    /**
     * Adjusts the x movement if the player is at the edge of a collidable tile and there
     * is a free space
     * 
     * @param collisionTilePos
     * @param deltaY
     * @param currentX
     * @param currentY
     * @return the adjusted x to move
     */
    private int adjustX(Vector2f collisionTilePos, float deltaY, int currentX, int currentY) {
        Map map = game.getMap();
        Tile collisionTile = map.getWorldCollidableTile((int)collisionTilePos.x, (int)collisionTilePos.y);
        if(collisionTile != null) {
            //DebugDraw.drawRectRelative(collisionTile.getBounds(), 0xff00ff00);
            
            int xIndex = collisionTile.getXIndex();
            int yIndex = collisionTile.getYIndex();
            
            int offset = 32;
            
            if(!map.checkTileBounds(xIndex - 1, yIndex) && !map.hasCollidableTile(xIndex - 1, yIndex)) {
                if(currentX+bounds.width < (collisionTile.getX() + offset)) {
                    //DebugDraw.drawRectRelative(map.getTile(0, xIndex-1, yIndex).getBounds(), 0xafff0000);
                    return currentX - 1;
                }
            }
            
            if(!map.checkTileBounds(xIndex + 1, yIndex) && !map.hasCollidableTile(xIndex + 1, yIndex)) {
                if(currentX > (collisionTile.getX() + collisionTile.getWidth() - offset)) {
                    //DebugDraw.drawRectRelative(map.getTile(0, xIndex+1, yIndex).getBounds(), 0xaf0000ff);
                    return currentX + 1;
                }
            }            
        }
        
        return currentX;
    }
    
    /**
     * Continue to check Y coordinate if X was blocked
     * @return true if we should continue collision checks
     */
    protected boolean continueIfBlock() {
        return true;
    }
    
    /**
     * Does client side movement prediction
     * 
     */
    public void movementPrediction(Map map, TimeStep timeStep, Vector2f vel) {    
        
        if(isAlive() && !vel.isZero()) {            
            int movementSpeed = calculateMovementSpeed();
                                    
            float dt = (float)timeStep.asFraction();            
            float deltaX = (vel.x * movementSpeed * dt);
            float deltaY = (vel.y * movementSpeed * dt);
            
            float newX = predictedPos.x + deltaX;
            float newY = predictedPos.y + deltaY;
            
            boolean isBlocked = false;
            boolean isBlockedByEntity = false;
            
            bounds.x = (int)newX;            
            if(map.rectCollides(bounds, 1, xCollisionTilePos)) {
                bounds.x = (int)predictedPos.x;
                newX = predictedPos.x;
                isBlocked = true;
            }
            else if(collidesAgainstEntity(bounds) || collidesAgainstMapObject(bounds)) {
                bounds.x = (int)predictedPos.x;
                newX = predictedPos.x;
                isBlocked = true;
                isBlockedByEntity = true;
            }
            
            bounds.y = (int)newY;            
            if(map.rectCollides(bounds, 1, yCollisionTilePos)) {
                bounds.y = (int)predictedPos.y;
                newY = predictedPos.y;
                isBlocked = true;
            }
            else if(collidesAgainstEntity(bounds) || collidesAgainstMapObject(bounds)) {
                bounds.y = (int)predictedPos.y;
                newY = predictedPos.y;
                isBlocked = true;
                isBlockedByEntity = true;
            }
            
            if(isBlocked) {
                /* some things want to stop dead it their tracks
                 * if a component is blocked
                 */
                if(!continueIfBlock()) {
                    bounds.setLocation(pos);
                    
                    newX = pos.x;
                    newY = pos.y;
                }  
                
                /*
                 * Otherwise determine if the character
                 * is a couple pixels off and is snagged on
                 * a corner, if so auto adjust them
                 */
                else if (!isBlockedByEntity) {
                    if(deltaX != 0 && deltaY == 0) {                
                        newY = adjustY(xCollisionTilePos, deltaX, (int)(predictedPos.x + deltaX), bounds.y);
                    }
                    else if(deltaX == 0 && deltaY != 0) {
                        newX = adjustX(yCollisionTilePos, deltaY, bounds.x, (int)(predictedPos.y + deltaY));
                    }
                }
            }
            
            predictedPos.set(newX, newY);
            
            clientSideCorrection(pos, predictedPos, predictedPos, 0.15f);
            lastMoveTime = timeStep.getGameClock();
        }        
        else {
            float alpha = 0.15f + 0.108f * (float)((timeStep.getGameClock() - lastMoveTime) / timeStep.getDeltaTime());
            if(alpha > 0.75f) {
                alpha = 0.75f;
            }
            clientSideCorrection(pos, predictedPos, predictedPos, alpha);
        }
    }
    
    protected boolean collidesAgainstEntity(Rectangle bounds) {
        List<ClientVehicle> vehicles = game.getVehicles();
        for(int i = 0; i < vehicles.size(); i++) {
            ClientVehicle v = vehicles.get(i);
            if(v.isRelativelyUpdated()) {
                if(v.isTouching(bounds)) {
                    return true;
                }
            }
        }
        
        List<ClientDoor> doors = game.getDoors();
        for(int i = 0; i < doors.size(); i++) {
            ClientDoor door = doors.get(i);
            if(door.isTouching(bounds)) {
                return true;
            }
        }
        
        return false;
    }
    
    protected boolean collidesAgainstMapObject(Rectangle bounds) {
        List<MapObject> mapObjects = game.getMapObjects();
        for(int i = 0; i < mapObjects.size(); i++) {
            MapObject object = mapObjects.get(i);
            if(object.isCollidable()) {
                if(object.isTouching(bounds) ) {                    
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * @return calculates the movement speed based on
     * state + current weapon + stamina
     */
    protected abstract int calculateMovementSpeed();
    
    /**
     * Correct the position
     * 
     * @param serverPos
     * @param predictedPos
     * @param out the output vector
     */
    public void clientSideCorrection(Vector2f serverPos, Vector2f predictedPos, Vector2f out, float alpha) {
        //float alpha = 0.15f;
        float dist = Vector2f.Vector2fDistanceSq(serverPos, predictedPos);
        
        /* if the entity is more than two tile off, snap
         * into position
         */
        if(dist > 62 * 62) {            
            Vector2f.Vector2fCopy(serverPos, out);
        }        
        else //if (dist > 22 * 22) 
        {                    
            out.x = predictedPos.x + (alpha * (serverPos.x - predictedPos.x));
            out.y = predictedPos.y + (alpha * (serverPos.y - predictedPos.y));            
        }            
            
    }        
    
    /**
     * Calculates the aiming accuracy of this entity.  The accuracy is impacted
     * by the entities current state.
     * 
     * @return a number ranging from [0,1] with 1 being the most accurate
     */
    public float getAimingAccuracy() {
        float accuracy = 0f;
        switch(getCurrentState()) {
            case CROUCHING:
                accuracy = 1f;
                break;                                        
            case IDLE:
                accuracy = .9f;
                break;
            case OPERATING_VEHICLE:
                accuracy = 1f;
                break;
            case RUNNING:
                accuracy = .5f;
                break;
            case SPRINTING:
                accuracy = 0f;
                break;
            case WALKING:
                accuracy = .9f;
                break;                    
            default: accuracy = 0f;
        }
        return accuracy;
    }
    
    
    public List<Tile> calculateLineOfSight(List<Tile> tiles) {
        Map map = game.getMap();
        Geom.calculateLineOfSight(tiles, getCenterPos(), getFacing(), getLineOfSight(), map, getHeightMask(), cache);
        
        int tileSize = tiles.size();
        List<ClientDoor> doors = game.getDoors();
        int doorSize = doors.size();
        
        Vector2f centerPos = getCenterPos();
        
        for(int j = 0; j < doorSize; j++ ) {
            ClientDoor door = doors.get(j);
            if(this.visualBounds.intersects(door.getBounds())) {        
                for(int i = 0; i < tileSize; i++) {
                    Tile tile = tiles.get(i);
                    if(Line.lineIntersectLine(centerPos, tile.getCenterPos(), 
                                              door.getPos(), door.getHandle())) {
                        tile.setMask(Tile.TILE_INVISIBLE);
                    }
                }
            }
        }
        /*
        List<ClientSmoke> smoke = game.getSmokeEntities();
        int smokeSize = smoke.size();
        
        if(smokeSize > 0) {
            for(int j = 0; j < smokeSize; j++) {
                ClientSmoke s = smoke.get(j);
                if(this.visualBounds.intersects(s.getBounds())) {
                    for(int i = 0; i < tileSize; i++) {
                        Tile tile = tiles.get(i);
                        if(tile.getMask() > 0) {                                                    
                            if(Line.lineIntersectsRectangle(centerPos, tile.getCenterPos(), s.getBounds())) {
                                tile.setMask(Tile.TILE_INVISIBLE);
                            }
                        }
                    }                    
                }
             
            }
        }*/
        
        return tiles;
    }
}
