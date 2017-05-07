/*
 * see license.txt 
 */
package seventh.game.weapons;

import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class Grenade extends Bullet {
    
    private long blowUpTime;
    private final int splashWidth;
    private final int maxSpread;
//    private final int flightSpeed;
    
    /**
     * @param position
     * @param speed
     * @param game
     * @param type
     */
    public Grenade(final Vector2f position, 
              final int speed, 
              final Game game, 
              final Entity owner, 
              final Vector2f targetVel, 
              final int damage) {
        super(position, speed, game, owner, targetVel, damage, false );
        
        this.setOrientation(owner.getOrientation());
        this.bounds.width = 5;
        this.bounds.height = 5;
        
        this.blowUpTime = 1500;
        this.splashWidth = 15;
        this.maxSpread = 25;
//        this.flightSpeed = 80;
                
        setType(Type.GRENADE); 
        
        this.onKill = new KilledListener() {
            
            @Override
            public void onKill(Entity entity, Entity killer) {                                
                game.newBigExplosion(getCenterPos(), owner, splashWidth, maxSpread, damage);
            }
        };
        
        this.onTouch = new OnTouchListener() {
            
            @Override
            public void onTouch(Entity me, Entity other) {
                /* the grenade is being thrown over collidables */
                //if(speed < flightSpeed) 
                {
                
                    targetVel.x = -targetVel.x;
                    targetVel.y = -targetVel.y;        
                    
                    decreaseSpeed(0.4f);
                }
            }
        };
        
    }
    
    /* (non-Javadoc)
     * @see seventh.game.weapons.Bullet#getOwnerHeightMask()
     */
    @Override
    protected int getOwnerHeightMask() {
        return Entity.STANDING_HEIGHT_MASK;
    }
    
    protected void decreaseSpeed(float factor) {
        speed =  (int)(speed * factor);
    }
    
    /* (non-Javadoc)
     * @see palisma.game.Entity#collideX(int, int)
     */
    @Override
    protected boolean collideX(int newX, int oldX) {
        
        /* the grenade is being thrown over collidables */
//        if(this.speed > flightSpeed) {
//            return false;
//        }
            
        this.targetVel.x = -this.targetVel.x;
        this.speed = (int)(this.speed * 0.7f);
        return true;
    }
    
    /* (non-Javadoc)
     * @see palisma.game.Entity#collideY(int, int)
     */
    @Override
    protected boolean collideY(int newY, int oldY) {
//        if(this.speed < flightSpeed) {
//            return false;
//        }
        
        this.targetVel.y = -this.targetVel.y;
        this.speed = (int)(this.speed * 0.7f);
        return true;        
    }
    
    /* (non-Javadoc)
     * @see palisma.game.Bullet#update(leola.live.TimeStep)
     */
    @Override
    public boolean update(TimeStep timeStep) {
        boolean isBlocked = super.update(timeStep);
        
        this.blowUpTime -= timeStep.getDeltaTime();
        if(this.speed > 0) {
            this.speed -= 5;
        }
        
        if(this.blowUpTime <= 0) {
            onBlowUp();
        }
        
        return isBlocked;
    }
    
    protected void onBlowUp() {
        kill(this);
    }
}
