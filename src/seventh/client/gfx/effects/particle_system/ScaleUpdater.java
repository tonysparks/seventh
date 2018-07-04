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
public class ScaleUpdater implements ParticleUpdater {

    private final float endingScale;
    private final float decayFactor;
    private final boolean isMin;
    
    /**
     * @param endingScale
     * @param decayFactor
     */
    public ScaleUpdater(float endingScale, float decayFactor) {
        this.endingScale = endingScale;
        this.decayFactor = decayFactor;
        this.isMin = decayFactor < 0;
    }
    
    @Override
    public void reset() {       
    }

    @Override
    public void update(TimeStep timeStep, ParticleData particles) {
        
        for(int i = 0; i < particles.numberOfAliveParticles; i++) {
            particles.scale[i] += this.decayFactor;
            if(this.isMin) {
                if(particles.scale[i] < this.endingScale) {
                    particles.scale[i] = this.endingScale;
                }
            }
            else if(particles.scale[i] > this.endingScale) {
                particles.scale[i] = this.endingScale;
            }
        }
    }

}
