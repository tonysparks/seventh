/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import seventh.client.gfx.effects.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Spawns a particle at the position of the {@link Emitter}
 * 
 * @author Tony
 *
 */
public class SetPositionSingleParticleGenerator implements SingleParticleGenerator {

    @Override
    public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
        Vector2f pos = particles.pos[index];
        pos.set(particles.emitter.getPos());;
    }

}
