/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import java.util.Random;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class BulletImpactEmitter extends Emitter {

	private Vector2f vel;
	private boolean isFlesh;
	/**
	 * @param pos
	 * @param timeToLive
	 * @param timeToNextSpawn
	 */
	public BulletImpactEmitter(Vector2f pos, Vector2f vel, int timeToLive, int timeToNextSpawn, boolean isFlesh) {
		super(pos, timeToLive, timeToNextSpawn);
		this.isFlesh = isFlesh;
		
		this.vel = vel.isZero() ? new Vector2f(-1.0f, -1.0f) : new Vector2f(-vel.x*1.0f, -vel.y*1.0f);
		this.setDieInstantly(false);
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Emitter#newParticle()
	 */
	@Override
	protected Particle newParticle() {
		Random random = getRandom();
		int maxSpread = 180;
		double rd = random.nextInt(maxSpread) / 100.0;
		int sd = random.nextInt(2);
		Vector2f s = sd>0 ? vel.rotate(rd) : vel.rotate(-rd);
		
		int pureBlack = isFlesh ? 0x8B0000 : 0x8B7355;//0x001f1f1f;//0x00000000;
		int gray = isFlesh ? 0x8B1A1A : 0x8B7355;//0x8B5A2B;
		
		return new ImpactParticle(getPos().createClone(), s, isFlesh ? 300:300, random.nextBoolean() ? pureBlack : gray);
	}

	class ImpactParticle extends Particle {

		private FadeValue alpha;
		private int speed;
		private final int maxSpeed;
		private int color;
		/**
		 * @param pos
		 * @param vel
		 * @param timeToLive
		 */
		public ImpactParticle(Vector2f pos, Vector2f vel, int timeToLive, int color) {
			super(pos, vel, timeToLive);
			alpha = new FadeValue(255, 0, timeToLive);
			maxSpeed = isFlesh ? 30:125;
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
			//canvas.drawImage(Art.smokeImage, renderX, renderY, colorS);
			canvas.fillCircle(1, (int)renderX, (int)renderY, colorS);
			//canvas.drawLine(colorS, renderX, renderY, colorS, null);
			canvas.setCompositeAlpha(1.0f);
//			int color = (alpha.getCurrentValue() << 24) | 0x00000000;			
//			canvas.fillCircle(10.0f, renderX, renderY, color);
			
		}
	}
}
