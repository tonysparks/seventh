/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class RenderableEffect implements Effect {

	private Renderable renderable;

	/**
	 * @param tex
	 * @param pos
	 * @param rotation
	 */
	public RenderableEffect(seventh.client.gfx.Renderable renderable) {
		super();
		this.renderable = renderable;
	}
	
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float a) {
		renderable.render(canvas, camera, a);
	}
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		renderable.update(timeStep);
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Effect#isDone()
	 */
	@Override
	public boolean isDone() {
		return false;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Effect#destroy()
	 */
	@Override
	public void destroy() {
	}
}
