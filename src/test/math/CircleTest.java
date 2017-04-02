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
}
