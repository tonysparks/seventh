/*
 * see license.txt 
 */
package seventh.game.weapons;

import seventh.game.Entity;
import seventh.game.Game;
import seventh.game.SoundType;
import seventh.game.net.NetBullet;
import seventh.game.net.NetEntity;
import seventh.map.Map;
import seventh.map.Tile;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class Bullet extends Entity {

	private Entity owner;
	protected Vector2f targetVel;
	private Vector2f previousPos, delta, origin;
	private int damage;
	
	private NetBullet netBullet;
	private int ownerHeightMask;
	
	private boolean piercing;
	private Entity lastEntityTouched;
	
	private static class BulletOnTouchListener implements Entity.OnTouchListener {
		private Game game;
		public BulletOnTouchListener(Game game) {
			this.game = game;
		}
		/* (non-Javadoc)
		 * @see palisma.game.Entity.OnTouchListener#onTouch(palisma.game.Entity, palisma.game.Entity)
		 */
		@Override
		public void onTouch(Entity me, Entity other) {
			Bullet bullet = (Bullet)me;
			Type otherType = other.getType();
			if(otherType == Type.PLAYER || otherType == Type.MECH) {
				if(other != bullet.lastEntityTouched) {
					
					other.damage(me, bullet.getDamage());										
					if(otherType==Type.MECH) {												
						bullet.kill(other);		
						game.emitSound(bullet.getId(), SoundType.IMPACT_METAL, bullet.getCenterPos());
					}
					else {
						
						if(!bullet.isPiercing()) {					
							bullet.kill(other);	
						}	
					}
																																			
					bullet.lastEntityTouched = other;
				}
			}
			else if (otherType == Type.LIGHT_BULB) {
				bullet.kill(other);
				other.kill(bullet);
			}
			
		}
	}
	
	/**
	 * @param position
	 * @param speed
	 * @param game
	 * @param owner
	 */
	public Bullet(Vector2f position, int speed, final Game game, Entity owner, Vector2f targetVel, int damage, boolean isPiercing) {
		super(position, speed, game, Type.BULLET);
		
		this.owner = owner;
		this.targetVel = targetVel;
		this.damage = damage;
		
		this.bounds.width = 4;
		this.bounds.height = 4;
		this.bounds.setLocation(getPos());
		
		this.setOrientation(owner.getOrientation());
		
		this.netBullet = new NetBullet();
		this.netBullet.damage = (byte)damage;
		this.netBullet.id = getId();
		this.netBullet.type = Type.BULLET.netValue();
				
		this.previousPos = new Vector2f();
		this.delta = new Vector2f();
		this.origin = new Vector2f(position);
		this.onTouch = new BulletOnTouchListener(game);
		
		this.ownerHeightMask = owner.getHeightMask();
		this.piercing = isPiercing;
	}
	
	/**
	 * @return the piercing
	 */
	public boolean isPiercing() {
		return piercing;
	}

	/**
	 * @return the height mask for if the entity is crouching or standing
	 */
	protected int getOwnerHeightMask() {
		return ownerHeightMask;
	}
	

	/**
	 * @return the damage points of this bullet
	 */
	public int getDamage() {
		return damage;
	}
	
	/**
	 * @return the owner
	 */
	public Entity getOwner() {
		return owner;
	}
	
	/**
	 * Emits an impact sound depending on the collision tile
	 * @param x
	 * @param y
	 */
	protected void emitImpactSound(int x, int y) {
		Tile tile = game.getMap().getWorldTile(0, x, y);
		if(tile!=null) {
			switch(tile.getSurfaceType()) {			
				case GRASS:
					game.emitSound(getId(), SoundType.IMPACT_FOLIAGE, getPos());
					break;
				case METAL:
					game.emitSound(getId(), SoundType.IMPACT_METAL, getPos());
					break;
				
				case WOOD:
					game.emitSound(getId(), SoundType.IMPACT_WOOD, getPos());
					break;
				case CEMENT:
				case DIRT:
				case UNKNOWN:
				case SAND:			
				case WATER:				
				default:
					game.emitSound(getId(), SoundType.IMPACT_DEFAULT, getPos());
					break;				
			}			
		}
		else {
			game.emitSound(getId(), SoundType.IMPACT_DEFAULT, getPos());
		}
	}
	
	
	/* (non-Javadoc)
	 * @see palisma.game.Entity#collideX(int, int)
	 */
	@Override
	protected boolean collideX(int newX, int oldX) {
		kill(this);
		emitImpactSound(oldX, bounds.y);
		return true;
	}
	
	/* (non-Javadoc)
	 * @see palisma.game.Entity#collideY(int, int)
	 */
	@Override
	protected boolean collideY(int newY, int oldY) {
		kill(this);
		emitImpactSound(bounds.x, oldY);
		return true;
	}
			
	/* (non-Javadoc)
	 * @see palisma.game.Entity#update(leola.live.TimeStep)
	 */
	@Override
	public boolean update(TimeStep timeStep) {	
		
		boolean isBlocked = false;
		vel.set(targetVel);
		
		Map map = this.game.getMap();
		
		double dt = timeStep.asFraction();
		
		previousPos.set(pos);
		
		int newX = (int)Math.round(pos.x + vel.x * speed * dt);
		int newY = (int)Math.round(pos.y + vel.y * speed * dt);
		
		delta.x = newX - previousPos.x;
		delta.y = newY - previousPos.y;
		
		int dx = delta.x < 0 ? -1 : delta.x > 0 ? 1 : 0;
		int dy = delta.y < 0 ? -1 : delta.y > 0 ? 1 : 0;
		
		int heightMask = getOwnerHeightMask();
				
		if(dx != 0 || dy != 0) {						
			do {		
				
				if(bounds.x != newX) {
					if(dx==0) {
//						System.out.println("x WHAT!");	
						break;
					}
					
					bounds.x += dx;
					if( map.rectCollides(bounds, heightMask) ) {
						isBlocked = collideX(newX, bounds.x-dx);
						if(isBlocked) {
							bounds.x -= dx;
						}
					}
				}
				
				if(bounds.y != newY && !isBlocked) {

					if(dy==0) {
//						System.out.println("y WHAT!");
						break;
					}
					
					bounds.y += dy;
					if( map.rectCollides(bounds, heightMask)) {
						isBlocked = collideY(newY, bounds.y-dy);	
						if(isBlocked) {
							bounds.y -= dy;
						}
					}
					
 				}
				
				if( bounds.y < 0 
					|| bounds.x < 0
					|| (bounds.y > map.getMapHeight() + 80)
					|| (bounds.x > map.getMapWidth() + 80)) {
					
					kill(this);
					break;
				}
				else {
					if ( game.doesTouchPlayers(this, origin, targetVel) && !this.piercing ) {
						break;
					}					
				}
			} while(!isBlocked && (bounds.x != newX || bounds.y != newY));
		}
		else {
			game.doesTouchPlayers(this, origin, targetVel);
		}
		
		getPos().set(bounds.x, bounds.y);
			
		
		return isBlocked;
	}
	
//	/* (non-Javadoc)
//	 * @see palisma.game.Entity#update(leola.live.TimeStep)
//	 */
//	@Override
//	public boolean update(TimeStep timeStep) {	
//		this.previousPos.set(getPos());
//		boolean isBlocked = super.update(timeStep);
//		vel.set(targetVel);
//		
//		Map map = this.game.getMap();
//		
//		if(isBlocked 
//			|| pos.y < 0 
//			|| pos.x < 0
//			|| (pos.y > map.getMapHeight() + 80)
//			|| (pos.x > map.getMapWidth() + 80)) {
//			
//			kill(this);
//		}
//		else {
//			
//			Vector2f.Vector2fSubtract(getPos(),this.previousPos, this.delta);
//			int dx = (int)this.delta.x < 0 ? -2 : (int)this.delta.x > 0 ? 2 : 0;
//			int dy = (int)this.delta.y < 0 ? -2 : (int)this.delta.y > 0 ? 2 : 0;
//			
//			if(dx != 0 || dy != 0) {
//				
//				while(!Vector2f.Vector2fApproxEquals(this.previousPos, getPos(), 5.0f)) {				
//					if ( game.doesTouchPlayers(this) ) {
//						break;
//					}
//					
//					if( ! FloatUtil.eq(this.previousPos.x, pos.x, 5.0f ) ) {
//						this.previousPos.x += dx;
//					}
//					
//					if( ! FloatUtil.eq(this.previousPos.y, pos.y, 5.0f ) ) {
//						this.previousPos.y += dy;
//					}
//								
//					this.bounds.setLocation(this.previousPos);
//				}
//			}
//			else {
//				game.doesTouchPlayers(this);
//			}
//		}
//		
//		return isBlocked;
//	}
	
	/* (non-Javadoc)
	 * @see palisma.game.Entity#getNetEntity()
	 */
	@Override
	public NetEntity getNetEntity() {	
		return getNetBullet();
	}
	
	/**
	 * @return the netBullet
	 */
	public NetBullet getNetBullet() {		
		setNetEntity(netBullet);
					
		netBullet.damage = (byte)this.damage;		
		netBullet.ownerId = this.owner.getId();
				
//		netBullet.targetVelX = this.targetVel.x;
//		netBullet.targetVelY = this.targetVel.y;
		netBullet.type = getType().netValue();
				
		return netBullet;
	}
	
}
