/*
 * see license.txt 
 */
package seventh.client.gfx.particle_system;

import java.util.Random;

import seventh.client.gfx.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.shared.Randomizer;
import seventh.shared.TimeStep;

/**
 * Randomly assigns a scaling factor to a particle
 * 
 * @author Tony
 *
 */
public class RandomScaleSingleParticleGenerator implements SingleParticleGenerator {

	private final float minScale, maxScale;
	
	/**
	 * 
	 */
	public RandomScaleSingleParticleGenerator(float minScale, float maxScale) {
		this.minScale = minScale;
		this.maxScale = maxScale;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle_system.BatchedParticleGenerator.SingleParticleGenerator#onGenerateParticle(int, seventh.shared.TimeStep, seventh.client.gfx.particle_system.ParticleData)
	 */
	@Override
	public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
		Random rand = particles.emitter.getRandom();
		float scale = (float)Randomizer.getRandomRange(rand, this.minScale, this.maxScale);
		particles.scale[index] = scale;
	}

}
