/*
 * see license.txt 
 */
package seventh.server;


import seventh.math.Rectangle;
import seventh.math.Vector2f;

/**
 * Some common functions exposed to the scripting runtime
 * 
 * @author Tony
 *
 */
public class SeventhScriptingCommonLibrary {

	
	/**
	 * Initializes a new {@link Vector2f}
	 * 
	 * @param x optional x component
	 * @param y optional y component
	 * @return the {@link Vector2f}
	 */
	public static Vector2f newVec2(Double x, Double y) {
		if(x!=null&&y!=null)
			return new Vector2f(x.floatValue(),y.floatValue());
		if(x!=null)
			return new Vector2f(x.floatValue(),0.0f);
		return new Vector2f();		
	}

	
	/**
	 * Initializes a new {@link Rectangle}
	 * 
	 * @param x optional x component
	 * @param y optional y component
	 * @param w optional w component
	 * @param h optional h component
	 * @return the {@link Rectangle}
	 */
	public static Rectangle newRect(Integer x, Integer y, Integer w, Integer h) {
		if (x != null && y != null && w != null && h != null) {
			return new Rectangle(x, y, w, h);
		}
		
		if (x != null && y != null) {
			return new Rectangle(0, 0, x, y);
		}
		
		return new Rectangle();
	}
}
