/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import seventh.client.ClientEntity;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public abstract class Emitter implements Effect {

	protected Timer timeToLive, nextSpawn;	
	private List<Particle> particles, deadParticles;
	
	private Vector2f pos;
	private Random random;
	
	protected int maxParticles;
	private int numberOfParticles;	
	
	private boolean dieInstantly;
	private boolean decrementParticles;
	
	private boolean kill, isPaused, isPersistent;
	
	protected Rectangle visibileBounds;
	/**
	 * 
	 */
	public Emitter(Vector2f pos, int timeToLive, int timeToNextSpawn) {
		this.pos = pos;
		
		this.timeToLive = new Timer(false, timeToLive);				
		this.nextSpawn = new Timer(true, timeToNextSpawn);		
		
		this.particles = new ArrayList<Particle>();
		this.deadParticles = new ArrayList<Particle>();
		
		this.visibileBounds = new Rectangle(250, 250);
		this.visibileBounds.centerAround(pos);
		
		this.maxParticles = -1;
		this.dieInstantly = true;
		this.decrementParticles = false;
		
		this.kill = false;
		this.isPaused = false;
		this.isPersistent = false;
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
    public void setPersistent(boolean persist) {
        this.isPersistent = persist;
    }
	
	/**
	 * @param decrementParticles the decrementParticles to set
	 */
	public void setDecrementParticles(boolean decrementParticles) {
		this.decrementParticles = decrementParticles;
	}
	
	/**
	 * @param dieInstantly the dieInstantly to set
	 */
	public void setDieInstantly(boolean dieInstantly) {
		this.dieInstantly = dieInstantly;
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
	public void setPos(Vector2f pos) {
		this.pos.set(pos);
		this.visibileBounds.centerAround(pos);
	}
	
	public void attachTo(ClientEntity ent) {
		this.pos = ent.getPos(); // TODO a bug waiting to happen
	}
	
	/**
	 * @return the random
	 */
	public Random getRandom() {
		if(random==null) random = new Random();
		return random;
	}
	
	protected void spread(Vector2f facing, int maxSpread) {
		Random random = getRandom();
		
		double rd = random.nextInt(maxSpread) / 100.0;
		int sd = random.nextInt(2);
		if (sd>0) {
			Vector2f.Vector2fRotate(facing, rd, facing);
		}
		else {
			Vector2f.Vector2fRotate(facing, -rd, facing);
		}
	}

	/**
	 * @return the pos
	 */
	public Vector2f getPos() {
		return pos;
	}
	
	public void kill() {
		this.kill = true;
	}
	
	
	/**
	 * Starts this emitter
	 */
	public void start() {
		this.timeToLive.start();
		this.nextSpawn.start();
		this.isPaused = false;
	}
	
	public void stop() {
		this.timeToLive.stop();
		this.nextSpawn.stop();
		this.isPaused = false;
	}
	
	public void pause() {
	    this.timeToLive.pause();
	    this.nextSpawn.pause();
	    this.isPaused = true;
	}
	
	public void reset() {
		this.timeToLive.reset();
		this.nextSpawn.reset();
		this.particles.clear();
		this.deadParticles.clear();
		this.numberOfParticles = 0;
		this.kill = false;
	}
	
	public void resetTimeToLive() {
		this.timeToLive.reset();
		this.numberOfParticles = 0;
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
		
		if(!isDieInstantly()) {
			return (!this.timeToLive.isTime() || !this.particles.isEmpty()) || this.isPaused || this.isPersistent;
		}
		
		return !this.timeToLive.isTime() || this.isPaused || this.isPersistent;
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
		reset();
		kill();
	}
	
	/**
	 * Spawns a particle
	 */
	protected boolean spawnParticle() {
		if(maxParticles < 0 || numberOfParticles < maxParticles) {
			this.particles.add(newParticle());		
			this.numberOfParticles++;
			return true;
		}
		return false;
	}
	
	/**
	 * @return a new {@link Particle}
	 */
	protected abstract Particle newParticle();
	
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		if(isAlive()) {
			this.timeToLive.update(timeStep);
			this.nextSpawn.update(timeStep);
			if(this.nextSpawn.isOnFirstTime() && !this.timeToLive.isTime()) {
				/** Determine if this emitter wants all of the particles created
				 * on this frame
				 */
				if(maxParticles>0 && this.nextSpawn.getEndTime() == 0) {					
					while(spawnParticle());
				}
				else {
					spawnParticle();
				}
			}
			

			this.particles.removeAll(this.deadParticles);
			this.deadParticles.clear();
			
			int size = this.particles.size();
			for(int i = 0; i < size; i++) {
				Particle p = this.particles.get(i);
				if(p.isAlive()) {
					p.update(timeStep);
				}
				else {
					this.deadParticles.add(p);
					if(decrementParticles) {
						this.numberOfParticles--;
					}
				}
			}
			
			
			this.visibileBounds.centerAround(pos);
		}
	}
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		if(isAlive() && this.visibileBounds.intersects(camera.getWorldViewPort())) {
			int size = this.particles.size();
			for(int i = 0; i < size; i++) {
				Particle p = this.particles.get(i);
				if(p.isAlive()) {
					p.render(canvas, camera, alpha);
				}
			}
		}
	}
}
