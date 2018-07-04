/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.Random;

import seventh.client.gfx.effects.particle_system.Emitter.ParticleUpdater;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Moves a particle
 * 
 * @author Tony
 *
 */
public class RandomMovementParticleUpdater implements ParticleUpdater {

    private float currentMaxSpeed;
    private final float maxSpeed;
    private final float maxSpeedDecay;
    private final float minSpeed;

    public RandomMovementParticleUpdater(float maxSpeed) {
        this(maxSpeed, 0, maxSpeed);
    }
    
    /**
     * @param maxSpeed
     * @param maxSpeedDecay
     * @param minSpeed
     */
    public RandomMovementParticleUpdater(float maxSpeed, float maxSpeedDecay, float minSpeed) {
        this.maxSpeed = maxSpeed;
        this.maxSpeedDecay = maxSpeedDecay;
        this.minSpeed = minSpeed;
        
        this.currentMaxSpeed = maxSpeed;
    }

    @Override
    public void reset() {
        this.currentMaxSpeed = this.maxSpeed;
    }
    
    @Override
    public void update(TimeStep timeStep, ParticleData particles) {
        float dt = (float)timeStep.asFraction();
        Random rand = particles.emitter.getRandom();
        for(int i = 0; i < particles.numberOfAliveParticles; i++) {
            Vector2f pos = particles.pos[i];
            Vector2f vel = particles.vel[i];
            float speed = particles.speed[i];
            
            float newX = (pos.x + vel.x * speed * dt);
            float newY = (pos.y + vel.y * speed * dt);
            
            pos.set(newX, newY);
                        
            float s = rand.nextFloat();
            speed = s * this.currentMaxSpeed;
            
            this.currentMaxSpeed -= this.maxSpeedDecay;            
            if(this.currentMaxSpeed < this.minSpeed) {
                this.currentMaxSpeed = this.minSpeed;
            }
            
            particles.speed[i] = speed;
            
        }
    }

}
