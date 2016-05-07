/*
 * see license.txt 
 */
package seventh.client.gfx.particle;

import java.util.Random;

import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.client.gfx.Art;
import seventh.client.gfx.TextureUtil;
import seventh.map.Tile;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class WallCrumbleEmitter extends Emitter {

	private TextureRegion[] images;
	private Vector2f vel;
	
	/**
	 * @param pos
	 * @param timeToLive
	 * @param timeToNextSpawn
	 */
	public WallCrumbleEmitter(Tile tile, Vector2f pos) {
		super(pos, 100_000, 0);
		
		// TODO: fix the flipping business
		// in tiles, this is driving me nuts!
		Sprite image = new Sprite(tile.getImage());
		image.flip(false, true);
		
		this.vel = new Vector2f(1, 0);
		
		Random rand = getRandom();
		this.images = new TextureRegion[44];
		int i = 0;
		for(; i < images.length-15; i++) {
			int x = rand.nextInt(image.getRegionWidth());
			int y = rand.nextInt(image.getRegionHeight());
			int width = rand.nextInt(image.getRegionWidth()/2);
			int height = rand.nextInt(image.getRegionHeight()/2);
			
			if(x+width > image.getRegionWidth()) {				
				width = image.getRegionWidth() - x;
			}
			
			if(y+height > image.getRegionHeight()) {
				height = image.getRegionHeight() - y;
			}
						
			Sprite sprite = new Sprite(image);
			TextureUtil.setFlips(sprite, tile.isFlippedHorizontal(), tile.isFlippedVertical(), tile.isFlippedDiagnally());
			sprite.setRegion(sprite, x, y, width, height);
			
			images[i] = sprite;					
		}
		
		Sprite sprite = new Sprite(Art.BLACK_IMAGE, 0, 0, 2, 4);
		for(;i<images.length;i++) {
			this.images[i] = sprite;
		}
		
		setPersistent(true);
		this.maxParticles = images.length;
	}

	/* (non-Javadoc)
	 * @see seventh.client.gfx.particle.Emitter#newParticle()
	 */
	@Override
	protected Particle newParticle() {
		Random rand = getRandom();
		int index = rand.nextInt(this.images.length);		
		TextureRegion image = this.images[index];
		
		Vector2f pos = getPos().createClone();
		double rotation = Math.toRadians(rand.nextInt(360));		
		Vector2f spreadDir = vel.rotate(rotation);
		
		CrubbledParticle particle = new CrubbledParticle(image, pos, spreadDir, (float)rand.nextInt(360), 1.0f, (int)getTimeToLive());
		particle.setSpeed(250.5f);
		particle.setFriction(40f);
		return particle;
	}
	
}
