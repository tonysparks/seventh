/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import seventh.client.gfx.effects.particle_system.Emitter.ParticleGenerator;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * Spawns a batch of particles
 * 
 * @author Tony
 *
 */
public class BatchedParticleGenerator implements ParticleGenerator {

    /**
     * Handles a single generated particle
     * 
     * @author Tony
     *
     */
    public static interface SingleParticleGenerator {
        void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles);    
    }
    
    private final Timer spawnTimer;
    private final int batchSize;
    private List<SingleParticleGenerator> generators;
    
    private final long minSpawnTime, maxSpawnTime;
    
    /**
     * 
     */
    public BatchedParticleGenerator(long spawnTime, int batchSize) {
        this(spawnTime, spawnTime, batchSize);
    }
    
    /**
     * @param spawnTime
     * @param batchSize
     */
    public BatchedParticleGenerator(long minSpawnTime, long maxSpawnTime, int batchSize) {
        this.spawnTimer = new Timer(true, minSpawnTime);
        this.minSpawnTime = minSpawnTime;
        this.maxSpawnTime = maxSpawnTime;
        
        this.batchSize = batchSize;
        this.generators = new ArrayList<>();
        
        this.spawnTimer.start();
    }
    
    public BatchedParticleGenerator addSingleParticleGenerator(SingleParticleGenerator gen) {
        this.generators.add(gen);
        return this;
    }

    protected void updateSingleParticles(int index, TimeStep timeStep, ParticleData particles) {
        for(int i = 0; i < this.generators.size();i++) {
            this.generators.get(i).onGenerateParticle(index, timeStep, particles);
        }
    }
    
    @Override
    public void reset() {                
        this.spawnTimer.reset();        
        this.spawnTimer.start();
    }
    
    @Override
    public void update(TimeStep timeStep, ParticleData particles) {
        if(particles.atLimit()) {
            return;
        }
        
        this.spawnTimer.update(timeStep);
        if(this.spawnTimer.isOnFirstTime()) {
            particles.emitter.resetUpdaters();
            
            Random rand = particles.emitter.getRandom();
            if(this.maxSpawnTime > this.minSpawnTime) {
                long timer = this.minSpawnTime + rand.nextInt((int)(this.maxSpawnTime - this.minSpawnTime));                
                this.spawnTimer.setEndTime(timer);
                this.spawnTimer.reset();
                
                particles.reset();                
            }
            
            for(int i = 0; i < this.batchSize; i++) {
                int index = particles.spawnParticle();
                if(index > -1) {                    
                    updateSingleParticles(index, timeStep, particles);
                }
            }
        }
    }

}
