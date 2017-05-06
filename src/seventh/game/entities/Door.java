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

    private static final int DOOR_WIDTH = 10;
    
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
            Vector2f hingeFacing = new Vector2f();
            switch(this) {        
                case NORTH_END:
                    Vector2f.Vector2fPerpendicular(facing, hingeFacing);
                    Vector2f.Vector2fMA(hingePos, hingeFacing, -DOOR_WIDTH, rearHingePos);
                    break;
                case SOUTH_END:                    
                    Vector2f.Vector2fPerpendicular(facing, hingeFacing);
                    Vector2f.Vector2fMA(hingePos, hingeFacing, DOOR_WIDTH, rearHingePos);
                    break;            
                case EAST_END:                    
                    Vector2f.Vector2fPerpendicular(facing, hingeFacing);
                    Vector2f.Vector2fMA(hingePos, hingeFacing, DOOR_WIDTH, rearHingePos);
                    break;
                case WEST_END:
                    Vector2f.Vector2fPerpendicular(facing, hingeFacing);
                    Vector2f.Vector2fMA(hingePos, hingeFacing, -DOOR_WIDTH, rearHingePos);
                    break;
                default:                    
            }
            
            return rearHingePos;
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
        this.setDoorState(DoorState.CLOSED);
        this.setFrontDoorHandle(new Vector2f(facing));
        this.setRearDoorHandle(new Vector2f(facing));
        this.setRearHingePos(new Vector2f());
        this.facing.set(facing);
                    
        this.setHinge(DoorHinge.fromVector(facing));
        this.setHandleTouchRadius(new Rectangle(48, 48));
        this.setHingeTouchRadius(new Rectangle(48,48));
        this.setAutoCloseRadius(new Rectangle(100, 100));
        
        this.bounds.set(this.getHandleTouchRadius());
        this.getHingeTouchRadius().centerAround(getPos());
        this.getAutoCloseRadius().centerAround(getPos());
        
        this.setAutoCloseTimer(new Timer(false, 5_000));
        this.getAutoCloseTimer().stop();
        
        this.setRotation(new SmoothOrientation(0.05));
        this.getRotation().setOrientation(this.getHinge().getClosedOrientation());
        setOrientation(this.getRotation().getOrientation());
        
        setDoorOrientation();
        
        this.setNetDoor(new NetDoor());
        
        setCanTakeDamage(true);
        
        this.setBlocked(false);
        
        this.onTouch = new OnTouchListener() {
            
            @Override
            public void onTouch(Entity me, Entity other) {
                // does nothing, just makes it so the game will
                // acknowledge these two entities can touch each other
            }
        };
    }

    private void setDoorOrientation() {
        Vector2f.Vector2fMA(getPos(), this.getRotation().getFacing(), 64, this.getFrontDoorHandle());
        this.setRearHingePos(this.getHinge().getRearHingePosition(getPos(), this.getRotation().getFacing(), this.getRearHingePos()));                
        Vector2f.Vector2fMA(this.getRearHingePos(), this.getRotation().getFacing(), 64, this.getRearDoorHandle());
        
        this.getHandleTouchRadius().centerAround(this.getFrontDoorHandle());
    }
    
    @Override
    public boolean update(TimeStep timeStep) {
        
        // if the door gets blocked by an Entity,
        // we stop the door.  Once the Entity moves
        // we continue opening the door.
        
        // The door can act as a shield to the entity
        float currentRotation = this.getRotation().getOrientation();
        
        switch(this.getDoorState()) {
            case OPENING: {
                this.getRotation().setDesiredOrientation(this.getTargetOrientation());
                this.getRotation().update(timeStep);
                
                setDoorOrientation();
                
                if(this.game.doesTouchPlayers(this)) {
                    if(!this.isBlocked()) {
                        this.game.emitSound(getId(), SoundType.DOOR_OPEN_BLOCKED, getPos());
                    }
                    
                    this.setBlocked(true);
                    
                    this.getRotation().setOrientation(currentRotation);
                    setDoorOrientation();
                    
                }
                else if(!this.getRotation().moved()) {
                    this.setDoorState(DoorState.OPENED);
                    this.setBlocked(false);
                }
                
                break;
            }
            case CLOSING: {
                this.getRotation().setDesiredOrientation(this.getTargetOrientation());
                this.getRotation().update(timeStep);

                setDoorOrientation();
                if(this.game.doesTouchPlayers(this)) {
                    if(!this.isBlocked()) {
                        this.game.emitSound(getId(), SoundType.DOOR_CLOSE_BLOCKED, getPos());
                    }
                    
                    this.setBlocked(true);
                    
                    this.getRotation().setOrientation(currentRotation);
                    setDoorOrientation();
                    
                }
                else if(!this.getRotation().moved()) {
                    this.setDoorState(DoorState.CLOSED);
                    this.setBlocked(false);
                }
                
                break;
            }
            case OPENED: {
                // part of the gameplay -- auto close
                // the door after a few seconds
                this.getAutoCloseTimer().update(timeStep);
                if(this.getAutoCloseTimer().isOnFirstTime()) {                    
                    if(!isPlayerNear()) {
                        close(this);
                        this.getAutoCloseTimer().stop();
                    }
                    else {
                        this.getAutoCloseTimer().reset();
                    }                    
                }
                
                break;
            }
            default: { 
                this.setBlocked(false);
            }
        }
        
        setOrientation(this.getRotation().getOrientation());
        
        //Vector2f.Vector2fMA(getPos(), this.rotation.getFacing(), 64, this.doorHandle);
    //    DebugDraw.drawLineRelative(getPos(), this.frontDoorHandle, 0xffffff00);
     //   DebugDraw.drawLineRelative(this.rearHingePos, this.rearDoorHandle, 0xffffff00);
        
        //DebugDraw.drawStringRelative("State: " + this.doorState, (int)getPos().x, (int)getPos().y, 0xffff00ff);
        //DebugDraw.drawStringRelative("Orientation: C:" + (int)Math.toDegrees(this.rotation.getOrientation())  + "   D:" + (int)Math.toDegrees(this.rotation.getDesiredOrientation()) , (int)getPos().x, (int)getPos().y + 20, 0xffff00ff);
        
        return false;
    }
    
    public boolean isOpened() {
        return this.getDoorState() == DoorState.OPENED;
    }
    
    public boolean isOpening() {
        return this.getDoorState() == DoorState.OPENING;
    }
    
    public boolean isClosed() {
        return this.getDoorState() == DoorState.CLOSED;
    }
    
    public boolean isClosing() {
        return this.getDoorState() == DoorState.CLOSING;
    }
    
    
    public void handleDoor(Entity ent) {
        if(isOpened()) {
            close(ent);
        }
        else if(isClosed()) {
            open(ent);            
        }
        else if(this.isBlocked()) {
            if(isOpening()) {
                close(ent);
            }
            else {
                open(ent);
            }
        }
    }
    
    public void open(Entity ent) {
        if(this.getDoorState() != DoorState.OPENED  ||
           this.getDoorState() != DoorState.OPENING ||
           this.getDoorState() != DoorState.CLOSING) {
            
            if(!canBeHandledBy(ent)) {
                return;
            }
            
            this.getAutoCloseTimer().reset();
                        
            this.setDoorState(DoorState.OPENING);
            this.game.emitSound(getId(), SoundType.DOOR_OPEN, getPos());
            
            Vector2f entPos = ent.getCenterPos();
            Vector2f hingePos = this.getFrontDoorHandle();//getPos();
            // figure out what side the entity is 
            // of the door hinge, depending on their
            // side, we set the destinationOrientation
            switch(this.getHinge()) {
                
                case NORTH_END:
                case SOUTH_END:
                    if(entPos.x < hingePos.x) {
                        this.setTargetOrientation((float)Math.toRadians(0));
                    }
                    else if(entPos.x > hingePos.x) {
                        this.setTargetOrientation((float)Math.toRadians(180));
                    }
                    break;
                case EAST_END:                    
                case WEST_END:
                    if(entPos.y < hingePos.y) {
                        this.setTargetOrientation((float)Math.toRadians(90));
                    }
                    else if(entPos.y > hingePos.y) {
                        this.setTargetOrientation((float)Math.toRadians(270));
                    }
                    break;
                default:
                    break;            
            }
        }
    }
    
    public void close(Entity ent) {
        if(this.getDoorState() != DoorState.CLOSED  ||
           this.getDoorState() != DoorState.OPENING ||
           this.getDoorState() != DoorState.CLOSING) {
            
            if(!canBeHandledBy(ent)) {
                return;
            }
            
            this.setDoorState(DoorState.CLOSING);            
            this.setTargetOrientation(this.getHinge().getClosedOrientation());        
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
        
        return this.getHandleTouchRadius().intersects(ent.getBounds()) ||
               this.getHingeTouchRadius().intersects(ent.getBounds()) ;
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
                if(this.getAutoCloseRadius().contains(other.getBounds())) {
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
        boolean isTouching = Line.lineIntersectsRectangle(getPos(), this.getFrontDoorHandle(), bounds) ||
                             Line.lineIntersectsRectangle(this.getRearHingePos(), this.getRearDoorHandle(), bounds);
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
        return getFrontDoorHandle();
    }
    
    
    @Override
    public NetEntity getNetEntity() {
        this.setNetEntity(getNetDoor());
        this.getNetDoor().hinge = this.getHinge().netValue();
        return this.getNetDoor();
    }

	private DoorState getDoorState() {
		return doorState;
	}

	private void setDoorState(DoorState doorState) {
		this.doorState = doorState;
	}

	private DoorHinge getHinge() {
		return hinge;
	}

	private void setHinge(DoorHinge hinge) {
		this.hinge = hinge;
	}

	private Vector2f getFrontDoorHandle() {
		return frontDoorHandle;
	}

	private void setFrontDoorHandle(Vector2f frontDoorHandle) {
		this.frontDoorHandle = frontDoorHandle;
	}

	private Vector2f getRearDoorHandle() {
		return rearDoorHandle;
	}

	private void setRearDoorHandle(Vector2f rearDoorHandle) {
		this.rearDoorHandle = rearDoorHandle;
	}

	private Vector2f getRearHingePos() {
		return rearHingePos;
	}

	private void setRearHingePos(Vector2f rearHingePos) {
		this.rearHingePos = rearHingePos;
	}

	private Rectangle getHandleTouchRadius() {
		return handleTouchRadius;
	}

	private void setHandleTouchRadius(Rectangle handleTouchRadius) {
		this.handleTouchRadius = handleTouchRadius;
	}

	private Rectangle getHingeTouchRadius() {
		return hingeTouchRadius;
	}

	private void setHingeTouchRadius(Rectangle hingeTouchRadius) {
		this.hingeTouchRadius = hingeTouchRadius;
	}

	private Rectangle getAutoCloseRadius() {
		return autoCloseRadius;
	}

	private void setAutoCloseRadius(Rectangle autoCloseRadius) {
		this.autoCloseRadius = autoCloseRadius;
	}

	private SmoothOrientation getRotation() {
		return rotation;
	}

	private void setRotation(SmoothOrientation rotation) {
		this.rotation = rotation;
	}

	private float getTargetOrientation() {
		return targetOrientation;
	}

	private void setTargetOrientation(float targetOrientation) {
		this.targetOrientation = targetOrientation;
	}

	private void setBlocked(boolean isBlocked) {
		this.isBlocked = isBlocked;
	}

	private NetDoor getNetDoor() {
		return netDoor;
	}

	private void setNetDoor(NetDoor netDoor) {
		this.netDoor = netDoor;
	}

	private Timer getAutoCloseTimer() {
		return autoCloseTimer;
	}

	private void setAutoCloseTimer(Timer autoCloseTimer) {
		this.autoCloseTimer = autoCloseTimer;
	}

}
