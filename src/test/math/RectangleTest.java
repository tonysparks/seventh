package test.math;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.math.Circle;
import seventh.math.OBB;
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
	 * 		the rectangleA contains rectangleB
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
	
	/*
	 * Purpose: rectangleA is same rectangleB 
	 * Input:
	 * 		rectangleA => (0,0) width 10 height 10
	 * 		rectangleB => (0,0) width 10 height 10
	 * Expected:
	 * 		the rectangleA contains same rectangle rectangleB
	 */
	@Test
	public void testRectangleContainSameRectangle() {
		final boolean contain = true;
		final boolean noContain = false;
		Rectangle rectangleA = new Rectangle(0,0,10,10);
		Rectangle rectangleB = new Rectangle(0,0,10,10);
		assertEquals(contain,rectangleA.contains(rectangleB));
		assertNotEquals(noContain,rectangleA.contains(rectangleB));
	}
	
	/*
	 * Purpose: rectangleA don't contain rectangleB 
	 * Input:
	 * 		rectangleA => (0,0) width 2 height 2
	 * 		rectangleB => (10,10) width 2 height 2
	 * Expected:
	 * 		the rectangleA don't contain rectangleB
	 */
	@Test
	public void testRectangleNoContainRectangle() {
		final boolean contain = true;
		final boolean noContain = false;
		Rectangle rectangleA = new Rectangle(0,0,2,2);
		Rectangle rectangleB = new Rectangle(10,10,2,2);
		assertEquals(noContain,rectangleA.contains(rectangleB));
		assertNotEquals(contain,rectangleA.contains(rectangleB));
	}
	
	/*
	 * Purpose: rectangle contain point
	 * Input:
	 * 		rectangleA => (0,0) width 4 height 4
	 * 		point => (1,1)
	 * Expected:
	 * 		the rectangleA contains the point
	 */
	@Test
	public void testRectangleContainPoint() {
		final boolean contain = true;
		final boolean noContain = false;
		Rectangle rectangle = new Rectangle(0,0,4,4);
		assertEquals(contain,rectangle.contains(1,1));
		assertNotEquals(noContain,rectangle.contains(1,1));
		
		assertEquals(contain,rectangle.contains(new Vector2f(1,1)));
		assertNotEquals(noContain,rectangle.contains(new Vector2f(1,1)));
	}
	
	/*
	 * Purpose: rectangle don't contain point
	 * Input:
	 * 		rectangleA => (0,0) width 4 height 4
	 * 		point => (10,10)
	 * Expected:
	 * 		the rectangleA contains the point
	 */
	@Test
	public void testRectangleNoContainPoint() {
		final boolean contain = true;
		final boolean noContain = false;
		Rectangle rectangle = new Rectangle(0,0,4,4);
		assertEquals(noContain,rectangle.contains(10,10));
		assertNotEquals(contain,rectangle.contains(10,10));
		assertEquals(noContain,rectangle.contains(new Vector2f(10,10)));
		assertNotEquals(contain,rectangle.contains(new Vector2f(10,10)));
	}
	
	/*
	 * Purpose: make rectangle that is intersection of two rectangle
	 * Input:
	 * 		rectangleA => (0,0) width 10 height 10
	 * 		rectangleB => (5,5) width 10 height 10
	 * Expected:
	 * 		the rectangleA contains intersectedRectangle
	 * 		the rectangleB contains intersectedRectangle
	 * 		intersectedRectangle => (5,5) width 10 height 10
	 */
	@Test
	public void testIntersectionRectangle() {
		final boolean contain = true;
		final boolean noContain = false;
		Rectangle rectangleA = new Rectangle(0,0,10,10);
		Rectangle rectangleB = new Rectangle(5,5,10,10);
		Rectangle expectedRectangle = new Rectangle(5,5,5,5);
		Rectangle intersectedRectangle = rectangleA.intersection(rectangleB);
		assertEquals(contain,rectangleA.contains(intersectedRectangle));
		assertEquals(contain,rectangleB.contains(intersectedRectangle));
		assertTrue(expectedRectangle.equals(intersectedRectangle));
	}
	
	/*
	 * Purpose: there is no rectangle that is intersection of two rectangle
	 * Input:
	 * 		rectangleA => (0,0) width 10 height 10
	 * 		rectangleB => (20,20) width 10 height 10
	 * Expected:
	 * 		the rectangleA contains intersectedRectangle
	 * 		the rectangleB contains intersectedRectangle
	 * 		intersectedRectangle => (5,5) width 10 height 10
	 */
	@Test
	public void testNoIntersectionRectangle() {
		final boolean contain = true;
		final boolean noContain = false;
		Rectangle rectangleA = new Rectangle(0,0,10,10);
		Rectangle rectangleB = new Rectangle(20,20,10,10);
		Rectangle intersectedRectangle = rectangleA.intersection(rectangleB);
		assertEquals(noContain,rectangleA.contains(intersectedRectangle));
		assertEquals(noContain,rectangleB.contains(intersectedRectangle));
	}
	
	/*
	 * Purpose: rectangle contain OBB
	 * Input:
	 * 		rectangle => (0,0) width 20 height 20
	 * 		obb => center (5,5) width 10 height 10
	 * Expected:
	 * 		the rectangle contains obb
	 */
	@Test
	public void testContainOBB(){
		final boolean contain = true;
		final boolean noContain = false;
		Rectangle rectangle = new Rectangle(0,0,20,20);
		OBB oob = new OBB(0,5,5,10,10);
		assertEquals(contain,rectangle.contains(oob));
		assertNotEquals(noContain,rectangle.contains(oob));
	}
	
	/*
	 * Purpose: rectangle don't contain OBB
	 * Input:
	 * 		rectangle => (0,0) width 20 height 20
	 * 		obb => center (30,30) width 5 height 5
	 * Expected:
	 * 		the rectangle don't contains obb
	 */
	@Test
	public void testContainNoOBB(){
		final boolean contain = true;
		final boolean noContain = false;
		Rectangle rectangle = new Rectangle(0,0,20,20);
		OBB oob = new OBB(0,30,30,5,5);
		assertEquals(noContain,rectangle.contains(oob));
		assertNotEquals(contain,rectangle.contains(oob));
	}
}
