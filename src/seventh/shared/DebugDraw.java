/*
 * see license.txt 
 */
package seventh.shared;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

import seventh.client.gfx.Camera;
import seventh.client.gfx.Canvas;
import seventh.math.OBB;
import seventh.math.Vector2f;

/**
 * A helper for drawing items to the screen.
 * 
 * @author Tony
 *
 */
public class DebugDraw {

	static abstract class Drawable {
		boolean offsetWithCamera;
		Integer color;
		int drawCount;
		Drawable(boolean offsetWithCamera, Integer color) {		
			this.offsetWithCamera = offsetWithCamera;
			this.color = color;
			this.drawCount = 0;
		}


		abstract void draw(Canvas canvas, Camera camera);
	}
	
	private static class StringDrawable extends Drawable {
		String text;
		int x, y;
		 
		StringDrawable(boolean offsetWithCamera, Integer color, String text, int x, int y) {
			super(offsetWithCamera, color);
			this.text = text;			
			this.x = x;
			this.y = y;
			this.color = color;
		}
		
		@Override
		public void draw(Canvas canvas, Camera camera) {
			
			float x = this.x;
			float y = this.y;
			if(offsetWithCamera) {
				x -= camera.getPosition().x;
				y -= camera.getPosition().y;
			}			
			canvas.drawString(text, (int)x, (int)y, color);
		}
	}
	
	private static class LineDrawable extends Drawable {
		Vector2f a, b;
		
		/**
		 * 
		 */
		public LineDrawable(boolean offsetWithCamera, Integer color, Vector2f a, Vector2f b) {
			super(offsetWithCamera, color);
			this.a = a; this.b = b;
		}
		@Override
		public void draw(Canvas canvas, Camera camera) {
			
			float ax = a.x;
			float ay = a.y;
			
			float bx = b.x;
			float by = b.y;
			
			if(offsetWithCamera) {
				ax -= camera.getPosition().x;
				ay -= camera.getPosition().y;
				
				bx -= camera.getPosition().x;
				by -= camera.getPosition().y;
			}			
			
			canvas.drawLine((int)ax, (int)ay, (int)bx, (int)by, color);
		}
	}
	
	private static class RectDrawable extends Drawable {
		int x; 
		int y; 
		int width; 
		int height;
		boolean fill;
		public RectDrawable(boolean offsetWithCamera, Integer color, boolean fill, int x, int y, int width, int height) {
			super(offsetWithCamera, color);
			this.fill = fill;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}

		@Override
		public void draw(Canvas canvas, Camera camera) {

			float x = this.x;
			float y = this.y;
			if(offsetWithCamera) {
				x -= camera.getPosition().x;
				y -= camera.getPosition().y;
			}			
			if(fill) {
				canvas.fillRect((int)x, (int)y, width, height, color);
			}
			else {
				canvas.drawRect((int)x, (int)y, width, height, color);
			}
		}
	}
	
	private static Queue<Drawable> drawCalls = new ConcurrentLinkedQueue<>();
	private static AtomicBoolean enabled = new AtomicBoolean(false);
	
	/**
	 * Enable draw calls.
	 * 
	 * @param enable
	 */
	public static void enable(boolean enable) {
		enabled.set(enable);
		
		if(!enabled.get()) {
			drawCalls.clear();
		}
	}
	
	/**
	 * Draws a string relative to the camera position.
	 * 
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 */
	public static void drawStringRelative(String text, int x, int y, Integer color) {
		if(enabled.get()) {
			drawCalls.add(new StringDrawable(true, color, text, x, y));
		}
	}
	
	/**
	 * Draws a string to the screen.
	 * 
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 */
	public static void drawString(String text, int x, int y, Integer color) {
		if(enabled.get()) {
			drawCalls.add(new StringDrawable(false, color, text, x, y));
		}
	}
	
	/**
	 * Draws a string relative to the camera position.
	 * 
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 */
	public static void drawStringRelative(String text, Vector2f pos, Integer color) {
		if(enabled.get()) {
			drawCalls.add(new StringDrawable(true, color, text, (int)pos.x, (int)pos.y) );
		}
	}
	
	/**
	 * Draws a string to the screen.
	 * 
	 * @param text
	 * @param x
	 * @param y
	 * @param color
	 */
	public static void drawString(String text, Vector2f pos, Integer color) {
		if(enabled.get()) {
			drawCalls.add(new StringDrawable(false, color, text, (int)pos.x, (int)pos.y));
		}
	}
	
	public static void drawLine(Vector2f a, Vector2f b, Integer color) {
		if(enabled.get()) {
			drawCalls.add(new LineDrawable(false, color, a, b));
		}
	}
	
	public static void drawLineRelative(Vector2f a, Vector2f b, Integer color) {
		if(enabled.get()) {
			drawCalls.add(new LineDrawable(true, color, a, b));
		}
	}
	
	public static void drawOOB(OBB oob, Integer color) {
		if(enabled.get()) {
			drawCalls.add(new LineDrawable(false, color, oob.topLeft, oob.topRight));
			drawCalls.add(new LineDrawable(false, color, oob.topRight, oob.bottomRight));
			drawCalls.add(new LineDrawable(false, color, oob.bottomRight, oob.bottomLeft));
			drawCalls.add(new LineDrawable(false, color, oob.bottomLeft, oob.topLeft));
		}
	}
	
	public static void drawOOBRelative(OBB oob, Integer color) {
		if(enabled.get()) {
			drawCalls.add(new LineDrawable(true, color, oob.topLeft, oob.topRight));
			drawCalls.add(new LineDrawable(true, color, oob.topRight, oob.bottomRight));
			drawCalls.add(new LineDrawable(true, color, oob.bottomRight, oob.bottomLeft));
			drawCalls.add(new LineDrawable(true, color, oob.bottomLeft, oob.topLeft));
		}
	}
	
	/**
	 * Draws a rectangle relative to the camera position.
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void drawRectRelative(int x, int y, int width, int height, Integer color) {
		if(enabled.get()) {
			drawCalls.add(new RectDrawable(true, color, false, x, y, width, height));
		}
	}
	
	/**
	 * Draws a rectangle
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void drawRect(int x, int y, int width, int height, Integer color) {
		if(enabled.get()) {
			drawCalls.add(new RectDrawable(false, color, false, x, y, width, height));
		}
	}
	
	/**
	 * Fills in a rectangle relative to the camera position
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void fillRectRelative(int x, int y, int width, int height, Integer color) {
		if(enabled.get()) {
			drawCalls.add(new RectDrawable(true, color, true, x, y, width, height));
		}
	}
	
	/**
	 * Fills in a rectangle
	 * 
	 * @param x
	 * @param y
	 * @param width
	 * @param height
	 * @param color
	 */
	public static void fillRect(int x, int y, int width, int height, Integer color) {
		if(enabled.get()) {
			drawCalls.add(new RectDrawable(false, color, true, x, y, width, height));
		}
	}
	
	/**
	 * Called by the client to do the actual rendering calls.
	 * 
	 * @param canvas
	 * @param camera
	 */
	public static void render(Canvas canvas, Camera camera) {
		if(enabled.get()) {
		
			for(Drawable d : drawCalls) {
				d.draw(canvas, camera);
				d.drawCount++;
			}
			
//			frameCounter++;
			
			for(Drawable d : drawCalls) {
				if(d.drawCount > 3) {
					drawCalls.remove(d);
				}
			}
//			if(frameCounter >= 0) 
//			{
//				drawCalls.clear();
//				frameCounter = 0;
//			}
		}
	}
	
}
