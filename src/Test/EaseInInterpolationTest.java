package Test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import seventh.shared.EaseInInterpolation;
import seventh.shared.TimeStep;

public class EaseInInterpolationTest {

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testEaseInInterpolation() {
	
	}

	@Test
	public void testEaseInInterpolationFloatFloatLong() {
		
	}
	/**
	 * Reset EaseInInterpolation object
	 * @Inputs float from, float to, long time
	 * @throws Exception
	 * @ExpectedOutput set the EaseInInterpolation
	 */
	
	@Test
	public void testReset() throws Exception {
		EaseInInterpolation EI = new EaseInInterpolation();
		EaseInInterpolation EI2 = new EaseInInterpolation(100f,0f,8);
		EI.reset(30f,20f,5);
		EI2.reset(30f, 20f, 0);
		float expected = 30f;
		
		System.out.println(EI.getValue());
	
		Assert.assertEquals(expected, EI.getValue(),0.0001);
		Assert.assertNotSame(expected, EI.getValue());
	}
	/**
	 * Test the update function 
	 * @Input TimeStep(GameClock : 80, DeltaTime : 40)
	 * @ExpectedOutput EI's properties, RemainingTime is decreased by 40 every calling update()
	 */
	@Test
	public void testUpdate() {
		EaseInInterpolation EI = new EaseInInterpolation(100f,0f,60);
		System.out.println(EI.getRemainingTime());
		TimeStep TS = new TimeStep();
		
		TS.setGameClock(80);
		TS.setDeltaTime(40);
		EI.update(TS);
		System.out.println(EI.getRemainingTime());
		Assert.assertTrue(!EI.isExpired());
	}

	@Test
	public void testIsExpired() {
		EaseInInterpolation EI = new EaseInInterpolation(60f,0f,60);
		System.out.println(EI.getRemainingTime());
		TimeStep TS = new TimeStep();
		
		TS.setGameClock(60);
		TS.setDeltaTime(30);
		EI.update(TS);
		System.out.println(EI.getRemainingTime());
		EI.update(TS);
		System.out.println(EI.getRemainingTime());
		EI.update(TS);
		System.out.println(EI.getRemainingTime());
		
		Assert.assertTrue(EI.isExpired());
	}

	@Test
	public void testGetRemainingTime() {
		
	}

	@Test
	public void testGetValue() {
		
	}

	@Test
	public void testGetTarget() {
		
	}

}
