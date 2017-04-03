package Test.shared;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.shared.FpsCounter;

public class FpsCounterTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	/**
	 * Test initialization
	 * @input null / integer
	 * @ExpectedOutput set FpsCounter.simplesize as default(100) / as 30
	 */
	@Test
	public void testFpsCounterInt() {
		FpsCounter fps = new FpsCounter();
		FpsCounter fps2 = new FpsCounter(30);
		
		final int expected = 100;
		final int expected2 = 30;
		
		assertEquals(expected,fps.getSampleSize());
		assertEquals(expected2,fps2.getSampleSize());
	}

	@Test
	public void testFpsCounter() {
		//fail("Not yet implemented");
	}

	@Test
	public void testUpdate() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGetSampleSize() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGetAvgFPS() {
		//fail("Not yet implemented");
	}

	@Test
	public void testGetFps() {
		//fail("Not yet implemented");
	}

}
