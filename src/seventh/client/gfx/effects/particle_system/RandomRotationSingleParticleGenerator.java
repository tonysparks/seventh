/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.Random;

import seventh.client.gfx.effects.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.shared.TimeStep;

/**
 * Randomly assigns a rotation factor to a particle
 * 
 * @author Tony
 *
 */
public class RandomRotationSingleParticleGenerator implements SingleParticleGenerator {

    public RandomRotationSingleParticleGenerator() {
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.particle_system.BatchedParticleGenerator.SingleParticleGenerator#onGenerateParticle(int, seventh.shared.TimeStep, seventh.client.gfx.particle_system.ParticleData)
     */
    @Override
    public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
        Random rand = particles.emitter.getRandom();
        float rotation = rand.nextInt(360);
        particles.rotation[index] = rotation;
    }

}
