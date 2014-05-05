/*
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
public class BrassEmitter extends Emitter {

	/**
	 * @param pos
	 * @param timeToLive
	 * @param timeToNextSpawn
	 */
	public BrassEmitter(Vector2f pos, int timeToLive, int timeToNextSpawn) {
		super(pos, timeToLive, timeToNextSpawn);
		this.maxParticles = 1;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Emitter#newParticle()
	 */
	@Override
	protected Particle newParticle() {
		Random r = getRandom();
		
		float x = r.nextFloat();
		float y = r.nextFloat();
		
		if(r.nextBoolean()) x=-x;
		if(r.nextBoolean()) y=-y;
		
		return new BrassParticle(getPos().createClone()
								, new Vector2f(x,y)
								, r.nextInt(360)
								, (int)this.timeToLive.getEndTime()
								);
	}
	
	static class BrassParticle extends Particle {

		private int speed;
		private int decRate;
		private float angle;
		
		private FadeValue alphaFade;
		
		/**
		 * @param pos
		 * @param vel
		 * @param timeToLive
		 */
		public BrassParticle(Vector2f pos, Vector2f vel, int angle, int timeToLive) {
			super(pos, vel, timeToLive);
			
			this.speed = 100;
			this.decRate = 50;
			this.angle = (float)angle;
			
			this.alphaFade = new FadeValue(255, 0, timeToLive + 1000);
		}
		
		@Override
		public void update(TimeStep timeStep) {	
			super.update(timeStep);
			
			alphaFade.update(timeStep);
			
			double dt = timeStep.asFraction();
			int newX = (int)Math.round(pos.x + vel.x * speed * dt);
			int newY = (int)Math.round(pos.y + vel.y * speed * dt);
			
			speed-=this.decRate;
			if(speed < 0) {
				speed = 0;
			}
			
			this.decRate = this.decRate/8;
			if(this.decRate <= 0) {
				this.decRate = 4;
			}
			
			pos.x = newX;
			pos.y = newY;
		}
		
		/* (non-Javadoc)
		 * @see seventh.client.gfx.particle.Particle#doRender(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, int, int)
		 */
		@Override
		protected void doRender(Canvas canvas, Camera camera, float renderX, float renderY) {
			
		//	float priorAlpha = canvas.getCompositeAlpha();
			
		//	float alpha = (float)this.alphaFade.getCurrentValue() / 255.0f;
			//canvas.setCompositeAlpha(alpha);		
			int x = (int)renderX;
			int y = (int)renderY;
			
			canvas.clearTransform();
			canvas.rotate(angle, x, y);
			canvas.fillRect(x, y, 2, 4, 0xafccbb00);		
		//	canvas.setCompositeAlpha(priorAlpha);
			canvas.clearTransform();
			//canvas.rotate(0, renderX, renderY);	
		}
	}

}
