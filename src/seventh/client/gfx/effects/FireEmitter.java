/*
 * see license.txt 
 */
package seventh.client.gfx.effects;

import java.util.Random;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Sprite;

import seventh.client.gfx.Art;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.client.gfx.particle.Emitter;
import seventh.client.gfx.particle.FadeValue;
import seventh.client.gfx.particle.Particle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * @author Tony
 *
 */
public class FireEmitter extends Emitter {

//	private BurnEmitter innerEmitter;
//	
//	/**
//	 * 
//	 */
//	public FireEmitter(Vector2f pos, int timeToLive) {
//		super(pos, timeToLive, 1);
//	}
//	
//	
//	
//	
//    class BurnEmitter extends Emitter {
    	private Color color;
    	private float startingSize;
    	private float endingSize;
    	
		/**
		 * @param pos
		 * @param timeToLive
		 * @param timeToNextSpawn
		 */
		public FireEmitter(Vector2f pos, int timeToLive, int color, float startingSize, float endingSize) {
			super(pos, timeToLive, 1);
			this.color = new Color(color);
			this.startingSize = startingSize;
			this.endingSize = endingSize;
			setDieInstantly(false);
		}
		
		
	
		@Override
		protected Particle newParticle() {
			this.particleBatchSize = 6;
			Random r = getRandom();
			
			Vector2f vel = new Vector2f(1,0);
			Vector2f.Vector2fRotate(vel, Math.toRadians(r.nextInt(360)), vel);
			Vector2f.Vector2fMult(vel, r.nextFloat()*5f, vel);
			
			int red = 0xEB502Fff;
			int yellow = 0xffF2F99F;
			
			Vector2f pos = new Vector2f(1,0);
			Vector2f.Vector2fRotate(pos, Math.toRadians(r.nextInt(360)), pos);
			Vector2f.Vector2fMA(getPos(), pos, r.nextInt(15), pos);
			
			return new FireParticle(pos, vel, 1500);
		}
	
		class FireParticle extends Particle {
				
			private float speed;
			private final float maxSpeed;
	//		private int color;
			private Sprite innerSprite, outerSprite;
			private float scale;
			private Color startColor, endColor;
			private float alpha;
			/**
			 * @param pos
			 * @param vel
			 * @param timeToLive
			 */
			public FireParticle(Vector2f pos, Vector2f vel, int timeToLive) {
				super(pos, vel, timeToLive);
	
				maxSpeed = 20f;
				speed = maxSpeed;
							
				float size = startingSize - (getRandom().nextInt(50) / 100.0f);
				this.scale = size;
				
				this.innerSprite = new Sprite(Art.smokeImage);
				this.innerSprite.scale(size);
				//int color = 0xE6F109ff;								
				
				this.startColor = new Color(color);
				this.alpha = this.startColor.a;
//				if(getRandom().nextInt(5)==4) 
//				{
//					this.startColor.set(0xffffffff);
//				}
				this.endColor = new Color(this.startColor);
				this.endColor.a = 0.25f;
				
	//			this.innerSprite = new Sprite(Art.smokeImage);
	//			this.innerSprite.scale(size-0.8f);
	//			this.outerSprite = new Sprite(Art.smokeImage);
	//			this.outerSprite.scale(size);
	//			
	//			this.startColor = new Color(0xE6F909ff);
	//			this.endColor = new Color(0xEB502F8f);
			}
			
			/* (non-Javadoc)
			 * @see seventh.client.gfx.particle.Particle#update(leola.live.TimeStep)
			 */
			@Override
			public void update(TimeStep timeStep) {		
				super.update(timeStep);
				
				double dt = timeStep.asFraction();
				int newX = (int)Math.round(pos.x + vel.x * speed * dt);
				int newY = (int)Math.round(pos.y + vel.y * speed * dt);
							
	//			float newX = pos.x + vel.x * speed * (float)dt;
	//			float newY = pos.y + vel.y * speed * (float)dt;
				
				speed *= 0.69f;//(float)getRandom().nextInt((int)maxSpeed);
				
				pos.x = newX;
				pos.y = newY;
				
				/*
				this.scale *= 0.969f;
				if(this.scale < 0.3f) {
					this.scale = 0.03f;
				}*/
				this.scale *= 1.031f;
				if(this.scale > endingSize) {
					this.scale = endingSize;
				}
				
				this.startColor.a *= 0.89f;
			}
			
			/* (non-Javadoc)
			 * @see seventh.client.gfx.particle.Particle#doRender(leola.live.gfx.Canvas, leola.live.gfx.Camera, int, int)
			 */
			@Override
			protected void doRender(Canvas canvas, Camera camera, float renderX,
					float renderY) {
				
				//int colorS = (alpha.getCurrentValue() << 24) | color;			
				//canvas.fillCircle(10.0f, renderX-3, renderY+3, colorS);
	//			canvas.setCompositeAlpha(alpha.getCurrentValue()/255.0f);
	//			canvas.drawImage(Art.smokeImage, renderX, renderY, colorS);
	//			canvas.setCompositeAlpha(1.0f);
	//			int color = (alpha.getCurrentValue() << 24) | 0x00000000;			
	//			canvas.fillCircle(10.0f, renderX, renderY, color);
				
				
				
				//float alpha = this.alpha.getCurrentValue() / 255;
				innerSprite.setPosition(renderX, renderY);
				//outerSprite.setPosition(renderX, renderY);
				
				//Color currentColor = this.startColor.lerp(this.endColor, 0.079f);
				//innerSprite.setColor(currentColor);
				
				innerSprite.setColor(this.startColor);
				innerSprite.setScale(this.scale);
				//innerSprite.setAlpha(alpha);
				
				//canvas.drawRawSprite(outerSprite);				
				canvas.drawRawSprite(innerSprite);
				
			}
		}
//    }
}
