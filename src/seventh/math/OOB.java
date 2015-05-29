/*
 * see license.txt 
 */
package seventh.math;

/**
 * Oriented Bounding Box
 * 
 * @author Tony
 *
 */
public class OOB {

	public float width;
	public float height;
	public float orientation;
	
	public Vector2f center;
	
	/**
	 * 
	 */
	public OOB(float orientation, Vector2f center, float width, float height) {
		this.orientation = orientation;
		this.center = center;
		this.width = width;
		this.height = height;
	}

	
}
