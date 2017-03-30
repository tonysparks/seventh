package testOracle;

import static org.junit.Assert.*;
import java.util.Random;
import org.junit.Test;

import seventh.shared.Randomizer;

public class RandomizerTest {

	/*
	 * Purpose: specify a minimum scope
	 * Input: getRandomRangeMin minimum scope 0.5
	 * Expected: 
	 * 			0.5 < random number
	 * 			random number < 1.0
	 */
	@Test
	public void testMinScope() {
		final double rangeMin = 0.5;
		final double rangeMax = 1.0;
		Randomizer randomizer = new Randomizer(new Random());
		assertTrue(rangeMin < randomizer.getRandomRangeMin(rangeMin));
		assertTrue(rangeMax > randomizer.getRandomRangeMin(rangeMin));
	}
	
	/*
	 * Purpose: specify a maximum scope
	 * Input: getRandomRangeMax maximum scope 0.8
	 * Expected: 
	 * 			0.0 < random number
	 * 			random number < 0.8
	 * 			
	 */
	@Test
	public void testMaxScope(){
		final double rangeMin = 0.0;
		final double rangeMax = 0.8;
		Randomizer randomizer = new Randomizer(new Random());
		assertTrue(rangeMin < randomizer.getRandomRangeMax(rangeMax));
		assertTrue(rangeMax > randomizer.getRandomRangeMax(rangeMax));
	}
}
