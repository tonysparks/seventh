/*
 * see license.txt 
 */
package seventh.client.gfx;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

import seventh.math.Rectangle;
import seventh.math.Vector2f;
import seventh.shared.TimeStep;

/**
 * Represents the mouse pointer for the game.  This implementation relies on an {@link TextureRegion} for the cursor
 * image.
 * 
 * @author Tony
 *
 */
public class ImageCursor extends Cursor {

	private TextureRegion cursorImg;

	/**
	 */
	public ImageCursor() {
		this(Art.cursorImg);
	}
	
	/**
	 * @param image 
	 * 			the cursor image to use
	 */
	public ImageCursor(TextureRegion image) {
		super(new Rectangle(image.getRegionWidth(), image.getRegionHeight()));
		this.cursorImg = image;
	}
	
	@Override
	public void update(TimeStep timeStep) {			
	}
	
	/**
	 * Draws the cursor on the screen
	 * @param canvas
	 */
	@Override 
	protected void doRender(Canvas canvas) {
		Vector2f cursorPos = getCursorPos();
		int imageWidth = cursorImg.getRegionWidth();
		int imageHeight = cursorImg.getRegionHeight();
		
		canvas.drawImage(cursorImg, (int)cursorPos.x - imageWidth/2, (int)cursorPos.y - imageHeight/2, null);				
	}
}
