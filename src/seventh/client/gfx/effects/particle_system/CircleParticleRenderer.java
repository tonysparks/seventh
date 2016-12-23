/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import com.badlogic.gdx.graphics.Color;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.effects.particle_system.Emitter.ParticleRenderer;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class CircleParticleRenderer implements ParticleRenderer {

	
	/**
	 * 
	 */
	public CircleParticleRenderer() {
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle_system.Emitter.ParticleRenderer#update(seventh.shared.TimeStep, seventh.client.gfx.particle_system.ParticleData)
	 */
	@Override
	public void update(TimeStep timeStep, ParticleData particles) {
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle_system.Emitter.ParticleRenderer#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, float, seventh.client.gfx.particle_system.ParticleData)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha, ParticleData particles) {
		
		Vector2f cameraPos = camera.getRenderPosition(alpha);
		for(int i = 0; i < particles.numberOfAliveParticles; i++) {
			
			Vector2f pos = particles.pos[i];
			float x = pos.x - cameraPos.x, y = pos.y - cameraPos.y;
			Color color = particles.color[i];
			
			canvas.fillCircle(1, x, y, Color.argb8888(color));			
		}				
	}

}
