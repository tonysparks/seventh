package test.math;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.math.Vector2f;

public class Vector2fGetSetTest {

	
	/*
	 * Purpose: To test Vector2f.get(int i). it returns x when the i is 0, y otherwise.
	 * Input: Vector2f(2,3), get(0), get(1);
	 * Expected: get(0) = 2, get(1) = 3
	 */
	@Test
	public void Vector2fGetTest() {
		Vector2f vector = new Vector2f(2,3);
		int i = 0;
		int j = 1;
		float actualX = vector.get(i);
		float actualY = vector.get(j);
		assertTrue(actualX == 2);
		assertTrue(actualY == 3);
	}
	
	/*
	 * Purpose: To test set(float x, float y). this.x = x, this.y = y
	 * Input: Vector2f(), set(2,3)
	 * Expected: Vector2f.x = 2, Vector2f.y = 3
	 */
	@Test
	public void Vector2fFloatSetTest() {
		Vector2f vector = new Vector2f();
		vector.set(2,3);
		assertTrue(vector.x == 2);
		assertTrue(vector.y == 3);
	}

	
	/*
	 * Purpose: To test set(Vector2f v). this.x = v.x, this.y = c.y
	 * Input: vectorOne(2,3), vectorTwo.set(vectorOne)
	 * Expected: vectorTwo.x = 2, vectorTwo.y = 3
	 */
	@Test
	public void Vector2fVector2fSetTest() {
		Vector2f vectorOne = new Vector2f(2,3);
		Vector2f vectorTwo = new Vector2f();
		vectorTwo.set(vectorOne);
		assertTrue(vectorTwo.x == 2);
		assertTrue(vectorTwo.y == 3);
	}
	
	/*
	 * Purpose: To test set(float[] v)
	 * Input: vector(), set(float[] v), v = {1, 2, 3}
	 * Expected: vector.x = 1, vector.y = 2
	 */
	@Test
	public void Vector2fFloatArrSetTest() {
		float[] v = {1, 2, 3};
		Vector2f vector = new Vector2f();
		vector.set(v);
		assertTrue(vector.x == 1);
		assertTrue(vector.y == 2);
	}
	
	

}
