/*
 * see license.txt 
 */
package seventh.client.gfx;

import com.badlogic.gdx.graphics.Color;

import seventh.math.Vector3f;

/**
 * @author Tony
 *
 */
public class Colors {

	public static int subtract(int left, int right) {
		int r = ( (left>>16) & 0x000000ff) - ( (right>>16) & 0x000000ff);  
		int g = ( (left>>8) & 0x000000ff) - ( (right>>8) & 0x000000ff);
		int b = ( (left>>0) & 0x000000ff) - ( (right>>0) & 0x000000ff);
		
		int result = 0xff000000 | (r<<16) | (g<<8) | (b<<0);		
		return result;
	}

	/**
	 * Converts the {@link Vector3f} into a int color
	 * @param v
	 * @param alpha
	 * @return the int color rgba
	 */
	public static int toColor(Vector3f v, float alpha) {
		return Color.rgba8888(v.x, v.y, v.z, alpha);
	}
	
	/**
	 * Converts the {@link Vector3f} into a int color
	 * @param v
	 * @return the int color rgba
	 */
	public static int toColor(Vector3f v) {
		return Color.rgba8888(v.x, v.y, v.z, 1.0f)<<16;
	}
	
	public static Vector3f toVector3f(int color) {
		Color c = new Color(color);
		return new Vector3f(c.r, c.g, c.b);
	}
}
