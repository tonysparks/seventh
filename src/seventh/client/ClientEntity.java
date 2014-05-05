/*
 * see license.txt 
 */
package seventh.client;

import java.util.Random;

import seventh.client.gfx.Renderable;
import seventh.game.Entity.Type;
import seventh.game.net.NetEntity;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public abstract class ClientEntity implements Renderable {
	protected int id;
	
	protected ClientGame game;
	protected Vector2f pos, facing, centerPos;
	protected Rectangle bounds;
	protected float orientation;
		
	protected long lastUpdate;
//	protected int events;
	
	protected Type type;
	
	private boolean updateReceived;
	
	protected NetEntity prevState, nextState;
	protected long prevTime, nextTime;
	
	private boolean isAlive;
	
	/**
	 * Invoked on each update
	 * @author Tony
	 *
	 */
	public static interface OnUpdate {
		void onUpdate(TimeStep timeStep, ClientEntity me);
	}
	
	private OnUpdate onUpdate;
	
	/**
	 * Invoked when removed from the world.
	 * @author Tony
	 *
	 */
	public static interface OnRemove {
		void onRemove(ClientEntity me, ClientGame game);
	}
	
	private OnRemove onRemove;
	
	
	/**
	 * 
	 */
	public ClientEntity(ClientGame game, Vector2f pos) {
		this.game = game;
		this.pos = pos;
//		this.pos = new Vector2f();		
		this.facing = new Vector2f();
		this.bounds = new Rectangle();
		this.centerPos = new Vector2f();
		this.isAlive = true;
//		this.pos.set(pos);
	}
	
	/**
	 * Updates the state of this entity
	 * 
	 * @param state
	 */
	public void updateState(NetEntity state, long time) {
		this.prevState = nextState;
		this.nextState = state;
		
		this.prevTime = this.nextTime;
		this.nextTime = time;
		
		this.id = state.id;
		this.type = Type.fromNet(state.type);
				
		//this.pos.set(state.posX, state.posY);
		this.bounds.setLocation(pos);
//		this.bounds.setSize(state.width, state.height);
		
		this.orientation = (float)Math.toRadians(state.orientation);
		this.facing.set(1,0);
		Vector2f.Vector2fRotate(facing, orientation, facing);
		
//		this.events = state.events;
		
		this.updateReceived = true;
	}
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
//		checkEvents(events);
		
		if(this.updateReceived) {
			lastUpdate = timeStep.getGameClock();
//			events = 0;
			this.updateReceived = false;
		}		
		
		interpolate(timeStep);
		
		if(onUpdate != null) {
			onUpdate.onUpdate(timeStep, this);
		}
	}
	
//	protected void checkEvents(int events) {		
//	}
	
	/**
	 * @return the onRemove
	 */
	public OnRemove getOnRemove() {
		return onRemove;
	}
	
	/**
	 * @param onRemove the onRemove to set
	 */
	public void setOnRemove(OnRemove onRemove) {
		this.onRemove = onRemove;
	}
	
	/**
	 * @return the onUpdate
	 */
	public OnUpdate getOnUpdate() {
		return onUpdate;
	}
	
	/**
	 * @param onUpdate the onUpdate to set
	 */
	public void setOnUpdate(OnUpdate onUpdate) {
		this.onUpdate = onUpdate;
	}
	
	/**
	 * @return true if the object should be rendered first (this
	 * is applicable to bombs, dropped items, etc. stuff that entities
	 * will render over
	 */
	public boolean isBackgroundObject() {
		return false;
	}
	
	
	/**
	 * @return the isAlive
	 */
	public boolean isAlive() {
		return isAlive;
	}
	
	/**
	 * @param isAlive the isAlive to set
	 */
	public void setAlive(boolean isAlive) {
		this.isAlive = isAlive;
	}
	
	
	/**
	 * Interpolates between the previous and current state sent
	 * from the server.
	 * 
	 * @param timeStep
	 */
	protected void interpolate(TimeStep timeStep) {
		if(this.prevState != null && this.nextState != null) {
			//long deltaTime = timeStep.getDeltaTime();
				
			// TODO :: figure out ping time
			float alpha = 0.75f; //(timeStep.getGameClock() - prevState.)
			//long cmdTime = prevTime + 33;
			//float alpha = ((cmdTime - prevTime) / (nextTime - prevTime)) / 1000.0f;
			
			float dist = (pos.x - nextState.posX) * (pos.x - nextState.posX) + 
						 (pos.y - nextState.posY) * (pos.y - nextState.posY);
			
			/* if the entity is more than two tile off, snap
			 * into position
			 */
			if(dist > 64 * 64) {
				this.pos.x = nextState.posX;
				this.pos.y = nextState.posY;					
			}
			else {
			
				this.pos.x = pos.x + (alpha * (nextState.posX - pos.x));
				this.pos.y = pos.y + (alpha * (nextState.posY - pos.y));
			}
			
			this.bounds.setLocation(pos);
			
			//if( Math.abs(prevState.orientation - nextState.orientation) > (Math.PI/2))
			if( Math.abs(prevState.orientation - nextState.orientation) > (30))
			{
				this.orientation = nextState.orientation;
			}
			else {
				this.orientation = prevState.orientation + (alpha * (nextState.orientation - prevState.orientation));
			}
			
			this.orientation = (float) Math.toRadians(this.orientation);
		}
	}
	
	public Vector2f getPrevPos() {
		return this.pos;
	}

	/**
	 * @return the random number generator
	 */
	public Random getRandom() {
		return game.getRandom();
	}
	
	/**
	 * @return the id
	 */
	public int getId() {
		return id;
	}

	/**
	 * @return the pos
	 */
	public Vector2f getPos() {
		return pos;
	}
	
	public Vector2f getCenterPos() {
		centerPos.set(pos.x + bounds.width/2, pos.y + bounds.height/2);
		return centerPos;
	}

	/**
	 * @return the facing
	 */
	public Vector2f getFacing() {		
		return facing;
	}

	/**
	 * @return the bounds
	 */
	public Rectangle getBounds() {
		return bounds;
	}

	/**
	 * @return the orientation
	 */
	public float getOrientation() {
		return orientation;
	}

	/**
	 * @return the lastUpdate
	 */
	public long getLastUpdate() {
		return lastUpdate;
	}

	/**
	 * @return the events
	 */
//	public int getEvents() {
//		return events;
//	}

	/**
	 * @return the type
	 */
	public Type getType() {
		return type;
	}
	
	/**
	 * @return true if this entity should be killed
	 * off because an update has not been sent out for it
	 * in a bit of time
	 */
	public boolean killIfOutdated(long gameClock) {
		return (gameClock - lastUpdate) > 2000;
	}
	
}
