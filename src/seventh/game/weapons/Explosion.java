/*
 * see license.txt 
 */
package seventh.game.weapons;

import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.game.net.NetEntity;
import seventh.game.net.NetExplosion;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.SoundType;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class Explosion extends Entity {

	private int damage;
	private Entity owner;
	private NetExplosion netEntity;
	private long explositionTime;
	
	private Rectangle center;
	private boolean checkedDestructableTiles;
	
	/**
	 * @param position
	 * @param speed
	 * @param game
	 * @param type
	 */
	public Explosion(Vector2f position, int speed, Game game, Entity owner, final int damage) {
		super(position, speed, game, Type.EXPLOSION);
				
		bounds.width = 60;
		bounds.height = 60;
		bounds.centerAround(position);
		
		center = new Rectangle();
		center.width = bounds.width/3;
		center.height = bounds.height/3;
		center.centerAround(position);
		
		this.damage = damage;
		this.owner = owner;
		
		game.emitSound(getId(), SoundType.EXPLOSION, getCenterPos());
		
		this.explositionTime = 550;
		this.checkedDestructableTiles = false;
		
		this.netEntity = new NetExplosion();
				
		this.onTouch = new OnTouchListener() {
			
			@Override
			public void onTouch(Entity me, Entity other) {
				if(other.getType() != Type.EXPLOSION && other.canTakeDamage()) {
					
					/* if this poor fellow is caught in the center
					 * of the blast they feel the full effect
					 */
					if(other.getBounds().intersects(center)) {
						other.damage(Explosion.this, damage);
					}
					else {
						/* splash damage is applied each frame,
						 * very deadly
						 */
						other.damage(Explosion.this, 2);						
					}
				}
			}
		};
	}
	
	/**
	 * @return the owner
	 */
	public Entity getOwner() {
		return owner;
	}
	
	/**
	 * @return the damage
	 */
	public int getDamage() {
		return damage;
	}

	/* (non-Javadoc)
	 * @see palisma.game.Entity#update(leola.live.TimeStep)
	 */
	@Override
	public boolean update(TimeStep timeStep) {
		super.update(timeStep);
		
		if(!this.checkedDestructableTiles) {
		    Vector2f center = getPos();
		    game.removeTileAtWorld((int)center.x, (int)center.y);
		    this.checkedDestructableTiles = true;
		}
		
		game.doesTouchPlayers(this);
		
		explositionTime -= timeStep.getDeltaTime();
		if(explositionTime <= 0 ) {
			kill(this);
		}
		
		return false;
	}

	/* (non-Javadoc)
	 * @see seventh.game.Entity#getNetEntity()
	 */
	@Override
	public NetEntity getNetEntity() {	
		return getNetExplotionEntity();
	}
	
	
	public NetEntity getNetExplotionEntity() {
		setNetEntity(netEntity);
		
//		this.netEntity.damage = (byte)this.damage;
		if(this.owner!=null) {
			this.netEntity.ownerId = this.owner.getId();
		}
		
		return this.netEntity;
	}
}
