/*
 * see license.txt 
 */
package seventh.map;

import seventh.client.gfx.AnimatedImage;
import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.shared.TimeStep;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * An animated tile
 * 
 * @author Tony
 *
 */
public class AnimatedTile extends Tile {

	private AnimatedImage image;
	
	/**
	 * @param image
	 * @param width
	 * @param height
	 */
	public AnimatedTile(AnimatedImage image, int width, int height) {
		super(null, width, height);
		this.image = image;
		this.image.loop(true);
	}
	
	/**
	 * @return the animated image
	 */
	public AnimatedImage getAnimatedImage() {
		return image;
	}
	
	/* (non-Javadoc)
	 * @see seventh.map.Tile#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		image.update(timeStep);
	}

	/* (non-Javadoc)
	 * @see seventh.map.Tile#render(seventh.client.gfx.Canvas, seventh.client.gfx.Camera, long)
	 */
	@Override
	public void render(Canvas canvas, Camera camera, float alpha) {	
		TextureRegion tex = image.getCurrentImage();
		canvas.drawScaledImage(tex, getRenderX(), getRenderY(), getWidth(), getHeight(), 0xFFFFFFFF);
//		canvas.drawImage(tex, getRenderX(), getRenderY(), 0xFFFFFFFF);
	}
}
