/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.Random;

import seventh.client.gfx.effects.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Spawns a particle randomly around a radius
 * 
 * @author Tony
 *
 */
public class RandomPositionInRadiusGrowthSingleParticleGenerator implements SingleParticleGenerator {

    private final int startingMaxDistance, endingMaxDistance;

    /**
     * 
     */
    public RandomPositionInRadiusGrowthSingleParticleGenerator(int startingMaxDistance, int endingMaxDistance) {        
        this.startingMaxDistance = startingMaxDistance;
        this.endingMaxDistance = endingMaxDistance;
    }

    @Override
    public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
        
        Random r = particles.emitter.getRandom();
        long remainingTime = particles.emitter.timeToLive.getRemainingTime();
        long endTime = particles.emitter.timeToLive.getEndTime();
        float alpha = 1.0f - ((float)remainingTime / (float)endTime);
        int maxDistance = this.startingMaxDistance + (int)((this.endingMaxDistance-this.startingMaxDistance) * alpha);
        
        
        Vector2f pos = particles.pos[index];
        pos.set(1,0);
        
        Vector2f.Vector2fRotate(pos, Math.toRadians(r.nextInt(360)), pos);
        Vector2f.Vector2fMA(particles.emitter.getPos(), pos, r.nextInt(maxDistance), pos);
        
    }

}
