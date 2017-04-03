package test.math;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.junit.Assert.*;

import org.junit.Test;

import seventh.math.FloatUtil;
import seventh.math.Vector2f;

public class FloatUtilTest {
	/*
	 * Purpose: Determine if two floats are equal by a given Epsilon.
	 * Input: float, float // float,float,float
	 * Expected: 
	 * 			if two float variables are Equal, return true
	 *			if two float variables aren't Equal, return false  
	 */
	@Test
	public void eqTest() {
		float x = 1.234f;
		float y = 1.2345f;
		float z = 1.24f;
		
		assertTrue(FloatUtil.eq(x,y));
		assertFalse(FloatUtil.eq(x,z));
		assertFalse(FloatUtil.eq(x,y,0.0001f));
		assertTrue(FloatUtil.eq(x,z,0.01f));
	}
	}
