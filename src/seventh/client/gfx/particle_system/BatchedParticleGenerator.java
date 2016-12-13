/*
 * see license.txt 
 */
package seventh.client.gfx.particle_system;

import java.util.ArrayList;
import java.util.List;

import seventh.client.gfx.particle_system.Emitter.ParticleGenerator;
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
	
	/**
	 * @param spawnTime
	 * @param batchSize
	 */
	public BatchedParticleGenerator(long spawnTime, int batchSize) {
		this.spawnTimer = new Timer(true, spawnTime);
		this.spawnTimer.start();
		
		this.batchSize = batchSize;
		this.generators = new ArrayList<>();
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
	public void update(TimeStep timeStep, ParticleData particles) {
		this.spawnTimer.update(timeStep);
		if(this.spawnTimer.isOnFirstTime()) {
			for(int i = 0; i < this.batchSize; i++) {
				int index = particles.spawnParticle();
				if(index > -1) {
					updateSingleParticles(index, timeStep, particles);
				}
			}
		}
	}

}
