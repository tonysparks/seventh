/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.Random;

import seventh.client.gfx.effects.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Spawns a particle randomly around a radius
 * 
 * @author Tony
 *
 */
public class RandomPositionInRadiusSingleParticleGenerator implements SingleParticleGenerator {

	private final int maxDistance;
	
	/**
	 * 
	 */
	public RandomPositionInRadiusSingleParticleGenerator(int maxDistance) {		
		this.maxDistance = maxDistance;
	}

	@Override
	public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
		if(this.maxDistance>0) {
			Random r = particles.emitter.getRandom();
	
			
			Vector2f pos = particles.pos[index];
			pos.set(1,0);
			
			Vector2f.Vector2fRotate(pos, Math.toRadians(r.nextInt(360)), pos);
			Vector2f.Vector2fMA(particles.emitter.getPos(), pos, r.nextInt(this.maxDistance), pos);
		}
	}

}
