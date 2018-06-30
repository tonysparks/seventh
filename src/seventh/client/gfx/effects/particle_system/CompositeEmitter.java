/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import seventh.client.entities.ClientEntity;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class CompositeEmitter extends Emitter {

    private Emitter[] subEmitters;
    
    /**
     * 
     */
    public CompositeEmitter(Vector2f pos, int timeToLive, int maxParticles, Emitter ... subEmitters) {
        super(pos, timeToLive, maxParticles);
        
        this.subEmitters = subEmitters;
    }
    
    /**
     * @return the subEmitters
     */
    public Emitter[] getSubEmitters() {
        return subEmitters;
    }
    
    @Override
    public Emitter attachTo(ClientEntity ent) {
        for(int i = 0; i < this.subEmitters.length; i++) {
            this.subEmitters[i].attachTo(ent);
        }
        super.attachTo(ent);
        return this;        
    }
    
    @Override
    public Emitter setPersistent(boolean persist) {
        for(int i = 0; i < this.subEmitters.length; i++) {
            this.subEmitters[i].setPersistent(persist);
        }
        super.setPersistent(persist);
        return this;
    }
    
    @Override
    public Emitter setPos(Vector2f pos) {
        for(int i = 0; i < this.subEmitters.length; i++) {
            this.subEmitters[i].setPos(pos);
        }
        super.setPos(pos);
        return this;
    }
    
    @Override
    public CompositeEmitter kill() {
        for(int i = 0; i < this.subEmitters.length; i++) {
            this.subEmitters[i].kill();
        }
        
        super.kill();        
        return this;
    }
    
    
    @Override
    public CompositeEmitter start() {
        for(int i = 0; i < this.subEmitters.length; i++) {
            this.subEmitters[i].start();
        }
        super.start();
        return this;
    }
    
    @Override
    public CompositeEmitter stop() {
        for(int i = 0; i < this.subEmitters.length; i++) {
            this.subEmitters[i].stop();
        }
        super.stop();
        return this;
    }
    
    @Override
    public CompositeEmitter pause() {
        for(int i = 0; i < this.subEmitters.length; i++) {
            this.subEmitters[i].pause();
        }
        super.pause();
        return this;
    }
    
    @Override
    public CompositeEmitter reset() {
        for(int i = 0; i < this.subEmitters.length; i++) {
            this.subEmitters[i].reset();
        }
        super.reset();
        return this;
    }
    
    @Override
    public CompositeEmitter resetTimeToLive() {
        for(int i = 0; i < this.subEmitters.length; i++) {
            this.subEmitters[i].resetTimeToLive();
        }
        super.resetTimeToLive();
        return this;
    }
    
    @Override
    public void destroy() {
        for(int i = 0; i < this.subEmitters.length; i++) {
            this.subEmitters[i].destroy();
        }
        super.destroy();        
    }
    
    @Override
    public void update(TimeStep timeStep) {
        for(int i = 0; i < this.subEmitters.length; i++) {
            this.subEmitters[i].update(timeStep);
        }
        super.update(timeStep);
    }
    
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        for(int i = 0; i < this.subEmitters.length; i++) {
            this.subEmitters[i].render(canvas, camera, alpha);
        }
        super.render(canvas, camera, alpha);        
    }
}
