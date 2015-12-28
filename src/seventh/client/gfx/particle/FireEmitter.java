/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import java.util.Random;

import seventh.client.ClientEntity;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.Colors;
import seventh.math.Vector2f;
import seventh.math.Vector3f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class FireEmitter extends Emitter {
		
	private Vector2f shrapnelVel;
	private int delayTime;
	
	private Vector3f startColor, endColor;
	
	public FireEmitter(Vector2f pos, int timeToLive, int timeToNextSpawn) {
		this(pos, timeToLive, timeToNextSpawn, 25);
	}
	
	/**
	 * @param pos
	 * @param timeToLive
	 * @param timeToNextSpawn
	 */
	public FireEmitter(Vector2f pos, int timeToLive, int timeToNextSpawn, int maxParticles) {
		super(pos, timeToLive, timeToNextSpawn);
		this.maxParticles = maxParticles;
		setDieInstantly(false);
		
		shrapnelVel = new Vector2f();
		
		this.delayTime = 90;
		
		Random r = getRandom();
		float x = r.nextFloat() * 40;
		float y = r.nextFloat() * 40;
		
		if(r.nextBoolean()) x=-x;
		if(r.nextBoolean()) y=-y;
		shrapnelVel.set(x,y);		
		
		this.startColor = //new Vector3f(1.0f, 0, 0 ); //
						Colors.toVector3f(0x00ff0000);
		this.endColor = //new Vector3f();
				Colors.toVector3f(0x00838B8B);//0x00838B8B);
		
		setDecrementParticles(true);
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Emitter#attachTo(seventh.client.ClientEntity)
	 */
	@Override
	public void attachTo(ClientEntity ent) {
		super.attachTo(ent);		
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Emitter#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		this.delayTime -= timeStep.getDeltaTime();
		
		if(delayTime < 0) {					
		
			super.update(timeStep);
//			if(isAlive()) {
//				for(int i = 0; i < 5; i++) {
//					spawnParticle();
//				}
//			}
		}
		
		
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Emitter#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {		
		super.render(canvas, camera, alpha);
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

//		int color = 0x008B8386;
//		switch(r.nextInt(4)) {
//			case 0: color = 0x00838B8B;
//				break;
//			case 1: color = 0x00808A87;
//				break;
//			case 2: color = 0x00838B83;
//				break;
//			default:
//				color = 0x008B8386;
//				break;
//		}
//		

		int timeToLive = 4000;
		
		Vector2f pos = getPos().createClone();

		pos.x += x * r.nextInt(25);
		pos.y += y * r.nextInt(25);
				
		return new SmokeParticle(pos, new Vector2f(x,y), timeToLive);
	}

	class SmokeParticle extends Particle {

		private FadeValue alpha;
		private int speed;
		private final int maxSpeed;
		private Vector3f currentColor, deltaColor;		
		
		/**
		 * @param pos
		 * @param vel
		 * @param timeToLive
		 */
		public SmokeParticle(Vector2f pos, Vector2f vel, int timeToLive) {
			super(pos, vel, timeToLive);
					
			this.currentColor = new Vector3f(startColor);
			this.deltaColor = new Vector3f();			
			Vector3f.Vector3fSubtract(endColor, startColor, deltaColor);
			Vector3f.Vector3fMult(deltaColor, 0.001f, deltaColor);			
			
			alpha = new FadeValue(40, 0, timeToLive-400);
			maxSpeed = 40;
			speed = maxSpeed;
			
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
			
			if(!currentColor.equals(endColor)) {
				Vector3f.Vector3fAdd(currentColor, deltaColor, currentColor);
			}
			else {
				System.out.println("!!");
			}
		}
		
		/* (non-Javadoc)
		 * @see seventh.client.gfx.particle.Particle#doRender(leola.live.gfx.Canvas, leola.live.gfx.Camera, int, int)
		 */
		@Override
		protected void doRender(Canvas canvas, Camera camera, float renderX,
				float renderY) {
			
			int color = Colors.toColor(currentColor);
			int colorS = (alpha.getCurrentValue() << 24) | color;			
			canvas.setCompositeAlpha(alpha.getCurrentValue()/255.0f);
			canvas.drawImage(Art.smokeImage, renderX, renderY, colorS);
			canvas.setCompositeAlpha(1.0f);			
		}
	}
}
