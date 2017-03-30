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


}
