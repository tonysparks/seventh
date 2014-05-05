/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

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
public class DebugSpriteEffect implements Effect {

	private Vector2f pos;

	private float rotation;
	private int color;
	private Sprite sprite;
	
	private int offsetX, offsetY;
	


	/**
	 * @param tex
	 * @param pos
	 * @param rotation
	 */
	public DebugSpriteEffect(TextureRegion tex, Vector2f pos, float rotation, int color) {
		super();
		this.pos = pos;
		this.rotation = rotation;
		this.color = color;
		
		this.sprite = new Sprite(tex);
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
		
		if(offsetX != 0 || offsetY != 0) {
			//sprite.setSize(16, 16);
			sprite.setPosition(rx-offsetX, ry-offsetY);
			//sprite.setPosition(rx+22, ry-43);
			sprite.setOrigin(offsetX, offsetY);
		}
		else {
			int w = sprite.getRegionWidth() / 2;
			int h = sprite.getRegionHeight() / 2;
			
			sprite.setPosition(rx-w, ry-h);
		}
		sprite.setRotation(this.rotation-90);
		
		float priorAlpha = canvas.getCompositeAlpha();
		
		canvas.drawSprite(sprite, (int)sprite.getX(), (int)sprite.getY(), color);
		canvas.drawRect( (int)sprite.getX(), (int)sprite.getY(), sprite.getRegionWidth(), sprite.getRegionHeight(), 0xff00aa00);
		canvas.setCompositeAlpha(priorAlpha);
	}
	
	/* (non-Javadoc)
	 * @see leola.live.gfx.Renderable#update(leola.live.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {	
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Effect#isDone()
	 */
	@Override
	public boolean isDone() {
		return false;
	}

}
