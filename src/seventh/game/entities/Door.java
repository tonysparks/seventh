/*
 * see license.txt 
 */
package seventh.game.entities;

import seventh.game.Game;
import seventh.game.SmoothOrientation;
import seventh.game.net.NetDoor;
import seventh.game.net.NetEntity;
import seventh.math.Line;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.SoundType;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * A Door in the game world.  It can be opened or closed.
 * 
 * @author Tony
 *
 */
public class Door extends Entity {

    public static final int DOOR_WIDTH = 4;
    
    public static enum DoorHinge {
        NORTH_END,
        SOUTH_END,
        EAST_END,
        WEST_END,
        
        ;
        
        private static DoorHinge[] values = values();
        
        public byte netValue() {
            return (byte)ordinal();
        }
        

        public float getClosedOrientation() {
            switch(this) {        
                case NORTH_END:
                    return (float)Math.toRadians(270);
                case SOUTH_END:                    
                    return (float)Math.toRadians(90);            
                case EAST_END:
                    return (float)Math.toRadians(180);
                case WEST_END:
                    return (float)Math.toRadians(0);
                default:
                    return 0;        
            }
        }
        
        public Vector2f getRearHandlePosition(Vector2f pos, Vector2f rearHandlePos) {
            rearHandlePos.set(pos);
            final float doorWidth = DOOR_WIDTH;
            switch(this) {        
                case NORTH_END:
                    rearHandlePos.x += doorWidth;
                    break;
                case SOUTH_END:                    
                    rearHandlePos.x += doorWidth;
                    break;            
                case EAST_END:
                    rearHandlePos.y += doorWidth;
                    break;
                case WEST_END:
                    rearHandlePos.y += doorWidth;
                    break;
                default:                    
            }
            
            return rearHandlePos;
        }
        
        public Vector2f getRearHingePosition(Vector2f hingePos, Vector2f facing, Vector2f rearHingePos) {
            Vector2f hingeFacing = rearHingePos; 
                    //new Vector2f();
            switch(this) {        
                case NORTH_END:
                    Vector2f.Vector2fPerpendicular(facing, hingeFacing);
                    Vector2f.Vector2fMA(hingePos, hingeFacing, -DOOR_WIDTH/2, rearHingePos);
                    break;
                case SOUTH_END:                    
                    Vector2f.Vector2fPerpendicular(facing, hingeFacing);
                    Vector2f.Vector2fMA(hingePos, hingeFacing, DOOR_WIDTH/2, rearHingePos);
                    break;            
                case EAST_END:                    
                    Vector2f.Vector2fPerpendicular(facing, hingeFacing);
                    Vector2f.Vector2fMA(hingePos, hingeFacing, DOOR_WIDTH/2, rearHingePos);
                    break;
                case WEST_END:
                    Vector2f.Vector2fPerpendicular(facing, hingeFacing);
                    Vector2f.Vector2fMA(hingePos, hingeFacing, -DOOR_WIDTH/2, rearHingePos);
                    break;
                default:                    
            }
            
            return rearHingePos;
        }
        
        public Vector2f getFrontHingePosition(Vector2f hingePos, Vector2f facing, Vector2f frontHingePos) {
            Vector2f hingeFacing = frontHingePos; 
                    //new Vector2f();
            switch(this) {        
                case NORTH_END:
                    Vector2f.Vector2fPerpendicular(facing, hingeFacing);
                    Vector2f.Vector2fMS(hingePos, hingeFacing, -DOOR_WIDTH/2, frontHingePos);
                    break;
                case SOUTH_END:                    
                    Vector2f.Vector2fPerpendicular(facing, hingeFacing);
                    Vector2f.Vector2fMS(hingePos, hingeFacing, DOOR_WIDTH/2, frontHingePos);
                    break;            
                case EAST_END:                    
                    Vector2f.Vector2fPerpendicular(facing, hingeFacing);
                    Vector2f.Vector2fMS(hingePos, hingeFacing, DOOR_WIDTH/2, frontHingePos);
                    break;
                case WEST_END:
                    Vector2f.Vector2fPerpendicular(facing, hingeFacing);
                    Vector2f.Vector2fMS(hingePos, hingeFacing, -DOOR_WIDTH/2, frontHingePos);
                    break;
                default:                    
            }
            
            return frontHingePos;
        }
        
        public static DoorHinge fromNetValue(byte value) {
            for(DoorHinge hinge : values) {
                if(hinge.netValue() == value) {
                    return hinge;
                }
            }
            return DoorHinge.NORTH_END;
        }
        
        public static DoorHinge fromVector(Vector2f facing) {
            if(facing.x > 0) {
                return DoorHinge.EAST_END;
            }
            
            if(facing.x < 0) {
                return DoorHinge.WEST_END;
            }
            
            if(facing.y > 0) {
                return DoorHinge.SOUTH_END;
            }
            
            if(facing.y < 0) {
                return DoorHinge.NORTH_END;
            }
            
            return DoorHinge.EAST_END;
        }
    }
    
    /**
     * Available door states
     * 
     * @author Tony
     *
     */
    public static enum DoorState {
        OPENED,
        OPENING,
        CLOSED,
        CLOSING,
        ;
        
        public byte netValue() {
            return (byte)ordinal();
        }
    }
    
    private DoorState doorState;
    private DoorHinge hinge;
     
    private Vector2f frontDoorHandle, 
                     rearDoorHandle,
                     frontHingePos,
                     rearHingePos;
    
    private Rectangle handleTouchRadius,
                      hingeTouchRadius,
                      autoCloseRadius;
    
    
    private SmoothOrientation rotation;
    private float targetOrientation;
    private boolean isBlocked;
    
    private NetDoor netDoor;
    
    private Timer autoCloseTimer;
    
    /**
     * @param id
     * @param position
     * @param speed
     * @param game
     * @param type
     */
    public Door(Vector2f position, Game game, Vector2f facing) {
        super(game.getNextPersistantId(), position, 0, game, Type.DOOR);
        this.doorState = DoorState.CLOSED;

        this.frontDoorHandle = new Vector2f(facing);
        this.frontHingePos = new Vector2f();

        this.rearDoorHandle = new Vector2f(facing);
        this.rearHingePos = new Vector2f();
        
        this.facing.set(facing);
                    
        this.hinge = DoorHinge.fromVector(facing);
        this.handleTouchRadius = new Rectangle(48, 48);
        this.hingeTouchRadius = new Rectangle(48,48);
        this.autoCloseRadius = new Rectangle(128, 128);
        
        this.bounds.set(this.handleTouchRadius);
        this.bounds.setLocation(getPos());
        
        this.hingeTouchRadius.centerAround(getPos());
        this.autoCloseRadius.centerAround(getPos());
        
        this.autoCloseTimer = new Timer(false, 5_000);
        this.autoCloseTimer.stop();
        
        this.rotation = new SmoothOrientation(0.1);
        this.rotation.setOrientation(this.hinge.getClosedOrientation());
        setOrientation(this.rotation.getOrientation());
        
        setDoorOrientation();
        
        this.netDoor = new NetDoor();
        
        setCanTakeDamage(true);
        
        this.isBlocked = false;
        
        this.onTouch = new OnTouchListener() {
            
            @Override
            public void onTouch(Entity me, Entity other) {
                // does nothing, just makes it so the game will
                // acknowledge these two entities can touch each other
            }
        };
    }

    private void setDoorOrientation() {
        this.frontHingePos = this.hinge.getFrontHingePosition(getPos(), this.rotation.getFacing(), this.frontHingePos);
        Vector2f.Vector2fMA(this.frontHingePos, this.rotation.getFacing(), 64, this.frontDoorHandle);
        
        this.rearHingePos = this.hinge.getRearHingePosition(getPos(), this.rotation.getFacing(), this.rearHingePos);           
        Vector2f.Vector2fMA(this.rearHingePos, this.rotation.getFacing(), 64, this.rearDoorHandle);
        
        this.handleTouchRadius.centerAround(this.frontDoorHandle);
    }
    
    @Override
    public boolean update(TimeStep timeStep) {
        
        // if the door gets blocked by an Entity,
        // we stop the door.  Once the Entity moves
        // we continue opening the door.
        
        // The door can act as a shield to the entity
        float currentRotation = this.rotation.getOrientation();
        
        switch(this.doorState) {
            case OPENING: {
                this.rotation.setDesiredOrientation(this.targetOrientation);
                this.rotation.update(timeStep);
                
                setDoorOrientation();
                
                if(this.game.doesTouchPlayers(this)) {
                    if(!this.isBlocked) {
                        this.game.emitSound(getId(), SoundType.DOOR_OPEN_BLOCKED, getPos());
                    }
                    
                    this.isBlocked = true;
                    
                    this.rotation.setOrientation(currentRotation);
                    setDoorOrientation();
                    
                }
                else if(!this.rotation.moved()) {
                    this.doorState = DoorState.OPENED;
                    this.isBlocked = false;
                }
                
                break;
            }
            case CLOSING: {
                this.rotation.setDesiredOrientation(this.targetOrientation);
                this.rotation.update(timeStep);

                setDoorOrientation();
                if(this.game.doesTouchPlayers(this)) {
                    if(!this.isBlocked) {
                        this.game.emitSound(getId(), SoundType.DOOR_CLOSE_BLOCKED, getPos());
                    }
                    
                    this.isBlocked = true;
                    
                    this.rotation.setOrientation(currentRotation);
                    setDoorOrientation();
                    
                }
                else if(!this.rotation.moved()) {
                    this.doorState = DoorState.CLOSED;
                    this.isBlocked = false;
                }
                
                break;
            }
            case OPENED: {
                // part of the gameplay -- auto close
                // the door after a few seconds
                this.autoCloseTimer.update(timeStep);
                if(this.autoCloseTimer.isOnFirstTime()) {                    
                    if(!isPlayerNear()) {
                        close(this);
                        this.autoCloseTimer.stop();
                    }
                    else {
                        this.autoCloseTimer.reset();
                    }                    
                }
                
                break;
            }
            default: { 
                this.isBlocked = false;
            }
        }
        
        setOrientation(this.rotation.getOrientation());
        
        //DebugDraw.fillRectRelative((int)getPos().x, (int)getPos().y, 5, 5, 0xffff0000);
        //DebugDraw.drawLineRelative(this.frontHingePos, this.frontDoorHandle, 0xffffff00);
        //DebugDraw.drawLineRelative(this.rearHingePos, this.rearDoorHandle, 0xffffff00);
                
        //DebugDraw.drawRectRelative(autoCloseRadius, 0xffff0000);
        //DebugDraw.drawStringRelative("State: " + this.doorState, (int)getPos().x, (int)getPos().y, 0xffff00ff);
        //DebugDraw.drawStringRelative("Orientation: C:" + (int)Math.toDegrees(this.rotation.getOrientation())  + "   D:" + (int)Math.toDegrees(this.rotation.getDesiredOrientation()) , (int)getPos().x, (int)getPos().y + 20, 0xffff00ff);
        
        return false;
    }
    
    public boolean isOpened() {
        return this.doorState == DoorState.OPENED;
    }
    
    public boolean isOpening() {
        return this.doorState == DoorState.OPENING;
    }
    
    public boolean isClosed() {
        return this.doorState == DoorState.CLOSED;
    }
    
    public boolean isClosing() {
        return this.doorState == DoorState.CLOSING;
    }
    
    
    public void handleDoor(Entity ent) {
        if(isOpened()) {
            close(ent);
        }
        else if(isClosed()) {
            open(ent);            
        }
        else if(this.isBlocked) {
            if(isOpening()) {
                close(ent);
            }
            else {
                open(ent);
            }
        }
    }
    
    public void open(Entity ent) {
        if(this.doorState != DoorState.OPENED  ||
           this.doorState != DoorState.OPENING ||
           this.doorState != DoorState.CLOSING) {
            
            if(!canBeHandledBy(ent)) {
                return;
            }
            
            this.autoCloseTimer.reset();
                        
            this.doorState = DoorState.OPENING;
            this.game.emitSound(getId(), SoundType.DOOR_OPEN, getPos());
            
            Vector2f entPos = ent.getCenterPos();
            Vector2f hingePos = this.frontDoorHandle;//getPos();
            // figure out what side the entity is 
            // of the door hinge, depending on their
            // side, we set the destinationOrientation
            switch(this.hinge) {
                
                case NORTH_END:
                case SOUTH_END:
                    if(entPos.x < hingePos.x) {
                        this.targetOrientation = (float)Math.toRadians(0);
                    }
                    else if(entPos.x > hingePos.x) {
                        this.targetOrientation = (float)Math.toRadians(180);
                    }
                    break;
                case EAST_END:                    
                case WEST_END:
                    if(entPos.y < hingePos.y) {
                        this.targetOrientation = (float)Math.toRadians(90);
                    }
                    else if(entPos.y > hingePos.y) {
                        this.targetOrientation = (float)Math.toRadians(270);
                    }
                    break;
                default:
                    break;            
            }
        }
    }
    
    public void close(Entity ent) {
        if(this.doorState != DoorState.CLOSED  ||
           this.doorState != DoorState.OPENING ||
           this.doorState != DoorState.CLOSING) {
            
            if(!canBeHandledBy(ent)) {
                return;
            }
            
            this.doorState = DoorState.CLOSING;            
            this.targetOrientation = this.hinge.getClosedOrientation();        
            this.game.emitSound(getId(), SoundType.DOOR_CLOSE, getPos());
        }
    }
        
    
    @Override
    public void damage(Entity damager, int amount) {
        // Do nothing
    }
    
    /**
     * Test if the supplied {@link Entity} is within reach to close or open this door.
     * 
     * @param ent
     * @return true if the Entity is within reach to to close/open this door
     */    
    public boolean canBeHandledBy(Entity ent) {      
        //DebugDraw.fillRectRelative(handleTouchRadius.x, handleTouchRadius.y, handleTouchRadius.width, handleTouchRadius.height, 0xff00ff00);
        //DebugDraw.fillRectRelative(hingeTouchRadius.x, hingeTouchRadius.y, hingeTouchRadius.width, hingeTouchRadius.height, 0xff00ff00);
        
        // we can close our selves :)
        if(ent==this) {
            return true;
        }
        
        return this.handleTouchRadius.intersects(ent.getBounds()) ||
               this.hingeTouchRadius.intersects(ent.getBounds()) ;
    }
    
    /**
     * @return true only if there is a player some what near this door (such that
     * it wouldn't be able to close or open properly)
     */
    public boolean isPlayerNear() {
        PlayerEntity[] playerEntities = game.getPlayerEntities();
        for(int i = 0; i < playerEntities.length; i++) {
            Entity other = playerEntities[i];
            if(other != null) {
                if(this.autoCloseRadius.contains(other.getBounds())) {
                    return true;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Test if the supplied {@link Entity} touches the door
     * 
     * @param ent
     * @return true if the Entity is within reach to to close/open this door
     */
    @Override
    public boolean isTouching(Entity ent) {
        return isTouching(ent.getBounds());
    }
    
    public boolean isTouching(Rectangle bounds) {
        boolean isTouching = Line.lineIntersectsRectangle(this.frontHingePos, this.frontDoorHandle, bounds) ||
                             Line.lineIntersectsRectangle(this.rearHingePos, this.rearDoorHandle, bounds);
//        if(isTouching) {
//            DebugDraw.fillRectRelative(bounds.x, bounds.y, bounds.width, bounds.height, 0xff00ff00);
//            DebugDraw.drawLineRelative(getPos(), this.frontDoorHandle, 0xffffff00);
//            DebugDraw.drawLineRelative(this.rearHingePos, this.rearDoorHandle, 0xffffff00);
//        }
        return isTouching;
    }
    
    /**
     * @return the isBlocked
     */
    public boolean isBlocked() {
        return isBlocked;
    }
    
    /**
     * @return the frontDoorHandle
     */
    public Vector2f getHandle() {
        return frontDoorHandle;
    }
    
    
    @Override
    public NetEntity getNetEntity() {
        this.setNetEntity(netDoor);
        this.netDoor.hinge = this.hinge.netValue();
        return this.netDoor;
    }

}
