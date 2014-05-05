/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import java.util.Random;

import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class BloodEmitter extends Emitter {

	private int particleTimeToLive;
	
	/**
	 * @param pos
	 * @param timeToLive
	 * @param timeToNextSpawn
	 */
	public BloodEmitter(Vector2f pos, int maxParticles, int emitterTimeToLive, int particleTimeToLive) {
		super(pos, emitterTimeToLive, 100);		
		//this.nextSpawn.setLoop(false);
		this.maxParticles = maxParticles;
		this.particleTimeToLive = particleTimeToLive;
		
	}
	
	/**
	 * @param pos
	 * @param timeToLive
	 * @param timeToNextSpawn
	 */
	public BloodEmitter(Vector2f pos) {
		this(pos, 3, 5200, 4000);
	}
	
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Emitter#newParticle()
	 */
	@Override
	protected Particle newParticle() {		
		Random r = getRandom();
		Vector2f pos = getPos().createClone();
		if(r.nextBoolean()) {
			pos.x += r.nextInt(15);
		}
		else {
			pos.x -= r.nextInt(15);
		}
		
		if(r.nextBoolean()) {
			pos.y += r.nextInt(15);
		}
		else {
			pos.y -= r.nextInt(15);
		}
		
		return new BloodParticle(pos, new Vector2f(), r.nextInt(360), this.particleTimeToLive);
	}

}
