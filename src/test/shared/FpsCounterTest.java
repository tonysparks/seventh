package test.shared;

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
	
	/**
	 * Testcase for update 
	 * @Input long
	 * @ExpectedOutput set all figures except simplesize
	 */
	@Test
	public void testUpdate() {
		long expectedAvg= 0;
		long expectedTally = 0;
		long expectedCS = 0;
		long expectedFps = 0;
		
		FpsCounter fps = new FpsCounter();
		for(int i = 300;i >=0; i--){
			
			fps.update(i);
			if(i>0){
				expectedFps = 1000/i;
			}
			expectedCS++;
			if(expectedCS > fps.getSampleSize()) {
				expectedAvg = expectedTally / fps.getSampleSize();
				expectedTally = 0;
				expectedCS = 0;
			}
			else{
				expectedTally += expectedFps;
			}
			System.out.println(fps.getAvgFPS());
			System.out.println(fps.getFps());
			assertEquals(expectedAvg,fps.getAvgFPS());
			assertEquals(expectedFps,fps.getFps());
		}
	}
	

}
