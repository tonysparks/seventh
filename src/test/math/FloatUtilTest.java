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
	
	/*
	 * Purpose: Determine if one float is greater than another
	 * Input: float, float
	 * Expected: 
	 * 			if one float is greater than another, return true
	 *			if one float isn't greater than another, return false  
	 */
	@Test
	public void gtTest() {
		float x = 1.234f;
		float y = 1.2450f;
		float z = 1.24501f;
		
		assertTrue(FloatUtil.gt(y,x));
		assertFalse(FloatUtil.gt(x,y));
		assertFalse(FloatUtil.gt(y,z));

	}
	
	
	/*
	 * Purpose: Determine if one float is less than another
	 * Input: float, float
	 * Expected: 
	 * 			if one float is less than another, return true
	 *			if one float isn't less than another, return false  
	 */
	@Test
	public void ltTest() {
		float x = 1.234f;
		float y = 1.2450f;
		float z = 1.24501f;
		
		assertFalse(FloatUtil.lt(y,x));
		assertTrue(FloatUtil.lt(x,y));
		assertFalse(FloatUtil.lt(y,z));
	}

	/*
	 * Purpose: Determine if one float is greater than another or equal
	 * Input: float, float
	 * Expected: 
	 * 			if one float is greater than another or equal, return true
	 *			if one float isn't greater than another or equal, return false  
	 */
	@Test
	public void gteTest() {
		float x = 1.234f;
		float y = 1.2450f;
		float z = 1.24501f;
		
		assertTrue(FloatUtil.gte(y,x));
		assertFalse(FloatUtil.gte(x,y));
		assertTrue(FloatUtil.gte(y,z));

	}
}
