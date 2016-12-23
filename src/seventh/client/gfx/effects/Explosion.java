/*
 * see license.txt 
 */
package seventh.client.gfx.effects;

import seventh.client.ClientGame;
import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.AnimationPool;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * @author Tony
 *
 */
public class Explosion implements Effect {

	private AnimatedImage image;
	private Vector2f pos;
	private Sprite sprite;
	private AnimationPool pool;
	/**
	 * 
	 */
	public Explosion(ClientGame game, Vector2f pos) {
		this.pos = pos;
		this.pool = game.getPools().getExplosion();		
		this.image = pool.create();
		this.sprite = new Sprite(this.image.getCurrentImage());
		this.sprite.setRotation(game.getRandom().nextInt(360));
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.image.update(timeStep);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {
		Vector2f cameraPos = camera.getRenderPosition(alpha);
		sprite.setRegion(this.image.getCurrentImage());
		sprite.setPosition(this.pos.x-cameraPos.x, this.pos.y-cameraPos.y);
		canvas.drawSprite(sprite);
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Effect#isDone()
	 */
	@Override
	public boolean isDone() {	
		return this.image.isDone();
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Effect#destroy()
	 */
	@Override
	public void destroy() {
		this.pool.free(image);
	}
}
