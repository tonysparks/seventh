/*
 * see license.txt 
 */
package seventh.client.gfx.particle_system;

import seventh.client.gfx.particle_system.Emitter.ParticleUpdater;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Moves a particle
 * 
 * @author Tony
 *
 */
public class MovementParticleUpdater implements ParticleUpdater {

	private final float minSpeed, speedDecay;
	
	/**
	 * 
	 */
	public MovementParticleUpdater(float minSpeed, float speedDecay) {
		this.minSpeed = minSpeed;		
		this.speedDecay = speedDecay;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle_system.Emitter.ParticleUpdater#update(seventh.shared.TimeStep, seventh.client.gfx.particle_system.ParticleData)
	 */
	@Override
	public void update(TimeStep timeStep, ParticleData particles) {
		float dt = (float)timeStep.asFraction();
		for(int i = 0; i < particles.numberOfAliveParticles; i++) {
			Vector2f pos = particles.pos[i];
			Vector2f vel = particles.vel[i];
			float speed = particles.speed[i];
			
			float newX = (pos.x + vel.x * speed * dt);
			float newY = (pos.y + vel.y * speed * dt);
			
			pos.set(newX, newY);
			
			speed -= this.speedDecay;
			if(speed < this.minSpeed) {
				speed = this.minSpeed;
			}
			
			particles.speed[i] = speed;
			
		}
	}

}
