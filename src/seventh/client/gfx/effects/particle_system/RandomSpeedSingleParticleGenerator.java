/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.Random;

import seventh.client.gfx.effects.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.shared.Randomizer;
import seventh.shared.TimeStep;

/**
 * Randomly assigns a speed factor to a particle
 * 
 * @author Tony
 *
 */
public class RandomSpeedSingleParticleGenerator implements SingleParticleGenerator {

	private final float minSpeed, maxSpeed;
	
	/**
	 * 
	 */
	public RandomSpeedSingleParticleGenerator(float minSpeed, float maxSpeed) {
		this.minSpeed = minSpeed;
		this.maxSpeed = maxSpeed;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle_system.BatchedParticleGenerator.SingleParticleGenerator#onGenerateParticle(int, seventh.shared.TimeStep, seventh.client.gfx.particle_system.ParticleData)
	 */
	@Override
	public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
		Random rand = particles.emitter.getRandom();
		float speed = (float)Randomizer.getRandomRange(rand, this.minSpeed, this.maxSpeed);
		particles.speed[index] = speed;
	}

}
