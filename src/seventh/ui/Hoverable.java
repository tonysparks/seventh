/*
 * see license.txt 
 */
package seventh.ui;

import seventh.client.gfx.Cursor;

/**
 * If the {@link Widget} can be hovered over by the {@link Cursor}
 * 
 * @author Tony
 *
 */
public interface Hoverable {

	/**
	 * If the {@link Cursor} is hovering over this {@link Widget}
	 * 
	 * @return true if the {@link Cursor} is hovering over this {@link Widget}
	 */
	public boolean isHovering();
}
