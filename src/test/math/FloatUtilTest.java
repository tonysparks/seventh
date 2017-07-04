package test.math;

import static java.lang.Math.cos;
import static java.lang.Math.sin;
import static org.junit.Assert.*;

import org.junit.Test;

import seventh.math.FloatUtil;
import seventh.math.Vector2f;

public class FloatUtilTest {
    /*
     * Purpose: Determine if two floats are equal by a given Epsilon.
     * Input: float, float // float,float,float
     * Expected: 
     *             if two float variables are Equal, return true
     *            if two float variables aren't Equal, return false  
     */
    @Test
    public void eqTest() {
        float x = 1.234f;
        float y = 1.2345f;
        float z = 1.24f;
        
        assertTrue(FloatUtil.eq(x,y));
        assertFalse(FloatUtil.eq(x,z));
        assertFalse(FloatUtil.eq(x,y,0.0001f));
        assertTrue(FloatUtil.eq(x,z,0.01f));
    }
    
    /*
     * Purpose: Determine if one float is greater than another
     * Input: float, float
     * Expected: 
     *             if one float is greater than another, return true
     *            if one float isn't greater than another, return false  
     */
    @Test
    public void gtTest() {
        float x = 1.234f;
        float y = 1.2450f;
        float z = 1.24501f;
        
        assertTrue(FloatUtil.gt(y,x));
        assertFalse(FloatUtil.gt(x,y));
        assertFalse(FloatUtil.gt(y,z));

    }
    
    
    /*
     * Purpose: Determine if one float is less than another
     * Input: float, float
     * Expected: 
     *             if one float is less than another, return true
     *            if one float isn't less than another, return false  
     */
    @Test
    public void ltTest() {
        float x = 1.234f;
        float y = 1.2450f;
        float z = 1.24501f;
        
        assertFalse(FloatUtil.lt(y,x));
        assertTrue(FloatUtil.lt(x,y));
        assertFalse(FloatUtil.lt(y,z));
    }

    /*
     * Purpose: Determine if one float is greater than another or equal
     * Input: float, float
     * Expected: 
     *             if one float is greater than another or equal, return true
     *            if one float isn't greater than another or equal, return false  
     */
    @Test
    public void gteTest() {
        float x = 1.234f;
        float y = 1.2450f;
        float z = 1.24501f;
        
        assertTrue(FloatUtil.gte(y,x));
        assertFalse(FloatUtil.gte(x,y));
        assertTrue(FloatUtil.gte(y,z));

    }
    /*
     * Purpose: Determine if one float is less than another or equal
     * Input: float, float
     * Expected: 
     *             if one float is less than another or equal, return true
     *            if one float isn't less than another or equal, return false  
     */
    @Test
    public void lteTest() {
        float x = 1.234f;
        float y = 1.2450f;
        float z = 1.24501f;
        
        assertFalse(FloatUtil.lte(y,x));
        assertTrue(FloatUtil.lte(x,y));
        assertTrue(FloatUtil.lte(y,z));
    }
    

    /*
     * Purpose: add two float array's element in same index
     * Input: float[], float[], float[]
     * Expected: 
     *             the added float array 
     */
    @Test
    public void Vector2fAddTest() {
        float[] a={1.234f,2.323f};
        float[] b={3.432f, 7.544f};
        float[] dest={0,0};
        float expectd1 =1.234f+3.432f;
        float expectd2 =2.323f+7.544f;
        FloatUtil.Vector2fAdd(a, b, dest);
        assertEquals(expectd1,dest[0],0.00001f);
        assertEquals(expectd2,dest[1],0.00001f);
    }
    
    /*
     * Purpose: subtract two float array's element in same index
     * Input: float[], float[], float[]
     * Expected: 
     *             the subtracted float array 
     */
    @Test
    public void Vector2fSubtractTest() {
        float[] a={1.234f,2.323f};
        float[] b={3.432f, 7.544f};
        float[] dest={0,0};
        float expectd1 =1.234f-3.432f;
        float expectd2 =2.323f-7.544f;
        FloatUtil.Vector2fSubtract(a, b, dest);
        assertEquals(expectd1,dest[0],0.00001f);
        assertEquals(expectd2,dest[1],0.00001f);
    }
    

    /*
     * Purpose: multiply two float array's element in same index
     * Input: float[], float[], float[]
     * Expected: 
     *             the multiplied float array 
     */
    @Test
    public void Vector2fMultTest() {
        float[] a={1.234f,2.323f};
        float[] b={3.432f, 7.544f};
        float[] dest={0,0};
        float expectd1 =1.234f*3.432f;
        float expectd2 =2.323f*7.544f;
        float expectd3 =1.234f*4.123f;
        float expectd4 =2.323f*4.123f;
        FloatUtil.Vector2fMult(a, b, dest);
        assertEquals(expectd1,dest[0],0.00001f);
        assertEquals(expectd2,dest[1],0.00001f);
        FloatUtil.Vector2fMult(a, 4.123f, dest);
        assertEquals(expectd3,dest[0],0.00001f);
        assertEquals(expectd4,dest[1],0.00001f);
    }
    
    /*
     * Purpose: divide two float array's element in same index
     * Input: float[], float[], float[]
     * Expected: 
     *             the divided float array 
     */
    @Test
    public void Vector2fDivTest() {
        float[] a={1.234f,2.323f};
        float[] b={3.432f, 7.544f};
        float[] dest={0,0};
        float expectd1 =1.234f/3.432f;
        float expectd2 =2.323f/7.544f;
        FloatUtil.Vector2fDiv(a, b, dest);
        assertEquals(expectd1,dest[0],0.00001f);
        assertEquals(expectd2,dest[1],0.00001f);
    }
    
    /*
     * Purpose: add two float array's element in different index(0 and 1, 1 and 0)
     * Input: float[], float[]
     * Expected: 
     *             the cross added float 
     */
    @Test
    public void Vector2fCrossTest() {
        float[] a={1.234f,2.323f};
        float[] b={3.432f, 7.544f};
        float expectd1 =1.234f*7.544f-2.323f*3.432f;
        assertEquals(expectd1,FloatUtil.Vector2fCross(a, b),0.00001f);
    }
    

    /*
     * Purpose: Determine if two float arrays are equal
     * Input: float[], float[]
     * Expected: 
     *             if two float arrays are Equal, return true
     *            if two float arrays aren't Equal, return false  
     */
    @Test
    public void Vector2fEqualsTest() {
        float[] a={1.234f,2.323f};
        float[] b={1.234f,2.323f};
        float[] c={3.432f, 7.544f};
        float[] d={1.234f, 7.544f};
        assertTrue(FloatUtil.Vector2fEquals(a, b));
        assertFalse(FloatUtil.Vector2fEquals(a, c));
        assertFalse(FloatUtil.Vector2fEquals(a, d));
    }
    

    /*
     * Purpose: Determine if two float arrays are equal by a giving Epsilon
     * Input: float[], float[]
     * Expected: 
     *             if two float arrays are Equal, return true
     *            if two float arrays aren't Equal, return false  
     */
    @Test
    public void Vector2fApproxEqualsTest() {
        float[] a={1.234f,2.323f};
        float[] b={1.234f,2.323f};
        float[] c={3.432f, 7.544f};
        float[] d={1.234f, 7.544f};
        assertTrue(FloatUtil.Vector2fApproxEquals(a, b));
        assertFalse(FloatUtil.Vector2fApproxEquals(a, c));
        assertFalse(FloatUtil.Vector2fApproxEquals(a, d));
    }
    
    /*
     * Purpose: rotate one float point(x,y) by radians
     * Input: float[],float, float[]
     * Expected: 
     *             the float array having rotated float point(x,y) 
     */
    @Test
    public void Vector2fRotateTest() {
        float[] a={1.234f,2.323f};
        float[] dest={0,0};
        float radians = 30.0f;
        float expectd1 =(float)(1.234f * cos(radians) - 2.323f * sin(radians));
        float expectd2 =(float)(1.234f * sin(radians) + 2.323f * cos(radians));
        FloatUtil.Vector2fRotate(a, radians, dest);
        assertEquals(expectd1,dest[0],0.00001f);
        assertEquals(expectd2,dest[1],0.00001f);
    }
    
    /*
     * Purpose: get the length from zero point(0,0) to one float point(x,y) 
     * Input: float[]
     * Expected: 
     *             the length from zero point(0,0) to one float point(x,y) 
     */
    @Test
    public void Vector2fLengthTest() {
        float[] a={1.234f,2.323f};
        float expectd1 =(float)Math.sqrt( 1.234f * 1.234f + 2.323f * 2.323f) ;
        assertEquals(expectd1,FloatUtil.Vector2fLength(a),0.00001f);
    }
    
    /*
     * Purpose: get the length'square from zero point(0,0) to one float point(x,y) 
     * Input: float[]
     * Expected: 
     *             the length'square from zero point(0,0) to one float point(x,y) 
     */
    @Test
    public void Vector2fLengthSqTest() {
        float[] a={1.234f,2.323f};
        float expectd1 =1.234f * 1.234f + 2.323f * 2.323f ;
        assertEquals(expectd1,FloatUtil.Vector2fLengthSq(a),0.00001f);
    }
    
    /*
     * Purpose: get the normalized point(x,y) from one float point(x,y) 
     * Input: float[], float[]
     * Expected: 
     *              the normalized point(x,y) from one float point(x,y) 
     */
    @Test
    public void Vector2fNormalizeTest() {
        float[] a={1.234f,2.323f};
        float[] b={0.0f,0.0f};
        float[] dest={0,0};
        float fLen = 1.0f / ( (float)Math.sqrt( (1.234f * 1.234f + 2.323f * 2.323f) ));
        float expectd1 = 1.234f * fLen;
        float expectd2 = 2.323f * fLen;
        FloatUtil.Vector2fNormalize(b, dest);
        FloatUtil.Vector2fNormalize(a, dest);        
        assertEquals(expectd1,dest[0],0.00001f);
        assertEquals(expectd2,dest[1],0.00001f);
    }
    

    /*
     * Purpose: copy one float array to another
     * Input: float[], float[]
     * Expected: 
     *             copied float array
     */
    @Test
    public void Vector2fCopyTest() {
        float[] a={1.234f,2.323f};
        float[] dest={0,0};
        float expectd1 = 1.234f;
        float expectd2 = 2.323f;
        FloatUtil.Vector2fCopy(a, dest);        
        assertEquals(expectd1,dest[0],0.00001f);
        assertEquals(expectd2,dest[1],0.00001f);
    }
    

    /*
     * Purpose: change each float array's element to zero
     * Input: float[]
     * Expected: 
     *             the array having zero element
     */
    @Test
    public void Vector2fZeroOutTest() {
        float[] a={1.234f,2.323f};
        float expectd1 = 0;
        float expectd2 = 0;
        FloatUtil.Vector2fZeroOut(a);        
        assertEquals(expectd1,a[0],0.00001f);
        assertEquals(expectd2,a[1],0.00001f);
    }
    

    /*
     * Purpose: check whether each float array's element is zero
     * Input: float[]
     * Expected: 
     *             if all array's elements are zero, return true
     *             unless, return false
     */
    @Test
    public void Vector2fIsZeroTest() {
        float[] a={1.234f,2.323f};
        float[] b={0,0};
        float[] c={0,0.3232f};
        assertFalse(FloatUtil.Vector2fIsZero(a));
        assertTrue(FloatUtil.Vector2fIsZero(b));
        assertFalse(FloatUtil.Vector2fIsZero(c));
    }
    
    /*
     * Purpose: make one vector2f object with one float array
     * Input: float[]
     * Expected: 
     *             the one vector2f object
     */
    @Test
    public void Vector2fToVector2fTest() {
        float[] a={1.234f,2.323f};
        Vector2f b = FloatUtil.Vector2fToVector2f(a);
        assertEquals(a[0],b.get(0),0.00001f);
        assertEquals(a[1],b.get(1),0.00001f);
    }
    
    /*
     * Purpose: make one array having zero elements
     * Input: 
     * Expected: 
     *             the array having zero elements
     */
    @Test
    public void Vector2fNewTest() {
        float[] a = FloatUtil.Vector2fNew();
        assertEquals(0,a[0],0.00001f);
        assertEquals(0,a[1],0.00001f);
    }
    
    /*
     * Purpose:Determine if each array'element in same index is greater than another or equal
     * Input: float[], float[]
     * Expected: 
     *             one array's all element is greater than another or equal, return true
     *             unless return false
     */
    @Test
    public void Vector2fGreaterOrEqTest() {
        float[] a={1.234f,2.323f};
        float[] b={3.432f, 7.544f};
        assertFalse(FloatUtil.Vector2fGreaterOrEq(a,b));
        assertTrue(FloatUtil.Vector2fGreaterOrEq(b,a));
    }
    


    /*
     * Purpose: round off one float array's each element
     * Input: float[], float[]
     * Expected: 
     *             the rounded array
     */
    @Test
    public void Vector2fRoundTest() {
        float[] a={1.234f,2.323f};
        float[] dest={0,0};
        float expectd1 = Math.round(a[0]);
        float expectd2 = Math.round(a[1]);
        FloatUtil.Vector2fRound(a, dest);        
        assertEquals(expectd1,dest[0],0.00001f);
        assertEquals(expectd2,dest[1],0.00001f);
    }
    


    /*
     * Purpose: make one float array with two floats
     * Input: float[], float, float
     * Expected: 
     *             the float array with two floats
     */
    @Test
    public void Vector2fSetTest() {
        float[] dest={0,0};
        float expectd1 = 1.234f;
        float expectd2 = 2.323f;
        FloatUtil.Vector2fSet(dest, expectd1, expectd2);        
        assertEquals(expectd1,dest[0],0.00001f);
        assertEquals(expectd2,dest[1],0.00001f);
    }
    /*
     * Purpose: make negated float array with one float array
     * Input: float[], float[]
     * Expected: 
     *             the negated float array 
     */
    
    @Test
    public void Vector2fNegateTest() {
        float[] a={1.234f,2.323f};
        float[] dest={0,0};
        float expectd1 = -1.234f;
        float expectd2 = -2.323f;
        FloatUtil.Vector2fNegate(a, dest);        
        assertEquals(expectd1,dest[0],0.00001f);
        assertEquals(expectd2,dest[1],0.00001f);
    }
}
