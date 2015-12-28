/*
 *	leola-live 
 *  see license.txt
 */
package seventh.client.gfx;

import seventh.shared.TimeStep;


/**
 * A {@link Renderable} is anything that needs to be rendered to the screen.
 * 
 * @author Tony
 *
 */
public interface Renderable {

	/**
	 * Update this Renderable.  Updates may include moving ahead with animation frames.
	 * 
	 * @param timeStep
	 */
	public void update(TimeStep timeStep);
	
	/**
	 * Render this object.
	 * 
	 * @param renderer
	 * @param camera
	 * @param alpha
	 */
	public void render(Canvas canvas, Camera camera, float alpha);
}
