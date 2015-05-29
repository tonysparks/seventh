/*
 * see license.txt 
 */
package seventh.math;

/**
 * @author Tony
 *
 */
public class Projection {

	private double min;
	private double max;
	
	/**
	 * @param min
	 * @param max
	 */
	public Projection(double min, double max) {
		this.min = min;
		this.max = max;
	}
	
	/**
	 * Determines if the other {@link Projection} overlaps with this one
	 * @param other
	 * @return true if they overlap
	 */
	public boolean overlaps(Projection other) {
		if(other.min > this.max || this.min > other.max) {
			return false;
		}
		
		return true;
	}

}
