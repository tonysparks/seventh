package test.math;

import static org.junit.Assert.*;

import org.easymock.EasyMock;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import seventh.math.Rectangle;
import seventh.math.Vector2f;

public class RectangleTest {

	/*
	 * Purpose: test Rectangle(Vector2f b, int width, int height)
	 * 		   
	 * Input: Vector2f b(5,5), width = 5, height =5
	 * Expected: rec.x = 5, rec.y = 5, rec.width = 5, rec.height =5
	 */

	@Test
	public void VectorConstructortest() {
		Vector2f b = new Vector2f(5,5);
		Rectangle rec = new Rectangle(b, 5, 5);
		assertEquals(rec.x, 5);
		assertEquals(rec.y, 5);
		assertEquals(rec.width, 5);
		assertEquals(rec.height,5);
	}
	
	
	/*
	 * Purpose: test Rectangle(int x, int y, int width, int height)
	 * 		   
	 * Input: int x =5, int y =5, width = 5, height =5
	 * Expected: rec.x = 5, rec.y = 5, rec.width = 5, rec.height =5
	 */

	@Test
	public void intConstructortest() {
		Rectangle rec = new Rectangle(5,5, 5, 5);
		assertEquals(rec.x, 5);
		assertEquals(rec.y, 5);
		assertEquals(rec.width, 5);
		assertEquals(rec.height,5);
	}
	
	
	/*
	 * Purpose: test Rectangle(int width, int height)
	 * 		   
	 * Input: int x = null, int y = null, width = 5, height =5
	 * Expected: rec.x = 0, rec.y = 0, rec.width = 5, rec.height =5
	 */

	@Test
	public void initConstructortest() {
		Rectangle rec = new Rectangle(5, 5);
		assertEquals(rec.x, 0);
		assertEquals(rec.y, 0);
		assertEquals(rec.width, 5);
		assertEquals(rec.height,5);
	}
	
	/*
	 * Purpose: test Rectangle(Rectangle rect)
	 * 		   
	 * Input: rect(1, 2, 3, 4)
	 * Expected: rec.x = 1, rec.y = 2, rec.width = 3, rec.height =4
	 */

	@Test
	public void RectangleConstructortest() {
		Rectangle rect = new Rectangle(1, 2, 3, 4);
		Rectangle rec = new Rectangle(rect);
		assertEquals(rec.x, 1);
		assertEquals(rec.y, 2);
		assertEquals(rec.width, 3);
		assertEquals(rec.height,4);
	}

}
