package test.shared;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.math.Vector2f;
import static java.lang.Math.atan2;
public class Vector2fNormalizeTest {

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
	
	
	

}
