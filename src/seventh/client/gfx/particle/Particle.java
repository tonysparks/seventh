/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Renderable;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;
import seventh.shared.Timer;

/**
 * @author Tony
 *
 */
public abstract class Particle implements Renderable {

	protected Timer timeToLive;
	protected Vector2f pos, vel;
	
	
	/**
	 * 
	 */
	public Particle(Vector2f pos, Vector2f vel, int timeToLive) {
		this.timeToLive = new Timer(false, timeToLive);
		if(timeToLive>0) {
			this.timeToLive.start();
		}
		
		this.pos = pos;
		this.vel = vel;
	}
	
	public boolean isAlive() {
		return !this.timeToLive.isTime();
	}

	/**
	 * @return the time remaining to live
	 */
	public long timeLeftToLive() {
		return this.timeToLive.getRemainingTime();
	}
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.timeToLive.update(timeStep);		
	}	
	
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {
		Vector2f c = camera.getPosition();
		float rx = (pos.x - c.x);
		float ry = (pos.y - c.y);

		doRender(canvas, camera, rx, ry);
	}
	
	protected abstract void doRender(Canvas canvas, Camera camera, float renderX, float renderY);
}
