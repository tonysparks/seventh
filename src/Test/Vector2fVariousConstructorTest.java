package testOracle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.math.Vector2f;

public class Vector2fVariousConstructorTest {
	
	
	/*
	 * Purpose: Test Vector2f Constructor. 
	 *          Vector2f's x and y value will be initialized in 0.
	 * Input: new Vector2f()
	 * Expected: x = 0, y = 0 
	 */
	
	@Test
	public void testConstructor() {
		Vector2f vector = new Vector2f();
	}

	/*
	 * Purpose: Test Vector2f Float Constructor. 
	 *          Vector2f's x and y value will be initialized in 0.
	 * Input: new Vector2f()
	 * Expected: x = 0, y = 0 
	 */
	
	@Test
	public void testFloatConstructor() {
		Vector2f vector = new Vector2f();
		float x = vector.x;
		float y = vector.y;
		assertTrue(x == 0);
		assertTrue(y == 0);
	}
	
	/*
	 * Purpose: Test Vector2f Float Array Constructor. 
	 *          Vector2f's x and y value will be initialized in v[0], v[1].
	 * Input: float[] v = {1,2};
	 * Expected: x = 1, y = 2 
	 */
	
	@Test
	public void testFloatArrConstructor() {
		float []v = {1, 2};
		Vector2f vector = new Vector2f(v);
		float x = vector.x;
		float y = vector.y;
		assertTrue(x == 1);
		assertTrue(y == 2);
	}
	
	/*
	 * Purpose: Test Vector2f Vector2f Constructor. 
	 *          Vector2f's x and y value will be initialized in vectorOne's x and y.
	 * Input: VectorOne x = 2; y = 3;
	 * Expected: VectorTwo x = 2; y = 3; 
	 */
	
	@Test
	public void testVector2fConstructor() {
		float x = 2;
		float y = 3;
		Vector2f vectorOne = new Vector2f(2, 3);
		Vector2f vectorTwo = new Vector2f(vectorOne);
		assertTrue(vectorTwo.x == 2);
		assertTrue(vectorTwo.y == 3);
		
	}
	

	

}
