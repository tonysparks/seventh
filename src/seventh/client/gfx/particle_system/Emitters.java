/*
 * see license.txt 
 */
package seventh.client.gfx.particle_system;

import seventh.client.gfx.Art;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class Emitters {

	public static Emitter newFireEmitter(Vector2f pos) {
		Emitter emitter = new Emitter(pos, 10_000, 100_000).setDieInstantly(false);
		emitter.addParticleGenerator(new FireGenerator(1.99f, 0xEB502Fff));
		//emitter.addParticleGenerator(new FireGenerator(0.954f, 0xFFFF09ff));
		emitter.addParticleUpdater(new KillUpdater());
		emitter.addParticleUpdater(new FireUpdater(12.2f));
		emitter.addParticleRenderer(new FireParticleRenderer());
		return emitter;
	}
	
	public static Emitter newBloodEmitter(Vector2f pos) {
		// 5, 5200, 4000, 0, 60);
		// int maxParticles, int emitterTimeToLive, int particleTimeToLive, int timeToNextSpawn, int maxSpread) {
		
		Emitter emitter = new Emitter(pos, 6_000, 5).setDieInstantly(false);
		BatchedParticleGenerator gen = new BatchedParticleGenerator(0, 5);
		gen.addSingleParticleGenerator(new RandomPositionInRadiusSingleParticleGenerator(60))
		   .addSingleParticleGenerator(new RandomRotationSingleParticleGenerator())
		   .addSingleParticleGenerator(new RandomScaleSingleParticleGenerator(0.15f, 1.9f))
		   .addSingleParticleGenerator(new RandomSpeedSingleParticleGenerator(10, 10))
		   .addSingleParticleGenerator(new RandomTimeToLiveSingleParticleGenerator(6_000, 6_000))
		   .addSingleParticleGenerator(new RandomSpriteSingleParticleGenerator(Art.bloodImages))
		;
		
		emitter.addParticleGenerator(gen);
		
		emitter.addParticleUpdater(new KillUpdater());
		emitter.addParticleUpdater(new MovementParticleUpdater(0, 2));
		emitter.addParticleUpdater(new AlphaDecayUpdater(0f, 0.9878f));
		emitter.addParticleRenderer(new BlendingParticleRenderer());
		
		return emitter;
	}
}
