/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import seventh.client.gfx.effects.particle_system.Emitter.ParticleUpdater;
import seventh.shared.TimeStep;

/**
 * Decays the alpha value of the particle color
 * 
 * @author Tony
 *
 */
public class AlphaDecayUpdater implements ParticleUpdater {

    private final float endingAlpha;
    private final float decayFactor;
    private       long startDecayAfterTime;
    
    public AlphaDecayUpdater(float endingAlpha, float decayFactor) {
        this(0L, endingAlpha, decayFactor);
    }
    
    public AlphaDecayUpdater(long startDecayAfterTime, float endingAlpha, float decayFactor) {
        this.startDecayAfterTime = startDecayAfterTime;
        this.endingAlpha = endingAlpha;
        this.decayFactor = decayFactor;
    }

    @Override
    public void update(TimeStep timeStep, ParticleData particles) {
        this.startDecayAfterTime -= timeStep.getDeltaTime();
        if(this.startDecayAfterTime < 0) {
            for(int i = 0; i < particles.numberOfAliveParticles; i++) {                
                particles.color[i].a *= this.decayFactor;
                if(particles.color[i].a < this.endingAlpha) {
                    particles.color[i].a = this.endingAlpha;
                }
            }
        }
    }

}
