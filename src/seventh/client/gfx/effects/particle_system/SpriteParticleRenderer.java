/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.effects.particle_system.Emitter.ParticleRenderer;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Renders the Sprite associated with the particle
 * 
 * @author Tony
 *
 */
public class SpriteParticleRenderer implements ParticleRenderer {


	@Override
	public void update(TimeStep timeStep, ParticleData particles) {
	}

	@Override
	public void render(Canvas canvas, Camera camera, float alpha, ParticleData particles) {		
		Vector2f cameraPos = camera.getRenderPosition(alpha);
		for(int i = 0; i < particles.numberOfAliveParticles; i++) {
			Sprite sprite = particles.sprite[i];
			Vector2f pos = particles.pos[i];
			sprite.setPosition(pos.x - cameraPos.x, pos.y - cameraPos.y);
			sprite.setScale(particles.scale[i]);
			sprite.setColor(particles.color[i]);
			sprite.setRotation(particles.rotation[i]);
			canvas.drawRawSprite(sprite);
		}		
	}

}
