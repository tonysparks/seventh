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

	
	
	/*
	 * Purpose: test Vector2fDet()
	 * 		    check the return value
	 *        
	 * Input: v1(2,4), v2(3,5), vector(v1, v2)
	 * Expected: -2
	 */
	
	@Test
	public void DetTest() {
		Vector2f vectorOne = new Vector2f(2,4);
		Vector2f vectorTwo = new Vector2f(3,5);
		Vector2f vector = new Vector2f();
		float actual = vector.Vector2fDet(vectorOne, vectorTwo);
		float expected = -2;
		assertTrue(actual == expected);		
	}
	
	/*
	 * Purpose: test AddTest()
	 * 		    check the return value
	 *        
	 * Input: a(2,4), b(3,5), dest(a, b, dest)
	 * Expected: dest.x = 5, dest.y = 9
	 */
	
	@Test
	public void AddTest() {
		Vector2f a = new Vector2f(2,4);
		Vector2f b = new Vector2f(3,5);
		Vector2f dest = new Vector2f();
		dest.Vector2fAdd(a, b, dest);
		assertTrue(dest.x == 5);
		assertTrue(dest.y == 9);
	}
	
	/*
	 * Purpose: test AddScalarTest()
	 * 		    check the return value
	 *        
	 * Input: a(2,4), b(3,5), dest(a, b, dest)
	 * Expected: dest.x = 5, dest.y = 9
	 */
	
	@Test
	public void AddScalarTest() {
		Vector2f a = new Vector2f(2,4);
		Vector2f dest = new Vector2f();
		dest.Vector2fAdd(a, 5, dest);
		assertTrue(dest.x == 7);
		assertTrue(dest.y == 9);
	}
	
	/*
	 * Purpose: test Vector2fSubtract(a, b, dest)
	 * 		    check the return value
	 *        
	 * Input: a(2,4), b(1,1), dest.subtract(a, b, dest)
	 * Expected: dest.x = 1, dest.y = 3
	 */
	
	@Test
	public void VetorSubTest() {
		Vector2f a = new Vector2f(2,4);
		Vector2f b = new Vector2f(1,1);
		Vector2f dest = new Vector2f();
		dest.Vector2fSubtract(a, b, dest);
		assertTrue(dest.x == 1);
		assertTrue(dest.y == 3);
	}
	
	/*
	 * Purpose: test Vector2fSubtract(a, scalar, dest)
	 * 		    check the return value
	 *        
	 * Input: a(8,6), Scalar = 2, subtract(a, 2, dest)
	 * Expected: dest.x = 6, dest.y = 4
	 */
	
	@Test
	public void ScalarSubTest() {
		Vector2f a = new Vector2f(8,6);
		Vector2f dest = new Vector2f();
		dest.Vector2fSubtract(a, 2, dest);
		assertTrue(dest.x == 6);
		assertTrue(dest.y == 4);
	}
	
}
