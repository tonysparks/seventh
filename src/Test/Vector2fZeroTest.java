package testOracle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.math.Vector2f;

public class Vector2fZeroTest {
	
	/*
	 * Purpose: To test zeroOut() which make x =0 and y = 0
	 * Input: Vector2f(2,3), vector.zeroOut();
	 * Expected: vector.x = 0, vector.y = 0;
	 */
	@Test
	public void Vector2fzeroOutTest() {
		Vector2f vector = new Vector2f(2,3);
		vector.zeroOut();
		assertTrue(vector.x == 0);
		assertTrue(vector.y == 0);
	}
	
	/*
	 * Purpose: To test isZero(). it returns True when x ==0 && y == 0, False otherwise
	 * Input: Vector2f(2,3), vector.isZero(), vector.set(0,0), vector.isZero();
	 * Expected: false , true
	 */
	@Test
	public void Vector2fisZeroTest() {
		Vector2f vector = new Vector2f(2,3);
		assertTrue(vector.isZero() == false);
		vector.set(0,0);
		assertTrue(vector.isZero() == true);
	}

}
