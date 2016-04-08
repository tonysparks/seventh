/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import java.util.Random;

import seventh.math.Vector2f;
import seventh.shared.Randomizer;

/**
 * @author Tony
 *
 */
public class BloodEmitter extends Emitter {

	private int particleTimeToLive;
	private int maxSpread;
	
	/**
	 * @param pos
	 * @param timeToLive
	 * @param timeToNextSpawn
	 */
	public BloodEmitter(Vector2f pos, int maxParticles, int emitterTimeToLive, int particleTimeToLive, int timeToNextSpawn, int maxSpread) {
		super(pos, emitterTimeToLive, timeToNextSpawn);		
		//this.nextSpawn.setLoop(false);
		this.maxParticles = maxParticles;
		this.particleTimeToLive = particleTimeToLive;
		this.maxSpread = maxSpread;
		
	}
	
	/**
	 * @param pos
	 * @param timeToLive
	 * @param timeToNextSpawn
	 */
	public BloodEmitter(Vector2f pos) {
		this(pos, 3, 5200, 4000, 100, 30);
	}
	
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Emitter#newParticle()
	 */
	@Override
	protected Particle newParticle() {		
		Random r = getRandom();
		Vector2f pos = getPos().createClone();
		final int distance = this.maxSpread;
		if(r.nextBoolean()) {
			pos.x += r.nextInt(distance);
		}
		else {
			pos.x -= r.nextInt(distance);
		}
		
		if(r.nextBoolean()) {
			pos.y += r.nextInt(distance);
		}
		else {
			pos.y -= r.nextInt(distance);
		}
		
		float scale = (float)Randomizer.getRandomRangeMin(r, 0.7f);
		
		return new BloodParticle(pos, new Vector2f(), r.nextInt(360), scale, this.particleTimeToLive);
	}

}
