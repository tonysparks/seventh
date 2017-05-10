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
    public static int getDoorWidth(){
    	return DOOR_WIDTH;
    }
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
        private DoorHingeEnd doorHingeEnd;
        public void setDoorHingeDirection(){
        	switch(this){
        	case NORTH_END: doorHingeEnd = new DoorNorthEnd();
        	case SOUTH_END: doorHingeEnd = new DoorSouthEnd();
        	case EAST_END: doorHingeEnd = new DoorEastEnd();
        	case WEST_END: doorHingeEnd = new DoorWestEnd();
        	default: throw new IllegalArgumentException("Incorrect DoorHinge");
        	}
        }

        public float getClosedOrientation() {
        	setDoorHingeDirection();
        	return doorHingeEnd.getClosedOrientation();
        }
        
        public Vector2f getRearHandlePosition(Vector2f pos, Vector2f rearHandlePos) {
        	setDoorHingeDirection();
        	return doorHingeEnd.getRearHandlePosition(pos, rearHandlePos);
        }
        
        public Vector2f getRearHingePosition(Vector2f hingePos, Vector2f facing, Vector2f rearHingePos) {
        	setDoorHingeDirection();
        	return doorHingeEnd.getRearHingePosition(hingePos, facing, rearHingePos);
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
        OPENED {
        	public void update(Door door,TimeStep timeStep){
                // part of the gameplay -- auto close
                // the door after a few seconds
        		door.autoCloseTimer.update(timeStep);
                if(door.autoCloseTimer.isOnFirstTime()) {                    
                    if(!door.isPlayerNear()) {
                    	door.close(door);
                    	door.autoCloseTimer.stop();
                    }
                    else {
                    	door.autoCloseTimer.reset();
                    }                    
                }
        	}
        },
        OPENING {
        	public void update(Door door,TimeStep timeStep){
        		door.rotation.setDesiredOrientation(door.getTargetOrientation());
        		door.rotation.update(timeStep);
                
        		door.setDoorOrientation();
                
                if(door.game.doesTouchPlayers(door)) {
                    if(!door.isBlocked()) {
                    	door.game.emitSound(door.getId(), SoundType.DOOR_OPEN_BLOCKED, door.getPos());
                    }
                    
                    door.isBlocked = true;
                    
                    door.rotation.setOrientation(door.rotation.getOrientation());
                    door.setDoorOrientation();
                    
                }
                else if(!door.rotation.moved()) {
                	door.doorState = DoorState.OPENED;
                	door.isBlocked = false;
                }
        	}
        },
        CLOSED,
        CLOSING {
        	public void update(Door door,TimeStep timeStep){
				door.rotation.setDesiredOrientation(door.getTargetOrientation());
				door.rotation.update(timeStep);

				door.setDoorOrientation();
                if(door.game.doesTouchPlayers(door)) {
                    if(!door.isBlocked()) {
                    	door.game.emitSound(door.getId(), SoundType.DOOR_CLOSE_BLOCKED, door.getPos());
                    }
                    
                    door.isBlocked = true;
                    
                    door.rotation.setOrientation(door.rotation.getOrientation());
                    door.setDoorOrientation();
                    
                }
                else if(!door.rotation.moved()) {
                	door.doorState = DoorState.CLOSED;
                	door.isBlocked = false;
                }
        	}
        },
        ;
        public void update(Door door,TimeStep timeStep) {
        	door.isBlocked = false;
		}
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
        this.doorState = DoorState.CLOSED;
        this.setFrontDoorHandle(new Vector2f(facing));
        this.rearDoorHandle = new Vector2f(facing);
        this.rearHingePos = new Vector2f();
        this.facing.set(facing);
        
        this.hinge = DoorHinge.fromVector(facing);
        this.handleTouchRadius = new Rectangle(48, 48);
        this.hingeTouchRadius = new Rectangle(48,48);
        this.autoCloseRadius = new Rectangle(100, 100);
        
        this.bounds.set(this.handleTouchRadius);
        this.hingeTouchRadius.centerAround(getPos());
        this.autoCloseRadius.centerAround(getPos());
        
        this.autoCloseTimer = new Timer(false, 5_000);
        this.autoCloseTimer.stop();
        
        this.rotation = new SmoothOrientation(0.05);
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
        Vector2f.Vector2fMA(getPos(), this.rotation.getFacing(), 64, this.getFrontDoorHandle());
        this.rearHingePos = this.hinge.getRearHingePosition(getPos(), this.rotation.getFacing(), this.rearHingePos);                
        Vector2f.Vector2fMA(this.rearHingePos, this.rotation.getFacing(), 64, this.rearDoorHandle);
        
        this.handleTouchRadius.centerAround(this.getFrontDoorHandle());
    }
    
    @Override
    public boolean update(TimeStep timeStep) {
        // if the door gets blocked by an Entity,
        // we stop the door.  Once the Entity moves
        // we continue opening the door.
        
        // The door can act as a shield to the entity
        doorState.update(this,timeStep);
        
        setOrientation(this.rotation.getOrientation());
        
        //Vector2f.Vector2fMA(getPos(), this.rotation.getFacing(), 64, this.doorHandle);
    //    DebugDraw.drawLineRelative(getPos(), this.frontDoorHandle, 0xffffff00);
     //   DebugDraw.drawLineRelative(this.rearHingePos, this.rearDoorHandle, 0xffffff00);
        
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
        if(this.doorState != DoorState.OPENED  ||
           this.doorState != DoorState.OPENING ||
           this.doorState != DoorState.CLOSING) {
            
            if(!canBeHandledBy(ent)) {
                return;
            }
            
            this.autoCloseTimer.reset();
                       
            this.doorState = DoorState.OPENING;
            this.game.emitSound(getId(), SoundType.DOOR_OPEN, getPos());
            
            setDoorDestinationOrientation(ent);
        }
    }

	private void setDoorDestinationOrientation(Entity ent) {
		//getPos();
		// figure out what side the entity is 
		// of the door hinge, depending on their
		// side, we set the destinationOrientation
		switch(this.hinge){
		    case NORTH_END:
		    case SOUTH_END:
		        new DoorSouthEnd().doorOpen(this,ent);
		        break;
		    case EAST_END:
		    case WEST_END:
		    	new DoorSouthEnd().doorOpen(this,ent);
		        break;
		    default:
		        break;   
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
            this.setTargetOrientation(this.hinge.getClosedOrientation());        
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
        for(int i = 0; i < game.getPlayerEntities().length; i++) {
            Entity other = game.getPlayerEntities()[i];
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
        boolean isTouching = Line.lineIntersectsRectangle(getPos(), this.getFrontDoorHandle(), bounds) ||
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
        return getFrontDoorHandle();
    }
    
    
    @Override
    public NetEntity getNetEntity() {
        this.setNetEntity(netDoor);
        this.netDoor.hinge = this.hinge.netValue();
        return this.netDoor;
    }

	public float getTargetOrientation() {
		return targetOrientation;
	}

	public void setTargetOrientation(float targetOrientation) {
		this.targetOrientation = targetOrientation;
	}

	public Vector2f getFrontDoorHandle() {
		return frontDoorHandle;
	}

	public void setFrontDoorHandle(Vector2f frontDoorHandle) {
		this.frontDoorHandle = frontDoorHandle;
	}

}
