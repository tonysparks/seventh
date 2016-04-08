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
		return getRandomRange(random, min, max);
	}	
	
	public double getRandomRangeMin(double min) {
		return getRandomRangeMin(random, min);
	}
	
	public double getRandomRangeMax(double max) {
		return getRandomRangeMax(random, max);
	}
	
	
	public static double getRandomRange(Random random, double min, double max) {
		return min + (random.nextDouble() * (max - min));
	}	
	
	public static double getRandomRangeMin(Random random, double min) {
		return getRandomRange(random, min, 1.0);
	}
	
	public static double getRandomRangeMax(Random random, double max) {
		return getRandomRange(random, 0.0, max);
	}
}
