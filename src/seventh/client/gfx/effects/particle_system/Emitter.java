/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import seventh.client.entities.ClientEntity;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.effects.Effect;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public class Emitter implements Effect {

    /**
     * Spawns and setup particles
     * 
     * @author Tony
     *
     */
    public static interface ParticleGenerator {
                
        /**
         * Resets the generator
         */
        public void reset();
        
        /**
         * Spawns the appropriate particles
         * 
         * @param timeStep
         * @param particles
         */
        public void update(TimeStep timeStep, ParticleData particles);
    }
    
    
    /**
     * Updates the particles
     * 
     * @author Tony
     *
     */
    public static interface ParticleUpdater {

        /**
         * Updates the particles
         * 
         * @param timeStep
         * @param particles
         */
        public void update(TimeStep timeStep, ParticleData particles);
    }
    
    /**
     * Spawns and setup particles
     * 
     * @author Tony
     *
     */
    public static interface ParticleRenderer {

        /**
         * Spawns the appropriate particles
         * 
         * @param timeStep
         * @param particles
         */
        public void update(TimeStep timeStep, ParticleData particles);
        
        public void render(Canvas canvas, Camera camera, float alpha, ParticleData particles);
    }
    
    protected Timer timeToLive;    
        
    private Vector2f pos;
    private Random random;
        
    private boolean dieInstantly;    
    private boolean kill, isPersistent;
    
    protected Rectangle visibileBounds;
    private ClientEntity attachedTo;
    
    private List<ParticleGenerator> generators;
    private List<ParticleUpdater> updaters;
    private List<ParticleRenderer> renderers;
    
    private ParticleData particles;
    private String name;
    
    /**
     * 
     */
    public Emitter(Vector2f pos, int timeToLive, int maxParticles) {
        this.pos = pos;
        
        this.generators = new ArrayList<>();
        this.updaters = new ArrayList<>();
        this.renderers = new ArrayList<>();
        
        this.particles = new ParticleData(maxParticles);
        this.particles.emitter = this;
        
        this.timeToLive = new Timer(false, timeToLive);                
        
                
        this.visibileBounds = new Rectangle(250, 250);
        this.visibileBounds.centerAround(pos);
        
        this.dieInstantly = true;
        
        this.kill = false;
        this.isPersistent = false;
        this.name = ""; // used for debugging purposes
    }
    
    public Emitter addParticleGenerator(ParticleGenerator generator) {
        this.generators.add(generator);
        return this;
    }
    
    public Emitter addParticleUpdater(ParticleUpdater updater) {
        this.updaters.add(updater);
        return this;
    }
    
    public Emitter addParticleRenderer(ParticleRenderer renderer) {
        this.renderers.add(renderer);
        return this;
    }
    
    /**
     * @return the name
     */
    public String getName() {
        return name;
    }
    
    /**
     * @param name the name to set
     */
    public Emitter setName(String name) {
        this.name = name;
        return this;
    }
    
    /**
     * If this emitter should stick around even after its timers have expired.  this should
     * be used if the emitter is cached and will want to be reused
     * 
     * @return the persist
     */
    public boolean isPersistent() {
        return isPersistent;
    }
    
    /**
     * @param persist the persist to set
     */
    public Emitter setPersistent(boolean persist) {
        this.isPersistent = persist;
        return this;
    }
        
    /**
     * @param dieInstantly the dieInstantly to set
     */
    public Emitter setDieInstantly(boolean dieInstantly) {
        this.dieInstantly = dieInstantly;
        return this;
    }
        
    /**
     * @return the dieInstantly
     */
    public boolean isDieInstantly() {
        return dieInstantly;
    }
    
    /**
     * @param pos the pos to set
     */
    public Emitter setPos(Vector2f pos) {
        this.pos.set(pos);
        this.visibileBounds.centerAround(pos);
        return this;
    }
    
    public Emitter attachTo(ClientEntity ent) {
        this.attachedTo = ent;
        this.pos.set(ent.getPos());
        return this;
    }
    
    public ClientEntity attachedTo() {
        return this.attachedTo;
    }
    
    /**
     * @return the random
     */
    public Random getRandom() {
        if(random==null) random = new Random();
        return random;
    }
    
    /**
     * @return the pos
     */
    public Vector2f getPos() {
        return pos;
    }
    
    public Emitter kill() {
        this.kill = true;
        return this;
    }
    
    
    /**
     * Starts this emitter
     */
    public Emitter start() {
        this.timeToLive.start();
        return this;
    }
    
    public Emitter stop() {
        this.timeToLive.expire();
        return this;
    }
    
    public Emitter pause() {
        this.timeToLive.pause();
        return this;
    }
    
    public Emitter reset() {
        resetTimeToLive();
        this.particles.reset();
        this.kill = false;
        return this;
    }
    
    public Emitter resetTimeToLive() {
        this.timeToLive.reset();
        for(int i = 0; i < this.generators.size(); i++) {
            this.generators.get(i).reset();
        }
        return this;
    }
    
    public long getTimeToLive() {
        return this.timeToLive.getEndTime();
    }
    
    /**
     * @return true if this is still active
     */
    public boolean isAlive() {
        if(this.kill) {
            return false;
        }
        
        boolean expired = this.timeToLive.isTime();
        boolean pseudoAlive = /*this.isPaused ||*/  this.isPersistent;
        
        if(isDieInstantly()) {
            return expired || pseudoAlive;
        }
                
        return (!expired || this.particles.numberOfAliveParticles > 0) || pseudoAlive;
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.particle.Effect#isDone()
     */
    @Override
    public boolean isDone() {    
        return !isAlive();
    }
    
    /* (non-Javadoc)
     * @see seventh.client.gfx.particle.Effect#destroy()
     */
    @Override
    public void destroy() {
        //reset();
        kill();
    }
        
    
    /* (non-Javadoc)
     * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
     */
    @Override
    public void update(TimeStep timeStep) {
        if(isAlive() /*&& !this.isPaused*/) {
            if(this.attachedTo!=null) {
                this.pos.set(this.attachedTo.getPos());
            }
            
            this.timeToLive.update(timeStep);
            
            if(!this.timeToLive.isTime()) {
                for(int i = 0; i < this.generators.size(); i++) {
                    this.generators.get(i).update(timeStep, this.particles);
                }
            }
            
            for(int i = 0; i < this.updaters.size(); i++) {
                this.updaters.get(i).update(timeStep, this.particles);
            }
            
            for(int i = 0; i < this.renderers.size(); i++) {
                this.renderers.get(i).update(timeStep, this.particles);
            }
            
            this.visibileBounds.centerAround(pos);
        }
    }
    
    /* (non-Javadoc)
     * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
     */
    @Override
    public void render(Canvas canvas, Camera camera, float alpha) {
        if(isAlive() /*&& !this.isPaused*/ && (this.attachedTo!=null || this.visibileBounds.intersects(camera.getWorldViewPort())) ) {
            for(int i = 0; i < this.renderers.size(); i++) {
                this.renderers.get(i).render(canvas, camera, alpha, this.particles);
            }
        }
    }
}
