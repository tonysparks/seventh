/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.Vector2f;

/**
 * A particle that has an attached image
 * 
 * @author Tony
 *
 */
public class ImageParticle extends Particle {

	private Sprite sprite;
	private float rotateDegrees;
	private float scale;
	
	/**
	 * @param pos
	 * @param vel
	 */
	public ImageParticle(TextureRegion image, Vector2f pos, Vector2f vel, float rotateDegrees, float scale, int timeToLive) {
		super(pos, vel, timeToLive);
		
		this.scale = scale;
		this.rotateDegrees = rotateDegrees;
		
		this.sprite = new Sprite();
		setImage(image);
	}
	
	/**
	 * @return the sprite
	 */
	public Sprite getSprite() {
		return sprite;
	}

	/**
	 * @param image the image to set
	 */
	public void setImage(TextureRegion image) {
		this.sprite.setTexture(image.getTexture());
		this.sprite.setRegion(image);		
		this.sprite.setSize(image.getRegionWidth(), image.getRegionHeight());
		
		this.sprite.setScale(this.scale);	
		this.sprite.rotate(this.rotateDegrees);
	}
		
	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Particle#doRender(leola.live.gfx.Canvas, leola.live.gfx.Camera, int, int)
	 */
	@Override
	protected void doRender(Canvas canvas, Camera camera, float rx, float ry) {
		sprite.setPosition(rx, ry);	
		canvas.drawRawSprite(sprite);
	}

}
