package test.math;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.math.Circle;
import seventh.math.Rectangle;
import seventh.math.Vector2f;

public class CircleTest {

	/*
	 * Purpose: a point is in Circle 
	 * Input: Circle => (0,0) radius 3, point => (0,0) 
	 * Expected: 
	 * 			the point is in the circle
	 */
	@Test
	public void testPointInCircle() {
		Vector2f vector2f = new Vector2f(0,0);
		Vector2f point = new Vector2f(0,0);
		Circle circle = new Circle(vector2f,3);
		assertTrue(Circle.circleContainsPoint(circle, point));
	}

	/*
	 * Purpose: a point is on Circle line
	 * Input: Circle => (0,0) radius 3, point => (4,0) 
	 * Expected: 
	 * 			the point is on the circle line
	 */
	@Test
	public void testPointOnCircleLine() {
		Vector2f vector2f = new Vector2f(0,0);
		Vector2f point = new Vector2f(4,0);
		Circle circle = new Circle(vector2f,3);
		assertTrue(Circle.circleContainsPoint(circle, point));
	}
	
	/*
	 * Purpose: a point is out of Circle
	 * Input: Circle => (0,0) radius 3, point => (100,0) 
	 * Expected: 
	 * 			return fail
	 * 			the point is out of the circle line.
	 * 			but it is error that calculating distance
	 */
	@Test
	public void testPointOutCircle() {
		Vector2f vector2f = new Vector2f(0,0);
		Vector2f point = new Vector2f(100,0);
		Circle circle = new Circle(vector2f,3);
		assertFalse(Circle.circleContainsPoint(circle, point));
	}
	
	/*
	 * Purpose: a circle is in Rectangle
	 * Input: Circle => (3,3) radius 1, rectangle => (0,0) width 4 height 4 
	 * Expected: 
	 * 			the circle is in the rectangle
	 */
	@Test
	public void testCircleInRectangle() {
		Circle circle = new Circle(new Vector2f(3,3),1);
		Rectangle rectangle = new Rectangle(new Vector2f(0,0),4,4);
		assertTrue(Circle.circleContainsRect(circle, rectangle));
	}
	
	/*
	 * Purpose: a circle is out of Rectangle
	 * Input: Circle => (10,10) radius 1, rectangle => (0,0) width 4 height 4 
	 * Expected: 
	 * 			the circle is out of the rectangle
	 */
	@Test
	public void testCircleOutRectangle() {
		Circle circle = new Circle(new Vector2f(10,10),1);
		Rectangle rectangle = new Rectangle(new Vector2f(0,0),4,4);
		assertFalse(Circle.circleContainsRect(circle, rectangle));
	}
	
	/*
	 * Purpose: a circle intersect Rectangle
	 * Input: Circle => (0,0) radius 3, rectangle => (2,2) width 4 height 4 
	 * Expected: 
	 * 			the circle intersect the rectangle
	 */
	@Test
	public void testCircleIntersectRectangle() {
		Circle circle = new Circle(new Vector2f(0,0),3);
		Rectangle rectangle = new Rectangle(new Vector2f(2,2),4,4);
		assertTrue(Circle.circleIntersectsRect(circle, rectangle));
	}
	
	/*
	 * Purpose: a circle intersect Rectangle in Rectangle
	 * Input: Circle => (0,0) radius 10, rectangle => (3,3) width 2 height 2 
	 * Expected: 
	 * 			the circle intersect the rectangle
	 */
	@Test
	public void testCircleIntersectInRectangle() {
		Circle circle = new Circle(new Vector2f(0,0),10);
		Rectangle rectangle = new Rectangle(new Vector2f(3,3),2,2);
		assertTrue(Circle.circleIntersectsRect(circle, rectangle));
	}
	
	/*
	 * Purpose: a circle don't intersect Rectangle on same Y axis
	 * Input: Circle => (0,0) radius 3, rectangle => (10,0) width 5 height 5 
	 * Expected: 
	 * 			the circle don't intersect the rectangle
	 */
	@Test
	public void testCircleNoIntersectRectangleSameYaxis() {
		Circle circle = new Circle(new Vector2f(0,0),3);
		Rectangle rectangle = new Rectangle(new Vector2f(10,0),5,5);
		assertFalse(Circle.circleIntersectsRect(circle, rectangle));
	}
	
	/*
	 * Purpose: a circle don't intersect Rectangle on same X axis
	 * Input: Circle => (0,0) radius 3, rectangle => (0,10) width 5 height 5 
	 * Expected: 
	 * 			the circle don't intersect the rectangle
	 */
	@Test
	public void testCircleNoIntersectRectangleSameXaxis() {
		Circle circle = new Circle(new Vector2f(0,0),3);
		Rectangle rectangle = new Rectangle(new Vector2f(0,10),5,5);
		assertFalse(Circle.circleIntersectsRect(circle, rectangle));
	}
}
