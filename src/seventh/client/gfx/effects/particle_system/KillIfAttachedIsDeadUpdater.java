/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import seventh.client.entities.ClientEntity;
import seventh.client.gfx.effects.particle_system.Emitter.ParticleUpdater;
import seventh.shared.TimeStep;

/**
 * Stop the emitter if the attached entity is dead
 * 
 * @author Tony
 *
 */
public class KillIfAttachedIsDeadUpdater implements ParticleUpdater {

	/**
	 * 
	 */
	public KillIfAttachedIsDeadUpdater() {
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle_system.Emitter.ParticleUpdater#update(seventh.shared.TimeStep, seventh.client.gfx.particle_system.ParticleData)
	 */
	@Override
	public void update(TimeStep timeStep, ParticleData particles) {
		Emitter emitter = particles.emitter;
		ClientEntity ent = emitter.attachedTo();
		if(ent!=null && !ent.isAlive()) {
			emitter.stop();
		}
	}

}
