/*
 * see license.txt 
 */
package seventh.client.gfx;

import seventh.math.Vector2f;

import com.badlogic.gdx.graphics.g2d.TextureRegion;

/**
 * @author Tony
 *
 */
public class Cursor {

	private Vector2f cursorPos;
	private TextureRegion cursorImg;
	private boolean isVisible;
	
	private float mouseSensitivity;
	
	private int prevX, prevY;
	
	/**
	 */
	public Cursor() {
		this(Art.cursorImg);
	}
	
	/**
	 * @param image 
	 * 			the cursor image to use
	 */
	public Cursor(TextureRegion image) {
		this.cursorImg= image;
		this.cursorPos = new Vector2f();
		this.isVisible = true;
		this.mouseSensitivity = 1.0f;
	}

	/**
	 * @param mouseSensitivity the mouseSensitivity to set
	 */
	public void setMouseSensitivity(float mouseSensitivity) {
		this.mouseSensitivity = mouseSensitivity;		
	}
	
	/**
	 * @return the mouseSensitivity
	 */
	public float getMouseSensitivity() {
		return mouseSensitivity;
	}
	
	/**
	 * @return the isVisible
	 */
	public boolean isVisible() {
		return isVisible;
	}
	
	/**
	 * @param isVisible the isVisible to set
	 */
	public void setVisible(boolean isVisible) {
		this.isVisible = isVisible;
	}

	/**
	 * Moves the cursor to the specified location
	 * 
	 * @param x
	 * @param y
	 */
	public void moveTo(int x, int y) {
		if(isVisible()) {			
			float deltaX = this.mouseSensitivity * (this.prevX - x);
			float deltaY = this.mouseSensitivity * (this.prevY - y);
			
			this.cursorPos.x -= deltaX;
			this.cursorPos.y -= deltaY;
			
			this.prevX = x;
			this.prevY = y;			
		}
	}
	
	/**
	 * Moves the cursor based on the delta movement
	 * @param dx either 1, -1 or 0
	 * @param dy either 1, -1 or 0
	 */
	public void moveByDelta(float dx, float dy) {
		float deltaX = this.mouseSensitivity * (dx*20);
		float deltaY = this.mouseSensitivity * (dy*20);
		
		this.prevX = (int)cursorPos.x;
		this.prevY = (int)cursorPos.y;
		
		this.cursorPos.x += deltaX;
		this.cursorPos.y += deltaY;		
	}
	
	/**
	 * @return the x position
	 */
	public int getX() {
		return (int)this.cursorPos.x;
	}
	
	/**
	 * @return the y position
	 */
	public int getY() {
		return (int)this.cursorPos.y;
	}
	
	/**
	 * @return the cursorPos
	 */
	public Vector2f getCursorPos() {
		return cursorPos;
	}
	
	/**
	 * Draws the cursor on the screen
	 * @param canvas
	 */
	public void render(Canvas canvas) {
		if(isVisible()) {
			int imageWidth = cursorImg.getRegionWidth();
			int imageHeight = cursorImg.getRegionHeight();
			canvas.drawImage(cursorImg, (int)cursorPos.x - imageWidth/2, (int)cursorPos.y - imageHeight/2, null);
		}
	}
}
