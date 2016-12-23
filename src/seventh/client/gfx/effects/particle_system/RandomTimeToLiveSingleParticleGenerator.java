/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.Random;

import seventh.client.gfx.effects.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.shared.TimeStep;

/**
 * Randomly assigns a time to live factor to a particle
 * 
 * @author Tony
 *
 */
public class RandomTimeToLiveSingleParticleGenerator implements SingleParticleGenerator {

	private final long minTimeToLive, maxTimeToLive;
	
	public RandomTimeToLiveSingleParticleGenerator(long minTimeToLive, long maxTimeToLive) {
		this.minTimeToLive = minTimeToLive;
		this.maxTimeToLive = maxTimeToLive;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle_system.BatchedParticleGenerator.SingleParticleGenerator#onGenerateParticle(int, seventh.shared.TimeStep, seventh.client.gfx.particle_system.ParticleData)
	 */
	@Override
	public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
		Random rand = particles.emitter.getRandom();
		long timeToLive = minTimeToLive + rand.nextInt( (int)maxTimeToLive);
		particles.timeToLive[index].setEndTime(timeToLive);
		particles.timeToLive[index].reset();				
	}

}
