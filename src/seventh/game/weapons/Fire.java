/*
 * see license.txt 
 */
package seventh.game.weapons;

import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.game.net.NetEntity;
import seventh.game.net.NetFire;
import seventh.map.Map;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class Fire extends Entity {
    
    private int damage;
    private Entity owner;
    private NetFire netEntity;
    private long torchTime;
    private Vector2f targetVel;
    
    /**
     * @param position
     * @param speed
     * @param game
     * @param type
     */
    public Fire(Vector2f position, int speed, Game game, Entity owner, final Vector2f targetVel, final int damage) {
        super(position, speed, game, Type.FIRE);                
        
        bounds.width = 8;
        bounds.height = 8;        
        
        this.damage = damage;
        this.owner = owner;
        
        this.torchTime = 10_000;
        this.targetVel = targetVel;
        
        this.collisionHeightMask = 0;                
        
        this.netEntity = new NetFire();
                
        this.onTouch = new OnTouchListener() {
            
            @Override
            public void onTouch(Entity me, Entity other) {
                if(other.getType() == Type.PLAYER && other.canTakeDamage()) {                                        
                    other.damage(Fire.this, 2);                                            
                }
                
                decreaseSpeed(0.0f);
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
     * @see seventh.game.weapons.Bullet#getOwnerHeightMask()
     */
//    @Override
//    protected int getOwnerHeightMask() {    
//        return 1;
//    }

    /* (non-Javadoc)
     * @see palisma.game.Entity#update(leola.live.TimeStep)
     */
    @Override
    public boolean update(TimeStep timeStep) {
        this.vel.set(this.targetVel);
        boolean isBlocked = super.update(timeStep);
                
        Map map = game.getMap();
        
        // grow the hit box
        // TODO: Hide players who
        // are within the bounds of smoke
        // to prevent cheating!!
        if(bounds.width < 64) {
            bounds.width += 1;
            if( map.rectCollides(bounds, 0) ) {
                isBlocked = true;
                bounds.width -= 1;
                                
            }        
        }
        
        if(bounds.height < 64) {
            bounds.height += 1;
            if( map.rectCollides(bounds, 0)) {
                isBlocked = true;
                bounds.height -= 1;
            }
        }
        
        
        torchTime -= timeStep.getDeltaTime();
        if(torchTime <= 0 ) {
            kill(this);
        }
        
        if(this.speed > 0) {
            this.speed -= 1;
        }
        if(speed < 0) {
            speed = 0;
        }
        
        game.doesTouchPlayers(this);

       // DebugDraw.drawRectRelative(this.bounds, 0x0a00ffff);
        
        return isBlocked;
    }
    
    @Override
    protected boolean collidesAgainstEntity(Rectangle bounds) {
        return collidesAgainstDoor(bounds) || collidesAgainstVehicle(bounds);
    }
    
    protected void decreaseSpeed(float factor) {
        speed =  (int)(speed * factor);
    }
    
    
    /* (non-Javadoc)
     * @see palisma.game.Entity#collideX(int, int)
     */
    @Override
    protected boolean collideX(int newX, int oldX) {            
       // this.targetVel.x = -this.targetVel.x;
        this.speed = (int)(this.speed * 0.7f);
        return true;
    }
    
    /* (non-Javadoc)
     * @see palisma.game.Entity#collideY(int, int)
     */
    @Override
    protected boolean collideY(int newY, int oldY) {
     //   this.targetVel.y = -this.targetVel.y;
        this.speed = (int)(this.speed * 0.7f);
        return true;        
    }

    /* (non-Javadoc)
     * @see seventh.game.Entity#getNetEntity()
     */
    @Override
    public NetEntity getNetEntity() {    
        return getNetFireEntity();
    }
    
    
    public NetEntity getNetFireEntity() {
        setNetEntity(netEntity);        
        this.netEntity.ownerId = this.owner.getId();        
        return this.netEntity;
    }
}
