package test.math;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.math.Vector2f;

public class Vector2fToStringTest {

	
	
	/*
	 * Purpose: test toString()
	 *        
	 * Input: vector(2.0f, 3.0f), vector.toString()
	 * Expected: {"x": 2.0, "y": 3.0}
	 */
	@Test
	public void toStringTest() {
		Vector2f vector = new Vector2f(2.0f,3.0f);
		String actual = vector.toString();
		String expected = "{ \"x\": 2.0, \"y\": 3.0}";
		assertEquals(expected, actual);
	}
	
	
	
	/*
	 * Purpose: test createClone()
	 *        	it returns clone of Vector2f
	 * Input: vector(2.0f, 3.0f), vector.toString()
	 * Expected: {"x": 2.0, "y": 3.0}
	 */
	@Test
	public void CloneTest() {
		Vector2f vector = new Vector2f(2.0f,3.0f);
		Vector2f clone = vector.createClone();
		assertEquals(vector, clone);
	}
	
	
	
	/*
	 * Purpose: test toArray()
	 *        	it returns float array {x, y}
	 * Input: vector(2.0f, 3.0f), vector.toArray();
	 * Expected: float{2.0f, 3.0f}
	 */
	@Test
	public void toArrayTest() {
		Vector2f vector = new Vector2f(2.0f,3.0f);
		float[] expected = {2.0f, 3.0f};
		float[] actual = vector.toArray();
		assertTrue(expected[0] == actual[0]);
		assertTrue(expected[1] == actual[1]);
	}
	
}