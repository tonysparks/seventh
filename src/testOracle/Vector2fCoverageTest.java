package testOracle;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.math.Vector2f;

public class Vector2fCoverageTest {

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
	
	
	/*
	 * Purpose: To test round(). x = Math.round(x), y = Math.round(y)
	 * Input: Vector2f(2.6f, 3.2f), vector.round()
	 * Expected: vector.x = 3, vector.y = 3
	 */
	@Test
	public void RoundTest() {
		Vector2f vector = new Vector2f(2.6f, 3.2f);
		vector.round();
		assertTrue(vector.x == 3);
		assertTrue(vector.y == 3);
	}
	
	
	/*
	 * Purpose: To test subtract(Vector2f v), it returns Vector2f(this.x-v.x, this.y - v.y)
	 * Input: vector(5,5), v = (3,3)
	 * Expected: dest.x = 2, dest.y = 2
	 */
	@Test
	public void SubtractTest() {
		Vector2f vector = new Vector2f(5, 5);
		Vector2f v = new Vector2f(3, 3);
		Vector2f dest = new Vector2f();
		dest = vector.subtract(v);
		assertTrue(dest.x == 2);
		assertTrue(dest.y == 2);	
	}
	
	/*
	 * Purpose: To test addition(Vector2f v), it returns Vector2f(this.x+v.x, this.y + v.y)
	 * Input: vector(5,5), v = (3,3)
	 * Expected: dest.x = 8, dest.y = 8
	 */
	@Test
	public void AdditionTest() {
		Vector2f vector = new Vector2f(5, 5);
		Vector2f v = new Vector2f(3, 3);
		Vector2f dest = new Vector2f();
		dest = vector.addition(v);
		assertTrue(dest.x == 8);
		assertTrue(dest.y == 8);	
	}
	
	
	/*
	 * Purpose: To test mult(Vector2f v), it returns Vector2f(this.x*v.x, this.y*v.y)
	 * Input: vector(5,5), v = (3,3)
	 * Expected: dest.x = 15, dest.y = 15
	 */
	@Test
	public void MultiplyVectorTest() {
		Vector2f vector = new Vector2f(5, 5);
		Vector2f v = new Vector2f(3, 3);
		Vector2f dest = new Vector2f();
		dest = vector.mult(v);
		assertTrue(dest.x == 15);
		assertTrue(dest.y == 15);	
	}
	
	/*
	 * Purpose: To test mult(float scalar), it returns Vector2f(this.x*scalar, this.y*scalar)
	 * Input: vector(5,5), scalar = 3
	 * Expected: dest.x = 15, dest.y = 15
	 */
	@Test
	public void MultiplyFloatTest() {
		Vector2f vector = new Vector2f(5, 5);
		Vector2f dest = new Vector2f();
		dest = vector.mult(3);
		assertTrue(dest.x == 15);
		assertTrue(dest.y == 15);	
	}
	
	/*
	 * Purpose: To test div(Vector2f v), it returns Vector2f(this.x/v.x, this.y/v.y)
	 * Input: vector(10, 10), v(2, 5)
	 * Expected: dest.x = 5, dest.y = 2
	 */
	@Test
	public void DivideVectorTest() {
		Vector2f vector = new Vector2f(10, 10);
		Vector2f v = new Vector2f(2, 5);
		Vector2f dest = new Vector2f();
		dest = vector.div(v);
		assertTrue(dest.x == 5);
		assertTrue(dest.y == 2);	
	}
	
	/*
	 * Purpose: To test div(float v), it returns Vector2f(this.x/scalar, this.y/scalar)
	 * Input: vector(10, 10), scalar = 5
	 * Expected: dest.x = 2, dest.y = 2
	 */
	@Test
	public void DivideFloatTest() {
		Vector2f vector = new Vector2f(10, 10);
		Vector2f dest = new Vector2f();
		dest = vector.div(5);
		assertTrue(dest.x == 2);
		assertTrue(dest.y == 2);	
	}

	/*
	 * Purpose: test normalize(), returns normalized x and y
	 * Input: vector.normalize(), x=3, y =4
	 * Expected: x = 3.0f * FlLen, y = 4.0f * FlLen;
	 */
	
	@Test
	public void NormalizeTest() {
		Vector2f vector = new Vector2f();
		vector.normalize();
		vector.set(3,4);
		vector.normalize();
		float FlLen = 1.0f/5.0f;
		float expectedX = 3.0f * FlLen;
		float expectedY = 4.0f * FlLen;
		assertTrue(vector.x == expectedX );
		assertTrue(vector.y == expectedY );
	}
	
	/*
	 * Purpose: test lengthSquared(), returns x*x + y*y
	 * Input: Vector2f(3,4)
	 * Expected: 25.0f
	 */
	
	@Test
	public void lenthSquaredTest() {
		Vector2f vector = new Vector2f(3, 4);
		float actual = vector.lengthSquared();
		float expected = 25.0f;
		assertTrue(actual == expected);
	}
	
	/*
	 * Purpose: test length(), returns Math.sqrt(x*x + y*y)
	 * Input: Vector2f(3,4)
	 * Expected: 5.0f
	 */
	
	@Test
	public void lenthTest() {
		Vector2f vector = new Vector2f(3, 4);
		float actual = vector.length();
		float expected = 5.0f;
		assertTrue(actual == expected);
	}
	
	/*
	 * Purpose: test rotate(double radians)
	 *          x1 = x * cos(radians) - y * sin(radians)
	 *          y1 = x * sin(radians) + y * cos(radians)
	 *          return Vector2f((float)x1, (float)y1)
	 * Input: vector(3,4), radians = 30
	 * Expected: x = (float)x1, y = (float)y1
	 */
	
	@Test
	public void RotateTest() {
		Vector2f vector = new Vector2f(3, 4);
		Vector2f dest = new Vector2f();
		double radians = 30;
		double x1 = 3 * Math.cos(radians) - 4 * Math.sin(radians);
		double y1 = 3 * Math.sin(radians) + 4 * Math.cos(radians);
		float expectedX = (float)x1;
		float expectedY = (float)y1;
		dest = vector.rotate(30.0d);
		assertTrue(dest.x == expectedX);
		assertTrue(dest.y == expectedY);
	}
	
	
	
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
	
	/*
	 * Purpose: test Vector2fMult(a,b, dest)
	 * 		    check the return value
	 *        
	 * Input: a(8,6), b(3,5), Vector2fMult(a, 2, dest)
	 * Expected: dest.x = 24, dest.y = 30
	 */
	
	@Test
	public void VectorMulTest() {
		Vector2f a = new Vector2f(8,6);
		Vector2f b = new Vector2f(3,5);
		Vector2f dest = new Vector2f();
		dest.Vector2fMult(a, b, dest);
		assertTrue(dest.x == 24);
		assertTrue(dest.y == 30);
	}
	
	/*
	 * Purpose: test Vector2fMult(a,scalar, dest)
	 * 		    check the return value
	 *        
	 * Input: a(8,6), scalar = 3, Vector2fMult(a, 3, dest)
	 * Expected: dest.x = 24, dest.y = 30
	 */
	
	@Test
	public void ScalarMulTest() {
		Vector2f a = new Vector2f(8,6);
		float scalar = 3;
		Vector2f dest = new Vector2f();
		dest.Vector2fMult(a, scalar, dest);
		assertTrue(dest.x == 24);
		assertTrue(dest.y == 18);
	}
	
	/*
	 * Purpose: test Vector2fDiv(a,b, dest)
	 * 		    check the return value
	 *        
	 * Input: a(8,6), b(2,3), Vector2fMult(a, b, dest)
	 * Expected: dest.x = 4, dest.y = 2
	 */
	
	@Test
	public void VectorDivTest() {
		Vector2f a = new Vector2f(8,6);
		Vector2f b = new Vector2f(2,3);
		Vector2f dest = new Vector2f();
		dest.Vector2fDiv(a, b, dest);
		assertTrue(dest.x == 4);
		assertTrue(dest.y == 2);
	}
	
	
	/*
	 * Purpose: test Scalar2fDiv(a,scalar, dest)
	 * 		    check the return value
	 *        
	 * Input: a(8,6), scalar = 2, Vector2fMult(a, scalar, dest)
	 * Expected: dest.x = 4, dest.y = 3
	 */
	
	@Test
	public void ScalarDivTest() {
		Vector2f a = new Vector2f(8,6);
		float scalar = 2;
		Vector2f dest = new Vector2f();
		dest.Vector2fDiv(a, 2, dest);
		assertTrue(dest.x == 4);
		assertTrue(dest.y == 3);
	}
	
	/*
	 * Purpose: test Vector2fRotate(a, radians, dest)
	 * 		    check the return value
	 *        
	 * Input: a(8,6), radians = 30, dest()
	 * Expected: dest.x = (float)(a.x * Math.cos(radians) - a.y * Math.sin(radians))
	 * 			 dest.y = (float)(a.x * Math.sin(radians) + a.y * Math.cos(radians))
	 */
	
	@Test
	public void VectorRotateTest () {
		Vector2f a = new Vector2f(8,6);
		double radians = 30;
		Vector2f dest = new Vector2f();
		float expectedX = (float)(a.x * Math.cos(radians) - a.y * Math.sin(radians));
		float expectedY = (float)(a.x * Math.sin(radians) + a.y * Math.cos(radians));
		dest.Vector2fRotate(a, radians, dest);
		float actualX = dest.x;
		float actualY = dest.y;
		assertTrue(actualX == expectedX);
		assertTrue(actualY == expectedY);
	}
	
	/*
	 * Purpose: test Vector2fAngle(a,b)
	 * 		    check the return value
	 *        
	 * Input: a(8,6), radians = 30, dest()
	 * Expected: dest.x = (float)(a.x * Math.cos(radians) - a.y * Math.sin(radians))
	 * 			 dest.y = (float)(a.x * Math.sin(radians) + a.y * Math.cos(radians))
	 */
	
	@Test
	public void AngleTest () {
			Vector2f a = new Vector2f(5,4);
			Vector2f b = new Vector2f(3,2);
			Vector2f dest = new Vector2f();
			double expectedAngle = Math.atan2(2.0f, 3.0f) - Math.atan2(4.0f,5.0f);
			double actualAngle = dest.Vector2fAngle(a, b);
			assertTrue(expectedAngle == actualAngle);	
	}
	

	
	

}
