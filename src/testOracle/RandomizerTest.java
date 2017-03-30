package testOracle;

import static org.junit.Assert.*;
import java.util.Random;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import seventh.shared.Randomizer;

public class RandomizerTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void test() {
		Random random = new Random();
		Randomizer randomizer = new Randomizer(random);
		randomizer.getRandomRangeMin(0.5);
		
		//randomizer.nextFloat();
		assertTrue(0.5 < randomizer.getRandomRange(random, 2.0,1.0));
	}

}
