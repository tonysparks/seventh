/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import seventh.client.gfx.effects.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.shared.TimeStep;

/**
 * Spawns a particle randomly around a radius
 * 
 * @author Tony
 *
 */
public class RandomScaleGrowthSingleParticleGenerator implements SingleParticleGenerator {

    private final float endingScale;

    /**
     * 
     */
    public RandomScaleGrowthSingleParticleGenerator(float endingScale) {                
        this.endingScale = endingScale;
    }

    @Override
    public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {        
        long remainingTime = particles.emitter.timeToLive.getRemainingTime();
        long endTime = particles.emitter.timeToLive.getEndTime();
        float alpha = 1.0f - ((float)remainingTime / (float)endTime);
        
        float startingScale = particles.scale[index];
        float scale = startingScale + ((this.endingScale-startingScale) * alpha);
        particles.scale[index] = scale;               
    }

}
