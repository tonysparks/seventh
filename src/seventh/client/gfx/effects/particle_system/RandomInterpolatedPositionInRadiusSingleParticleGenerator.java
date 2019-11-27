/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.Random;

import seventh.client.gfx.effects.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.math.Vector2f;
import seventh.shared.*;

/**
 * Spawns a particle randomly around a radius
 * 
 * @author Tony
 *
 */
public class RandomInterpolatedPositionInRadiusSingleParticleGenerator implements SingleParticleGenerator {

    private final int maxDistance;
    private final float minAlpha;
    private final float maxAlpha;
    
    private Vector2f cache;
    /**
     * 
     */
    public RandomInterpolatedPositionInRadiusSingleParticleGenerator(float minAlpha, float maxAlpha, int maxDistance) {
        this.minAlpha = minAlpha;
        this.maxAlpha = maxAlpha;
        this.maxDistance = maxDistance;
        this.cache = new Vector2f();
    }

    @Override
    public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
        if(this.maxDistance>0) {
            Random r = particles.emitter.getRandom();
    
            
            Vector2f pos = particles.pos[index];
            pos.set(1,0);
            
            Vector2f emitterPos = particles.emitter.getPos();
            Vector2f emitterPrevPos = particles.emitter.getPreviousPos();
            
            float alpha = (float)Randomizer.getRandomRange(r, minAlpha, maxAlpha);
            
            Vector2f.Vector2fRotate(pos, Math.toRadians(r.nextInt(360)), pos);
            Vector2f.Vector2fInterpolate(emitterPrevPos, emitterPos, alpha, this.cache);
            
            Vector2f.Vector2fMA(this.cache, pos, r.nextInt(this.maxDistance), pos);
        }
    }

}
