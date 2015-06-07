/*
 * see license.txt 
 */
package seventh.client.gfx;

import seventh.math.Vector2f;
import seventh.shared.TimeStep;

import com.badlogic.gdx.graphics.g2d.Sprite;

/**
 * @author Tony
 *
 */
public class TankTrackMarks implements Renderable {

	static class TankTrack {
		float x, y;
		float orientation;
		float alpha;
		
		public void set(float x, float y, float orientation) {
			this.x = x; 
			this.y = y;
			this.orientation = orientation;
			this.alpha = 0.55f;
		}
		
		public void decay(TimeStep timeStep) {
			if(this.alpha > 0) {
				this.alpha -= 0.000445f;
				if(this.alpha < 0) {
					this.alpha = 0f;
				}
			}
		}
	}
	
	private Sprite trackMarkSprite;
	private TankTrack[] tracks;
	private int index;
	/**
	 * 
	 */
	public TankTrackMarks(int size) {
		if(size<=0) {
			throw new IllegalArgumentException("Size can not be zero!");
		}
		
		this.tracks = new TankTrack[size];
		for(int i = 0; i < tracks.length;i++) {
			this.tracks[i] = new TankTrack();
		}
		
		
		this.trackMarkSprite = new Sprite(Art.tankTrackMarks);

	}
	
	public void add(Vector2f p, float orientation) {
		add(p.x, p.y, orientation);
	}

	public void add(float x, float y, float orientation) {
		this.tracks[this.index].set(x, y, orientation);
		this.index = (this.index+1) % this.tracks.length;
	}
	
	/* (non-Javadoc)
	 * @see seventh.client.gfx.Renderable#update(seventh.shared.TimeStep)
	 */
	@Override
	public void update(TimeStep timeStep) {
		for(int i = 0; i < tracks.length;i++) {
			TankTrack track = tracks[i];
			track.decay(timeStep);
		}
	}
	
	@Override
	public void render(Canvas canvas, Camera camera, long alpha) {
		Vector2f cameraPos = camera.getPosition();		
		for(int i = 0; i < tracks.length;i++) {
			TankTrack track = tracks[i];
			
			float rx = track.x-cameraPos.x;
			float ry = track.y-cameraPos.y;
						
			trackMarkSprite.setRotation( (float)Math.toDegrees(track.orientation)-90f);			
			trackMarkSprite.setOrigin(16, 8);
			trackMarkSprite.setPosition(rx-16, ry-16);
			trackMarkSprite.setColor(0.86f, .2f, 0.6f, track.alpha);
			canvas.drawRawSprite(trackMarkSprite);
	
		}
	}
	
	public void clear() {
		for(int i = 0; i < tracks.length;i++) {
			this.tracks[i].set(0, 0, 0);
		}
	}
}
