/*
 * see license.txt 
 */
package seventh.game.weapons;

import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.game.net.NetEntity;
import seventh.game.net.NetFire;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class Fire extends Bullet {
    
    private int damage;
    private Entity owner;
    private NetFire netEntity;
    private long torchTime;
    
    
    /**
     * @param position
     * @param speed
     * @param game
     * @param type
     */
    public Fire(Vector2f position, int speed, Game game, Entity owner, final Vector2f targetVel, final int damage) {
        super(position, speed, game, owner, targetVel, damage, false);
                
        setType(Type.FIRE);
        
        bounds.width = 16;
        bounds.height = 16;
        
        this.damage = damage;
        this.owner = owner;
        
        this.torchTime = 10_000;
        
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
    @Override
    protected int getOwnerHeightMask() {    
        return 1;
    }

    /* (non-Javadoc)
     * @see palisma.game.Entity#update(leola.live.TimeStep)
     */
    @Override
    public boolean update(TimeStep timeStep) {
        boolean isBlocked = super.update(timeStep);
                
        //game.doesTouchPlayers(this);
        
        torchTime -= timeStep.getDeltaTime();
        if(torchTime <= 0 ) {
            kill(this);
        }
        
        if(this.speed > 0) {
            this.speed -= 5;
        }
        if(speed < 0) {
            speed = 0;
        }

        return isBlocked;
    }
    
    protected void decreaseSpeed(float factor) {
        speed =  (int)(speed * factor);
    }
    
    
    /* (non-Javadoc)
     * @see palisma.game.Entity#collideX(int, int)
     */
    @Override
    protected boolean collideX(int newX, int oldX) {            
        this.targetVel.x = -this.targetVel.x;
        this.speed = (int)(this.speed * 0.7f);
        return true;
    }
    
    /* (non-Javadoc)
     * @see palisma.game.Entity#collideY(int, int)
     */
    @Override
    protected boolean collideY(int newY, int oldY) {
        this.targetVel.y = -this.targetVel.y;
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
