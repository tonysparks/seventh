/*
 * see license.txt 
 */
package seventh.client.gfx.effects.particle_system;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.effects.particle_system.Emitter.ParticleRenderer;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class BlendingSpriteParticleRenderer implements ParticleRenderer {

	/**
	 * 
	 */
	public BlendingSpriteParticleRenderer() {
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
		int src = canvas.getSrcBlendFunction();
		int dst = canvas.getDstBlendFunction();
		//canvas.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_FUNC_ADD);
		canvas.setBlendFunction(GL20.GL_SRC_ALPHA, GL20.GL_ONE);
		Gdx.gl20.glBlendEquation(GL20.GL_FUNC_ADD);
		
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
		
		canvas.setBlendFunction(src, dst);
	}

}
