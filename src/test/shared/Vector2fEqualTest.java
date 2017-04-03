package Test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.math.Vector2f;

public class Vector2fEqualTest {

	
	/*
	 * Purpose: test equals(Object 0)
	 * 			if(o instanceof Vector2f) check v.x == x && v.y == y
	 * 			if it is correct, return true, false otherwise 
	 * Input: 1) vector(2.0f, 3.0f), vector.equals(3)
	 * 		  2) vector(2.0f, 3.0f), another(2.0f, 3.0f), vector.equals(another) 
	 * Expected: 1) false
	 * 			 2) true
	 */
	@Test
	public void EqualsTest() {
		Vector2f vector = new Vector2f(2.0f,3.0f);
		boolean expected = false;
		boolean actual = vector.equals(3);
		assertEquals(expected, actual);
		Vector2f another = new Vector2f(2.0f, 3.0f);
		expected = true;
		actual = vector.equals(another);
		assertEquals(expected, actual);
	}

	
	/*
	 * Purpose: test hashCode()
	 *          it returns x.hashCode() + y.hashCode();
	 * Input: vector(2.0f, 3.0f)
	 * Expected: x.hashCode() + y.hashCode();
	 */
	@Test
	public void HashCodeTest() {
		Vector2f vector = new Vector2f(2.0f,3.0f);
		Float x = 2.0f;
		Float y = 3.0f;
		int expected =  x.hashCode() + y.hashCode();
		int actual = vector.hashCode();
		assertEquals(expected, actual);
	}
	
	
}
