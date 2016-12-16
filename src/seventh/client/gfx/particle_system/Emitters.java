/*
 * see license.txt 
 */
package seventh.client.gfx.particle_system;

import com.badlogic.gdx.graphics.Color;

import seventh.client.gfx.Art;
import seventh.client.gfx.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

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
	
	public static Emitter newBulletImpactEmitter(Vector2f pos, Vector2f targetVel) {
		// 5, 5200, 4000, 0, 60);
		// int maxParticles, int emitterTimeToLive, int particleTimeToLive, int timeToNextSpawn, int maxSpread) {
		
		Vector2f vel = targetVel.isZero() ? new Vector2f(-1.0f, -1.0f) : new Vector2f(-targetVel.x*1.0f, -targetVel.y*1.0f);
		
		Emitter emitter = new Emitter(pos, 200, 30).setDieInstantly(false);
		BatchedParticleGenerator gen = new BatchedParticleGenerator(0, 30);
		gen.addSingleParticleGenerator(new SingleParticleGenerator() {
			
				@Override
				public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
					particles.speed[index] = 125f;
				}
			})
		   .addSingleParticleGenerator(new SetPositionSingleParticleGenerator()) 
		   .addSingleParticleGenerator(new RandomColorSingleParticleGenerator(new Color(0x8B7355ff), new Color(0x8A7355ff)))
		   .addSingleParticleGenerator(new RandomVelocitySingleParticleGenerator(vel, 30))
		   .addSingleParticleGenerator(new RandomTimeToLiveSingleParticleGenerator(200, 250))		   
		;
		
		emitter.addParticleGenerator(gen);
		
		emitter.addParticleUpdater(new KillUpdater());
		emitter.addParticleUpdater(new RandomMovementParticleUpdater(85));
		emitter.addParticleUpdater(new AlphaDecayUpdater(0f, 0.72718f));
		emitter.addParticleRenderer(new CircleParticleRenderer());
		
		return emitter;
	}
}
