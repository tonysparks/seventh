/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import java.util.Random;

import seventh.client.entities.ClientEntity;
import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class RocketTrailEmitter extends Emitter {

	private ClientEntity entity;
	private int width, height;
	
	/**
	 * @param pos
	 * @param timeToLive
	 * @param timeToNextSpawn
	 */
	public RocketTrailEmitter(Vector2f pos, int timeToLive, int timeToNextSpawn) {
		this(pos, timeToLive, timeToNextSpawn, Art.smokeImage.getRegionWidth(), Art.smokeImage.getRegionHeight());
	}
	
	/**
	 * @param pos
	 * @param timeToLive
	 * @param timeToNextSpawn
	 */
	public RocketTrailEmitter(Vector2f pos, int timeToLive, int timeToNextSpawn, int width, int height) {
		super(pos, timeToLive, timeToNextSpawn);
		setDieInstantly(false);
		
		this.width = width;
		this.height = height;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Emitter#attachTo(seventh.client.ClientEntity)
	 */
	@Override
	public void attachTo(ClientEntity ent) {
		super.attachTo(ent);
		this.entity = ent;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Emitter#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {	
		super.update(timeStep);
		
		if(this.entity!=null) {
			if(!this.entity.isAlive()) {
//				this.kill();
				stop();
			}
		}
		
		
//		width-=1;
//		if(width<0) width=0;
//		height-=1;
//		if(height<0) height=0;
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
				
		
		Vector2f pos = getPos().createClone();
		if(this.entity!=null) {
			Rectangle bounds = this.entity.getBounds();
			pos.x -= bounds.width/2;
			pos.y -= bounds.height/2;
			
			Vector2f.Vector2fMS(pos, this.entity.getFacing(), 35, pos);
		}
		
		int color = 0x008B8386;
		switch(r.nextInt(4)) {
			case 0: color = 0x00838B8B;
				break;
			case 1: color = 0x00808A87;
				break;
			case 2: color = 0x00838B83;
				break;
			default:
				color = 0x008B8386;
				break;
		}
		
		return new SmokeParticle(pos, new Vector2f(x,y), 6000, color, width, height);
	}

	class SmokeParticle extends Particle {

		private FadeValue alpha;
		private int speed;
		private final int maxSpeed;
		private int color;
		private int width, height;
//		private Sprite image;
		/**
		 * @param pos
		 * @param vel
		 * @param timeToLive
		 */
		public SmokeParticle(Vector2f pos, Vector2f vel, int timeToLive, int color, int width, int height) {
			super(pos, vel, timeToLive);
			alpha = new FadeValue(85, 0, timeToLive-400);
			maxSpeed = 80;
			speed = maxSpeed;
		
			this.width = width;
			this.height = height;
			
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
			
			setPos(newX, newY);			
		}
		
		/* (non-Javadoc)
		 * @see seventh.client.gfx.particle.Particle#doRender(leola.live.gfx.Canvas, leola.live.gfx.Camera, int, int)
		 */
		@Override
		protected void doRender(Canvas canvas, Camera camera, float renderX,
				float renderY) {
			
			int colorS = (alpha.getCurrentValue() << 24) | color;			
			canvas.setCompositeAlpha(alpha.getCurrentValue()/255.0f);
			//canvas.drawImage(Art.smokeImage, renderX, renderY, colorS);
			canvas.drawScaledImage(Art.smokeImage, renderX, renderY, width, height, colorS);
			canvas.setCompositeAlpha(1.0f);			
		}
	}
}
