/*
 * see license.txt 
 */
package seventh.client.gfx;

/**
 * First renders to the framebuffer
 * 
 * @author Tony
 *
 */
public interface FrameBufferRenderable extends Renderable {
	
	/**
	 * Renders to the frame buffer
	 * 
	 * @param canvas
	 * @param camera
	 */
	public void frameBufferRender(Canvas canvas, Camera camera, float alpha);
	
	/**
	 * @return true if expired
	 */
	public boolean isExpired();
}
