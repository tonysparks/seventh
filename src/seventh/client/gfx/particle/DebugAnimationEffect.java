/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 *
 */
public class DebugAnimationEffect implements Effect {

	private AnimatedImage anim;
	private Vector2f pos;
	private boolean persist;
	private FadeValue fade;
	private float rotation;
	private Sprite sprite;
	
	private int offsetX, offsetY;
	
	/**
	 * @param anim
	 * @param pos
	 * @param persist
	 */
	public DebugAnimationEffect(AnimatedImage anim, Vector2f pos, float rotation) {
		this(anim, pos, rotation, false);
	}
	
	/**
	 * @param anim
	 * @param pos
	 * @param persist
	 */
	public DebugAnimationEffect(AnimatedImage anim, Vector2f pos, float rotation, boolean persist) {
		this(anim, pos, rotation, persist, 4000);
	}

	/**
	 * @param anim
	 * @param pos
	 * @param persist
	 */
	public DebugAnimationEffect(AnimatedImage anim, Vector2f pos, float rotation, boolean persist, int fadeTime) {
		super();
		this.anim = anim;
		this.pos = pos;
		this.persist = persist;
		this.rotation = rotation;
		this.fade = new FadeValue(255, 0, fadeTime);
		
		this.sprite = new Sprite(anim.getCurrentImage());
		this.sprite.flip(false, true);
	}
	
	public void setOffset(int x, int y) {
		this.offsetX = x;
		this.offsetY = y;
	}
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#render(leola.live.gfx.Canvas, leola.live.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, long a) {
			
		int rx = (int)(pos.x);
		int ry = (int)(pos.y);
		
		float alpha = 1.0f;
		if(!this.persist) {
			 alpha = (float)this.fade.getCurrentValue() / 255.0f;
		}
		
		float priorAlpha = canvas.getCompositeAlpha();
		canvas.setCompositeAlpha(alpha);
		
		TextureRegion region = anim.getCurrentImage();
		sprite.setRegion(region);
				
		if(offsetX != 0 || offsetY != 0) {
			//sprite.setSize(16, 16);
			sprite.setPosition(rx-offsetX, ry-offsetY);
			//sprite.setPosition(rx+22, ry-43);
			sprite.setOrigin(offsetX, offsetY);
		}
		else {
			int w = region.getRegionWidth() / 2;
			int h = region.getRegionHeight() / 2;
			
			sprite.setPosition(rx-w, ry-h);
		}
		sprite.setRotation(this.rotation-90);
		
//		sprite.setColor(1, 1, 1, alpha);
		canvas.drawSprite(sprite);
		canvas.drawRect( (int)sprite.getX(), (int)sprite.getY(), sprite.getRegionWidth(), sprite.getRegionHeight(), 0xff00aa00);
		canvas.setCompositeAlpha(priorAlpha);
	}
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {	
		anim.update(timeStep);
		
//		if(anim.isDone() && this.persist) {
//			anim.reset();
//		}
		
		if(!this.persist) {
			this.fade.update(timeStep);
		}
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Effect#isDone()
	 */
	@Override
	public boolean isDone() {
		return !persist && anim.isDone() && this.fade.isDone();
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Effect#destroy()
	 */
	@Override
	public void destroy() {		
	}
}
