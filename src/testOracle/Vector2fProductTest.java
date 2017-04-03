package testOracle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.math.Vector2f;

public class Vector2fProductTest {

	
	/*
	 * Purpose: test Vector2fCrossProduct()
	 * 		    check the return value
	 *        
	 * Input: v1(2,4), v2(3,5), vector(v1, v2)
	 * Expected: -2
	 */
	
	@Test
	public void CrossProductTest() {
		Vector2f vectorOne = new Vector2f(2,4);
		Vector2f vectorTwo = new Vector2f(3,5);
		Vector2f vector = new Vector2f();
		float actual = vector.Vector2fCrossProduct(vectorOne, vectorTwo);
		float expected = -2;
		assertTrue(actual == expected);		
	}
	
	/*
	 * Purpose: test Vector2fDotProduct()
	 * 		    check the return value
	 *        
	 * Input: v1(2,4), v2(3,5), vector(v1, v2)
	 * Expected: 26
	 */
	
	@Test
	public void DotProductTest() {
		Vector2f vectorOne = new Vector2f(2,4);
		Vector2f vectorTwo = new Vector2f(3,5);
		Vector2f vector = new Vector2f();
		float actual = vector.Vector2fDotProduct(vectorOne, vectorTwo);
		float expected = 26;
		assertTrue(actual == expected);		
	}

}
