/*
 * see license.txt 
 */
package seventh.client.entities;

import seventh.client.ClientGame;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.effects.particle_system.Emitter;
import seventh.client.gfx.effects.particle_system.Emitters;
import seventh.client.weapon.ClientWeapon;
import seventh.game.net.NetBullet;
import seventh.game.net.NetEntity;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class ClientBullet extends ClientEntity {
    
    private Vector2f origin;
    private int ownerId;
    private int iterations;
    
    private Vector2f vel;
        
    static class BulletOnRemove implements OnRemove {
        private Vector2f vel = new Vector2f();
        
        @Override
        public void onRemove(ClientEntity me, ClientGame game) {
            ClientBullet bullet = (ClientBullet)me;            
            bullet.trailEffect.kill();
            
            Vector2f.Vector2fSubtract(bullet.getPos(), bullet.getOrigin(), vel);
            Vector2f.Vector2fNormalize(vel, vel);
            
            Vector2f pos = me.getCenterPos();
            Vector2f.Vector2fMA(pos, vel, 5.0f, pos);
            
            if(!game.doesEntityTouchOther(me)) {
                game.addBackgroundEffect(Emitters.newBulletImpactEmitter(pos, vel));
            }
            
        }
    }
    
    private Emitter trailEffect;
    
    /**
     * @param game
     * @param pos
     */
    public ClientBullet(ClientGame game, Vector2f pos) {
        super(game, pos);
        origin = new Vector2f(pos);
        
        vel = new Vector2f();
                                
        bounds.width = 5;
        bounds.height = 5;
        
        trailEffect = Emitters.newBulletTracerEmitter(pos.createClone(), 10_000)
                              .attachTo(this);
        
        reset();
        
        setOnRemove(new BulletOnRemove());
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientEntity#reset()
     */
    @Override
    public void reset() {    
        super.reset();
        
        origin.zeroOut();
        vel.zeroOut();
        
        ownerId = -1;        
        lastUpdate = 0;

        prevState = null;
        nextState = null;
        
        iterations = 0;

        trailEffect.reset();
        
//        trailEffect = Emitters.newBulletTracerEmitter(pos.createClone(), 10_000)
//                  .attachTo(this);
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    /**
     * @param origin the origin to set
     */
    public void setOrigin(Vector2f origin) {
        this.pos.set(origin);
        this.origin.set(origin);
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientEntity#killIfOutdated(long)
     */
    @Override
    public boolean killIfOutdated(long gameClock) {    
        return (gameClock - lastUpdate) > 450; // 800                
    }
    
    /**
     * @return the ownerId
     */
    public int getOwnerId() {
        return ownerId;
    }
    
    /**
     * @return the origin
     */
    public Vector2f getOrigin() {
        return origin;
    }
    
    private void emitBulletCasing(int ownerId) {
        ClientEntity ent = game.getEntities().getEntity(ownerId);
        if(ent instanceof ClientPlayerEntity) {
            ClientPlayerEntity playerEntity = (ClientPlayerEntity)ent;
            ClientWeapon weapon = playerEntity.getWeapon();
            if(weapon!=null && weapon.isAutomatic()) {                
                playerEntity.emitBulletCasing();
            }
        }
    }
    
    /* (non-Javadoc)
     * @see palisma.client.ClientEntity#updateState(palisma.game.net.NetEntity)
     */
    @Override
    public void updateState(NetEntity state, long time) {        
        super.updateState(state,time);
        
        NetBullet bullet = (NetBullet)state;
        if(this.ownerId < 0) {
            emitBulletCasing(bullet.ownerId);
        }
        
        this.ownerId = bullet.ownerId;
        
        this.iterations++;
        
        if(vel.isZero()) {            
            /*ClientPlayer player = game.getPlayers().getPlayer(bullet.ownerId);
            if(player!=null&&player.isAlive()) {
                Vector2f.Vector2fCopy(player.getEntity().getFacing(), vel);
            }
            else*/ 
            {
                Vector2f.Vector2fSubtract(getPos(), getOrigin(), vel);
                Vector2f.Vector2fNormalize(vel, vel);
                facing.set(vel);
                movementDir.set(vel);                
            }            
        }
    }
    
    /* (non-Javadoc)
     * @see seventh.client.ClientEntity#update(seventh.shared.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        super.update(timeStep);
        
        
        Vector2f.Vector2fSubtract(getPos(), getOrigin(), vel);
        Vector2f.Vector2fNormalize(vel, vel);
        facing.set(vel);
        movementDir.set(vel);
        
//        if(Vector2f.Vector2fDistanceSq(getOrigin(), getPos()) > 20 ) {
//            Vector2f.Vector2fMS(getPos(), vel, tracerLength, oldPos);
//        }
        
        trailEffect.update(timeStep);
    }

    /* (non-Javadoc)
     * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        // only render if we have enough updates -- otherwise this
        // will render a single particle out of place, looks stupid
        if(iterations > 3) {
            trailEffect.render(canvas, camera, alpha);
        }
        
    }

}
