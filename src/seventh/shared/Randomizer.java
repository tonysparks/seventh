/*
 * see license.txt 
 */
package seventh.shared;

import java.util.Random;

/**
 * Utility for random numbers
 * 
 * @author Tony
 *
 */
public class Randomizer {

	private Random random;
	
	/**
	 * @param random
	 */
	public Randomizer(Random random) {
		this.random = random;
	}

	public int nextInt(int max) {
		return this.random.nextInt(max);
	}
	
	public boolean nextBoolean() {
		return this.random.nextBoolean();
	}
	
	public double nextDouble() {
		return this.random.nextDouble();
	}
	
	public float nextFloat() {
		return this.random.nextFloat();
	}
	
	
	public double getRandomRange(double min, double max) {
		return min + (this.random.nextDouble() * (max - min));
	}	
	
	public double getRandomRangeMin(double min) {
		return getRandomRange(min, 1.0);
	}
	
	public double getRandomRangeMax(double max) {
		return getRandomRange(0.0, max);
	}
}
