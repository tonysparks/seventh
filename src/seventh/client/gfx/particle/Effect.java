/*
 * The Seventh
 * see license.txt 
 */
package seventh.client.gfx.particle;

import seventh.client.gfx.Renderable;

/**
 * @author Tony
 *
 */
public interface Effect extends Renderable {

	/**
	 * @return true if this effect is done
	 */
	public boolean isDone();
}
