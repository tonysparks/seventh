/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.client.gfx.Art;
import seventh.client.gfx.effects.particle_system.Emitter.ParticleGenerator;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public class FireGenerator implements ParticleGenerator {

    private Timer spawnTimer;
    private int particleBatchSize;
    private final float startingScale;
    private final int startColor;
    
    public FireGenerator(float startingScale, int startColor) {
        this.spawnTimer = new Timer(true, 1);
        this.particleBatchSize = 76;
        
        this.startingScale = startingScale;
        this.startColor = startColor;
    }

    @Override
    public void reset() {
        this.spawnTimer.reset();
    }
    
    @Override
    public void update(TimeStep timeStep, ParticleData particles) {
        this.spawnTimer.update(timeStep);
        if(this.spawnTimer.isOnFirstTime()) {
            Random rand = particles.emitter.getRandom();
            Vector2f emitterPos = particles.emitter.getPos();
            for(int i = 0; i < this.particleBatchSize; i++) {
                int index = particles.spawnParticle();
                if(index > -1) {
                    Vector2f vel = particles.vel[index];
                    vel.set(1, 0);
                    Vector2f.Vector2fRotate(vel, Math.toRadians(rand.nextInt(360)), vel);
                    //Vector2f.Vector2fMult(vel, rand.nextFloat(), vel);
                    
                    Vector2f pos = particles.pos[index];
                    pos.set(1, 0);
                    
                    Vector2f.Vector2fRotate(pos, Math.toRadians(rand.nextInt(360)), pos);
                    Vector2f.Vector2fMA(emitterPos, pos, rand.nextInt(55), pos);
                    
                    
                    particles.speed[index] = 1000f;
                    particles.scale[index] = this.startingScale - (rand.nextInt(5) / 100.0f);
                    particles.sprite[index] = new Sprite(Art.smokeImage);
                    particles.color[index].set(this.startColor);
                }
            }
        }
    }

}
