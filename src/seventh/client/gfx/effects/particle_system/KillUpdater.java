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
public class KillUpdater implements ParticleUpdater {

    /**
     * 
     */
    public KillUpdater() {
    }

    /* (non-Javadoc)
     * @see seventh.client.gfx.particle_system.Emitter.ParticleUpdater#update(seventh.shared.TimeStep, seventh.client.gfx.particle_system.ParticleData)
     */
    @Override
    public void update(TimeStep timeStep, ParticleData particles) {        
        for(int i = 0; i < particles.numberOfAliveParticles; i++) {
            particles.timeToLive[i].update(timeStep);
            if(particles.timeToLive[i].isTime()) {                
                particles.kill(i);
            }
        }
    }

}
