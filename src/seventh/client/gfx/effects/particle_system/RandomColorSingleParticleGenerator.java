/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;

import seventh.client.gfx.effects.particle_system.BatchedParticleGenerator.SingleParticleGenerator;
import seventh.shared.TimeStep;

/**
 * Randomly assigns a {@link Color} to a particle
 * 
 * @author Tony
 *
 */
public class RandomColorSingleParticleGenerator implements SingleParticleGenerator {

	private Color[] colors;
	
	public RandomColorSingleParticleGenerator(Color ... colors) {
		this.colors = colors;
	} 

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle_system.BatchedParticleGenerator.SingleParticleGenerator#onGenerateParticle(int, seventh.shared.TimeStep, seventh.client.gfx.particle_system.ParticleData)
	 */
	@Override
	public void onGenerateParticle(int index, TimeStep timeStep, ParticleData particles) {
		Random rand = particles.emitter.getRandom();
		particles.color[index].set(this.colors[rand.nextInt(this.colors.length)]);				
	}

}
