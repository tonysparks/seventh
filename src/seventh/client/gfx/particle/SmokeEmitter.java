/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import java.util.Random;

import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class SmokeEmitter extends Emitter {

	/**
	 * @param pos
	 * @param timeToLive
	 * @param timeToNextSpawn
	 */
	public SmokeEmitter(Vector2f pos, int timeToLive, int timeToNextSpawn) {
		super(pos, timeToLive, timeToNextSpawn);
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Emitter#newParticle()
	 */
	@Override
	protected Particle newParticle() {
		Random r = getRandom();
		float x = r.nextFloat() * 2.0f;
		float y = r.nextFloat() * 2.0f;
		
		if(r.nextBoolean()) x=-x;
		if(r.nextBoolean()) y=-y;
		
		int pureBlack = 0x00000000;
		int gray = 0x001f1f1f;
		
		return new SmokeParticle(getPos().createClone(), new Vector2f(x,y), 3000, r.nextBoolean() ? pureBlack : gray);
	}

	class SmokeParticle extends Particle {

		private FadeValue alpha;
		private int speed;
		private final int maxSpeed;
		private int color;
		/**
		 * @param pos
		 * @param vel
		 * @param timeToLive
		 */
		public SmokeParticle(Vector2f pos, Vector2f vel, int timeToLive, int color) {
			super(pos, vel, timeToLive);
			alpha = new FadeValue(235, 0, timeToLive-100);
			maxSpeed = 40;
			speed = maxSpeed;
			
			this.color = color;
		}
		
		/* (non-Javadoc)
		 * @see seventh.client.gfx.particle.Particle#update(leola.live.TimeStep)
		 */
		@Override
		public void update(TimeStep timeStep) {		
			super.update(timeStep);
			alpha.update(timeStep);
			
			double dt = timeStep.asFraction();
			int newX = (int)Math.round(pos.x + vel.x * speed * dt);
			int newY = (int)Math.round(pos.y + vel.y * speed * dt);
			
			speed = getRandom().nextInt(maxSpeed);
			
			pos.x = newX;
			pos.y = newY;
		}
		
		/* (non-Javadoc)
		 * @see seventh.client.gfx.particle.Particle#doRender(leola.live.gfx.Canvas, leola.live.gfx.Camera, int, int)
		 */
		@Override
		protected void doRender(Canvas canvas, Camera camera, float renderX,
				float renderY) {
			
			int colorS = (alpha.getCurrentValue() << 24) | color;			
			//canvas.fillCircle(10.0f, renderX-3, renderY+3, colorS);
			canvas.setCompositeAlpha(alpha.getCurrentValue()/255.0f);
			canvas.drawImage(Art.smokeImage, renderX, renderY, colorS);
			canvas.setCompositeAlpha(1.0f);
//			int color = (alpha.getCurrentValue() << 24) | 0x00000000;			
//			canvas.fillCircle(10.0f, renderX, renderY, color);
			
		}
	}
}
