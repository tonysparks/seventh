/*
 * see license.txt 
 */
package seventh.server;


import seventh.math.Rectangle;
import seventh.math.Vector2f;

/**
 * @author Tony
 *
 */
public class LeolaScriptLibrary {

	public static Vector2f newVec(Double x, Double y) {
		if(x!=null&&y!=null)
			return new Vector2f(x.floatValue(),y.floatValue());
		if(x!=null)
			return new Vector2f(x.floatValue(),0.0f);
		return new Vector2f();		
	}

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
