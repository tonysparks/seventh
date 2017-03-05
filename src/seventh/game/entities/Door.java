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
import seventh.shared.DebugDraw;
import seventh.shared.TimeStep;

/**
 * A Door in the game world.  It can be opened or closed.
 * 
 * @author Tony
 *
 */
public class Door extends Entity {

	enum DoorHinge {
		NORTH_END,
		SOUTH_END,
		EAST_END,
		WEST_END,
		
		;
		
		public byte netValue() {
			return (byte)ordinal();
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
	enum DoorState {
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
	 
	private Vector2f doorHandle;
	private Rectangle touchRadius;
	
	
	private SmoothOrientation rotation;
	private float targetOrientation;
	
	private NetDoor netDoor;
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
		this.doorHandle = new Vector2f(facing);
		this.facing.set(facing);
					
		this.hinge = DoorHinge.fromVector(facing);
		this.touchRadius = new Rectangle(64, 64);
		
		this.rotation = new SmoothOrientation(0.05);
		this.rotation.setOrientation(getClosedOrientation());
		
		Vector2f.Vector2fMA(getPos(), this.rotation.getFacing(), 64, this.doorHandle);
		
		this.netDoor = new NetDoor();
		
		setCanTakeDamage(true);
		
		this.onTouch = new OnTouchListener() {
			
			@Override
			public void onTouch(Entity me, Entity other) {
				// does nothing, just makes it so the game will
				// acknowledge these two entities can touch each other
			}
		};
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
				
				Vector2f.Vector2fMA(getPos(), this.rotation.getFacing(), 64, this.doorHandle);
				if(this.game.doesTouchPlayers(this)) {
					this.rotation.setOrientation(currentRotation);
					Vector2f.Vector2fMA(getPos(), this.rotation.getFacing(), 64, this.doorHandle);
				}
				else if(!this.rotation.moved()) {
					this.doorState = DoorState.OPENED;
				}
				
				break;
			}
			case CLOSING: {
				this.rotation.setDesiredOrientation(this.targetOrientation);
				this.rotation.update(timeStep);

				Vector2f.Vector2fMA(getPos(), this.rotation.getFacing(), 64, this.doorHandle);
				if(this.game.doesTouchPlayers(this)) {
					this.rotation.setOrientation(currentRotation);
					Vector2f.Vector2fMA(getPos(), this.rotation.getFacing(), 64, this.doorHandle);
				}
				else if(!this.rotation.moved()) {
					this.doorState = DoorState.CLOSED;
				}
				
				break;
			}
			default: { 
				/* do nothing */ 
			}
		}
		
		
		//Vector2f.Vector2fMA(getPos(), this.rotation.getFacing(), 64, this.doorHandle);
		DebugDraw.drawLineRelative(getPos(), this.doorHandle, 0xffff00ff);
		
		DebugDraw.drawStringRelative("State: " + this.doorState, (int)getPos().x, (int)getPos().y, 0xffff00ff);
		DebugDraw.drawStringRelative("Orientation: C:" + (int)Math.toDegrees(this.rotation.getOrientation())  + "   D:" + (int)Math.toDegrees(this.rotation.getDesiredOrientation()) , (int)getPos().x, (int)getPos().y + 20, 0xffff00ff);
		
		return false;
	}
	
	public boolean isOpened() {
		return this.doorState == DoorState.OPENED;
	}
	
	public boolean isClosed() {
		return this.doorState == DoorState.CLOSED;
	}
	
	public void open(Entity ent) {
		if(this.doorState != DoorState.OPENED  ||
		   this.doorState != DoorState.OPENING ||
		   this.doorState != DoorState.CLOSING) {
			
			if(!canBeHandledBy(ent)) {
				return;
			}
			
			this.doorState = DoorState.OPENING;
			
			Vector2f entPos = ent.getCenterPos();
			Vector2f hingePos = getCenterPos();
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
			this.targetOrientation = getClosedOrientation();			
		}
	}
	
	private float getClosedOrientation() {
		switch(this.hinge) {		
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
		Vector2f entPos = ent.getCenterPos();
		Vector2f hingePos = getCenterPos();
		
		
		// figure out what side the entity is 
		// of the door hinge, depending on their
		// side, we adjust the door hit box
		switch(this.hinge) {
			
			case NORTH_END:
				if(entPos.x < hingePos.x) {					
					this.touchRadius.setLocation( (int)hingePos.x - 64, (int)hingePos.y - 64);
				}
				else if(entPos.x > hingePos.x) {
					this.touchRadius.setLocation( (int)hingePos.x + 0, (int)hingePos.y - 64);
				}
				break;
			case SOUTH_END:
				if(entPos.x < hingePos.x) {					
					this.touchRadius.setLocation( (int)hingePos.x - 64, (int)hingePos.y);
				}
				else if(entPos.x > hingePos.x) {
					this.touchRadius.setLocation( (int)hingePos.x + 0, (int)hingePos.y);
				}
				break;
			case EAST_END:
				if(entPos.y < hingePos.y) {					
					this.touchRadius.setLocation( (int)hingePos.x - 64, (int)hingePos.y - 64);
				}
				else if(entPos.y > hingePos.y) {					
					this.touchRadius.setLocation( (int)hingePos.x - 64, (int)hingePos.y + 0);
				}
				break;
			case WEST_END:
				if(entPos.y < hingePos.y) {					
					this.touchRadius.setLocation( (int)hingePos.x + 0, (int)hingePos.y - 64);
				}
				else if(entPos.y > hingePos.y) {					
					this.touchRadius.setLocation( (int)hingePos.x + 0, (int)hingePos.y + 0);
				}
				break;
			default:
				break;			
		}
		
		DebugDraw.fillRectRelative(touchRadius.x, touchRadius.y, touchRadius.width, touchRadius.height, 0xff00ff00);
		
		return (this.touchRadius.intersects(ent.getBounds()));
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
		return Line.lineIntersectsRectangle(getPos(), this.doorHandle, bounds);
	}
	
	
	
	@Override
	public NetEntity getNetEntity() {
		
		return this.netDoor;
	}

}
