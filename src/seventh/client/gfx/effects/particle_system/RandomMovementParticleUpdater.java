/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.Random;

import seventh.client.gfx.effects.particle_system.Emitter.ParticleUpdater;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Moves a particle
 * 
 * @author Tony
 *
 */
public class RandomMovementParticleUpdater implements ParticleUpdater {

	private final float maxSpeed;
	
	/**
	 * 
	 */
	public RandomMovementParticleUpdater(float maxSpeed) {
		this.maxSpeed = maxSpeed;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle_system.Emitter.ParticleUpdater#update(seventh.shared.TimeStep, seventh.client.gfx.particle_system.ParticleData)
	 */
	@Override
	public void update(TimeStep timeStep, ParticleData particles) {
		float dt = (float)timeStep.asFraction();
		Random rand = particles.emitter.getRandom();
		for(int i = 0; i < particles.numberOfAliveParticles; i++) {
			Vector2f pos = particles.pos[i];
			Vector2f vel = particles.vel[i];
			float speed = particles.speed[i];
			
			float newX = (pos.x + vel.x * speed * dt);
			float newY = (pos.y + vel.y * speed * dt);
			
			pos.set(newX, newY);
			
			speed = rand.nextInt( (int)this.maxSpeed);
			
			particles.speed[i] = speed;
			
		}
	}

}
