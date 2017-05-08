package test.math;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
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
	
	/*
	 * Purpose: construct same rectangle each constructor
	 * Input:
	 * 		A => (0,0) width 0 height 0 
	 * 		B => vector2f(0,0) width 0 height 0
	 * 		C => width 0 height 0  
	 * 		D => nothing
	 * Expected:
	 * 		all rectangle are same (0,0) width 0 height 0
	 */
	@Test
	public void testConstructRectangle(){
		final boolean same = true;
		Rectangle rectangleA = new Rectangle(0,0,0,0);
		Rectangle rectangleB = new Rectangle(new Vector2f(0,0),0,0);
		Rectangle rectangleC = new Rectangle(0,0);
		Rectangle rectangleD = new Rectangle();
		Rectangle rectangleE = new Rectangle(rectangleA);
		assertEquals(same,rectangleA.equals(rectangleB));
		assertEquals(same,rectangleA.equals(rectangleC));
		assertEquals(same,rectangleA.equals(rectangleD));
		assertEquals(same,rectangleA.equals(rectangleE));
		assertEquals(same,rectangleB.equals(rectangleA));
		assertEquals(same,rectangleB.equals(rectangleC));
		assertEquals(same,rectangleB.equals(rectangleD));
		assertEquals(same,rectangleB.equals(rectangleE));
		assertEquals(same,rectangleC.equals(rectangleA));
		assertEquals(same,rectangleC.equals(rectangleB));
		assertEquals(same,rectangleC.equals(rectangleD));
		assertEquals(same,rectangleC.equals(rectangleE));
		assertEquals(same,rectangleD.equals(rectangleA));
		assertEquals(same,rectangleD.equals(rectangleB));
		assertEquals(same,rectangleD.equals(rectangleC));
		assertEquals(same,rectangleD.equals(rectangleE));
		assertEquals(same,rectangleE.equals(rectangleA));
		assertEquals(same,rectangleE.equals(rectangleB));
		assertEquals(same,rectangleE.equals(rectangleC));
		assertEquals(same,rectangleE.equals(rectangleD));
		assertEquals(0,rectangleA.getX());
		assertEquals(0,rectangleA.getY());
		assertEquals(0,rectangleA.getWidth());
		assertEquals(0,rectangleA.getHeight());
	}
	
	/*
	 * Purpose: rectangle add x,y
	 * Input:
	 * 		all rectangles => (1,1) width 2 height 2
	 * 		add (2,2)
	 * Expected:
	 * 		x,y of rectangles are (3,3)
	 */
	@Test
	public void testRectangleAdd(){
		Rectangle rectangleA = new Rectangle(1,1,2,2);
		Rectangle rectangleB = new Rectangle(1,1,2,2);
		Rectangle rectangleC = new Rectangle(1,1,2,2);
		
		rectangleA.add(2, 2);
		assertEquals(3,rectangleA.getX());
		assertEquals(3,rectangleA.getY());
		assertEquals(2,rectangleA.getWidth());
		assertEquals(2,rectangleA.getHeight());
		
		rectangleB.add(new Vector2f(2,2));
		assertEquals(3,rectangleB.getX());
		assertEquals(3,rectangleB.getY());
		assertEquals(2,rectangleB.getWidth());
		assertEquals(2,rectangleB.getHeight());
		
		rectangleC.add(new Rectangle(2,2,0,0));
		assertEquals(3,rectangleC.getX());
		assertEquals(3,rectangleC.getY());
		assertEquals(2,rectangleC.getWidth());
		assertEquals(2,rectangleC.getHeight());
	}
	
	/*
	 * Purpose: make (0,0,0,0)rectangle to (1,2,3,4)rectangle
	 * Input:
	 * 		rectangle => (x,y) (0,0) width 0 height 0
	 * 		setBounds : (x,y) (1,2) width 3 height 4
	 * Expected:
	 * 		rectangle => (x,y) (1,2) width 3 height 4
	 */
	@Test
	public void testSetBoundintValue(){
		final int expectedX = 1;
		final int expectedY = 2;
		final int expectedWidth = 3;
		final int expectedheight = 4;
		Rectangle rectangle = new Rectangle(0,0,0,0);
		rectangle.set(1,2,3,4);
		assertEquals(expectedX,rectangle.getX());
		assertEquals(expectedY,rectangle.getY());
		assertEquals(expectedWidth,rectangle.getWidth());
		assertEquals(expectedheight,rectangle.getHeight());
	}
	
	/*
	 * Purpose: make (0,0,0,0)rectangle to (1,2,3,4)rectangle
	 * Input:
	 * 		rectangle => (x,y) (0,0) width 0 height 0
	 * 		set : Vector2f(1,2) width 3 height 4
	 * Expected:
	 * 		rectangle => (x,y) (1,2) width 3 height 4
	 */
	@Test
	public void testSetVector2f(){
		final int expectedX = 1;
		final int expectedY = 2;
		final int expectedWidth = 3;
		final int expectedheight = 4;
		Rectangle rectangle = new Rectangle(0,0,0,0);
		rectangle.set(new Vector2f(1,2),3,4);
		assertEquals(expectedX,rectangle.getX());
		assertEquals(expectedY,rectangle.getY());
		assertEquals(expectedWidth,rectangle.getWidth());
		assertEquals(expectedheight,rectangle.getHeight());
	}

	/*
	 * Purpose: make (0,0,0,0)rectangle to (1,2,3,4)rectangle
	 * Input:
	 * 		rectangle => (x,y) (0,0) width 0 height 0
	 * 		setBounds : Vector2f(1,2) width 3 height 4
	 * Expected:
	 * 		rectangle => (x,y) (1,2) width 3 height 4
	 */
	@Test
	public void testSetBoundVector2f(){
		final int expectedX = 1;
		final int expectedY = 2;
		final int expectedWidth = 3;
		final int expectedheight = 4;
		Rectangle rectangle = new Rectangle(0,0,0,0);
		rectangle.setBounds(new Vector2f(1,2),3,4);
		assertEquals(expectedX,rectangle.getX());
		assertEquals(expectedY,rectangle.getY());
		assertEquals(expectedWidth,rectangle.getWidth());
		assertEquals(expectedheight,rectangle.getHeight());
	}
	
	/*
	 * Purpose: make (0,0,0,0)rectangle to (1,2,3,4)rectangle
	 * Input:
	 * 		rectangle => (x,y) (0,0) width 0 height 0
	 * 		setBounds : Rectangle -> (x,y) (1,2) width 3 height 4
	 * Expected:
	 * 		rectangle => (x,y) (1,2) width 3 height 4
	 */
	@Test
	public void testSetBoundRectangle(){
		final int expectedX = 1;
		final int expectedY = 2;
		final int expectedWidth = 3;
		final int expectedheight = 4;
		Rectangle rectangle = new Rectangle(0,0,0,0);
		rectangle.setBounds(new Rectangle(1,2,3,4));
		assertEquals(expectedX,rectangle.getX());
		assertEquals(expectedY,rectangle.getY());
		assertEquals(expectedWidth,rectangle.getWidth());
		assertEquals(expectedheight,rectangle.getHeight());
	}
	
	/*
	 * Purpose: make (0,0,0,0)rectangle to (1,2,3,4)rectangle
	 * Input:
	 * 		rectangle => (x,y) (0,0) width 0 height 0
	 * 		set : Rectangle -> (x,y) (1,2) width 3 height 4
	 * Expected:
	 * 		rectangle => (x,y) (1,2) width 3 height 4
	 */
	@Test
	public void testSetRectangle(){
		final int expectedX = 1;
		final int expectedY = 2;
		final int expectedWidth = 3;
		final int expectedheight = 4;
		Rectangle rectangle = new Rectangle(0,0,0,0);
		rectangle.set(new Rectangle(1,2,3,4));
		assertEquals(expectedX,rectangle.getX());
		assertEquals(expectedY,rectangle.getY());
		assertEquals(expectedWidth,rectangle.getWidth());
		assertEquals(expectedheight,rectangle.getHeight());
	}
	
	/*
	 * Purpose: make (1,2,3,4)rectangle to (0,0,0,0)rectangle
	 * Input:
	 * 		rectangle => (x,y) (1,2) width 3 height 4
	 * Expected:
	 * 		rectangle => (x,y) (0,0) width 0 height 0
	 */
	@Test
	public void testZeroOut(){
		final int expected = 0;
		Rectangle rectangle = new Rectangle(1,2,3,4);
		rectangle.zeroOut();
		assertEquals(expected,rectangle.getX());
		assertEquals(expected,rectangle.getY());
		assertEquals(expected,rectangle.getWidth());
		assertEquals(expected,rectangle.getHeight());
	}
}
