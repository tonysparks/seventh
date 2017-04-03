package test.math;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.math.Circle;
import seventh.math.Rectangle;
import seventh.math.Vector2f;

public class RectangleTest {

	/*
	 * Purpose: rectangleA intersect rectangleB 
	 * Input:
	 * 		rectangleA => (0,0) width 4 height 4
	 * 		rectangleB => (3,3) width 6 height 5
	 * Expected:
	 * 		the rectangleA intersect rectangleB
	 */
	@Test
	public void testRectangleIntersectRectangle() {
		final boolean intersect = true;
		final boolean noIntersect = false;
		Rectangle rectangleA = new Rectangle(0,0,4,4);
		Rectangle rectangleB = new Rectangle(3,3,6,5);
		assertEquals(intersect,rectangleA.intersects(rectangleB));
		assertNotEquals(noIntersect,rectangleA.intersects(rectangleB));
	}
	
	/*
	 * Purpose: rectangleA don't intersect rectangleB 
	 * Input:
	 * 		rectangleA => (0,0) width 1 height 1
	 * 		rectangleB => (10,10) width 5 height 5
	 * Expected:
	 * 		the rectangleA don't intersect rectangleB
	 */
	@Test
	public void testRectangleNoIntersectRectangle() {
		final boolean intersect = true;
		final boolean noIntersect = false;
		Rectangle rectangleA = new Rectangle(0,0,1,1);
		Rectangle rectangleB = new Rectangle(10,10,5,5);
		assertEquals(noIntersect,rectangleA.intersects(rectangleB));
		assertNotEquals(intersect,rectangleA.intersects(rectangleB));
	}
	
	/*
	 * Purpose: rectangleA contains rectangleB 
	 * Input:
	 * 		rectangleA => (0,0) width 10 height 10
	 * 		rectangleB => (0,0) width 2 height 2
	 * Expected:
	 * 		the rectangleA don't intersect rectangleB
	 */
	@Test
	public void testRectangleContainRectangle() {
		final boolean contain = true;
		final boolean noContain = false;
		Rectangle rectangleA = new Rectangle(0,0,10,10);
		Rectangle rectangleB = new Rectangle(0,0,2,2);
		assertEquals(contain,rectangleA.contains(rectangleB));
		assertNotEquals(noContain,rectangleA.contains(rectangleB));
	}
}
