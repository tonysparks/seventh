/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import seventh.client.gfx.effects.particle_system.Emitter.ParticleUpdater;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class AlphaDecayUpdater implements ParticleUpdater {

	private final float endingAlpha;
	private final float decayFactor;
	/**
	 * 
	 */
	public AlphaDecayUpdater(float endingAlpha, float decayFactor) {
		this.endingAlpha = endingAlpha;
		this.decayFactor = decayFactor;
	}

	@Override
	public void update(TimeStep timeStep, ParticleData particles) {
		
		for(int i = 0; i < particles.numberOfAliveParticles; i++) {
			particles.color[i].a *= this.decayFactor;
			if(particles.color[i].a < this.endingAlpha) {
				particles.color[i].a = this.endingAlpha;
			}
		}
	}

}
