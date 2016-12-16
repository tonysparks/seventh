/*
 * see license.txt 
 */
package seventh.client.gfx.particle_system;

import java.util.Random;

import seventh.client.gfx.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Spawns a particle randomly around a radius
 * 
 * @author Tony
 *
 */
public class RandomVelocitySingleParticleGenerator implements SingleParticleGenerator {

	private final Vector2f startingVel;
	private final int maxDistance;
	
	/**
	 * 
	 */
	public RandomVelocitySingleParticleGenerator(Vector2f startingVel, int maxDistance) {
		this.startingVel = startingVel;
		this.maxDistance = maxDistance;
	}

	@Override
	public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
		Random r = particles.emitter.getRandom();
		
		Vector2f vel = particles.vel[index];
		vel.set(this.startingVel);		

		int rotateBy = r.nextInt(maxDistance); 		
		Vector2f.Vector2fRotate(vel, Math.toRadians((r.nextInt(2) > 0) ? rotateBy : -rotateBy), vel);
	}

}
