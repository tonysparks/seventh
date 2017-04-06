/*
 * see license.txt 
 */
package seventh.game.weapons;

import seventh.game.Game;
import seventh.game.entities.Entity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public class SmokeGrenade extends Grenade {

    private Timer emitSmokeTimer;
    private Timer timeToLive;
    
    /**
     * @param position
     * @param speed
     * @param game
     * @param owner
     * @param targetVel
     * @param damage
     */
    public SmokeGrenade(Vector2f position, int speed, final Game game, Entity owner, Vector2f targetVel) {
        super(position, speed, game, owner, targetVel, 0);
        setType(Type.SMOKE_GRENADE);
        
        this.emitSmokeTimer = new Timer(true, 800).stop();
        this.timeToLive = new Timer(false, 10_000 + (game.getRandom().nextInt(10) * 100)).stop();
        
        this.onKill = new KilledListener() {
            
            @Override
            public void onKill(Entity entity, Entity killer) {                                
                
//                final Timer smokeEmitter = new Timer(true, 500) {
//                    public void onFinish(Timer timer) {
//                        Vector2f vel = new Vector2f(1, 0);
//                        Vector2f.Vector2fRotate(vel, Math.toRadians(game.getRandom().nextInt(360)), vel);
//                        game.newSmoke(getPos(), 10, vel, getOwner());
//                    }
//                };
//                
//                smokeEmitter.start();
//                game.addGameTimer(smokeEmitter);
//                
//                final Timer timeToLive = new Timer(false, 5_000 + (game.getRandom().nextInt(10) * 100)) {
//                    public void onFinish(Timer timer) {
//                        smokeEmitter.stop();
//                    }
//                };
//                
//                game.addGameTimer(timeToLive);
            }
        }; 

    }
    
    /* (non-Javadoc)
     * @see seventh.game.weapons.Grenade#update(seventh.shared.TimeStep)
     */
    @Override
    public boolean update(TimeStep timeStep) {
        this.emitSmokeTimer.update(timeStep);
        this.timeToLive.update(timeStep);
        
        if(this.emitSmokeTimer.isOnFirstTime()) {
            Vector2f vel = new Vector2f(1, 0);
            Vector2f.Vector2fRotate(vel, Math.toRadians(game.getRandom().nextInt(360)), vel);            
            Smoke smoke = game.newSmoke(getPos().createClone(), 50, vel, getOwner());
            Vector2f center = getPos();
            int radius = 150;
            Vector2f spawn = game.findFreeRandomSpot(smoke, (int)center.x - radius, (int)center.y - radius, radius*2, radius*2);
            if(spawn!=null) {
                smoke.getPos().set(spawn);
            }
        }
        
        if(this.timeToLive.isTime()) {
            kill(this);
        }
        
        if(!this.timeToLive.isUpdating()) {
            return super.update(timeStep);
        }
        
        return true;
    }
    
    
    
    @Override
    protected void onBlowUp() {
        this.emitSmokeTimer.start();
        this.timeToLive.start();
    }

}
