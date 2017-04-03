package test.shared;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.Assert;
import seventh.shared.EaseInInterpolation;

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

	@Test
	public void testUpdate() {
		
	}

	@Test
	public void testIsExpired() {
		
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
