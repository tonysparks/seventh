package test.math;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.junit.Assert.*;

import org.junit.Test;

import seventh.math.OBB;
import seventh.math.Rectangle;
import seventh.math.Vector2f;

public class OBBTest {

    public float width;
    public float height;
    public float orientation;
    
    public Vector2f center;
    
    public Vector2f topLeft, topRight, bottomLeft, bottomRight;
	
	
    /*
     * Purpose: Test Defaults Constructor
     * Input: obb default
     * Expected: 
     *             All Entities of Constructor are 0
     */
	@Test
	public void DefaultConstructorTest() {
		OBB obb = new OBB();
		assertTrue(0 == obb.getOrientation());
		assertTrue(0 == obb.getWidth()); 
		assertTrue(0 == obb.getHeight());
		assertTrue(0 == obb.length());
	}
	
	
    /*
     * Purpose: Test Constructor of a default rectangle as input
     * Input: obb default Rectangle
     * Expected: 
     *             All Entities of Constructor are 0
     */
	@Test
	public void DefaultRectangleConstructorTest() {
		Rectangle r = new Rectangle();
		OBB obb = new OBB(r);
		assertTrue(0 == obb.getOrientation());
		assertTrue(0 == obb.getWidth()); 
		assertTrue(0 == obb.getHeight());
		assertTrue(0 == obb.length());
		assertEquals(new Vector2f(0, 0), obb.getCenter());
	}

	
    /*
     * Purpose: Test Constructor of a rectangle as input
     * Input: obb Rectangle
     * Expected: 
     *             All Entities of Constructor are same as rectangle's
     *             width : 3
     *             height : 4
     *             length : 5
     *             center : (2, 3)
     */
	@Test
	public void RectangleConstructorTest() {
		Rectangle r = new Rectangle(1, 1, 3, 4); // r.x + r.width/2, r.y + r.height/2
		OBB obb = new OBB(r);
		assertTrue(0 == obb.getOrientation());
		assertTrue(3 == obb.getWidth()); 
		assertTrue(4 == obb.getHeight());
		assertTrue(5 == obb.length());
		assertEquals(new Vector2f(2, 3), obb.getCenter());
	}
	
	
    /*
     * Purpose: Test Constructor of a rectangle, an orientation as input
     * Input: obb Orientation, Rectangle
     * Expected: 
     *             All Entities of Constructor are same as rectangle's
     *             width : 3
     *             height : 4
     *             center : (2, 3)
     *             
     *             orientation : 3
     */
	@Test
	public void OrientationRectangleConstructorTest() {
		Rectangle r = new Rectangle(1, 1, 3, 4);
		OBB obb = new OBB(3, r);
		assertTrue(3 == obb.getOrientation());
		assertTrue(3 == obb.getWidth()); 
		assertTrue(4 == obb.getHeight());
		assertEquals(new Vector2f(2, 3), obb.getCenter());
	}
	
    /*
     * Purpose: Test Constructor of an default OBB as input
     * Input: obb default OBB
     * Expected: 
     *             All Entities of Constructor are same as origin OBB's
     */
	@Test
	public void DefaultOBBConstructorTest() {
		OBB obb = new OBB();
		OBB obb_param = new OBB(obb);
		assertTrue(obb_param.getOrientation() == obb.getOrientation());
		assertTrue(obb_param.getWidth() == obb.getWidth()); 
		assertTrue(obb_param.getHeight() == obb.getHeight());
		assertEquals(obb_param.getCenter(), obb.getCenter());
		assertTrue(obb_param.length() == obb.length());
	}

    /*
     * Purpose: Test Constructor of an OBB as input
     * Input: obb OBB
     * Expected: 
     *             All Entities of Constructor are same as origin OBB's
     */
	@Test
	public void OBBConstructorTest() {
		Rectangle r = new Rectangle(1, 1, 3, 4);
		OBB obb = new OBB(3, r);
		OBB obb_param = new OBB(obb);
		assertTrue(obb_param.getOrientation() == obb.getOrientation());
		assertTrue(obb_param.getWidth() == obb.getWidth()); 
		assertTrue(obb_param.getHeight() == obb.getHeight());
		assertEquals(obb_param.getCenter(), obb.getCenter());
		assertTrue(obb_param.length() == obb.length());
	}

    /*
     * Purpose: Test Constructor of orientation, center x,y , width, height as input
     * Input: obb orientation = 1, center x = 1, center y = 1, width = 3, height = 4
     * Expected: 
     *             orientation : 1
     *             width : 3
     *             height : 4
     *             center : (1, 1)
     */
	@Test
	public void AllParamConstructorTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);
		assertTrue(1 == obb.getOrientation());
		assertTrue(3 == obb.getWidth()); 
		assertTrue(4 == obb.getHeight());
		assertEquals(new Vector2f(1, 1), obb.getCenter());
	}
	
    /*
     * Purpose: Test Constructor of orientation, center vector, width, height as input
     * Input: obb orientation = 1, center vector (1, 1), width = 3, height = 4
     * Expected: 
     *             orientation : 1
     *             width : 3
     *             height : 4
     *             center : (1, 1)
     */
	@Test
	public void AllParamWithVectorConstructorTest() {
		OBB obb = new OBB(1, new Vector2f(2, 3), 3, 4);
		assertTrue(1 == obb.getOrientation());
		assertTrue(3 == obb.getWidth()); 
		assertTrue(4 == obb.getHeight());
		assertEquals(new Vector2f(2, 3), obb.getCenter());
	}
	

	
	
	
    /*
     * Purpose: Test update of orientation, center vector as input
     * Input: update orientation = 2, center vector(4, 4)
     * Expected: 
     *             orientation : 1 -> 2
     *             center : (4, 4)
     */
	@Test
	public void OriCenterVectorUpdateTest() {
		OBB obb = new OBB();

		obb.update(2, new Vector2f(4, 4));
		assertTrue(2 == obb.getOrientation());
		assertEquals(new Vector2f(4, 4), obb.getCenter());
		
	}
	
    /*
     * Purpose: Test update of orientation, center as input
     * Input: update orientation = 2, center x = 4, y = 4
     * Expected: 
     *             orientation : 1 -> 2
     *             center : (4, 4)
     */
	@Test
	public void OriCenterUpdateTest() {
		OBB obb = new OBB();
		
		obb.update(2, 4, 4);
		assertTrue(2 == obb.getOrientation());
		assertEquals(new Vector2f(4, 4), obb.getCenter());
		
	}
	
    /*
     * Purpose: Test rotateAround of center vector as input
     * Input: rotateAround position vector(4, 4) newOrientation = 2
     * Expected: 
     *             orientation : 2
     *             center : (7.9763327, 2.5205483)
     */
	@Test
	public void RotateAroundTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);

		float x = obb.center.x;
		float y = obb.center.y;
		obb.rotateAround(new Vector2f(4, 4), 2);
		assertTrue(2 == obb.getOrientation());
		x -= 4;
		y -= 4;
		float x2 = (float)(x * cos(2) - y * sin(2));
        float y2 = (float)(x * sin(2) + y * cos(2));
        x2 += 4;
        y2 += 4;
		assertEquals(new Vector2f(x2, y2), obb.getCenter());
	}
	
	
    /*
     * Purpose: Test rotateTo of new Orientation as input
     * Input: rotateTo newOrientation = 2
     * Expected: 
     *             orientation : 2
     */
	@Test
	public void RotateToTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);

		obb.rotateTo(2);
		assertTrue(2 == obb.getOrientation());
		
	}
	

    /*
     * Purpose: Test rotate of adjustBy as input
     * Input: rotate adjustBy = 2, orientation += adjustBy
     * Expected: 
     *             orientation : 3
     */
	@Test
	public void rotateTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);

		obb.rotate(2);
		assertTrue(3 == obb.getOrientation());
		
	}
	
    /*
     * Purpose: Test translate of move position x, y as input
     * Input: translate move x = 2, y = 2    center x += move x, center y += move y
     * Expected: 
     *             center = (3, 3)
     */
	@Test
	public void TranslateVectorTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);
	
		obb.translate(new Vector2f(2, 2));
		assertEquals(new Vector2f(1+2, 1+2), obb.getCenter());
	}
	
    /*
     * Purpose: Test translate of move position vector as input
     * Input: translate move vector (2, 2) center (x+2, y+2)
     * Expected: 
     *             center = (3, 3)
     */
	@Test
	public void TranslateTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);

		obb.translate(2, 2);
		assertEquals(new Vector2f(1+2, 1+2), obb.getCenter());
	}
	
	
    /*
     * Purpose: Test setLocation of center position as input
     * Input: setLocation new center x, new center y
     * Expected: 
     *             center = (3, 3)
     */
	@Test
	public void SetLocationTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);

		obb.setLocation(3, 3);
		assertEquals(new Vector2f(3, 3), obb.getCenter());
	}
	
	/*
     * Purpose: Test setLocation of center position vector as input
     * Input: setLocation vector(new center x, new center y)
     * Expected: 
     *             center = (3, 3)
     */
	@Test
	public void SetLocationVectorTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);

		obb.setLocation(new Vector2f(3, 3));
		assertEquals(new Vector2f(3, 3), obb.getCenter());
	}
	
	/*
     * Purpose: Test setBound of new width, height as input
     * Input: setBound width = 4, height = 5
     * Expected: 
     *             width = 4
     *             height = 5
     */
	@Test
	public void SetBoundTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);

		obb.setBounds(4, 5);
		assertTrue(4 == obb.getWidth()); 
		assertTrue(5 == obb.getHeight());
	}
	
	
	/*
     * Purpose: Test contains of inner point as input
     * Input: contains point(0, 0) which in OBB
     * Expected: 
     *             return true
     */
	@Test
	public void InnerPointContainsTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);
		
		assertEquals(true, obb.contains(0, 0));
	}
	
	
	/*
     * Purpose: Test contains of external point as input
     * Input: contains point(6, 6) which out of OBB
     * Expected: 
     *             return false
     */
	@Test
	public void ExternPointContainsTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);
		
		assertEquals(false, obb.contains(6, 6));
	}
	
	/*
     * Purpose: Test contains of inner vector point as input
     * Input: contains point(0, 0) which in OBB
     * Expected: 
     *             return true
     */
	@Test
	public void InnerVectorContainsTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);
		
		assertEquals(true, obb.contains(new Vector2f(0, 0)));
	}
	
	/*
     * Purpose: Test contains of external vector point as input
     * Input: contains point(6, 6) which out of OBB
     * Expected: 
     *             return false
     */
	@Test
	public void ExternVectorContainsTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);
		
		assertEquals(false, obb.contains(new Vector2f(6, 6)));
	}
	
	
	/*
     * Purpose: Test intersects of intersect rectangle as input
     * Input: intersects intersect rectangle
     * Expected: 
     *             return true
     */
	@Test
	public void InnerRectangleIntersectsTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);
		Rectangle inter_r = new Rectangle(1, 1, 3, 4);
		Rectangle extern_r = new Rectangle(10, 10, 3, 4);
		assertEquals(true, obb.intersects(inter_r));
		assertEquals(false, obb.intersects(extern_r));
	}
	
	/*
     * Purpose: Test intersects of extern rectangle as input
     * Input: intersects extern rectangle
     * Expected: 
     *             return false
     */
	@Test
	public void ExternRectangleIntersectsTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);
		Rectangle extern_r = new Rectangle(10, 10, 3, 4);
		assertEquals(false, obb.intersects(extern_r));
	}
	
	/*
     * Purpose: Test expensiveIntersects of inner rectangle as input
     * Input: expensiveIntersects intersect rectangle
     * Expected: 
     *             return true
     */
	@Test
	public void InnerRectangleExpensiveIntersectsTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);
		Rectangle inner_r = new Rectangle(1, 1, 2, 2);
		Rectangle extern_r = new Rectangle(10, 10, 3, 4);
		assertEquals(true, obb.expensiveIntersects(inner_r));
		assertEquals(false, obb.expensiveIntersects(extern_r));
	}
	
	/*
     * Purpose: Test expensiveIntersects of extern rectangle as input
     * Input: expensiveIntersects extern rectangle
     * Expected: 
     *             return false
     */
	@Test
	public void ExternRectangleExpensiveIntersectsTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);
		Rectangle extern_r = new Rectangle(10, 10, 3, 4);
		assertEquals(false, obb.expensiveIntersects(extern_r));
	}
	
	/*
     * Purpose: Test intersects of inter obb as input
     * Input: intersects inter obb
     * Expected: 
     *             return true
     */
	@Test
	public void InnerOBBIntersectsTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);
		OBB inter_obb = new OBB(1, 2, 2, 3, 4);
		assertEquals(true, obb.intersects(inter_obb));
	}

	/*
     * Purpose: Test intersects of extern obb as input
     * Input: intersects extern obb
     * Expected: 
     *             return false
     */
	@Test
	public void ExternOBBIntersectsTest() {
		OBB obb = new OBB(1, 1, 1, 3, 4);
		OBB extern_obb = new OBB(5, 5, 2, 3, 3);
		assertEquals(false, obb.intersects(extern_obb));
	}
	
}
