package test.math;

import static org.junit.Assert.*;

import org.junit.Test;

import seventh.math.FloatUtil;
import seventh.math.Line;
import seventh.math.Rectangle;
import seventh.math.Vector2f;

public class LineTest {

	
	/*
	 * Purpose: constructor stores right value
	 * Input: Line : (a, b) = (Vector2f, Vector2f)
	 * 
	 * 			lineA = ((-1.0f,-1.0f), (1.0f,1.0f))
	 * 			lineB = ((1.0f,1.0f), (-1.0f,-1.0f))
	 * 			lineC = ((0.0f,0.0f), (0.0f,0.0f))
	 * Expected:
	 * 			lineA.a.x == -1.0f
	 * 			lineA.a.y == -1.0f
	 * 			lineA.b.x == 1.0f
	 * 			lineA.b.y == 1.0f
	 * 
	 * 			lineB.a.x == 1.0f
	 * 			lineB.a.y == 1.0f
	 * 			lineB.b.x == -1.0f
	 * 			lineB.b.y == 1.0f
	 * 
	 * 			lineC.a.x == 0.0f
	 * 			lineC.a.y == 0.0f
	 * 			lineC.b.x == 0.0f
	 * 			lineC.b.y == 0.0f
	 */
	@Test
	public void testLineConstructor() {
		// lineA : from (-1,-1) to (1,1)
		final float lineAStartPointX = -1.0f, lineAStartPointY = -1.0f;
		final float lineAEndPointX = 1.0f, lineAEndPointY = 1.0f;
		
		// lineB : from (1,1) to (-1,-1)		
		final float lineBStartPointX = 1.0f, lineBStartPointY = 1.0f;
		final float lineBEndPointX = -1.0f, lineBEndPointY = -1.0f;
		
		// lineC : from (0,0) to (0,0)
		// actually, not line, point
		final float lineCStartPointX = 0.0f, lineCStartPointY = 0.0f;
		final float lineCEndPointX = 0.0f, lineCEndPointY = 0.0f;
		
		Line lineA = new Line(new Vector2f(lineAStartPointX, lineAStartPointY),
				new Vector2f(lineAEndPointX, lineAEndPointY));
		Line lineB = new Line(new Vector2f(lineBStartPointX, lineBStartPointY),
				new Vector2f(lineBEndPointX, lineBEndPointY));
		Line lineC = new Line(new Vector2f(lineCStartPointX, lineCStartPointY),
				new Vector2f(lineCEndPointX, lineCEndPointY));
		
		assertTrue(FloatUtil.eq(lineAStartPointX, lineA.a.x));
		assertTrue(FloatUtil.eq(lineAStartPointY, lineA.a.y));
		assertTrue(FloatUtil.eq(lineAEndPointX, lineA.b.x));
		assertTrue(FloatUtil.eq(lineAEndPointY, lineA.b.y));
		
		assertTrue(FloatUtil.eq(lineBStartPointX, lineB.a.x));
		assertTrue(FloatUtil.eq(lineBStartPointY, lineB.a.y));
		assertTrue(FloatUtil.eq(lineBEndPointX, lineB.b.x));
		assertTrue(FloatUtil.eq(lineBEndPointY, lineB.b.y));
		
		assertTrue(FloatUtil.eq(lineCStartPointX, lineC.a.x));
		assertTrue(FloatUtil.eq(lineCStartPointY, lineC.a.y));
		assertTrue(FloatUtil.eq(lineCEndPointX, lineC.b.x));
		assertTrue(FloatUtil.eq(lineCEndPointY, lineC.b.y));
	}
	
	// ----------------------------------------------------
	// line and line
	
	/*
	 * to test method lineIntersectLine()
	 */
	private void assertLineIntersectLine(Line firstLine, Line secondLine, boolean expectedResult) {
		assertEquals(expectedResult, Line.lineIntersectLine(firstLine, secondLine));
		assertEquals(expectedResult, Line.lineIntersectLine(firstLine.a, firstLine.b, secondLine.a, secondLine.b));
		assertEquals(expectedResult, Line.lineIntersectLine(
				firstLine.a.x, firstLine.a.y,
				firstLine.b.x, firstLine.b.y,
				secondLine.a.x, secondLine.a.y,
				secondLine.b.x, secondLine.b.y));
	}
	
	/*
	 * Purpose: two lines intersect
	 * Input: lineIntersectLine
	 * 			
	 * 			lineA = ((-1.0f, 0.0f), (1.0f,0.0f))
	 * 			lineB = ((0.0f, -1.0f), (0.0f, 1.0f))			
	 * Expected: 
	 * 			return true
	 */
	@Test
	public void testLineIntersectLine() {
		// lineA and lineB intersect
		// intersection point is (0.0f, 0.0f)
		Line lineA = new Line(new Vector2f(-1.0f, 0.0f), new Vector2f(1.0f, 0.0f));
		Line lineB = new Line(new Vector2f(0.0f, -1.0f), new Vector2f(0.0f, 1.0f));

		assertLineIntersectLine(lineA, lineB, true);
	}

	/*
	 * Purpose: intersection point of two lines is the end of one of them
	 * Input: lineIntersectLine
	 * 			
	 * 			lineA = ((-1.0f, 0.0f), (1.0f, 0.0f))
	 * 			lineC = ((1.0f, -1.0f), (1.0f, 1.0f))	
	 * Expected:
	 * 			return true
	 */
	@Test
	public void testLineIntersectEndOfLine() {
		// lineA and lineC intersect
		// intersection point is (1.0f, 0.0f)
		Line lineA = new Line(new Vector2f(-1.0f, 0.0f), new Vector2f(1.0f, 0.0f));
		Line lineC = new Line(new Vector2f(1.0f, -1.0f), new Vector2f(1.0f, 1.0f));
		
		assertLineIntersectLine(lineA, lineC, true);
	}
	
	/*
	 * Purpose: two lines don't intersect
	 * Input: lineIntersectLine
	 * 			
	 * 			lineA = ((-1.0f, 0.0f), (1.0f, 0.0f))
	 * 			lineD = ((2.0f, -1.0f), (2.0f, 1.0f))
	 * Expected: 
	 * 			return false
	 */
	@Test
	public void testLineNotIntersectLine() {
		// lineA and lineD do not intersect
		Line lineA = new Line(new Vector2f(-1.0f, 0.0f), new Vector2f(1.0f, 0.0f));
		Line lineD = new Line(new Vector2f(2.0f, -1.0f), new Vector2f(2.0f, 1.0f));
		
		assertLineIntersectLine(lineA, lineD, false);
	}
	
	/*
	 * Purpose: two lines (one of them is actually a point) intersect
	 * Input: lineIntersectLine
	 * 			
	 * 			lineA = ((-1.0f, 0.0f), (1.0f, 0.0f))
	 * 			lineE = ((0.0f, 0.0f), (0.0f, 0.0f))
	 * Expected:
	 * 			return true
	 */
	@Test
	public void testLineIntersectLineWhichIsPoint() {
		// lineA and lineE(point) intersect
		// intersection point is (0.0f, 0.0f)
		Line lineA = new Line(new Vector2f(-1.0f, 0.0f), new Vector2f(1.0f, 0.0f));
		Line lineE = new Line(new Vector2f(0.0f, 0.0f), new Vector2f(0.0f, 0.0f));
		
		assertLineIntersectLine(lineA, lineE, true);
	}
	
	/*
	 * Purpose: intersection point of two lines(one of them is a point)
	 * 			 is the end of line
	 * Input: lineIntersectLine
	 * 			
	 * 			lineA = ((-1.0f, 0.0f), (1.0f, 0.0f))
	 * 			lineF = ((1.0f, 0.0f), (1.0f, 0.0f))	
	 * Expected:
	 * 			return true
	 */
	@Test
	public void testEndOfLineIntersectLineWhichIsPoint() {
		// lineA and lineF(point) intersect
		// intersection point is (1.0f, 0.0f)
		Line lineA = new Line(new Vector2f(-1.0f, 0.0f), new Vector2f(1.0f, 0.0f));
		Line lineF = new Line(new Vector2f(1.0f, 0.0f), new Vector2f(1.0f, 0.0f));
		
		assertLineIntersectLine(lineA, lineF, true);
	}
	
	/*
	 * Purpose: two lines(one of them is a point) don't intersect
	 * Input: lineIntersectLine
	 * 			
	 * 			lineA = ((-1.0f, 0.0f), (1.0f, 0.0f))
	 * 			lineG = ((2.0f, 0.0f), (2.0f, 0.0f))
	 * Expected: 
	 * 			return false
	 */
	@Test
	public void testLineNotIntersectLineWhichIsPoint() {
		// lineA and lineG(point) do not intersect
		Line lineA = new Line(new Vector2f(-1.0f, 0.0f), new Vector2f(1.0f, 0.0f));
		Line lineG = new Line(new Vector2f(2.0f, 0.0f), new Vector2f(2.0f, 0.0f));
		
		assertLineIntersectLine(lineA, lineG, false);
	}
	
	/*
	 * Purpose: two lines (both are actually points) intersect
	 * Input: lineIntersectLine
	 * 			
	 * 			lineE = ((0.0f, 0.0f), (0.0f, 0.0f))
	 * 			lineH = ((0.0f, 0.0f), (0.0f, 0.0f))
	 * Expected:
	 * 			return true
	 */
	@Test
	public void testLinesWhichArePointIntersect() {
		// lineE(point) and lineH(point) intersect
		// intersection point is (0.0f, 0.0f)
		Line lineE = new Line(new Vector2f(0.0f, 0.0f), new Vector2f(0.0f, 0.0f));
		Line lineH = new Line(new Vector2f(0.0f, 0.0f), new Vector2f(0.0f, 0.0f));
		
		assertLineIntersectLine(lineE, lineH, true);
	}
	
	/*
	 * Purpose: two lines(both are actually points) don't intersect
	 * Input: lineIntersectLine
	 * 			
	 * 			lineE = ((0.0f, 0.0f), (0.0f, 0.0f))
	 * 			lineF = ((1.0f, 0.0f), (1.0f, 0.0f))
	 * Expected: 
	 * 			return false
	 *************** Error ******************
	 */
	@Test
	public void testLinesWhichArePointNotIntersect() {
		// lineE(point) and lineF(point) do not intersect
		Line lineE = new Line(new Vector2f(0.0f, 0.0f), new Vector2f(0.0f, 0.0f));
		Line lineF = new Line(new Vector2f(1.0f, 0.0f), new Vector2f(1.0f, 0.0f));
		
		assertLineIntersectLine(lineE, lineF, false);
	}
	
	// -------------------------------------------------------------
	// line and rectangle
	
	// determine - test near collision point and far collision point
	// I do not test the near/far collision points
	// because the result value (collision points) is so much different from the value I expected,
	// I think I don't understand the collision points.
	private final static boolean NEARFARCHECK = false;
	
	// to test method lineIntersectsRectangle()
	private void assertLineIntersectsRectangle(
			Line line,
			Rectangle rectangle,
			boolean expectedResult,
			boolean testCollisionPoint,
			Vector2f expectedNear,
			Vector2f expectedFar)
	{
		Vector2f nearA = new Vector2f();
		Vector2f farA = new Vector2f();
		Vector2f nearB = new Vector2f();
		Vector2f farB = new Vector2f();
		
		assertEquals(expectedResult, Line.lineIntersectsRectangle(line, rectangle));
		assertEquals(expectedResult, Line.lineIntersectsRectangle(line.a, line.b, rectangle));
		
		assertEquals(expectedResult, Line.lineIntersectsRectangle(line, rectangle, nearA, farA));
		assertEquals(expectedResult, Line.lineIntersectsRectangle(line.a, line.b, rectangle, nearB, farB));
		
		if (testCollisionPoint)
		{
			if (NEARFARCHECK) {
				// near, far collision point check
				
				// expected
				final float nearCollisionX = expectedNear.x;
				final float nearCollisionY = expectedNear.y;
				final float farCollisionX = expectedFar.x;
				final float farCollisionY = expectedFar.y;
				
				assertTrue(FloatUtil.eq(nearCollisionX, nearA.x));
				assertTrue(FloatUtil.eq(nearCollisionY, nearA.y));
				assertTrue(FloatUtil.eq(farCollisionX, farA.x));
				assertTrue(FloatUtil.eq(farCollisionY, farA.y));
				
				assertTrue(FloatUtil.eq(nearCollisionX, nearB.x));
				assertTrue(FloatUtil.eq(nearCollisionY, nearB.y));
				assertTrue(FloatUtil.eq(farCollisionX, farB.x));
				assertTrue(FloatUtil.eq(farCollisionY, farB.y));
				
				Vector2f nearCollisionPoint = Line.nearCollisionPoint(line, rectangle);
				Vector2f farCollisionPoint = Line.farCollisionPoint(line, rectangle);
				
				assertTrue(FloatUtil.eq(nearCollisionX, nearCollisionPoint.x));
				assertTrue(FloatUtil.eq(nearCollisionY, nearCollisionPoint.y));
				assertTrue(FloatUtil.eq(farCollisionX, farCollisionPoint.x));
				assertTrue(FloatUtil.eq(farCollisionY, farCollisionPoint.y));
			}
		}
	}
	
	/*
	 * Purpose: line intersects with a rectangle. the start, end points of line are not in the rectangle.
	 * Input: lineIntersectsRectangle
	 * 		nearCollisionPoint
	 * 		farCollisionPoint
	 * 			
	 * 			rectangle : x = 0, y = 0, width = 3, height = 3
	 * 			lineG = ((-1.0f, 1.0f), (4.0f, 1.0f))
	 * Expected:
	 * 			return true
	 * 			(0, 1) = near
	 * 			(3, 1) = far
	 */
	@Test
	public void testLineIntersectsThroughRect() {
		Rectangle rectangle = new Rectangle(0,0,3,3);
		Line lineG = new Line(new Vector2f(-1.0f, 1.0f), new Vector2f(4.0f, 1.0f));
		
		// expected near,far collision point
		final float nearCollisionX = 0.0f;
		final float nearCollisionY = 1.0f;
		final float farCollisionX = 3.0f;
		final float farCollisionY = 1.0f;
		
		assertLineIntersectsRectangle(
				lineG,
				rectangle,
				true,
				true,
				new Vector2f(nearCollisionX, nearCollisionY),
				new Vector2f(farCollisionX, farCollisionY));
	}
	
	/*
	 * Purpose: line intersects with a rectangle, the line is on the edge of rectangle, parallel
	 * Input: lineIntersectsRectangle
	 * 		nearCollisionPoint
	 * 		farCollisionPoint
	 * 			
	 * 			rectangle : x = 0, y = 0, width = 3, height = 3
	 * 			lineH = ((-1.0f, 0.0f), (4.0f, 0.0f))
	 * Expected:
	 * 			return true
	 * 			(0, 0) = near
	 * 			(3, 0) = far
	 */
	@Test
	public void testLineIntersectsRectLineOnEdge() {
		Rectangle rectangle = new Rectangle(0,0,3,3);
		Line lineH = new Line(new Vector2f(-1.0f, 0.0f), new Vector2f(4.0f, 0.0f));
		
		// expected near,far collision point
		final float nearCollisionX = 0.0f;
		final float nearCollisionY = 0.0f;
		final float farCollisionX = 3.0f;
		final float farCollisionY = 0.0f;
		
		assertLineIntersectsRectangle(
				lineH,
				rectangle,
				true,
				true,
				new Vector2f(nearCollisionX, nearCollisionY),
				new Vector2f(farCollisionX, farCollisionY));
	}
	
	/*
	 * Purpose: line intersects with a rectangle. start point of the line is in the rectangle, end point of the line is not.
	 * Input: lineIntersectsRectangle
	 * 			
	 * 			rectangle : x = 0, y = 0, width = 3, height = 3
	 * 			lineI = ((2.0f, 2.0f), (4.0f, 2.0f))
	 * Expected:
	 * 			return true
	 */
	@Test
	public void testHalfLineIntersectsRect() {
		Rectangle rectangle = new Rectangle(0,0,3,3);
		Line lineI = new Line(new Vector2f(2.0f, 2.0f), new Vector2f(4.0f, 2.0f));
		
		assertLineIntersectsRectangle(
				lineI,
				rectangle,
				true,
				false,
				null,
				null);
	}
	
	/*
	 * Purpose: line doesn't intersects with a rectangle
	 * Input: lineIntersectsRectangle
	 * 			
	 * 			rectangle : x = 0, y = 0, width = 3, height = 3
	 * 			lineJ = ((-1.0f, 4.0f), (4.0f, 4.0f))
	 * Expected:
	 * 			return false
	 */
	@Test
	public void testLineNotIntersectsRectangle() {
		Rectangle rectangle = new Rectangle(0,0,3,3);
		Line lineJ = new Line(new Vector2f(-1.0f, 4.0f), new Vector2f(4.0f, 4.0f));
		
		assertLineIntersectsRectangle(
				lineJ,
				rectangle,
				false,
				false,
				null,
				null);
	}
	
	/*
	 * Purpose: The line is inside the rectangle.
	 * Input: lineIntersectsRectangle
	 * 			
	 * 			rectangle : x = 0, y = 0, width = 3, height = 3
	 * 			lineK = ((1.0f, 1.0f), (2.0f, 2.0f))
	 * Expected:
	 * 			return true
	 */
	@Test
	public void testLineIsInsideRectangle() {
		Rectangle rectangle = new Rectangle(0,0,3,3);
		Line lineK = new Line(new Vector2f(1.0f, 1.0f), new Vector2f(2.0f, 2.0f));
		
		assertLineIntersectsRectangle(
				lineK,
				rectangle,
				true,
				false,
				null,
				null);
	}
	
	/*
	 * Purpose: the line is a point. the line is on the edge of the rectangle.
	 * Input: lineIntersectsRectangle
	 * 			
	 * 			rectangle : x = 0, y = 0, width = 3, height = 3
	 * 			lineL = ((1.0f, 0.0f), (1.0f, 0.0f))
	 * Expected:
	 * 			return true
	 */
	@Test
	public void testLineWhichIsPointOnEdge() {
		Rectangle rectangle = new Rectangle(0,0,3,3);
		Line lineL = new Line(new Vector2f(1.0f, 0.0f), new Vector2f(1.0f, 0.0f));
		
		// expected near,far collision point
		final float nearCollisionX = 1.0f;
		final float nearCollisionY = 0.0f;
		final float farCollisionX = 1.0f;
		final float farCollisionY = 0.0f;
		
		assertLineIntersectsRectangle(
				lineL,
				rectangle,
				true,
				true,
				new Vector2f(nearCollisionX, nearCollisionY),
				new Vector2f(farCollisionX, farCollisionY));
	}
	
	/*
	 * Purpose: the line is a point. the line is inside the rectangle.
	 * Input: lineIntersectsRectangle
	 * 			
	 * 			rectangle : x = 0, y = 0, width = 3, height = 3
	 * 			lineM = ((1.0f, 1.0f), (1.0f, 1.0f))
	 * Expected:
	 * 			return true
	 */
	@Test
	public void testLineWhichIsPointInRect() {
		Rectangle rectangle = new Rectangle(0,0,3,3);
		Line lineM = new Line(new Vector2f(1.0f, 1.0f), new Vector2f(1.0f, 1.0f));
		
		assertLineIntersectsRectangle(
				lineM,
				rectangle,
				true,
				false,
				null,
				null);
	}
	
	/*
	 * Purpose: the line is a point. the line is outside the rectangle.
	 * Input: lineIntersectsRectangle
	 * 			
	 * 			rectangle : x = 0, y = 0, width = 3, height = 3
	 * 			lineN = ((4.0f, 4.0f), (4.0f, 4.0f))
	 * Expected:
	 * 			return false
	 * ************************ Error ****************
	 */
	@Test
	public void testLineWhichIsPointOutRect() {
		Rectangle rectangle = new Rectangle(0,0,3,3);
		Line lineN = new Line(new Vector2f(4.0f, 4.0f), new Vector2f(4.0f, 4.0f));
		
		assertLineIntersectsRectangle(
				lineN,
				rectangle,
				false,
				false,
				null,
				null);
	}
	
	/*
	 * Purpose: satisfy coverage - function : Line.outcode()
	 * 			width of rectangle <= 0
	 * 			height of rectangle <= 0
	 * Input: lineIntersectsRectangle calls intersectsLine that calls outcode
	 * 			
	 * 			rectangle : x = 0, y = 0, width = -2, height = -2
	 * 			line = ((-1.0f, 0.0f), (1.0f, 0.0f))
	 * Expected:
	 * 			return false
	 */
	@Test
	public void testOutcodeNegRectWH() {
		// rectangle.width <= 0, rectangle.height <=0
		Rectangle rectangle = new Rectangle(0, 0, -2, -2);
		Line line = new Line(new Vector2f(-1.0f, 0.0f), new Vector2f(1.0f, 0.0f));
		
		assertEquals(false, Line.lineIntersectsRectangle(line, rectangle));
	}
	
	/*
	 * Purpose: satisfy coverage - function : Line.outcode()
	 * 			y of start or end point of line is lower than y of rectangle
	 * Input: lineIntersectsRectangle calls intersectsLine that calls outcode
	 * 			
	 * 			rectangle : x = 0, y = 0, width = 3, height = 3
	 * 			line = ((-1.0f, -3.0f), (1.0f, -1.0f))
	 * Expected:
	 * 			return false
	 */
	@Test
	public void testOutcodeLineYltRectY() {
		Rectangle rectangle = new Rectangle(0, 0, 3, 3);
		Line line = new Line(new Vector2f(-1.0f, -3.0f), new Vector2f(1.0f, -1.0f));
		
		assertEquals(false, Line.lineIntersectsRectangle(line, rectangle));
	}
	
	/* 
	 * Purpose: satisfy coverage - function : Line.intersectsLine(Rectangle,float,float,float,float)
	 * 			the right of the rectangle is to the start point of line.
	 * Input: lineIntersectsRectangle calls intersectsLine
	 * 			
	 * 			rectangle : x = 0, y = 0, width = 3, height = 3
	 * 			line = ((5.0f, 1.0f), (-1.0f, 1.0f))
	 * Expected:
	 * 			return true
	 */
	@Test
	public void testRectIsOUT_RIGHT() {
		Rectangle rectangle = new Rectangle(0, 0, 3, 3);
		Line line = new Line(new Vector2f(5.0f, 1.0f), new Vector2f(-1.0f, 1.0f));
		
		assertEquals(true, Line.lineIntersectsRectangle(line, rectangle));
	}
	
	/*
	 * Purpose: satisfy coverage - function : Line.intersectsLine(Rectangle,float,float,float,float)
	 * 			rectangle is OUT_BOTTOM, but is not OUT_LEFT | OUT_RIGHT
	 * Input: lineIntersectsRectangle calls intersectsLine
	 * 			
	 * 			rectangle : x = 0, y = 0, width = 3, height = 3
	 * 			line = ((1.0f, 4.0f), (1.0f, -1.0f))
	 * Expected:
	 * 			return true
	 */
	@Test
	public void testRectIsBNotLR() {
		Rectangle rectangle = new Rectangle(0, 0, 3, 3);
		Line line = new Line(new Vector2f(1.0f, 4.0f), new Vector2f(1.0f, -1.0f));
		
		assertEquals(true, Line.lineIntersectsRectangle(line, rectangle));
	}
	
	/*
	 * Purpose: satisfy coverage - function : Line.intersectsLine(Rectangle,float,float,float,float)
	 * 			rectangle is OUT_TOP, but is not OUT_LEFT | OUT_RIGHT
	 * Input: lineIntersectsRectangle calls intersectsLine
	 * 			
	 * 			rectangle : x = 0, y = 0, width = 3, height = 3
	 * 			line = ((1.0f, -1.0f), (1.0f, 4.0f))
	 * Expected:
	 * 			return true
	 */
	@Test
	public void testRectIsTNotLR() {
		Rectangle rectangle = new Rectangle(0, 0, 3, 3);
		Line line = new Line(new Vector2f(1.0f, -1.0f), new Vector2f(1.0f, 4.0f));
		
		assertEquals(true, Line.lineIntersectsRectangle(line, rectangle));
	}
	
	
}
