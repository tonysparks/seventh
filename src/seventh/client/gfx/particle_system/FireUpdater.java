/*
 * see license.txt 
 */
package seventh.client.gfx.particle_system;

import seventh.client.gfx.particle_system.Emitter.ParticleUpdater;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class FireUpdater implements ParticleUpdater {

	private final float endingScale;
	
	/**
	 * 
	 */
	public FireUpdater(float endingScale) {
		this.endingScale = endingScale;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle_system.Emitter.ParticleUpdater#update(seventh.shared.TimeStep, seventh.client.gfx.particle_system.ParticleData)
	 */
	@Override
	public void update(TimeStep timeStep, ParticleData particles) {
		double dt = timeStep.asFraction();
		for(int i = 0; i < particles.numberOfAliveParticles; i++) {
			Vector2f pos = particles.pos[i];
			Vector2f vel = particles.vel[i];
			float speed = particles.speed[i];
			
			int newX = (int)Math.round(pos.x + vel.x * speed * dt);
			int newY = (int)Math.round(pos.y + vel.y * speed * dt);
			
			
			pos.x = newX;
			pos.y = newY;
			
			particles.speed[i]   *= 0.99f;
			particles.color[i].a *= 0.99f;
			
			particles.scale[i] *= 1.039f;
			if(particles.scale[i] > this.endingScale) {
				particles.scale[i] = this.endingScale;
			}
		}
	}

}
