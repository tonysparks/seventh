/*
 * see license.txt 
 */
package seventh.client.gfx.particle_system;

import java.util.Random;

import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.gfx.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.shared.TimeStep;

/**
 * Randomly assigns a {@link Texture} to a particle
 * 
 * @author Tony
 *
 */
public class RandomSpriteSingleParticleGenerator implements SingleParticleGenerator {

	private TextureRegion[] regions;
	/**
	 * 
	 */
	public RandomSpriteSingleParticleGenerator(TextureRegion[] regions) {
		this.regions = regions;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle_system.BatchedParticleGenerator.SingleParticleGenerator#onGenerateParticle(int, seventh.shared.TimeStep, seventh.client.gfx.particle_system.ParticleData)
	 */
	@Override
	public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
		Random rand = particles.emitter.getRandom();
		particles.sprite[index] = new Sprite(this.regions[rand.nextInt(this.regions.length)]);
		particles.sprite[index].flip(false, true);
	}

}
